package lessons.advanced.chapter4

object ImplicitIntro extends App {

  val pair = "Gourav" -> "555"
  val intPair = 1 -> 2

  case class Person(name: String) {
    def greet: String = s"Hi my name is $name"
  }

  implicit  def fromStringToPerson(str: String): Person = Person(str)

  println("Peter".greet)

  /*

  class A {

    def greet = "some value"
  }

  implicit def stringToA(str: String): A = new A

  */

  // implicit paramters
  def increment(x: Int)(implicit amount: Int) : Int = x + amount
  implicit  val defaultAmount = 10
  increment(2)
  // Not same as Default Arguments
}
