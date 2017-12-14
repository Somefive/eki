package engines

import java.io.File

import models.Publication
import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.index.IndexWriterConfig.OpenMode
import org.apache.lucene.index.{DirectoryReader, IndexWriter, IndexWriterConfig}
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.store.FSDirectory
import org.wltea.analyzer.lucene.IKAnalyzer
import utils.console.ColorForConsole._
import utils.console.Loggable
import utils.i18n.StringHelper._

import scala.language.implicitConversions

object LuceneEngine extends Loggable {
  lazy val DEFAULT_NAME = "IKAnalyzer"
  val analyzers: Map[String, Analyzer] = Map("IKAnalyzer" -> new IKAnalyzer(), "StandardAnalyzer" -> new StandardAnalyzer())
  def getAnalyzer(name: String = DEFAULT_NAME): Analyzer = if (analyzers.contains(name)) analyzers(name) else analyzers(DEFAULT_NAME)

  lazy val searchers: Map[String, IndexSearcher] = analyzers.map(p => p._1 -> new IndexSearcher(DirectoryReader.open(getDir(p._1))))
  def getSearcher(name: String = DEFAULT_NAME): IndexSearcher = if (searchers.contains(name)) searchers(name) else searchers(DEFAULT_NAME)

  def getDir(name: String = DEFAULT_NAME): FSDirectory = FSDirectory.open(new File(s"./index_$name").toPath)

  def indexing(analyzerName: String): Unit = {
    val analyzer = getAnalyzer(analyzerName)
    logger.info(s"Indexing with analyzer $analyzerName ".withColor(LIGHT_PURPLE) + s"${CorpusEngine.publications.length} Publications".withColor(LIGHT_GREEN))
    val config = new IndexWriterConfig(analyzer)
    config.setOpenMode(OpenMode.CREATE)
    val writer = new IndexWriter(getDir(analyzerName), config)
    var counter = 0
    CorpusEngine.publications.foreach(pub => {
      writer.addDocument(pub.toDocument)
      counter += 1
      if (counter % 2000 == 0) logger.info("Indexed ".withColor(PURPLE) + s"$counter Publications".withColor(GREEN))
    })
    writer.close()
    logger.info("Indexed".withColor(LIGHT_GREEN))
  }

  val BATCH_SIZE = 1000

  def doc(docId: Int, searcherName: String): Option[Publication] = {
    val d = getSearcher(searcherName).doc(docId)
    if (d != null)
      Some(Publication(d))
    else
      None
  }

  def searchByField(field: String, value: String, searcherName: String = DEFAULT_NAME): Array[Publication] = {
    val parser = new QueryParser(field, searcherName)
    val query = parser.parse(value)
    getSearcher(searcherName).search(query, BATCH_SIZE).scoreDocs.flatMap(s => doc(s.doc, searcherName))
  }

  lazy val GENERAL_SEARCH_FIELDS = List("title_zh", "title_en", "authors_zh", "authors_en", "filename", "org_zh", "org_en", "keywords_zh", "keywords_en", "topics")

  def mkQs(q: String, fields: List[String] = GENERAL_SEARCH_FIELDS, strict: Boolean = false): String = fields
    .map { field => field + ':' + '"' + q + '"' }
    .mkString(if (strict) " AND " else " OR " )

  def generalSearch(qs: String, skip: Int = 0, limit: Int = 20, analyzer: String = DEFAULT_NAME, strict: Boolean = true): (Long, Array[Publication]) = {
    val queryString =
      if (strict) mkQs(qs)
      else qs.splitAndNonEmpty("\\s").map(subQs => "(" + mkQs(subQs) + ")").mkString(" AND ")
    search(queryString, skip, limit, analyzer)
  }

  def search(qs: String, skip: Int = 0, limit: Int = 20, analyzer: String = DEFAULT_NAME): (Long, Array[Publication]) = {
    val parser = new QueryParser("title_zh", analyzer)
    val query = parser.parse(qs)
    val result = getSearcher(analyzer).search(query, BATCH_SIZE)
    (result.totalHits, result.scoreDocs.slice(skip, skip + limit).flatMap(s => doc(s.doc, analyzer)))
  }

  implicit def apply(analyzerName: String): Analyzer = getAnalyzer(analyzerName)
}
