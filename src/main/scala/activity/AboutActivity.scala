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

import org.scaloid.common._

import _root_.android.animation.ObjectAnimator
import _root_.android.content.{Context, Intent}
import _root_.android.net.Uri
import _root_.android.os.Bundle
import _root_.android.view.{GestureDetector, MotionEvent, View}
import _root_.android.view.GestureDetector.{SimpleOnGestureListener, OnDoubleTapListener}
import _root_.android.view.View.OnTouchListener
import _root_.android.widget.ImageView

class AboutActivity extends SActivity {
  override def onCreate(bundle: Bundle) {
    super.onCreate(bundle)
    setContentView(R.layout.about)

    val gd = new GestureDetector(this, new MyDoubleTapDetector)

    val view = find[ImageView](R.id.about_launcher_icon)
    view.setOnTouchListener(new OnTouchListener {
      override def onTouch(view: View, event: MotionEvent): Boolean = {
        gd.onTouchEvent(event)
        true
      }
    })
  }

  class MyDoubleTapDetector extends SimpleOnGestureListener with OnDoubleTapListener {
    override def onDoubleTap(event: MotionEvent): Boolean = {
      val view = find[ImageView](R.id.about_launcher_icon)
      ObjectAnimator
        .ofFloat(view, "rotation", 0f, 360f)
        .setDuration(1000)
        .start()
      true
    }
  }
}
