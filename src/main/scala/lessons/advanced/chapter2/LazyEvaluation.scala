package lessons.advanced.chapter2

object LazyEvaluation extends App {

  // lazy delays evaluation of values
  lazy val x: Int = {
    println("Hello...")
    42
  }
  println(x) // gets evaluated and thus prints hello and returns 42
  println(x) // no evaluation second time and thus returns previously evaluated value of 42

  // Examples of implications

  // 1. Side-effects
  def sideEffectCondition: Boolean = {
    println("Boo")
    true
  }

  def simpleCondition: Boolean = false

  lazy val lazyCondition = sideEffectCondition

  println(if(simpleCondition && lazyCondition) "yes" else "no") // as first condition is false second condition doesn't get evaluated and thus no side effects are printed

  // in conjunction with call by name
  // n is evaluated every time to return an Int Value
  def byName(n: => Int): Int = n + n + n + 1

  // call by need
  def byNameWithLazyVal(n: => Int): Int = {
    lazy val t = n
    t + t + t + 1
  }

  def retrieveMagicValue = {
    println("Waiting")
    Thread.sleep(1000)
    42
  }

  println(byName(retrieveMagicValue))
  println(byNameWithLazyVal(retrieveMagicValue))

  // Filtering with lazy val
  def lessThan30(n: Int): Boolean = {
    println(s"$n is less")
    n < 30
  }

  def moreThan20(n: Int): Boolean = {
    println(s"$n is more")
    n > 20
  }

  val numbers = List(1, 25, 40, 5, 23)
  val lt30 = numbers.filter(lessThan30)
  val gt20 = lt30.filter(moreThan20)

  println(gt20)

  val lt20lazy = numbers.withFilter(lessThan30)
  val gt20lazy = lt20lazy.withFilter(moreThan20)
  gt20lazy.foreach(println)

  // for-comprehensions use withFilter with Guards
  for{
    a <- List(1, 2, 3) if a % 2 == 0
  } yield a + 1
  List(1, 2, 3).withFilter( _ %2 == 0).map( _ + 1)

}
