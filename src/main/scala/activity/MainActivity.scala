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
import _root_.android.text.{Editable, TextWatcher}
import _root_.android.view.{Menu, MenuItem, View}
import _root_.android.view.View.OnFocusChangeListener
import _root_.android.widget.{ArrayAdapter, Toast, Spinner}

import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.client.methods.HttpGet

// These are throwing an exception. It would be good to file a bug upstream
// but I'm going to wait until Scala 2.10.0-RC3 comes out to see if it's fixed.
// import scala.concurrent.future
// import scala.concurrent.ExecutionContext.Implicits.global
// Instead, we can rely on the old, deprecated version.
import scala.concurrent.ops._

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

    findView(TR.offset).setText("-600 KHz")

    if (getString(R.string.development_build) == "true") {
      // If we have a network connection, go ahead and check for an update.
      val connMgr = getSystemService(Context.CONNECTIVITY_SERVICE).asInstanceOf[ConnectivityManager]
      val networkInfo = connMgr.getActiveNetworkInfo()

      if (networkInfo != null && networkInfo.isConnected) {
        spawn {
          val client = new DefaultHttpClient()
          val httpGet =
            new HttpGet(s"${getString(R.string.updates_url)}/commit-sha.txt")

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
                if (right != getString(R.string.version)) {
                  val builder = new AlertDialog.Builder(this)
                  builder
                    .setMessage(R.string.new_update_available_prompt)
                    .setTitle(R.string.new_update_available)
                    .setPositiveButton(
                      R.string.yes,
                      new DialogInterface.OnClickListener() {
                        def onClick(dialog: DialogInterface, id: Int) {
                          val latestAPK =
                            s"${getString(R.string.updates_url)}/simpleradio-$right.apk"
                          val intent = new Intent(Intent.ACTION_VIEW, Uri.parse(latestAPK))
                          startActivity(intent)
                        }
                      })
                    .setNeutralButton(
                      R.string.downgrade_to_stable,
                      new DialogInterface.OnClickListener() {
                        def onClick(dialog: DialogInterface, id: Int) {
                          Toast.makeText(
                            MainActivity.this,
                            "NYAN.",
                            Toast.LENGTH_SHORT).show()
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
