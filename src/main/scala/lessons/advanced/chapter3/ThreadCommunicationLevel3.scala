package lessons.advanced.chapter3

import scala.collection.mutable
import scala.util.Random

object ThreadCommunicationLevel3 extends App {

  /*
  Producer-Consumer Problem
   */


  val container = new mutable.Queue[Int]
  var i = 0
  val r1 = new Random()
  val r2 = new Random()

  val producer = new Thread(() => {
    while (true) {
      container.synchronized {
        while(container.size == 3) {
          println("Full.....................")
          container.wait()
        }
        println(s"Producer 1: ${i+1}")
        container.enqueue(i+1)
        i += 1
        container.notify()
        Thread.sleep(r1.nextInt(500))
      }
    }
  })

  val producer2 = new Thread(() => {
    while (true) {
      container.synchronized {
        while(container.size == 3) {
          println("Full.....................")
          container.wait()
        }
        println(s"Producer 2: ${i+1}")
        container.enqueue(i+1)
        i += 1
        container.notify()
        Thread.sleep(r1.nextInt(500))
      }
    }
  })

  val consumer = new Thread(() => {
    while (true) {
      container.synchronized {
        if(container.isEmpty) {
          println("Empty.................")
          container.wait()
        }
        println(s"Consumer 1: ${container.dequeue()}")
        container.notify()
        Thread.sleep(r2.nextInt(800))
      }
    }
  })

  producer.start()
  producer2.start()
  consumer.start()

  producer.join()
  consumer.join()

  println("finished")
}
