package org.broadinstitute.entrycreator

/**
  * Created by amr on 10/20/2016.
  */
case class Config (entryId: String = "", var version: Option[Long] = None, out: String = "", test: Boolean = false)
