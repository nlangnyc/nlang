package com.fun.finished

import dispatch._
import Defaults._
import org.json4s._
import org.json4s.jackson.JsonMethods._

import scala.concurrent.Future

case class Page(ns: Int, pageid: Int, revisions: List[Revision])
case class Revision(`*`: String, contentformat: String, contentmodel: String){
  def contents = `*`
}

object WikiGet extends (String => Future[String]){
  implicit val format = DefaultFormats

  val english = url("http://en.wikipedia.org/w/api.php")
    .GET <<? Map("action" -> "query", "format" -> "json", "prop" -> "revisions", "rvprop" -> "content")

  def pull(title: String) = Http(english <<? Map("titles" -> title) OK as.String)

  def apply(title: String) = pull(title) map { result =>
    val mapped = (parse(result) \ "query" \ "pages").extract[Map[Int,Page]]
    val text = mapped collect {
      case (_, Page(_, _, revisions)) => revisions.foldLeft("")(_ + _.contents)
    }
    text.foldLeft("")(_ + _)
  }
}
