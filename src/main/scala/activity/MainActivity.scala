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
import _root_.android.widget.{ArrayAdapter, EditText, Toast, Spinner}

import com.edgesysdesign.simpleradio.devel.Devel
import com.edgesysdesign.simpleradio.Implicits._
import com.edgesysdesign.frequency.FrequencyImplicits._

class MainActivity extends Activity with TypedActivity {

  override def onCreate(bundle: Bundle) {
    super.onCreate(bundle)
    val res = getResources()
    setContentView(R.layout.receive)

    val gd = new GestureDetector(this, new FrequencyFlingDetector)
    val frequency = findView(TR.frequency)
    frequency.setText("145.170".MHz)
/*    frequency.setOnTouchListener(new OnTouchListener {
      override def onTouch(view: View, event: MotionEvent): Boolean = {
        gd.onTouchEvent(event)
        true
      }
    })
*/

    val plToneSpinner = findView(TR.pl_tone)
    val plTonesAdapter = ArrayAdapter.createFromResource(
      this,
      R.array.pl_tones,
      android.R.layout.simple_spinner_item)
    plTonesAdapter.setDropDownViewResource(
      android.R.layout.simple_spinner_dropdown_item)
    plToneSpinner.setAdapter(plTonesAdapter)

    val modesSpinner = findView(TR.mode)
    val modesAdapter = ArrayAdapter.createFromResource(
      this,
      R.array.modes,
      android.R.layout.simple_spinner_item)
    modesAdapter.setDropDownViewResource(
      android.R.layout.simple_spinner_dropdown_item)
    modesSpinner.setAdapter(modesAdapter)

    findView(TR.offset).setText("-600 KHz")

    if (res.getBoolean(R.bool.development_build) && Build.PRODUCT != "sdk") {
      Devel.checkForUpdates(this)
    }
  }

  class FrequencyFlingDetector extends SimpleOnGestureListener with OnDoubleTapListener {
    override def onDoubleTap(event: MotionEvent): Boolean = {
      val frequency = findView(TR.frequency)
      ObjectAnimator
        .ofFloat(frequency, "rotation", 0f, 360f)
        .setDuration(1000)
        .start()
      frequency.setText(frequency.getText.toString.MHz + 50.kHz)
      true
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
          .setPositiveButton(
            R.string.okay,
            new DialogInterface.OnClickListener() {
              def onClick(dialog: DialogInterface, which: Int) {
                val values = new ContentValues()
                values.put("label", labelInput.getText.toString)
                values.put(
                  "frequency",
                  Long.box(findView(TR.frequency).getText.toString.MHz.Hz))
                values.put("pl_tone", findView(TR.pl_tone).getSelectedItem.toString.toDouble)
                values.put("mode", findView(TR.mode).getSelectedItem.toString)

                val db = new MemoryEntryHelper(MainActivity.this).getWritableDatabase
                db.insert("memory_entries", null, values)
                Toast.makeText(
                  MainActivity.this,
                  getString(R.string.memory_saved),
                  Toast.LENGTH_SHORT).show()
              }
            }).show()
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
