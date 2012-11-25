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

import _root_.android.app.Activity
import _root_.android.os.Bundle
import _root_.android.view.{Menu, MenuItem, View}
import _root_.android.view.View.OnFocusChangeListener
import _root_.android.content.Intent
import _root_.android.widget.Toast
import _root_.android.text.{TextWatcher, Editable}

class MainActivity extends Activity with TypedActivity {
  override def onCreate(bundle: Bundle) {
    super.onCreate(bundle)
    setContentView(R.layout.receive)
    findView(TR.frequency).setText("145.170")
    findView(TR.pl_tone).setText("123.0 Hz")
    findView(TR.offset).setText("-600 KHz")
  }

  override def onCreateOptionsMenu(menu: Menu): Boolean = {
    val inflater = getMenuInflater()
    inflater.inflate(R.menu.test_menu, menu)
    true
  }

  def onCreateMemoryClick(item: MenuItem) {
    val toast = Toast.makeText(
      this,
      "NYAN NYAN NYAN NYAN NYAN-NYAN NYAN NYAN",
      Toast.LENGTH_SHORT)
    toast.show()
  }

  /** Delegate to an 'about' activity.
    *
    * This is bad, these should be one function with a pattern match.
    * But this works for experimenting. So meh.
    *
    * @todo Fix above.
    */
  def onAboutClick(item: MenuItem) {
    val intent = new Intent(this, classOf[AboutActivity])
    startActivity(intent)
  }
}
