package com.schuwalow.simplaex

class EncoderSpec extends UnitSpec {

  it("should work for the provided examples - 1") {
    val encoder = Encoder[Summary]
    val expected =
      """30212888860247708058
        |3
        |0977dca4-9906-3171-bcec-87ec0df9d745,0.6794981485066369,1851028776
        |5fac6dc8-ea26-3762-8575-f279fe5e5f51,0.7626710614484215,1005421520
        |4d968baa-fe56-3ba0-b142-be9f457c9ff4,0.6532229483547558,237475359""".stripMargin
    encoder
      .encode(
        Summary(
          BigInt("30212888860247708058"),
          3,
          Map(
            UserId("0977dca4-9906-3171-bcec-87ec0df9d745") -> ((0.6794981485066369, 1851028776)),
            UserId("5fac6dc8-ea26-3762-8575-f279fe5e5f51") -> ((0.7626710614484215, 1005421520)),
            UserId("4d968baa-fe56-3ba0-b142-be9f457c9ff4") -> ((0.6532229483547558, 237475359))
          )
        )
      )
      .mkString("\n") === expected
  }

  it("should work for the provided examples - 2") {
    val encoder = Encoder[Summary]
    val expected =
      """25493180386520262311
        |2
        |0977dca4-9906-3171-bcec-87ec0df9d745,0.50374610727888,280709214
        |023316ec-c4a6-3e88-a2f3-1ad398172ada,0.3196604691859787,1579431460""".stripMargin
    encoder
      .encode(
        Summary(
          BigInt("25493180386520262311"),
          2,
          Map(
            UserId("0977dca4-9906-3171-bcec-87ec0df9d745") -> ((0.50374610727888, 280709214)),
            UserId("023316ec-c4a6-3e88-a2f3-1ad398172ada") -> ((0.3196604691859787, 1579431460))
          )
        )
      )
      .mkString("\n") === expected
  }

}
