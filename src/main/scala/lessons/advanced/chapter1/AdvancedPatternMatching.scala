package lessons.advanced.chapter1

object AdvancedPatternMatching extends App {

  val numbers = List(1)
  val description = numbers match {
    case head :: Nil => print(s"element $head")
    case _ =>
  }

  /*
  constants
  wildcards
  case classes
  tuples
  some special magic like list
   */

  class Person(val name: String, val age: Int)

  object Person {

    def unapply(arg: Person): Option[(String, Int)] = Some((arg.name, arg.age))

    def unapply(age: Int): Option[String] =
      Some(if(age < 21) "minor" else "major")
  }

  val bob = new Person("Bob", 20)
  val greeting = bob match {
    case Person(n, a) => s"Name: $n , age: $a"
  }
  print(greeting)

  /*
  Create Companion Object of Class with unapply method returning Some Value
   */

  // Exercise
  /*
  Match against
   */

  object SingleDigit {

    def unapply(num: Int): Option[Boolean] =
      if(num < 10) Some(true)
      else None
  }

  object SingleDigitAdvanced {

    def unapply(num: Int): Boolean = num < 10
  }

  object Even {

    def unapply(num: Int): Option[Boolean] =
      if(num % 2 == 0) Some(true)
      else None
  }

  object EvenAdvanced {

    def unapply(num: Int): Boolean = num % 2 == 0
  }

  val aNumber: Int = 12
  val mathProperty = aNumber match {
    case SingleDigit(yes) => s"$aNumber is $yes"
    case Even(yes) => s"$aNumber is $yes"
    case _ => "Just Generic"
  }
  print(s"Math Property: $mathProperty")

  val mathPropertyAdvanced = aNumber match {
    case SingleDigitAdvanced() => s"$aNumber "
    case EvenAdvanced() => s"$aNumber "
    case _ => "Just Generic"
  }
  print(s"Math Property: $mathProperty")

  // Infix Pattern Matching
  case class Or[A, B](a: A, b: B) {
  }

  val either = Or[Int, String](10, "Mary")
  val humanDesc = either match {
    case age Or name => s"$name and $age"
  }

  // Decomposing Sequences
  val vararg = numbers match {
    case List(1, _*) => "Starting with 1"
  }

  abstract class MyClass[+A]{
    def head: A = ???
    def tail: MyClass[A] = ???
  }
  case object Empty extends MyClass[Nothing]
  case class Cons[+A](override val head: A, override val tail: MyClass[A] ) extends MyClass[A]{
  }

  object MyClass {
    def unapplySeq[A](list: MyClass[A]): Option[Seq[A]] =
      if (list == Empty) Some(Seq.empty)
      else unapplySeq(list.tail).map( list.head +: _)
  }

  val mList : MyClass[Int] = Cons(1, Cons(2, Empty))
  val decomposed = mList match {
    case MyClass(1, 2, _*) => "Yes"
    case _ => "something"
  }

  // Custom return types for Pattern Matching
  // Any return type can be used for pattern matcher given they provide following 2 methods
  // isEmpty: Boolean, get: Something


}
