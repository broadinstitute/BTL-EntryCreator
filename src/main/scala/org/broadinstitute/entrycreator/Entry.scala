package org.broadinstitute.entrycreator
import org.json4s.JsonAST._
import org.json4s.{CustomSerializer, DefaultFormats, Extraction}
import org.json4s.native.{JsonMethods, Serialization}

/**
  * Created by amr on 11/14/2016.
  */
case class Entry(id: String, version: Long)

object Entry {
  // Define all formats needed for JSON conversion
  implicit val JSONformats = DefaultFormats.lossless
  // Serializer to go between Json and MetricEntry - a bit tricky because AnalysisMetrics can be many subtypes
  // Note - THERE MUST BE A MetricsType FOR EACH TOP LEVEL AnalysisMetrics.  IT MUST MATCH THE INHERITED CLASS NAME.
  case object MetricEntrySerializer extends CustomSerializer[MetricEntry](format => ( {
    case JObject(JField("metricType", JString(s)) :: JField("metric", o) :: Nil) =>
      val mType = MetricsType.withName(s)
      val metric = MetricsType.extractVal(mType, o)
      MetricEntry(mType, metric)
  }, {
    case Entry(id, version) =>
      JObject(JField("id", JString(id)) ::
        JField("version", JLong(version)) :: Nil)
  }
    ))

}