import org.scalatest.{FlatSpec, Matchers}
import org.broadinstitute.entrycreator.EntryCreator.createSampleEntry

/**
  * Created by amr on 10/20/2016.
  */
class EntryCreatorSpec extends FlatSpec with Matchers{
  "EntryCreator" should "create an entry with default version" in {
    createSampleEntry("EntryCreatorSpec_1", -999)
  }
  it should "create an entry with specified version" in {
    createSampleEntry("EntryCreatorSpec_2", 1)
  }
}
