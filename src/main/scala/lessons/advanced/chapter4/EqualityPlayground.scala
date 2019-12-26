package lessons.advanced.chapter4

import lessons.advanced.chapter4.TypeClasses1.User

object EqualityPlayground extends App{

  /*
Exercise
  1. Implement equal class
 */

  trait Equal[T] {

    def equal(a: T, b:T): Boolean
  }

  implicit object NameEqualizer extends  Equal[User] {

    override def equal(a: User, b: User): Boolean = a.name == b.name
  }

//  implicit object AgeEqualizer extends  Equal[User] {
//
//    override def equal(a: User, b: User): Boolean = a.age == b.age
//  }

  val john = User("John", 20, "john@gmail.com")

  implicit class EqualEnrichment[T](value: T) {
    def ===(anotherValue: T)(implicit equalizer: Equal[T]): Boolean = equalizer.equal(value, anotherValue)
    def !==(anotherValue: T)(implicit equalizer: Equal[T]): Boolean = !equalizer.equal(value, anotherValue)
  }

  val marry = User("Marry", 20, "john@gmail.com")

  println(john.===(john)(NameEqualizer))
  println(john === john) // new EqualEnrichment[User](john).===(john)(NameEqualizer)
  println(john !== marry)



}
