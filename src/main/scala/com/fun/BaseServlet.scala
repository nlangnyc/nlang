package com.fun

import com.fun.finished.{WikiGet, Memoize}
import org.scalatra._
import scalate.ScalateSupport

import scala.concurrent.ExecutionContext

class BaseServlet(implicit val executor: ExecutionContext = scala.concurrent.ExecutionContext.global)
    extends FunStack with FutureSupport {

  val stored = new Memoize(WikiGet)

  get("/") {
    <html>
      <body>
        <h1>Hello, world!</h1>
        Say <a href="hello-scalate">hello to Scalate</a>.
      </body>
    </html>
  }

  get("/hello"){
    contentType = "text/hmtl"
    jade("hello-scalate")
  }

  get("/wiki/:name"){
    stored(params("name"))
  }
}
