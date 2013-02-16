package com.edgesysdesign.simpleradio

package object util {
  lazy val sine = for (i <- 1 to 1024) yield (32768 * math.sin(i * (math.Pi / 1024))).toInt
  lazy val cosine = for (i <- 1 to 1024) yield (32768 * math.cos(i * (math.Pi / 1024))).toInt
}
