package lessons.advanced.chapter2

object CurriesAndPAF extends App {

  // Curried Functions
  val supperAdder : Int => Int => Int = x => y => x + y

  val adder2 = supperAdder(2)

  println(adder2(10))

  // Partial Applied Functions
  def curriedAdder(x: Int)(y: Int)(z: Int) : Int = x + y + z
  val add4: Int => Int => Int =  curriedAdder(4)

  // Transforming method to functions is called Lifting (ETA-EXPANSION)

  // functions != Methods
  def inc(x: Int) = x + 1

  List(1, 2, 3).map(inc) // compiler changes it to List(1, 2, 3).map( x => x + 1)

  // Controlling ETA-EXPANSION

  val add5 = curriedAdder(5) _ // Do ETA-Expansion for me by applying first parameter and then return the function

  // Exercise
  val simpleAdderFunction: (Int, Int) => Int = (x, y) => x + y

  def simpleAdderMethod(x: Int, y:Int): Int = x + y

  def simpleCurriedAdder(x: Int)(y: Int) = x + y

  /*
  add7: Int => Int = y => 7 + y
  */

  val add7A = (x: Int) => simpleAdderFunction(x, 7)
  val add7B = simpleAdderFunction.curried(7)
  val add7C = simpleCurriedAdder(7) _ // PAF
  val add7D = simpleCurriedAdder(7)(_) // PAF
  val add7E = simpleAdderMethod(7, _: Int) // y => simpleAdderMethod(y, 7)
  val add7F = simpleAdderFunction(7, _:Int)  // _ forces compiler to convert simple method/function to Function Values by doing ETA Expansion

  // Underscores are powerful
  def concatenator(a: String, b: String, c: String) : String = a + b + c // x => concatenator("Hello", x, "Singh")
  val insertName = concatenator("Hello", _: String, "Singh")
  println(insertName("Gourav"))

  /*
  Exercises
  1. Process List of numbers and return their string representation
  Use %4.2f, %8.6g and %14.12f with a curried formatter
   */

  def curriedFormatter(formatter: String)(number: Double): String = formatter.format(number)
  val numbers = List(Math.PI, Math.E, 1, 9.8, 1.3e-12)

  val simpleFormat = curriedFormatter("%4.2f") _
  val seriousFormat = curriedFormatter("%8.6f") _
  val preciseFormat : Double => String =  curriedFormatter("%14.12f")

  println(numbers.map(simpleFormat))
  println(numbers.map(seriousFormat))
  println(numbers.map(preciseFormat))

  println(numbers.map(curriedFormatter("%14.12f")))

  /*
  Differences
  1. functions vs methods
  2. parameters: by-name vs -lambda
   */

  def byName(x: => Int) = x +1
  def byFunction(f: () => Int) = f() + 1

  def method: Int = 42
  def parentMethod() : Int = 42

  /*
  calling byName and byFunction
  - int
  -
   */

  byName(42) // ok
  byName(method) // Ok
  byName(parentMethod()) // Ok
  byName(parentMethod) //Ok byName(parentMethod())
  // byName( () => 42) Not OK
  byName( (() => 42)())
  // byName(parentMethod _) Not Ok

  // byFunction(45) Not Ok
  // byFunction(method) Not Ok as compiler doesn't do ETA expansion for parenthesis less methods
  byFunction(parentMethod) // Ok
  byFunction( () => 46) // Ok
  byFunction(parentMethod _) // byFunction( () => parentMethod())
}
