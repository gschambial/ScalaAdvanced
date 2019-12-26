package lessons.advanced.chapter2

import scala.annotation.tailrec

object Streams extends App {

  /*
 Exercise: Implement a lazily evaluated single linked stream of elements
  */

  abstract class MyStream[+A] {

    def isEmpty: Boolean
    def head: A
    def tail: MyStream[A]
    def #::[B >: A](element: B): MyStream[B] // prepend
    def ++[B >: A](anotherStream: => MyStream[B]): MyStream[B] // concatenate

    def foreach(f: A => Unit): Unit
    def map[B >: A](f: A => B): MyStream[B]
    def flatMap[B >: A](f: A => MyStream[B]): MyStream[B]
    def filter(predicate: A => Boolean): MyStream[A]

    def take(n: Int): MyStream[A] // takes first n elements of stream
    def takeAsList(n: Int): List[A]

    @tailrec
    final def toList[B >: A](acc: List[B] = Nil): List[B] = {
      if(isEmpty) acc
      else tail.toList(head :: acc)
    }
  }

  object MyStream{
    def from[A](start: A)(generator: A => A): MyStream[A] = {
      new Cons(start, MyStream.from(generator(start))(generator))
      // new Cons(0, MyStream.from(1)(generator))
      // new Cons(0, 1, MyStream.from(2)(generator))
      // // new Cons(0, 1, ........ MyStream.from(10)(generator))
    }

    def fib[Int](first: Int, second: Int)(generator: (Int, Int) => Int): MyStream[Int] = {

      new Cons(first, MyStream.fib(second, generator(first, second))(generator))
      // new Cons(0, MyStream.from(1, 1))
      // new Cons(0, new Cons(1, MyStream.from(1, 2)))
      // new Cons(0, new Cons(1, new Cons(1, MyStream.from(2, 3))))
    }


  }

  /*
  naturals = MyStream.from(1)(x => x + 1) == stream of naturals (potentially infinite)
  naturals.take(100).foreach(println) lazily evaluated stream of first 100 naturals
  naturals.foreach(println) will crash as it is infinite
  naturals.map( _ * 2) // stream of evens potentially infinite
   */

  class Cons[A](hd: A, tl: => MyStream[A]) extends MyStream[A] {

    override def isEmpty: Boolean = false

    override val head: A = hd

    override lazy val tail: MyStream[A] = tl

      /*
      val s = new Cons(1, Empty)
      val prepended = 1 #:: s = new Cons(1, s)
       */

    override def #::[B >: A](element: B): MyStream[B] = new Cons(element, this)

    override def ++[B >: A](anotherStream: => MyStream[B]): MyStream[B] = new Cons(head, tail ++ anotherStream)

    override def foreach(f: A => Unit): Unit = {
      f(head)
      tail.foreach(f)
    }

    // Classics
    override def map[B >: A](f: A => B): MyStream[B] = new Cons(f(head), tail.map(f))

    override def flatMap[B >: A](f: A => MyStream[B]): MyStream[B] = f(head) ++ tail.flatMap(f)

    override def filter(predicate: A => Boolean): MyStream[A] =
      if(predicate(head))
        new Cons(head, tail.filter(predicate))
      else tail.filter(predicate)

    override def take(n: Int): MyStream[A] = {
      if(n <= 0) Empty
      else if(n == 1) new Cons(head, Empty)
      else new Cons(head, tail.take(n-1))
    }

    override def takeAsList(n: Int): List[A] = take(n).toList()
  }

  object Empty extends MyStream[Nothing] {

    override def isEmpty: Boolean = true

    override def head: Nothing = throw new NoSuchElementException

    override def tail: MyStream[Nothing] = throw new NoSuchElementException

    override def #::[B >: Nothing](element: B): MyStream[B] = new Cons[B](element, this)

    override def ++[B >: Nothing](anotherStream: => MyStream[B]): MyStream[B] = anotherStream

    override def foreach(f: Nothing => Unit): Unit = ()

    override def map[B >: Nothing](f: Nothing => B): MyStream[B] = this

    override def flatMap[B >: Nothing](f: Nothing => MyStream[B]): MyStream[B] = this

    override def filter(predicate: Nothing => Boolean): MyStream[Nothing] = this

    override def take(n: Int): MyStream[Nothing] = this

    override def takeAsList(n: Int): List[Nothing] = Nil // List[Nothing]
  }

  val naturals = MyStream.from(1)( x => x + 1)
  println(naturals.head)
  println(naturals.tail.head)
  println(naturals.tail.tail.head)

  val startFrom0 = 0 #:: naturals
  println(startFrom0.head)

  startFrom0.take(1000).foreach(println)

  startFrom0.map( x => x * 2).take(100).foreach(println)

  println(startFrom0.flatMap( x => new Cons(x, new Cons(x + 1, Empty))).take(10).toList())

  println(startFrom0.filter( _ < 10).take(10).toList())

  /*
  Exercises
    1. Stream of fibonacci numbers
    2. Stream of prime numbers with Eratosthnes' sieve
      [2, 3, 4 ......]
      filter out all numbers divisible by 2
      filter out all numbers divisible by 3
      ................................... 5
   */

   /*
   0, 1,
    */

  val f = MyStream.fib[Int](0, 1)( (x, y) => x + y)
  val first10 = f.take(10).foreach(println)

  def prime(numbers: MyStream[Int]): MyStream[Int] = {
    if(numbers.isEmpty) numbers
    else
      new Cons(numbers.head, prime(numbers.tail.filter( _ % numbers.head != 0)))
    // new Cons(2, prime(new Cons(3, MyStream.from(4)).filter( _ % 2 != 0) )
    // new Cons(2, new Cons(3, prime(5,
    //                                  MyStream.from(4)).filter( _ % 2 != 0).filter( _ % 3 != 0))
    //                                                                                              )))

    // take(10)

    /*

    new Cons(2, )

     */
  }

  println("primes......")
  val allNaturals = MyStream.from(2)( _ + 1)
  prime(allNaturals).take(10).foreach(println)


}
