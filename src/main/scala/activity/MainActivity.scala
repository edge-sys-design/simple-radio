/** Copyright (C) 2012 Edge System Design, LLC.  All rights reserved.
  *
  * This program is free software; you can redistribute it and/or modify
  * it under the terms of the GNU General Public License as published by
  * the Free Software Foundation; either version 2 of the License, or
  * (at your option) any later version.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  * GNU General Public License for more details.
  *
  * You should have received a copy of the GNU General Public License
  * along with this program. If not, see <http://www.gnu.org/licenses/>.
  *
  * Author(s): Ricky Elrod <relrod@edgesysdesign.com>
  */

package com.edgesysdesign.simpleradio

import _root_.android.animation.ObjectAnimator
import _root_.android.app.{Activity, AlertDialog}
import _root_.android.content.{ContentValues, DialogInterface, Intent}
import _root_.android.os.{AsyncTask, Build, Bundle}
import _root_.android.text.{Editable, TextWatcher}
import _root_.android.view.{Menu, MenuItem, View}
import _root_.android.view.{GestureDetector, MotionEvent, View}
import _root_.android.view.GestureDetector.{SimpleOnGestureListener, OnDoubleTapListener}
import _root_.android.view.View.OnTouchListener
import _root_.android.widget.{AdapterView, ArrayAdapter, BaseAdapter, EditText, TextView, Toast}
import _root_.android.widget.{Spinner}
import _root_.android.widget.AdapterView.OnItemClickListener

import com.edgesysdesign.simpleradio.devel.Devel
import com.edgesysdesign.simpleradio.Implicits._
import com.edgesysdesign.frequency.FrequencyImplicits._
import com.edgesysdesign.frequency.HamFrequency

import scala.collection.mutable.ArrayBuffer
import scala.language.reflectiveCalls

class MainActivity extends Activity with TypedActivity {
  val memories = new ArrayBuffer[MemoryEntry]

  override def onCreate(bundle: Bundle) {
    super.onCreate(bundle)
    val res = getResources()
    setContentView(R.layout.receive)
    val db = new MemoryEntryHelper(this).getWritableDatabase

    val frequency = findView(TR.frequency)
    frequency.setText(HamFrequency("145.170".MHz))

    val plTonesSpinner = findView(TR.pl_tone)
    val plTonesAdapter = ArrayAdapter.createFromResource(
      this,
      R.array.pl_tones,
      android.R.layout.simple_spinner_item)
    plTonesAdapter.setDropDownViewResource(
      android.R.layout.simple_spinner_dropdown_item)
    plTonesSpinner.setAdapter(plTonesAdapter)

    val modesSpinner = findView(TR.mode)
    val modesAdapter = ArrayAdapter.createFromResource(
      this,
      R.array.modes,
      android.R.layout.simple_spinner_item)
    modesAdapter.setDropDownViewResource(
      android.R.layout.simple_spinner_dropdown_item)
    modesSpinner.setAdapter(modesAdapter)

    findView(TR.offset).setText("-600 KHz")

    val memoryCursor = db
      .query("memory_entries", null, null, null, null, null, null)
      .tap(_.moveToFirst())
    for (i <- 0 to memoryCursor.getCount() - 1) {
      memories.append(MemoryEntry.fromCursor(memoryCursor))
      memoryCursor.moveToNext()
    }

    val memoriesAdapter = new MemoryEntryAdapter(
      memories,
      this)

    findView(TR.memory_recall).tap { v =>
      v.setAdapter(memoriesAdapter)
      v.setOnItemClickListener(new OnItemClickListener {
        def onItemClick(
          parent: AdapterView[_],
          view: View,
          position: Int,
          id: Long) {
          val entry = parent.getItemAtPosition(position).asInstanceOf[MemoryEntry]

          frequency.setText(entry.frequency.toLong.Hz.MHz)
          modesSpinner.setSelection(modesAdapter.getPosition(entry.mode))

          entry.plTone match {
            case Some(plTone) =>
              plTonesSpinner.setSelection(plTonesAdapter.getPosition(plTone.toString))
            case _ => // TODO: Set it to 'Off' or similar.
          }

          // TODO: Add shift/offset, once we save those.

          Toast.makeText(
            MainActivity.this,
            getString(R.string.switched_to_memory).format(entry.label),
            Toast.LENGTH_SHORT).show()
        }
      })
    }

    if (res.getBoolean(R.bool.development_build) && Build.PRODUCT != "sdk") {
      Devel.checkForUpdates(this)
    }
  }

  override def onCreateOptionsMenu(menu: Menu): Boolean = {
    val inflater = getMenuInflater()
    inflater.inflate(R.menu.test_menu, menu)
    true
  }

  /** Handle items selected from the Options menu.
    *
    * @param item The [[android.view.MenuItem]] that was pressed.
    */
  override def onOptionsItemSelected(item: MenuItem): Boolean = {
    item.getItemId match {
      case R.id.create_new => {

        val labelInput = new EditText(this)
        labelInput.setHint(R.string.memory_name_placeholder)
        labelInput.setSingleLine(true)

        val labelDialog = new AlertDialog.Builder(this)
          .setTitle(R.string.memory_label)
          .setMessage(R.string.memory_query)
          .setView(labelInput)
          .setPositiveButton(R.string.okay, null)
          .show()

        // Override the button's onClick so that we can validate.
        labelDialog
          .getButton(DialogInterface.BUTTON_POSITIVE)
          .setOnClickListener(
            new View.OnClickListener {
              def onClick(v: View) {
                if (labelInput.getText.toString.isEmpty) {
                  Toast.makeText(
                    MainActivity.this,
                    getString(R.string.text_field_unempty),
                    Toast.LENGTH_SHORT).show()
                } else {
                  val values = new ContentValues()
                  values.put("label", labelInput.getText.toString)
                  values.put(
                    "frequency",
                    Long.box(HamFrequency(findView(TR.frequency).getText.toString.MHz).Hz.toLong))
                  values.put("pl_tone", findView(TR.pl_tone).getSelectedItem.toString.toDouble)
                  values.put("mode", findView(TR.mode).getSelectedItem.toString)

                  val db = new MemoryEntryHelper(MainActivity.this).getWritableDatabase
                  val id = db.insert("memory_entries", null, values)

                  val memoryCursor = db
                    .query(
                      "memory_entries",
                      null,
                      "_id = ?",
                      Array(id.toString),
                      null,
                      null,
                      null)
                    .tap(_.moveToFirst())

                  val adapter = findView(TR.memory_recall)
                    .getAdapter
                    .asInstanceOf[MemoryEntryAdapter]

                  adapter
                    .memories
                    .append(MemoryEntry.fromCursor(memoryCursor))

                  runOnUiThread {
                    adapter.notifyDataSetChanged()
                }
                Toast.makeText(
                  MainActivity.this,
                  getString(R.string.memory_saved),
                  Toast.LENGTH_SHORT).show()

                  labelDialog.dismiss()
                }
              }
            })
      }
      case R.id.about => {
        val intent = new Intent(this, classOf[AboutActivity])
        startActivity(intent)
      }
      case R.id.sample_audio => {
        val intent = new Intent(this, classOf[SampleAudioActivity])
        startActivity(intent)
      }
      case _ =>
    }
    true
  }
}
