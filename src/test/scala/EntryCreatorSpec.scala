import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.client.RequestBuilding._
import org.scalatest.{FlatSpec, Matchers}
import org.broadinstitute.entrycreator.EntryCreator.createSampleEntry
import scala.concurrent.duration._
import scala.concurrent.Await
import akka.http.scaladsl.model.StatusCodes._
import akka.stream.ActorMaterializer
import scala.language.postfixOps

/**
  * Created by amr on 10/20/2016.
  */
class EntryCreatorSpec extends FlatSpec with Matchers{
  private implicit lazy val system = ActorSystem()
  private implicit lazy val materializer = ActorMaterializer()
  private implicit lazy val ec = system.dispatcher
  // This pathPrefix for remote test on MDBeta
  //private val pathPrefix = "http://btllims.broadinstitute.org:9101/MD"
  // This pathPrefix for localhost testing.
  private val pathPrefix = "http://gp3c5-33b.broadinstitute.org:9100/MD"
  private val set_id_1 = "EntryCreatorSpec_1"
  private val set_id_2 = "EntryCreatorSpec_2"
  "EntryCreator" should "create an entry with default long version" in {
    val response = createSampleEntry(set_id_1, None, pathPrefix)
    val result = Await.result(response, 5 seconds)
    val version_string = result.toString.substring(result.toString.indexOf('{') + 1, result.toString.indexOf('}'))
    val version = version_string.substring(11)
    version.length should be >= 13
    result.status shouldBe Created
    val delPath = s"$pathPrefix/delete/metrics?id=$set_id_1&version=$version"
    val request = Http().singleRequest(Post(uri = delPath))
    val del_result = Await.result(request, 5 seconds)
    del_result.status shouldBe OK
  }
  it should "create an entry with specified version" in {
    val response = createSampleEntry(set_id_2, Some(1L), pathPrefix)
    val result = Await.result(response, 5 seconds)
    val version_string = result.toString.substring(result.toString.indexOf('{') + 1, result.toString.indexOf('}'))
    val version = version_string.substring(11)
    version should be ("1")
    result.status shouldBe Created
    val delPath = s"$pathPrefix/delete/metrics?id=$set_id_2&version=$version"
    val request = Http().singleRequest(Post(uri = delPath))
    val del_result = Await.result(request, 5 seconds)
    del_result.status shouldBe OK
  }

}
