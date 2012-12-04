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

// TODO: This will probably be made a separate library.
package com.edgesysdesign.simpleradio
import language.implicitConversions

package object FrequencyImplicits {

  class FrequencyImplicit(frequency: Double) {
    def this(frequency: Long) = this(frequency.toDouble)
    def this(frequency: Int) = this(frequency.toDouble)
    def this(frequency: String) = this(Frequency.toHz(frequency) / 1000000.0)
    def KHz() = new Frequency((frequency * 1000).toLong)
    def MHz() = new Frequency((frequency * 1000000).toLong)
    def Hz() = new Frequency(frequency.toLong)
  }

  implicit def doubleToFrequency(frequency: Double) = new FrequencyImplicit(frequency)
  implicit def intToFrequency(frequency: Int) = new FrequencyImplicit(frequency)
  implicit def LongToFrequency(frequency: Long) = new FrequencyImplicit(frequency)
  implicit def StringToFrequency(frequency: String) = new FrequencyImplicit(frequency)

}
