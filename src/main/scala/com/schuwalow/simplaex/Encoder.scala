package com.schuwalow.simplaex

trait Encoder[A] {

  def encode(a: A): List[String]

}

object Encoder {

  def apply[A](implicit ev: Encoder[A]) = ev

  def instance[A](f: A => List[String]): Encoder[A] =
    new Encoder[A] {
      def encode(a: A): List[String] = f(a)
    }
}
