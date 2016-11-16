package org.broadinstitute.entrycreator
import akka.http.scaladsl.marshalling.Marshalling.Opaque
import akka.http.scaladsl.marshalling.{Marshaller, _}
import akka.http.scaladsl.model.MediaTypes._
import akka.http.scaladsl.unmarshalling.{Unmarshaller, _}
import org.json4s.Extraction
import org.json4s.native.JsonMethods.{parse, compact, render}
import org.json4s.DefaultFormats

/**
  * Created by amr on 11/16/2016.
  */
trait EntryJson[T] {
  private def getMarshaller(getJson: (T) => String) : ToEntityMarshaller[T] =
    Marshaller.strict((s) => Opaque(() => getJson(s)))

  /**
    * Get the json to object unmarshaller for a given type
    *
    * @param getObj callback to get object for json input
    * @return object representation of json input
    */
  private def getUnmarshaller(getObj: (String) => T) : FromEntityUnmarshaller[T] =
    Unmarshaller.byteStringUnmarshaller.forContentTypes(`application/json`).mapWithCharset { (data, charset) =>
      val jsonStr = data.decodeString(charset.nioCharset.name)
      getObj(jsonStr)
    }

  // Storage (un)marshaller
  lazy implicit val marshaller = getMarshaller(writeJson)
  lazy implicit val unmarshaller = getUnmarshaller(readJson)
  implicit val tManifest: Manifest[T]
  // Imports needed for JSON conversions
  // Define all formats needed for JSON conversion
  implicit val JSONformats = DefaultFormats.lossless
  /**
    * Create JSON compact string from object.
    * Note this could simply be:
    * Serialization.write[T](o)
    * but then it would not include the object type.
    *
    * @param o object to be converted
    * @return JSON created from object
    */
  def writeJson(o: T): String = compact(render(Extraction.decompose(o)))

  /**
    * Create object from JSON (json does not need to include object type)
    *
    * @param j JSON to convert
    * @return Object created from JSON string
    */
  def readJson(j: String): T = {
    val jAST = parse(j)
    jAST.extract[T]
  }
}
