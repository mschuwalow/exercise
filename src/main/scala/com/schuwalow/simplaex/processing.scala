package com.schuwalow.simplaex

import zio._
import zio.stream._
import cats.implicits._
import cats.data.NonEmptyList
import java.nio.file.Path
import better.files.File
import com.schuwalow.simplaex.services.logger

object processing {

  def summarize[E](windowSize: Int): Stream[E, Row] => Stream[E, Summary] =
    stream =>
      stream
        .buffer(windowSize)
        .mapAccum[List[Row], Option[Summary]](Nil) {
          case (state, row) =>
            val nextState = row :: state
            if (nextState.size == windowSize)
              (Nil, Some(createSummary(nextState.reverse)))
            else
              (nextState, None)
        }
        .collect {
          case Some(value) => value
        }

  def writeToPath[A: Encoder](path: Path): TaskR[logger.Logger, A => Task[Unit]] =
    for {
      root    <- ZIO.succeed(File(path))
      _       <- ZIO.effect(root.createDirectoryIfNotExists(true))
      counter <- Ref.make(0)
      env     <- ZIO.environment[logger.Logger]
      writer = (a: A) =>
        (for {
          encoded <- ZIO.succeed(Encoder[A].encode(a))
          id      <- counter.modify(current => (current, current + 1))
          target  = root / show"${id}.txt"
          _       <- logger.info(show"Writing summary to file ${target.pathAsString}")
          _       <- ZIO.effect(target.write(encoded.mkString("\n")))
        } yield ()).provide(env)
    } yield writer

  private[this] def createSummary(rows: List[Row]): Summary = {
    // calculate everything in one pass
    val stats = rows.foldLeft(SummaryState.empty) {
      case (state, next) =>
        SummaryState(
          state.sum + next.value3,
          state.valuesPerUser + (next.user -> state.valuesPerUser
            .get(next.user)
            .map(next :: _)
            .getOrElse(NonEmptyList.one(next)))
        )
    }
    Summary(
      stats.sum,
      stats.valuesPerUser.keys.size,
      stats.valuesPerUser.mapValues(rows => (math.mean(rows.map(_.value1)), rows.head.value2))
    )
  }

  final private[this] case class SummaryState(
    sum: Long,
    valuesPerUser: Map[UserId, NonEmptyList[Row]]
  )

  private[this] object SummaryState {

    def empty: SummaryState =
      SummaryState(0L, Map())
  }

}
