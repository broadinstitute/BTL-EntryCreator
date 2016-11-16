package org.broadinstitute.entrycreator

/**
  * Created by amr on 11/14/2016.
  */
case class Entry(version: Long)

object Entry extends EntryJson[Entry]{
  val tManifest = implicitly[Manifest[Entry]]
}
case class EntryWithId(id: String, version: Option[Long])

object EntryWithId extends EntryJson[EntryWithId]{
  val tManifest = implicitly[Manifest[EntryWithId]]
  def apply(id: String, e: Entry): EntryWithId = {
    EntryWithId(id, Some(e.version))
  }
}