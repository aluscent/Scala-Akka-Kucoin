package org

import spray.json.{DefaultJsonProtocol, RootJsonFormat}

trait KucoinJsonLibrary extends DefaultJsonProtocol {
  implicit def codeWrapperFormat[A](implicit wrapperFormat: RootJsonFormat[A]): RootJsonFormat[CodeWrapper[A]] = jsonFormat2(CodeWrapper[A])
  implicit def timeWrapperFormat[A](implicit wrapperFormat: RootJsonFormat[A]): RootJsonFormat[TimeWrapper[A]] = jsonFormat2(TimeWrapper[A])
  implicit val symbolFormat: RootJsonFormat[Symbol] = jsonFormat16(Symbol)
  implicit val symbolListFormat: RootJsonFormat[SymbolList] = jsonFormat2(SymbolList)
  implicit val tickerFormat: RootJsonFormat[Ticker] = jsonFormat8(Ticker)
}
