package engines

import java.io.File

import models.Publication
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.index.{DirectoryReader, IndexWriter, IndexWriterConfig}
import org.apache.lucene.index.IndexWriterConfig.OpenMode
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.queryparser.xml.builders.BooleanQueryBuilder
import org.apache.lucene.search.{BooleanQuery, IndexSearcher, Query}
import org.apache.lucene.store.FSDirectory
import utils.console.Loggable
import org.wltea.analyzer.lucene.IKAnalyzer
import utils.console.ColorForConsole._

object LuceneEngine extends Loggable {
  val INDEX_DIR_PATH = "./index"
  val indexDir = new File(INDEX_DIR_PATH)
  val dir: FSDirectory = FSDirectory.open(indexDir.toPath)
  val analyzer = new IKAnalyzer()
//  val analyzer = new StandardAnalyzer()
  lazy val reader: DirectoryReader = DirectoryReader.open(dir)
  lazy val searcher = new IndexSearcher(reader)

  def indexing(): Unit = {
    logger.info("Indexing... ".withColor(LIGHT_PURPLE) + s"${CorpusEngine.publications.length} Publications".withColor(LIGHT_GREEN))
    val config = new IndexWriterConfig(analyzer)
    config.setOpenMode(OpenMode.CREATE)
    val writer = new IndexWriter(dir, config)
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

  def doc(docId: Int): Option[Publication] = {
    val d = searcher.doc(docId)
    if (d != null)
      Some(Publication(d))
    else
      None
  }

  def searchByField(field: String, value: String): Array[Publication] = {
    val parser = new QueryParser(field, analyzer)
    val query = parser.parse(value)
    searcher.search(query, BATCH_SIZE).scoreDocs.flatMap(s => doc(s.doc))
  }

  lazy val GENERAL_SEARCH_FIELDS = List("title_zh", "title_en", "authors_zh", "authors_en", "filename", "org_zh", "org_en", "keywords_zh", "keywords_en")
  def generalSearch(qs: String, skip: Int = 0, limit: Int = 20): Array[Publication] = {
    val fields: List[String] = GENERAL_SEARCH_FIELDS.map { field => field + ':' + '"' + qs + '"' }
    val queryString: String = fields.mkString(" OR ")
    val parser = new QueryParser("title_zh", analyzer)
    val query = parser.parse(queryString)
    searcher.search(query, BATCH_SIZE).scoreDocs.slice(skip, skip + limit).flatMap(s => doc(s.doc))
  }
}
