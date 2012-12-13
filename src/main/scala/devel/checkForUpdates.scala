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

package com.edgesysdesign.simpleradio.devel

import _root_.android.app.{Activity, AlertDialog}
import _root_.android.content.{Context, DialogInterface, Intent}
import _root_.android.net.{ConnectivityManager, Uri}
import _root_.android.text.Html
import _root_.android.widget.Toast

import com.edgesysdesign.simpleradio.Implicits._
import com.edgesysdesign.simpleradio.R

import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.client.methods.HttpGet

import scala.concurrent.future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.io.Source

object Devel {
  def checkForUpdates(context: Context) {
    // If we have a network connection, go ahead and check for an update.
    val connMgr = context.getSystemService(
      Context.CONNECTIVITY_SERVICE).asInstanceOf[ConnectivityManager]
    val networkInfo = connMgr.getActiveNetworkInfo()

    if (networkInfo != null && networkInfo.isConnected) {
      future {
        val client = new DefaultHttpClient()
        val httpGet =
          new HttpGet(s"${context.getString(R.string.updates_url)}/commit-info.txt")

        val result: Either[Throwable, String] = try {
          val response = client.execute(httpGet)
          val is = response.getEntity().getContent()
          Right(Source.fromInputStream(is).mkString.trim)
        }
        catch {
          case e: Throwable => Left(e)
        }

        context.asInstanceOf[Activity].runOnUiThread {
          result.fold(
            left =>
              Toast.makeText(
                context,
                context.getString(R.string.updates_error),
                Toast.LENGTH_SHORT).show(),
            right => {
              val commitSplit = right.split(" ", 2)
              val commitHash = commitSplit.head
              if (commitHash != context.getString(R.string.version)) {
                val message = Html.fromHtml(
                  context.getString(R.string.new_update_available_prompt).format(
                    commitSplit.tail.mkString))
                val builder = new AlertDialog.Builder(context)
                builder
                .setMessage(message)
                .setTitle(R.string.new_update_available)
                .setPositiveButton(
                  R.string.yes,
                  new DialogInterface.OnClickListener() {
                    def onClick(dialog: DialogInterface, id: Int) {
                      val latestAPK =
                        context.getString(R.string.updates_url) +
                        s"/simpleradio-$commitHash.apk"
                      val intent = new Intent(Intent.ACTION_VIEW, Uri.parse(latestAPK))
                      context.startActivity(intent)
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
