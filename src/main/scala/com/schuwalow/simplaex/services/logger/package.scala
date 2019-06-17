package com.schuwalow.simplaex.services

import zio.ZIO
import cats.Show

package object logger extends Logger.Service[Logger] {
  final def trace[A: Show](a: A)(implicit line: sourcecode.Line, file: sourcecode.File): ZIO[Logger, Nothing, Unit] =
    ZIO.accessM(_.logger.trace(a))
  final def debug[A: Show](a: A)(implicit line: sourcecode.Line, file: sourcecode.File): ZIO[Logger, Nothing, Unit] =
    ZIO.accessM(_.logger.debug(a))
  final def info[A: Show](a: A)(implicit line: sourcecode.Line, file: sourcecode.File): ZIO[Logger, Nothing, Unit] =
    ZIO.accessM(_.logger.info(a))
  final def warn[A: Show](a: A)(implicit line: sourcecode.Line, file: sourcecode.File): ZIO[Logger, Nothing, Unit] =
    ZIO.accessM(_.logger.warn(a))
  final def error[A: Show](a: A)(implicit line: sourcecode.Line, file: sourcecode.File): ZIO[Logger, Nothing, Unit] =
    ZIO.accessM(_.logger.error(a))
}
