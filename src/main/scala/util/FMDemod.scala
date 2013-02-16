package com.edgesysdesign.simpleradio.util
import java.io._
import java.nio._

object FMDemod {
  val bytesPerSample = 2

  def main(args: Array[String]) {
    require(
      args.length == 2,
      "Usage: scala FMDemod inputfile outputfile")

    val input = new File(args(0))
    val output = new File(args(1))
    require(input.exists, "The input file does not exist.")
    require(output.canWrite, "The output file must be writable.")

    val inputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(input)))
    val outputStream = new FileOutputStream(output).getChannel()

    case class Sample(var samI: Short, var samQ: Short)
    val oldSample = Sample(0, 0)
    val newSample = Sample(0, 0)

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

          oldSample.samI = newSample.samI
          oldSample.samQ = newSample.samQ

          newSample.samI = sample.getShort(0)
          newSample.samQ = sample.getShort(2)

          val result = (((newSample.samI * oldSample.samQ) - (newSample.samQ * oldSample.samI)) / 65535.0).toShort

          val samples = ByteBuffer
            .allocate(2)
            .order(ByteOrder.LITTLE_ENDIAN)

          samples
            .asShortBuffer
            .put(result)

          outputStream.write(samples)
        }
    } catch {
      case e: Throwable => {
        outputStream.close()
      }
    }
  }
}
