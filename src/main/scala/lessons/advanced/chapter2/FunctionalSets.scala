package lessons.advanced.chapter2

import scala.annotation.tailrec

object FunctionalSets extends App {

  // Sets are Functions

  var mySet = MySet(1, 2, 3)
  mySet.foreach(println)
  mySet + 5 ++ MySet(-1, -2) filter( _ % 2 == 0) flatMap ( x => MySet(x  , x * 10)) foreach println
  (mySet - 2) foreach println

  val negative = !mySet
  println(negative(2))
  println(negative(5))

  val negativeEven = negative.filter( _ % 2 == 0)
  println(negativeEven(5))

  val negativeEven5 = negativeEven + 5

  var anotherSet = Set(4, 5)
}

trait MySet[A] extends (A => Boolean) {

  def apply(elem: A): Boolean = contains(elem)

  def contains(elem: A) : Boolean
  def +(elem: A) : MySet[A]
  def ++(anotherSet: MySet[A]): MySet[A]

  def map[B](f: A => B) : MySet[B]
  def flatMap[B](f: A => MySet[B]) : MySet[B]
  def filter(f: A => Boolean): MySet[A]
  def foreach(f: A => Unit): Unit

  def -(elem: A): MySet[A]
  def --(anotherSet: MySet[A]): MySet[A] // difference
  def &(anotherSet: MySet[A]): MySet[A] // intersection

  def unary_! : MySet[A]
}

object MySet {

  def apply[A](values: A*): MySet[A] = {

    @tailrec
    def buildSet(valSeq: Seq[A], acc: MySet[A]): MySet[A] = {
      if(valSeq.isEmpty) acc
      else buildSet(valSeq.tail, acc + valSeq.head)
    }
    buildSet(values, Empty())
  }
}

case class Elem[A](val head: A, val tail: MySet[A]) extends MySet[A]{

  override def contains(elem: A): Boolean = elem == head || tail.contains(elem)

  override def +(elem: A): MySet[A] = {
    if(this contains elem) this
    else Elem(elem, this)
  }

  override def ++(anotherSet: MySet[A]): MySet[A] = {
    tail ++ anotherSet + head
  }

  override def map[B](f: A => B): MySet[B] = tail.map(f) + f(head)

  override def flatMap[B](f: A => MySet[B]): MySet[B] = tail.flatMap(f) ++ f(head)

  override def filter(f: A => Boolean): MySet[A] = {
    val filterTail = tail filter f
    if(f(head)) filterTail + head
    else filterTail
  }

  override def foreach(f: A => Unit): Unit = {
    f(head)
    tail foreach f
  }

  override def -(elem: A): MySet[A] =
    if(head == elem) tail
    else tail - elem + head

  override def --(anotherSet: MySet[A]): MySet[A] = filter(x => !anotherSet(x))

  override def &(anotherSet: MySet[A]): MySet[A] = filter(anotherSet)

  // new Operator
  override def unary_! : MySet[A] = new PropertyBasedSet[A]( x => !this.contains(x))
}

case class Empty[A]() extends MySet[A]{

  override def contains(elem: A): Boolean = false

  override def +(elem: A): MySet[A] = Elem(elem, this)

  override def ++(anotherSet: MySet[A]): MySet[A] = anotherSet

  override def map[B](f: A => B): MySet[B] = Empty[B]()

  override def flatMap[B](f: A => MySet[B]): MySet[B] = Empty[B]()

  override def filter(f: A => Boolean): MySet[A] = this

  override def foreach(f: A => Unit): Unit = ()

  override def -(elem: A): MySet[A] = Empty()

  override def --(anotherSet: MySet[A]): MySet[A] = Empty()

  override def &(anotherSet: MySet[A]): MySet[A] = Empty()

  override def unary_! : MySet[A] = new PropertyBasedSet[A]( _ => true)

}

// All elements of type A which satisfy a property
// {x in A | property(x) }
class PropertyBasedSet[A](property: A => Boolean) extends MySet[A]{
  override def contains(elem: A): Boolean = property(elem)

  // { x in A | property(x) } + element = {x in A | property(x) || x == element}
  override def +(elem: A): MySet[A] =
    new PropertyBasedSet[A]( x => property(x) || x == elem)

  override def ++(anotherSet: MySet[A]): MySet[A] =
    new PropertyBasedSet[A]( x => property(x) || anotherSet(x))

  override def map[B](f: A => B): MySet[B] = politelyFail

  override def flatMap[B](f: A => MySet[B]): MySet[B] = politelyFail

  override def foreach(f: A => Unit): Unit = politelyFail

  override def filter(f: A => Boolean): MySet[A] = new PropertyBasedSet[A]( x => property(x) || f(x))

  override def -(elem: A): MySet[A] = filter(x => x != elem)

  override def --(anotherSet: MySet[A]): MySet[A] = filter(!anotherSet)

  override def &(anotherSet: MySet[A]): MySet[A] = filter(anotherSet)

  override def unary_! : MySet[A] = new PropertyBasedSet[A]( x=> !property(x))

  def politelyFail = throw new IllegalArgumentException("Really deep rabbit hole")
}

