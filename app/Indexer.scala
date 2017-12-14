import engines.LuceneEngine

object Indexer {
  def main(args: Array[String]): Unit = {
    val indexers = if (args.length > 0) args else LuceneEngine.analyzers.keys.toArray
    indexers.foreach(LuceneEngine.indexing)
  }
}
