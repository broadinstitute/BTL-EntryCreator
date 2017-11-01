package org.broadinstitute.entrycreator

/**
  * Created by amr on 10/20/2016.
  */
case class Config (
                    entryId: String = "",
                    version: Option[Long] = None,
                    out: String = "",
                    host: String = "http://btllims.broadinstitute.org",
                    port: Int = 9100
                  )
