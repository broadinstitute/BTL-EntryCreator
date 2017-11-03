package org.broadinstitute.entrycreator
import java.io.PrintWriter
import scala.language.postfixOps
import scala.concurrent.duration._
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.client.RequestBuilding._
import akka.http.scaladsl.model.ContentTypes._
import akka.http.scaladsl.model.{HttpEntity, HttpResponse, StatusCodes}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import com.typesafe.scalalogging.Logger
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success}
import org.broadinstitute.entrycreator.Entry._
import scopt.OptionParser

/**
  * Created by amr on 10/20/2016.
  */
object EntryCreator extends App {
  implicit lazy val system = ActorSystem()
  implicit lazy val materializer = ActorMaterializer()
  implicit lazy val ec = system.dispatcher
  lazy val logger = Logger("EntryCreator")

  def parser: OptionParser[Config] = {
    new scopt.OptionParser[Config]("EntryCreator") {
      head("EntryCreator", "1.0.3")
      opt[String]('i', "entryId").valueName("<id>").required().action((x, c) => c.copy(entryId = x))
        .text("The ID of the analysis to create an entry in MD for.")
      opt[Long]('v', "<version>").valueName("<number>").optional().action((x, c) => c.copy(version = Some(x)))
        .text("Optional version string for the entry.")
      opt[String]('o', "out").valueName("<path>").required().action((x,c) => c.copy(out = x))
        .text("full path, including file name, for output EntryCreator.")
      opt[String]('H', "HOST").valueName("<host url>").optional().action((x, c) => c.copy(host = x))
        .text("Optional. Specify database host. Default is http:\\\\btllims.broadinstitute.org.")
      opt[Int]('P', "PORT").valueName("<port>").optional().action((x, c) => c.copy(port = x))
        .text("Optional database host port. Default is 9100. Use 9101 for MdBeta.")
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

  def execute(config: Config): Unit = {
    val pathPrefix = s"${config.host}:${config.port}/MD"
    val response = createSampleEntry(config.entryId, config.version, pathPrefix)
    response onComplete {
      case Success(s) =>
        val entryFuture = response.flatMap( response => Unmarshal(response.entity).to[Entry])
        s.status match {
          case StatusCodes.Created => logger.info("Creation successful: " + s.status)
            val me = Await.result(entryFuture, 5 seconds)
            val e = EntryWithId(config.entryId, me)
            val json = EntryWithId.writeJson(e)
            val pw = new PrintWriter(s"${config.out}/${e.id}.EntryCreator.json")
            pw.write(json)
            pw.close()
            logger.info(s"Version assigned: ${e.version.getOrElse(0L)}")
            System.exit(0)
          case _ =>
            val failMsg = s"Unexpected response: + ${s.status}\n${s.entity.toString}"
            failureExit(failMsg)
        }
      case Failure(f) => failureExit(s"Creation failed: $f")
    }
  }

  def createSampleEntry(id: String, version: Option[Long], pathPrefix: String): Future[HttpResponse] = {
    def createJson: String = {
      val e = EntryWithId(id, version)
      EntryWithId.writeJson(e)
    }
    val json = createJson
    logger.info(s"JSON created: $json")
    val path = s"$pathPrefix/add/metrics"
    logger.info(s"Request path: $path")
    Http().singleRequest(
      Post(path, HttpEntity(contentType = `application/json`, string = json))
    )
  }
}