package com.schuwalow

package object simplaex {
  import com.schuwalow.simplaex.services.logger
  import zio.blocking

  type AppR = logger.Logger with blocking.Blocking

}
