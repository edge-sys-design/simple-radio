/** Copyright (C) 2013 Edge System Design, LLC.  All rights reserved.
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

class AboutActivity extends SActivity {
  onCreate {
    contentView {
      new SVerticalLayout {
        SImageView().imageResource(R.drawable.launcher_icon)
        STextView(R.string.version) marginBottom 20.dip
        STextView(R.string.copyright) marginBottom 20.dip
        STextView(R.string.disclaimer) marginBottom 20.dip
        STextView(R.string.get_involved)
      }.padding(16.dip)
    }
  }
}
