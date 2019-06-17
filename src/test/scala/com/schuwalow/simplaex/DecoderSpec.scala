package com.schuwalow.simplaex

class DecoderSpec extends UnitSpec {

  it("should behave correctly for the provided example") {
    val decoder = Decoder[Row]
    val input =
      "0977dca4-9906-3171-bcec-87ec0df9d745,kFFzW4O8gXURgP8ShsZ0gcnNT5E=,0.18715484122922377,982761284,8442009284719321817"
    decoder.decode(input) === Row(
      UserId("0977dca4-9906-3171-bcec-87ec0df9d745"),
      "kFFzW4O8gXURgP8ShsZ0gcnNT5E=",
      0.18715484122922377,
      982761284,
      8442009284719321817L
    )
  }

}
