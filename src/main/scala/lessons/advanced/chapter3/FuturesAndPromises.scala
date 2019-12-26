package lessons.advanced.chapter3

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Success, Try, Failure}

object FuturesAndPromises extends App {

  def calculateMeaningOfLife: Int = {
    Thread.sleep(1000)
    42
  }

  val aFuture = Future {
    calculateMeaningOfLife
  }

  println(aFuture.value) // Option[Try[Int]]
  println("Waiting for future to complete...")
  aFuture.onComplete {
    case Success(meaningOfLife) => println(s"Meaning of life: $meaningOfLife")
    case Failure(e) => s"I have failed $e"
  }
  Thread.sleep(2000)
}