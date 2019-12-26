package lessons.advanced.chapter3

object ThreadCommunication extends App {

  /*
  Producer-Consumer Problem
   */
  class Container {
    var value = 0

    def isEmpty = value == 0

    def get = {
      val result = value
      value = 0
      result
    }

    def set(newValue: Int) = value = newValue
  }

  val container = new Container
  val producer = new Thread(() => {
    while (true) {
      container.synchronized {
        if (container.isEmpty) {
          println(s"Producing: 1")
          container.set(1)
          container.notify()
        }
        container.wait()
      }
    }
  })
  producer.start()

  val consumer = new Thread(() => {
    while (true) {
      container.synchronized {
        if (!container.isEmpty) {
          println(s"Consuming: ${container.get}")
          container.notify()
        }
        container.wait()
      }
    }
  })
  consumer.start()

  producer.join()
  consumer.join()

  println("finished")
}
