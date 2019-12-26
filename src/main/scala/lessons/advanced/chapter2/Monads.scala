package lessons.advanced.chapter2

object Monads extends App {

  trait Attempt[+A] {

    def flatMap[B >: A](f: A => Attempt[B]): Attempt[B]
  }

  object Attempt {

    def apply[A](a: => A): Attempt[A] =
      try {
      Success(a)
    }catch {
      case e: Throwable => Failure(e)
    }
  }

  case class Success[+A](value: A) extends Attempt[A] {

    override def flatMap[B >: A](f: A => Attempt[B]): Attempt[B] =
      try {
        f(value)
      }catch {
        case e: Throwable => Failure(e)
      }
  }

  case class Failure(e: Throwable) extends Attempt[Nothing]{

    override def flatMap[B >: Nothing](f: Nothing => Attempt[B]): Attempt[B] = this
  }


  /*

  Lazy Monad
    1. Implement one

    2. Monads - unit/flatMap
       Monads - unit/map/flatten
   */

  object Lazy {

    def apply[A](a: => A): Lazy[A] = new Lazy[A](a)
  }

  class Lazy[+A](value: => A){

    private lazy val internal = value

    def use = internal

    def flatMap[B >: A](f: (=> A) => Lazy[B]): Lazy[B] = f(value)
  }

  val lazyInstance = Lazy {
    println("Something")
    42
  }

  val firstLazy = Lazy {
    println("Evaluation")
    100
  }

  val use1 = firstLazy.flatMap( x => Lazy {
    10 * x
  })

  val use2 = use1.flatMap( x => Lazy {
    10 * x
  })

  println(use1.use)
  println(use2.use)

}
