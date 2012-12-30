import java.io._
import java.nio._

object IQEncoder {
  val sine = for (i <- 1 to 1024) yield 65536 * math.sin(i * (math.Pi / 1024)) toInt
  val cosine = for (i <- 1 to 1024) yield 65536 * math.cos(i * (math.Pi / 1024)) toInt
  val sampleRate = 16E3
  val bytesPerSample = 2
  val fCarrier = 750
  val move = (2 * sine.length * fCarrier) / sampleRate toInt

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
        .continually(inputStream.readShort())
        .foreach { s =>
          val sample = ByteBuffer
            .allocate(4)
            .order(ByteOrder.BIG_ENDIAN)
            .putShort(s)
            .order(ByteOrder.LITTLE_ENDIAN)
            .getShort(0)
          val samI = (sample * sign * (sine(position) / 65535.0)).toShort
          val samQ = (sample * sign * (cosine(position) / 65535.0)).toShort

          val samples = ByteBuffer
            .allocate(4)
            .order(ByteOrder.LITTLE_ENDIAN)

          samples
            .asShortBuffer
            .put(samI)
            .put(samQ)

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
