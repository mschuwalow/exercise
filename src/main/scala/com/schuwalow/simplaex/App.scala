package com.schuwalow.simplaex

import zio.ZIO
import com.schuwalow.simplaex.services.logger
import zio.blocking.Blocking
import cats.implicits._
import java.nio.file.Paths
import zio.stream.Stream
import java.nio.file.Path
import pureconfig.error.ConfigReaderFailures

object App extends zio.App {

  final case class ConfigLoadingFailure(causes: ConfigReaderFailures)
      extends Exception(show"Loading config failed: ${causes.toList.mkString(", ")}")

  override def run(args: List[String]): ZIO[Environment, Nothing, Int] =
    (for {
      cfg <- loadConfig
      _   <- mkEnv >>> app(cfg.port, Paths.get(cfg.outputDir), cfg.windowSize)
    } yield ()).foldM(
      e => ZIO.effectTotal(println(show"App exited because of ${e.printStackTrace()}")).const(1),
      _ => ZIO.succeed(0)
    )

  def app(port: Int, rootPath: Path, windowSize: Int) =
    for {
      _         <- logger.info("Starting server")
      write     <- processing.writeToPath[Summary](rootPath)
      processor = (stream: Stream[Throwable, Row]) => processing.summarize(windowSize)(stream).tap(write).runDrain
      _         <- server.serve(port, processor)
    } yield ()

  def mkEnv: ZIO[Environment, Nothing, AppR] = {
    val loggerM = logger.Slf4jLogger.create(shorten = true)
    ZIO.succeed {
      new loggerM.Module with Blocking.Live {}
    }
  }

  val loadConfig = ZIO.fromEither(pureconfig.loadConfig[AppConfig]).mapError(ConfigLoadingFailure(_))

}
