package com.schuwalow.simplaex

import cats.Reducible
import cats.implicits._

object math {

  def mean[F[_]: Reducible, T](elements: F[T])(implicit num: Numeric[T]): Double = {
    // Reducible instance gurantees that we have at least one element. We therefore don't have to deal with division by zero
    val ds = elements.toNonEmptyList.map(x => num.toDouble(x))
    val result = ds.foldLeft((0.0, 0L)) {
      case ((sum, count), next) =>
        (sum + next, count + 1L)
    }
    result._1 / result._2
  }

}
