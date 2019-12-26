package lessons.advanced.chapter4

object OrganizingImplicits extends App {

  println(List(1,4,5,2).sorted)

  implicit def reverseOrdering: Ordering[Int] = Ordering.fromLessThan(_ > _)
  // implicit def reverseOrdering: Ordering[Int] = Ordering.fromLessThan(_ > _) // Won't be considered
  // scala.Predef



  case class Person(name: String, age: Int)

  val persons = List(
    Person("Gourav", 27),
    Person("Unique", 29),
    Person("Tushar", 28)
  )

  object AgeOrdering {
    implicit def ageOrdering: Ordering[Person] = Ordering.fromLessThan((x, y) => x.age < y.age)
  }
  object NameOrdering {
    implicit def nameOrdering: Ordering[Person] = Ordering.fromLessThan((x, y) => x.name.compareTo(y.name) < 0)
  }
  import AgeOrdering._
  println(persons.sorted)

  /*
  Implicit Scopes
    - Local
    - Imported
    - Companions of all types involved
      - Lis, Ordering, Person or any Super Types

   */

  /*

  1. Single Possible value?
    - Companion Object
  2. More than 1 possible values?
    - Primary implicit in Companion
    - Other ones in Local
  3. More than 1 with same importance?
    - Group all implicit conversions in Separate Objects
   */

  /*
  Exercises
    1. Add 3 Orderings
      - Total Price - Mostly used
      - By Unit Count -
      - By Unit Price -
   */

  case class Purchase(units: Int, unitPrice: Double)

  object Purchase {

    implicit def totalPriceOrdering: Ordering[Purchase] = Ordering.fromLessThan( (x, y) => (x.unitPrice * x.units) < (y.unitPrice * y.units))
  }

  object UnitCountOrdering {

    implicit def unitCountOrdering: Ordering[Purchase] = Ordering.fromLessThan( (x, y) => x.units < y.units)
  }

  object UnitPriceOrdering {

    implicit def unitPriceOrdering: Ordering[Purchase] = Ordering.fromLessThan( (x, y) => x.unitPrice < y.unitPrice)
  }

}
