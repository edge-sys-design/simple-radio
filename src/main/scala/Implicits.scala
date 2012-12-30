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

import com.edgesysdesign.frequency.Frequency

package object Implicits {

  /** Convert a function to a Runnable for runOnUiThread.
   *
   * @param f The function to implicitly convert.
   */
  implicit def toRunnable[F](f: => F): Runnable = new Runnable() { def run() = f }

  /** Convert a Frequency to a String.
    *
    * @param frequency The frequency to convert.
    */
  implicit def freqToString(f: Frequency) = new String(f.toString)

  /** A rough implementation of Ruby's "tap" method.
    *
    * This method lets you "tap" objects which don't return themselves when you
    * call their setters.
    *
    * For example, Android's [[android.widget.EditText]] doesn't return the
    * EditText when you call setHint(). setHint instead returns nothing (void).
    *
    * This "tap" method lets you work around this by doing something like:
    *
    * {{{
    *  val et = new EditText(context).tap { obj =>
    *    obj.setHint(R.string.hint)
    *    obj.setSingleLine(true)
    *  }
    *  // et is now an EditText, but those setters have been invoked on it.
    *  }}}
    */
  implicit def anyToTap[A](that: A) = new {
    def tap(f: (A) => Unit): A = {
      f(that)
      that
    }
  }
}
