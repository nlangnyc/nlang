package com.fun.finished

import scala.collection.mutable.{Map => MMap}
import scala.concurrent.{ExecutionContext, Future}

trait Memoization[A, B]{
  def memo(key: A, value: => Future[B])(implicit ex: ExecutionContext): Future[B] = get(key) recoverWith{
    case NotFound => set(key, value) //race condition!
  }

  def get(key: A): Future[B]

  def set(key: A, value: => Future[B]): Future[B]
}

class Memoize[A, B](f: A => Future[B])(implicit ex: ExecutionContext) extends Memoization[A, B] with (A => Future[B]){
  protected val contents = MMap.empty[A, Future[B]] //Really want LRU...

  def apply(value: A) = memo(value, f(value))

  def get(key: A): Future[B] = contents getOrElse (key, Future failed NotFound)

  def set(key: A, value: => Future[B]) ={
    val out = value
    contents(key) = out
    out
  }
}

object NotFound extends Exception