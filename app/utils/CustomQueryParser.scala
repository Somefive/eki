package utils

import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.document.IntPoint
import org.apache.lucene.index.Term
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.{Query, TermQuery, TermRangeQuery}
import org.apache.lucene.util.BytesRefBuilder
import org.apache.lucene.util.NumericUtils

class CustomQueryParser(val f: String, val a: Analyzer) extends QueryParser(f, a) {

  lazy val INTFIELD_NAME = "year"

  override protected def newRangeQuery(field: String, part1: String, part2: String, startInclusive: Boolean, endInclusive: Boolean): Query = {
    if (INTFIELD_NAME.equals(field)) return IntPoint.newRangeQuery(field, part1.toInt, part2.toInt)
    super.newRangeQuery(field, part1, part2, startInclusive, endInclusive).asInstanceOf[TermRangeQuery]
  }

  override protected def newTermQuery(term: Term): Query = {
    if (INTFIELD_NAME.equals(term.field)) {
      val refBuilder = new BytesRefBuilder()
      NumericUtils.intToSortableBytes(term.text().toInt, refBuilder.bytes(), 0)
      new TermQuery(new Term(term.field, refBuilder))
    } else
      super.newTermQuery(term)
  }
}