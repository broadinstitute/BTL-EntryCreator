package org.broadinstitute.entrycreator

/**
  * Created by amr on 10/20/2016.
  */
case class Config (analysisId: String = "", var version: Option[Long] = None, out: String = "", test: Boolean = false)
