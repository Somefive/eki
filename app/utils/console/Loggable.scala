package utils.console

import java.util.logging.Logger

trait Loggable {
  lazy val logger: Logger = Logger.getGlobal
}
