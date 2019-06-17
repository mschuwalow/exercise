package com.schuwalow.simplaex

import zio._
import zio.blocking
import java.net.ServerSocket
import java.net.Socket
import com.schuwalow.simplaex.services.logger
import cats.implicits._
import scala.io.Source
import zio.stream._

object server {

  final case class DecodingFailure(cause: String) extends Exception(cause)

  /**
   * this will start a server on the given port and accept up to maxConcurrent concurrent connections.
   * All connections will try to decode the transmitted data and enqueue it into a shared queue that will
   * be passed to the provided stream processor.
   */
  def serve[A: Decoder](
    port: Int,
    processor: Stream[Throwable, A] => Task[Unit],
    maxConcurrent: Int = 4,
    capacity: Int = 50000
  ): ZIO[logger.Logger with blocking.Blocking, Throwable, Unit] =
    mkSocket(port).use { s =>
      Queue.bounded[A](capacity).flatMap { queue =>
        (for {
          stream <- ZIO.succeed(Stream.fromQueue(queue))
          worker = mkTcpConnection(s).use { c =>
            val source = ZIO.effect(Source.fromInputStream(c.getInputStream()).getLines())
            val decode = (raw: String) =>
              ZIO
                .fromEither(Decoder[A].decode(raw).leftMap(DecodingFailure(_)))
                .tapError(e => logger.debug(show"Decoding failed: ${e.cause}"))
            source
              .flatMap(offerIterator(_, decode, queue))
          }
          _ <- ZIO.foreach(List.fill(maxConcurrent) {
                worker
                  .mapError(_ => logger.info("Closing connection because of error"))
                  .either
                  .unit
              })(_.forever.fork.supervised)
          _ <- processor(stream)
        } yield ()).ensuring(queue.shutdown)
      }
    }

  private[this] def offerIterator[A1, A2, R](
    source: Iterator[A1],
    transform: A1 => TaskR[R, A2],
    queue: Queue[A2]
  ): TaskR[R, Unit] =
    if (source.hasNext) transform(source.next()).flatMap(queue.offer).unit *> offerIterator(source, transform, queue)
    else ZIO.unit

  private[this] def mkSocket(port: Int) =
    ZManaged.make[logger.Logger, Throwable, ServerSocket] {
      ZIO.effect(new ServerSocket(port))
    } { socket =>
      ZIO
        .effect(socket.close)
        .foldM(e => logger.warn(show"Closing socket failed with ${e.printStackTrace}").unit, _ => ZIO.unit)
    }

  /**
   * Two interesting things are happening here.
   * 1: We need to construct a ZManaged explicitly as it has two 'phases' of resource acquisition. reserve goes first and is where effects
   * used with ZManaged#make are placed. Reserve is not interruptible which is not what we want for ServerSocket#accept.
   * To fix this we can place it in the second phase of resource acquisition.
   *
   * 2: ServerSocket#accept ignores InterrputedExceptions and has to be interrupted by closing the socket.
   */
  private[this] def mkTcpConnection(socket: ServerSocket) =
    ZManaged {
      Ref.make[Option[Socket]](None).map { ref =>
        Reservation(
          ZIO.uninterruptibleMask { restore =>
            for {
              env <- ZIO.environment[logger.Logger]
              s <- restore(
                    blocking
                      .effectBlockingCancelable(socket.accept()) {
                        // interupt accept by closing the socket.
                        ZIO
                          .effect(socket.close())
                          .foldM(
                            e => logger.warn(show"Closing socket failed with ${e.printStackTrace}").provide(env).unit,
                            _ => ZIO.unit
                          )
                      }
                  )
              _ <- logger.info("Accepted connection")
              _ <- ref.set(Some(s))
            } yield s
          },
          ref.get.flatMap {
            case None => ZIO.unit
            case Some(s) =>
              ZIO
                .effect(s.close())
                .foldM(e => logger.warn(show"Closing connection failed with ${e.printStackTrace}").unit, _ => ZIO.unit)
          }
        )
      }
    }
}
