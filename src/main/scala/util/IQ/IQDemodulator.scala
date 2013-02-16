package com.edgesysdesign.simpleradio.util
import java.io._
import java.nio._

object IQDemodulator {
  val sampleRate = 16E3
  val bytesPerSample = 2
  val fBandwidth = 2000
  val move = ((2 * sine.length * fBandwidth) / sampleRate).toInt

  def main(args: Array[String]) {
    require(
      args.length == 2,
      "Usage: scala IQEncoder inputfile outputfile")

    val input = new File(args(0))
    val output = new File(args(1))
    require(input.exists, "The input file does not exist.")
    require(output.canWrite, "The output file must be writable.")

    val inputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(input)))
    val outputStream = new FileOutputStream(output).getChannel()
    var position = 1
    var sign = 1

    try {
      Iterator
        .continually(inputStream.readShort(), inputStream.readShort())
        .foreach { sam =>

          val sample = ByteBuffer
            .allocate(4)
            .order(ByteOrder.BIG_ENDIAN)
            .putShort(sam._1)
            .putShort(sam._2)
            .order(ByteOrder.LITTLE_ENDIAN)

          val demodSamI = (sample.getShort(0) * sign * (sine(position) / 65535.0)).toShort
          val demodSamQ = (sample.getShort(2) * sign * (cosine(position) / 65535.0)).toShort

          //println("%d - %d = %d".format(demodSamI, demodSamQ, (demodSamI - demodSamQ).toShort))

          val samples = ByteBuffer
            .allocate(2)
            .order(ByteOrder.LITTLE_ENDIAN)

          samples
            .asShortBuffer
            .put((demodSamI - demodSamQ).toShort)

          //println("%d".format(sample2.getShort(0), sample2.getShort(2), samples.getShort(0)))

          position += move

          if (position >= sine.size) {
            position = position - sine.size
            sign = -sign
          }

          outputStream.write(samples)
        }
    } catch {
      case e: Throwable => {
        outputStream.close()
      }
    }
  }
}
