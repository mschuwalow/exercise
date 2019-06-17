package com.schuwalow.simplaex.services.logger

import org.slf4j
import cats.Show
import zio.ZIO
import org.slf4j.{ Logger => SLogger }

object Slf4jLogger {

  final class Builder private[Slf4jLogger] (
    private[this] val inner: SLogger,
    private[this] val shorten: sourcecode.File => String
  ) extends Serializable {

    trait Module extends Logger {
      override val logger = new Logger.Service[Any] {

        def trace[A](a: A)(implicit S: Show[A], line: sourcecode.Line, file: sourcecode.File): ZIO[Any, Nothing, Unit] =
          ZIO.effectTotal(inner.trace(s"${shorten(file)}:${line.value} - ${S.show(a)}"))

        def debug[A](a: A)(implicit S: Show[A], line: sourcecode.Line, file: sourcecode.File): ZIO[Any, Nothing, Unit] =
          ZIO.effectTotal(inner.debug(s"${shorten(file)}:${line.value} - ${S.show(a)}"))

        def info[A](a: A)(implicit S: Show[A], line: sourcecode.Line, file: sourcecode.File): ZIO[Any, Nothing, Unit] =
          ZIO.effectTotal(inner.info(s"${shorten(file)}:${line.value} - ${S.show(a)}"))

        def warn[A](a: A)(implicit S: Show[A], line: sourcecode.Line, file: sourcecode.File): ZIO[Any, Nothing, Unit] =
          ZIO.effectTotal(inner.warn(s"${shorten(file)}:${line.value} - ${S.show(a)}"))

        def error[A](a: A)(implicit S: Show[A], line: sourcecode.Line, file: sourcecode.File): ZIO[Any, Nothing, Unit] =
          ZIO.effectTotal(inner.error(s"${shorten(file)}:${line.value} - ${S.show(a)}"))
      }
    }
  }

  def create(shorten: Boolean = true)(implicit clazz: sourcecode.FullName): Builder = {
    val clazzName: String = clazz.value.stripSuffix(".clazz")
    val inner             = slf4j.LoggerFactory.getLogger(clazzName)
    new Builder(inner, if (shorten) short else _.value)
  }

  private[this] def short(file: sourcecode.File) =
    file.value.split("/").last
}
