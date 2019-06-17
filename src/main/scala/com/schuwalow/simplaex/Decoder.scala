package com.schuwalow.simplaex

trait Decoder[A] {
  import Decoder._

  def decode(a: String): Either[DecodingFailure, A]

}

object Decoder {

  type DecodingFailure = String

  def apply[A](implicit ev: Decoder[A]) = ev

  def instance[A](f: String => Either[DecodingFailure, A]) =
    new Decoder[A] {
      def decode(a: String) = f(a)
    }

}
