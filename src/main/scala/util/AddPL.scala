package com.edgesysdesign.simpleradio.util
import java.io._
import java.nio._

object AddPL {
  val sampleRate = 44100

  def main(args: Array[String]) {
    require(
      args.length == 2,
      "Usage: scala AddPL inputfile outputfile")

    val input = new File(args(0))
    val output = new File(args(1))
    require(input.exists, "The input file does not exist.")
    require(output.canWrite, "The output file must be writable.")

    val inputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(input)))
    val outputStream = new FileOutputStream(output).getChannel()
    var position = 1
    var sign = 1

    val plTone = 131.8
    val amplitude = 0.2

    val move = ((2 * sine.length * plTone) / sampleRate).toInt

    try {
      Iterator
        .continually(inputStream.readShort())
        .foreach { sam =>
          val sampleBuffer = ByteBuffer
            .allocate(4)
            .order(ByteOrder.BIG_ENDIAN)
            .putShort(sam)
            .order(ByteOrder.LITTLE_ENDIAN)

          val sample = sampleBuffer.getShort(0)
          val samOut = (((1 - amplitude) * sample.toFloat) + (amplitude * sign * sine(position))).toShort

          val samples = ByteBuffer
            .allocate(2)
            .order(ByteOrder.LITTLE_ENDIAN)

          samples
            .asShortBuffer
            .put(samOut)

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
