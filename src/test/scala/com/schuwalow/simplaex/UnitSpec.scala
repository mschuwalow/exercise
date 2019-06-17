package com.schuwalow.simplaex

import org.scalatest.{ FunSpec, Matchers, OptionValues }
import zio.DefaultRuntime

abstract class UnitSpec extends FunSpec with OptionValues with Matchers with DefaultRuntime
