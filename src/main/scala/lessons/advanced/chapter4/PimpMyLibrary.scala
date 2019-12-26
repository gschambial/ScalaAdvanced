package lessons.advanced.chapter4

object PimpMyLibrary extends App {

  // 2.isPrime

  implicit class RichInt(value:Int) {

    def isEven: Boolean = value %2 == 0

    def sqrt: Double = Math.sqrt(value)

    def times(f: () => Unit) = {

      def timesAux(i: Int): Unit = {
        if(i == 1) {
          ()
        }else {
          f()
          timesAux(i-1)
        }
      }
      timesAux(value)
    }

    def *[T](list: List[T]): List[T] = {

      def concat(n: Int): List[T] = {
        if(n <= 0)
          list
        else
          concat(n-1) ++ list
      }
      concat(value)
    }
  }

  implicit class RicherInt(value:RichInt) {

    def isOdd: Boolean = !value.isEven
  }

  new RichInt(42).isEven
  42.isEven  // new RichInt(42).isEven

  // Compiler doesn't do multiple/recursive conversions
  // 42.isOdd

  /*
  Exercises:
    Enrich the String
      1. asInt
      2. encrypt
   Enrich Int
      1. times(function)
   */

  implicit class RichString(value: String){

    def asInt: Int = value.asInt

    def encrypt: String = value.toLowerCase()
  }

  /*
  Tips
    1. keep Type Enrichment to implicit classes and Type Classes
    2. avoid implicit def as much as possible
    3. package implicits clearly, bring into scope only what you need
   */

}
