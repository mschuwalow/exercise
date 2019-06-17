package com.schuwalow.simplaex.services.logger

import zio.ZIO
import cats.Show

trait Logger extends Serializable {
  def logger: Logger.Service[Any]
}

object Logger {

  trait Service[R] extends Serializable {
    def trace[A: Show](a: A)(implicit line: sourcecode.Line, file: sourcecode.File): ZIO[R, Nothing, Unit]
    def debug[A: Show](a: A)(implicit line: sourcecode.Line, file: sourcecode.File): ZIO[R, Nothing, Unit]
    def info[A: Show](a: A)(implicit line: sourcecode.Line, file: sourcecode.File): ZIO[R, Nothing, Unit]
    def warn[A: Show](a: A)(implicit line: sourcecode.Line, file: sourcecode.File): ZIO[R, Nothing, Unit]
    def error[A: Show](a: A)(implicit line: sourcecode.Line, file: sourcecode.File): ZIO[R, Nothing, Unit]
  }

}
