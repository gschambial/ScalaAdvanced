package lessons.advanced.chapter3

import scala.concurrent.{Await, Future, Promise}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Random, Success, Try}
import scala.concurrent.duration._

object FutureExercises extends App {

  /*
  1. fulfill a future immediately with a value
  2. inSequence(fa, fb)
  3. first(fa, fb) => fx  whichever future finishes first
  4. last(fa, fb) => fx  whichever future finishes last
  5. retryUntil(action: () => Future[T], condition: T => Boolean)
   */

  // 1

  def fulfillImmediately[T](value: T) = Future(value)

  // 2
  val random = new Random()
  val random2 = new Random()

  val fa = Future {
    Thread.sleep(random.nextInt(100))
    1
  }
  val fb = Future {
    Thread.sleep(random2.nextInt(200))
    2
  }

  def inSequence[A, B](fa: Future[A], fb: Future[B]): Future[B] = {
    for {
      _ <- fa
      fbResult <- fb
    } yield fbResult
  }

  def inSequence2[A, B](fa: Future[A], fb: Future[B]): Future[B] = {
    fa.flatMap(_ => fb)
  }

  val result1 = inSequence(fa, fb)
  val result2 = inSequence2(fa, fb)

  Await.result(result1, 1.seconds)
  Await.result(result2, 1.seconds)

  println(s"Result1 : $result1")
  println(s"Result2 : $result2")

  Thread.sleep(2000)

  // 3

  def first(fa: Future[Int], fb: Future[Int]): Future[Int] = {
    val promise = Promise[Int]()

    def tryComplete(result: Try[Int], promise: Promise[Int]): Any = {
      result match {
        case Success(r) => try {
          promise.success(r)
        } catch {
          case _ =>
        }
        case Failure(e) => try {
          promise.failure(e)
        } catch {
          case _ =>
        }
      }
    }

    fa.onComplete(tryComplete(_, promise))
    fb.onComplete(tryComplete(_, promise))

    /*
        fa.onComplete(_ => promise.tryComplete(_))
        fb.onComplete(_ => promise.tryComplete(_))

        OR

        fa.onComplete(promise.tryComplete)  // lifted to function value
        fb.onComplete(promise.tryComplete)
     */
    promise.future
  }

  val firstOne = first(fa, fb)
  Await.result(firstOne, 1.seconds)
  println(s"First: $firstOne")

  def last(fa: Future[Int], fb: Future[Int]): Future[Int] = {
    val promise = Promise[Int]()
    val lastPromise = Promise[Int]()

    def checkComplete(result: Try[Int]) : Any = {
      if (!promise.tryComplete(result))
        lastPromise.complete(result)
    }

    fa.onComplete(checkComplete)
    fb.onComplete(checkComplete)
    lastPromise.future
  }

  val lastOne = first(fa, fb)
  Await.result(lastOne, 1.seconds)
  println(s"Last: $lastOne")

  // 4
  def retryUntil[T](action: () => Future[T], condition: T => Boolean): Future[T] = {
    action()
      .filter(condition)
      .recoverWith {
        case _ => retryUntil(action, condition)
      }
  }

  val random3 = new Random()
  val action = () => Future {
    Thread.sleep(100)
    val nextValue = random.nextInt(100)
    println(s"nextValue; $nextValue")
    nextValue
  }

  val condition = (value: Int) => value < 50

  retryUntil(action, condition).foreach(println)

  Thread.sleep(5000)
}
