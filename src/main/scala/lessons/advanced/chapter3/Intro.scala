package lessons.advanced.chapter3

import java.util.concurrent.Executors

import jdk.nashorn.internal.runtime.regexp.joni.ast.BackRefNode

object Intro extends App {

  // JVM Threads
  val aThread = new Thread(() => println("Thread running..."))
  aThread.start();

  val helloThread = new Thread(new Runnable {
    override def run(): Unit = (1 to 5).foreach(_ => println("Hello"))
  })
  val goodbyeThread = new Thread(new Runnable {
    override def run(): Unit = (1 to 5).foreach(_ => println("Goodbye"))
  })
  helloThread.start()
  goodbyeThread.start()

  // Executors
  val pool = Executors.newFixedThreadPool(10)
  pool.execute( () => {
    // Thread.sleep(1000)
    println("Done after 1 second")
  })
  pool.execute( () => {
    // Thread.sleep(1000)
    println("Done after 1 second")
  })
  pool.execute( () => {
    // Thread.sleep(2000)
    println("Almost Done after 2 second")
  })
  // pool.shutdown()
  // pool.execute( () => println("Not possible after shutdown to submit any action"))

  pool.shutdownNow() // Any sleeping Threads would throw exception

  def runInParaller = {
    var x = 0
    val thread1 = new Thread( () => {
      x = 1
    })
    val thread2 = new Thread( () => {
      x = 2
    })
    thread1.start()
    thread2.start()
    println(s"x: $x")
  }

  (1 to 5).foreach( _ => runInParaller)

  // Race Condition - 2 Threads trying to set the same value

  class BankAccount(var amount: Int) {

    override def toString: String = "" + amount
  }

  def buy(account: BankAccount, thing: String, price: Int) : Unit = {
    account.amount -= price
    println(s"I bought: $thing, My Balance $account")
  }

  // OPTION 1 - synchronized
  def buySafe(account: BankAccount, thing: String, price: Int) : Unit = {
    account.synchronized {
      account.amount -= price
      println(s"I bought: $thing, My Balance $account")
    }
  }

  for {
    i <- (1 to 10)
  } yield {
    val account = new BankAccount(1000)
    val thread1 = new Thread( () => buy(account, "Shoe", 100))
    val thread2 = new Thread( () => buy(account, "Bag", 300))
    thread1.start()
    thread2.start()
    Thread.sleep(10)
    if(account.amount != 600) println(s"AHA: $account")
  }

  // OPTION 2 -- @volatile

  /**

   Exercise:

   1. Construct 50 "inception" threads
    - println("Hello from thread #")
      - in reverse order
   */

  def createThread(max: Int, n: Int = 1): Thread = {
    if(n < max){
      val t = createThread(max, n + 1)
      t.start()
      t.join()
    }
    new Thread( () => println(s"Hi from $n"))
  }

  createThread(10)

  var x = 0;
  var mappingThreads = (1 to 100).map( _ => new Thread( () => x += 1))
  mappingThreads.foreach(_.start())
  mappingThreads.foreach(_.join())
  println(x)

  /*
  Biggest value of x? 100
  Smallest value of x? 1
   */

  var message = ""
  var awesomeThread = new Thread( () => {
    Thread.sleep(1000)
    message = "Awesome"}
  )
  message = "Sucks"
  awesomeThread.start()
  Thread.sleep(1000)
  awesomeThread.join()
  println(s"message: $message")

  /*
  message ?
    - Awesome - if awesomeThread gets execution
    - Sucks - if some other thread gets control
   */


}
