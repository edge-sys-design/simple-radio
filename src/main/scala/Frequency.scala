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

/** A representation of a RF frequency.
  *
  * This class represents a frequency, such as (but not limited to) those in the
  * amateur radio bands. It provides methods for working with the frequency and
  * transforming it in various ways -- however it does remain immutable.
  *
  * It handles frequencyes in the following forms:
  * "146" => 146.000.000
  * "146.52" => 146.520.000
  * "146.520" => 146.520.000
  * "146.520.000" => 146.520.000
  *
  * {{{
  * val f = new Frequency("146.520")
  * f defaultOffset
  * f + 0.300
  * // etc.
  * }}}
  */
class Frequency(val frequency: String) {
  def this(frequency: Long) = this(Frequency.toMHz(frequency))

  override def toString = frequency
  def toMHz() = Frequency.toMHz(this)
  def toHz() = Frequency.toHz(this)
}

object Frequency {

  /** Convert a frequency from Hertz to Megahertz.
    *
    * @param frequency The frequency in Kilohertz.
    * @return The frequency in Megahertz, in three segments, separated with '.'.
    */
  def toMHz(frequency: Long): String = {
    val frequencyStringBuilder = new StringBuilder
    val split = (frequency.toDouble / 1000000).toString split("\\.")
    frequencyStringBuilder.append(split.head)
    frequencyStringBuilder.append(split.last + ("0" * (6 - split.last.length)))
    frequencyStringBuilder.insert(frequencyStringBuilder.length - 3, ".")
    frequencyStringBuilder.insert(frequencyStringBuilder.length - 7, ".")
    frequencyStringBuilder toString
  }

  def toMHz(frequency: Frequency): String = toMHz(toHz(frequency.frequency))


  /** Convert a frequency from Megahertz to Hertz.
    *
    * @throws IllegalArgumentException if an invalid frequency is given.
    * @param frequency The frequency in Megahertz.
    * @return The frequency in Hertz.
    */
  def toHz(frequency: String): Long = {
    val frequencySplit: List[Long] = frequency.split("\\.").toList.map(segment =>
      segment match {
        case "" => 0
        case x => {
          if (x.size > 3) {
            throw new IllegalArgumentException(
              "Sections of the frequency should be no longer than 3 numbers.")
          }
          if (!x.forall(_.isDigit)) {
            throw new IllegalArgumentException(
              "The frequency should only contain numbers and decimals.")
          }
          x.toLong
        }
      })

    frequencySplit.size match {
      case 1 => frequencySplit.head * 1000000
      case 2 => {
        (frequencySplit.head * 1000000) +
        (frequencySplit(1) * 1000 * math.pow(
          10,
          3 - (frequencySplit(1).toString size)).toLong)
      }
      case 3 => {
        (frequencySplit.head * 1000000) +
        (frequencySplit(1) * 1000 * math.pow(
          10,
          3 - (frequencySplit(1).toString size)).toLong) +
        (frequencySplit(2) * math.pow(
          10,
          3 - (frequencySplit(2).toString size)).toLong)
      }
    }
  }

  def toHz(frequency: Frequency): Long = toHz(frequency.frequency)
}
