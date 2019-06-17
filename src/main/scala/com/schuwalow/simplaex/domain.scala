package com.schuwalow.simplaex
import scala.util.Try
import cats.implicits._

final case class UserId(value: String) extends AnyVal

final case class Row(
  user: UserId,
  payload: String,
  value1: Double,
  value2: Long,
  value3: Long
)

object Row {

  implicit val decoder: Decoder[Row] =
    Decoder.instance { raw =>
      val fields = raw.split(',')
      if (fields.length != 5) {
        Left("wrong number of fields")
      } else {
        (for {
          value3  <- Try(fields(4).toLong)
          value2  <- Try(fields(3).toLong)
          value1  <- Try(fields(2).toDouble)
          payload = fields(1)
          uuid    = UserId(fields(0))
        } yield Row(uuid, payload, value1, value2, value3)).toEither
          .leftMap(e => show"Failed to decode string `$raw`: ${e.printStackTrace()}")
      }
    }
}

final case class Summary(
  sum: BigInt,
  uniqueUsers: Int,
  userStats: Map[UserId, (Double, Long)]
)

object Summary {
  implicit val encoder: Encoder[Summary] =
    Encoder.instance { summary =>
      List(
        summary.sum.toString(),
        summary.uniqueUsers.toString()
      ) ++ summary.userStats.map { case (UserId(id), (average, latest)) => show"${id},${average},${latest}" }
    }
}
