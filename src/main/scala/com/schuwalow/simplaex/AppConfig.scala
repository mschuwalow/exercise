package com.schuwalow.simplaex

import pureconfig.ConfigConvert
import pureconfig.generic.semiauto._

final case class AppConfig(
  outputDir: String,
  port: Int = 1000,
  windowSize: Int = 5
)

object AppConfig {
  implicit val convert: ConfigConvert[AppConfig] = deriveConvert
}
