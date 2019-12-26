package lessons.advanced.chapter1

import scala.util.Try

object DarkSugars extends App {

  // 1. Methods with Single Params
  def singleArgMethod(x: Int): String = s"$x little ducks"

  val description = singleArgMethod {
    // write some complex code
    10
  }

  val aTryInstance = Try {
    throw new RuntimeException
  }

  List(1, 2, 3).map {
    x =>
      x + 1
  }

  // 2. Single Abstract method
  trait Action {
    def act(x: Int): Int
  }

  val anInstance: Action = new Action {
    override def act(x: Int): Int = x + 1
  }

  val funkyInstance: Action = x => x + 1

  val aThread = new Thread(new Runnable {
    override def run(): Unit = print("Run")
  })

  val aSweeterThread = new Thread(() => print("Run"))

  abstract class AnAbstractType {

    def implemented: Int = 23
    def f(a: Int): Int
  }

  val anAbstractInstance : AnAbstractType = (a) => a * 10

  // 3. :: and #:: methods are special

  val prependList = 2 :: List(3, 4)
  // 2.::(List(3, 4)) -- Not Actually
  // List(3, 4).::(2) -- Actual

  // Why? Scala Spec - Associativity of method gets decided by last char of method

  class MyStream[Int] {

    def -->:(x: Int): MyStream[Int] = new MyStream[Int]
  }

  val myStream = 1 -->: 2 -->: new MyStream();

  // 4. multi-word method naming

  class TeenGirl(name: String){
    def `and then said`(gossip: String): Unit = print(s"$name and $gossip")
  }

  val lilly = new TeenGirl("Lilly")
  lilly `and then said` "hello"

  // 5.
  class Composite[A, B]{
  }

  val compositeInstance : Composite[Int, String] = ???

  val fancyComposite: Int Composite String = ???

  class -->[A, B]{
  }

  val towards: Int --> String = ???

  // 6. update() is very special muck like apply()
  val anArray = Array(1, 2, 3)
  anArray(2) = 7 // anArray.update(2, 7) -- rewritten by compiler
  // used in mutable collections
  // apply() and update()

  // 7. Setters for mutable containers
  class MutableInt {
    private var internalMember: Int = 0
    def member: Int = internalMember // getter
    def member_=(value: Int): Unit =
      internalMember = value // setter
  }

  val aMutableContainer : MutableInt = new MutableInt
  aMutableContainer.member = 42  // aMutableContainer.member_=(42)
}
