package engines

import java.io._

import models.Publication
import utils.console.Loggable
import utils.console.ColorForConsole._

import scala.collection.mutable.ListBuffer
import scala.util.matching.Regex

trait CorpusEngine extends Loggable {
  protected val fileReader = new FileReader("./data/corpus.txt")
  protected val bufferedReader = new BufferedReader(fileReader)

  protected val stdFormat: Regex = raw".*<(.+)>=?(.*)".r

  protected def readLine(raw: String): Option[(String, String)] = {
    raw match {
      case stdFormat(op, data) => Some((op.trim, data.trim))
      case _ => None
    }
  }

  protected def read(logInterval: Int = 0): List[Publication] = {
    val pubs: ListBuffer[Publication] = ListBuffer()
    val map = scala.collection.mutable.Map[String, String]()
    var rawLine = bufferedReader.readLine()
    var lineCount = 1
    while (rawLine != null) {
      readLine(rawLine) match {
        case Some(info) =>
          if (info._1 == "REC") {
            if (map.nonEmpty) {
              pubs.append(Publication(map.toMap))
              map.clear()
            }
          }
          else
            map += info
        case None =>
      }
      rawLine = bufferedReader.readLine()
      lineCount += 1
      if (logInterval > 0 && lineCount % logInterval == 0)
        logger.info("[CorpusEngine] ".withColor(LIGHT_CYAN)
          + "Reading Corpus ".withColor(LIGHT_BLUE) + s"$lineCount, ".withColor(LIGHT_GREEN)
          + "Generate Publications ".withColor(LIGHT_BLUE) + s"${pubs.length}".withColor(LIGHT_GREEN))
    }
    pubs.append(Publication(map.toMap))
    logger.info("[CorpusEngine] ".withColor(LIGHT_CYAN) + "Corpus Read. Total ".withColor(BLUE) + s"${pubs.length}".withColor(GREEN))
    fileReader.close()
    bufferedReader.close()
    pubs.toList
  }

  lazy val publications: List[Publication] = read(100000)
  private lazy val map: Map[String, Publication] = publications.map(pub => pub.filename -> pub).toMap
  def findPublication(filename: String): Option[Publication] = map.get(filename)
}

object CorpusEngine extends CorpusEngine {
}