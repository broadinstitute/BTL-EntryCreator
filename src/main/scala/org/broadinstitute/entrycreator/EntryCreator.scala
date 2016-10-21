package org.broadinstitute.entrycreator
import java.io.PrintWriter

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.client.RequestBuilding._
import akka.http.scaladsl.model.ContentTypes._
import akka.http.scaladsl.model.{HttpEntity, HttpResponse, StatusCodes}
import akka.stream.ActorMaterializer
import com.typesafe.scalalogging.Logger

import scala.annotation.tailrec
import scala.concurrent.Future
import scala.util.{Failure, Success}
/**
  * Created by amr on 10/20/2016.
  */
object EntryCreator extends App {
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val ec = system.dispatcher
  val logger = Logger("EntryCreator")

  def parser = {
    new scopt.OptionParser[Config]("EntryCreator") {
      head("EntryCreator", "1.0")
      opt[String]('I', "sampleId").valueName("<id>").required().action((x, c) => c.copy(sampleId = x))
        .text("The ID of the sample to create an entry in MD for.")
      opt[Long]('V', "version").valueName("<version>").optional().action((x, c) => c.copy(version = x))
        .text("Optional version string for the entry.")
      opt[Boolean]('T', "test").valueName("<test>").hidden().optional().action((x, c) => c.copy(test = x))
        .text("Optional. Set to true for testing.")
      help("help").text("Prints this help text.")
      note("\n A tool for creating blank MD entries.")
    }
  }

  parser.parse(args, Config()
  ) match {
    case Some(config) => execute(config)
    case None => failureExit("Please provide valid input.")
  }

  def failureExit(msg: String) {
    logger.error(s"EntryCreator failed: $msg")
    System.exit(1)
  }

  def execute(config: Config) = {
    val entry = createSampleEntry(config.sampleId, config.version, config.test)
    entry onComplete {
      case Success(s) =>
        s.status match {
          case a @ (StatusCodes.Created | StatusCodes.OK) => logger.info("Creation successful: " + s.status)
            val id = config.sampleId
            val version = entry.toString//.substring(entry.toString.indexOf('{') + 1, entry.toString.indexOf('}'))
            println(version)
            val json = s"""{\"id\": \"$id\", $version}"""
            val pw = new PrintWriter(config.sampleId + ".EntryCreator.json")
            pw.write(json)
            pw.close()
            logger.info(s"Version assigned: $version")
            System.exit(0)
          case _ =>
            val failMsg = s"Creation failed: " + s.status
            failureExit(failMsg)
        }
      case Failure(f) => failureExit(s"Creation failed: $f")
    }
  }

  def createSampleEntry(id: String, version: Long, test: Boolean): Future[HttpResponse] = {
    def createJson: String = {
      if (version == -999) {
        s"""{\"id\": \"$id\"}"""
      } else {
        s"""{\"id\": \"$id\", \"version\": $version}"""
      }
    }
    val json = createJson
    logger.info(s"JSON created: $json")
    var port = 9100
    if (test) port = 9101
    val path = s"http://btllims.broadinstitute.org:$port/MD/find/metrics"
    Http().singleRequest(
      Post(uri = path, entity = HttpEntity(contentType = `application/json`, string = json))
    )
  }
  def extractSubstringByKeys(str: String, delim: String, keys: List[Char]): List[String] = {
    val stringList = str.split(delim).toIterator
    def populateList(mList: scala.collection.mutable.ListBuffer[String]): List[String] = {
      @tailrec
      def matchAccumulator(mList: scala.collection.mutable.ListBuffer[String]): List[String] = {
        def isMatch(s: String): Boolean = {
          val result = s.forall(keys.contains(_))
          result
        }
        if (stringList.hasNext) {
          val current = stringList.next()
          if (isMatch(current)) {
            mList += current
          }
        }
        matchAccumulator(mList)
      }
      matchAccumulator(mList)
    }
    populateList(scala.collection.mutable.ListBuffer[String]())
  }
}