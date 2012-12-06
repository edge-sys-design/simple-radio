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

import _root_.android.app.{Activity, AlertDialog}
import _root_.android.content.{Context, DialogInterface, Intent}
import _root_.android.net.{ConnectivityManager, Uri}
import _root_.android.os.{AsyncTask, Bundle}
import _root_.android.text.{Editable, Html, TextWatcher}
import _root_.android.view.{Menu, MenuItem, View}
import _root_.android.view.View.OnFocusChangeListener
import _root_.android.widget.{ArrayAdapter, Toast, Spinner}

import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.client.methods.HttpGet

import scala.concurrent.future
import scala.concurrent.ExecutionContext.Implicits.global

class MainActivity extends Activity with TypedActivity {

  override def onCreate(bundle: Bundle) {
    super.onCreate(bundle)
    setContentView(R.layout.receive)
    findView(TR.frequency).setText("145.170")

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

    if (getString(R.string.development_build) == "true") {
      // If we have a network connection, go ahead and check for an update.
      val connMgr = getSystemService(Context.CONNECTIVITY_SERVICE).asInstanceOf[ConnectivityManager]
      val networkInfo = connMgr.getActiveNetworkInfo()

      if (networkInfo != null && networkInfo.isConnected) {
        future {
          val client = new DefaultHttpClient()
          val httpGet =
            new HttpGet(s"${getString(R.string.updates_url)}/commit-info.txt")

          val result: Either[Throwable, String] = try {
            val response = client.execute(httpGet)
            val is = response.getEntity().getContent()
            Right(scala.io.Source.fromInputStream(is).mkString.trim)
          }
          catch {
            case e: Throwable => Left(e)
          }

          runOnUiThread {
            result.fold(
              left =>
                Toast.makeText(
                  this,
                  getString(R.string.updates_error),
                  Toast.LENGTH_SHORT).show(),
              right => {
                val commitSplit = right.split(" ", 2)
                val commitHash = commitSplit.head
                if (commitHash != getString(R.string.version)) {
                  val message = Html.fromHtml(
                    getString(R.string.new_update_available_prompt).format(
                      commitSplit.tail.mkString))
                  val builder = new AlertDialog.Builder(this)
                  builder
                    .setMessage(message)
                    .setTitle(R.string.new_update_available)
                    .setPositiveButton(
                      R.string.yes,
                      new DialogInterface.OnClickListener() {
                        def onClick(dialog: DialogInterface, id: Int) {
                          val latestAPK =
                            s"${getString(R.string.updates_url)}/simpleradio-$commitHash.apk"
                          val intent = new Intent(Intent.ACTION_VIEW, Uri.parse(latestAPK))
                          startActivity(intent)
                        }
                      })
                    .setNegativeButton(
                      R.string.no,
                      new DialogInterface.OnClickListener() {
                        def onClick(dialog: DialogInterface, id: Int) {
                        }
                      }).create().show()
                }
              }
            )
          }
        }
      }
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
    item getItemId match {
      case R.id.create_new => {
        Toast.makeText(
          this,
          "NYAN NYAN NYAN NYAN NYAN-NYAN NYAN NYAN",
          Toast.LENGTH_SHORT).show()
      }
      case R.id.about => {
        val intent = new Intent(this, classOf[AboutActivity])
        startActivity(intent)
      }
      case _ =>
    }
    true
  }
}
