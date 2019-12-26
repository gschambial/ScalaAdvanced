package lessons.advanced.chapter2

object PartialFunctions extends App {

  val aFunction: Int => Int = x => x +1

  val aFunction1 = (x: Int) => x + 1

  val aFussyFunction : Int => Int = x => {
    if(x == 5)  5
    else if(x == 10) 10
    else throw new RuntimeException("Unmatched input")
  }

  val nicerFunction = (x: Int) => x match {
    case 5 => 5
    case 10 => 10
  }

  val aPartialFunction : PartialFunction[Int, Int] = {
    case 5 => 5
    case 10 => 10
  }

  // Partial Functions are actually based on Pattern Matching

  aPartialFunction.isDefinedAt(10)

  // Partial functions can be lifted to Total Function to return None

  val lifted = aPartialFunction.lift

  lifted(11) // Returns None

  // Chaining of Partial Functions

  val pfChain = aPartialFunction.orElse[Int, Int] {
    case 55 => 55
  }

  println(pfChain(55))

  // PF extend Normal Functions

  val aTotalFunction : Int => Int = {
    case 1 => 99
  }

  // HOF accepts Partial Functions as well

  val aMappedList = List(1, 2).map {
    case 1 => "One"
    case 2 => "Two"
  }

  // NOTE: PF can only have one Parameter Type

  /**
   * Exercises
   *
   * 1. Partial Function Instance
   * 2. Chatbot as a Partial Function
   *  -
   */

  // Syntactic Sugar
  val chatbot : PartialFunction[String, String] = {
    case "Name" => "Gourav"
    case "Age" => "27"
  }

  // Using Anonymous Instance Implementation
  val chatbotUnderTheHood : PartialFunction[String, String] = new PartialFunction[String, String] {

    override def apply(x: String) : String = x match {
      case "Name" => "Gourav"
      case "Age" => "Age"
    }

    override def isDefinedAt(x: String): Boolean = {
      x == "Name" || x == "Age"
    }
  }

  scala.io.Source.stdin.getLines().map(chatbot).foreach(println)
  // scala.io.Source.stdin.getLines().foreach( line => println("Answer: " + chatbot(line)))
}
