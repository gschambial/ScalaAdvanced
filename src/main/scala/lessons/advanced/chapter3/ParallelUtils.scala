package lessons.advanced.chapter3

import java.util.concurrent.ForkJoinPool
import java.util.concurrent.atomic.AtomicReference

import scala.collection.parallel.immutable.ParVector
import scala.collection.parallel.CollectionConverters._
import scala.collection.parallel.{ForkJoinTaskSupport, Task, TaskSupport}

object ParallelUtils extends App {

  // Parallel Collections
  val parallelList = List(1, 2, 3).par

  val aParVector = ParVector[Int](1, 2, 3)

  /*
  Seq
  Vector
  Map - Hash, Tree
  Set - Hash, Tree
   */

  def measure[T](operation: => T): Long = {
  val start = System.currentTimeMillis()
    operation
    System.currentTimeMillis() - start
  }


  val smallList = (1 to 10000000).toList
  val serialTime = measure {
    smallList.map( _ + 1)
  }

  val parallelTime = measure {
    smallList.par.map( _ + 1)
  }

  println(s"Serial Time: $serialTime")
  println(s"Parallel Time: $parallelTime")

  /*
  Parallel Collection works Map-Reduce Model
    - split the elements into chunks - splitter
    - operation
    - reduce - combiner
   */

  // map, flatMap, filter, foreach, reduce, fold

  // fold, reduce with non-associative operators
  println(List(1, 2, 3).reduce(_ - _))
  println(List(1, 2, 3).par.reduce(_ - _))

  var sum = 0
  List(1, 2, 3).par.foreach( sum += _)
  println(sum) // race conditions

  // Configuring Parallel Collection
  aParVector.tasksupport = new ForkJoinTaskSupport(new ForkJoinPool(2))

  /*
  alternative
    - ThreadPoolTaskSupport
    - ExecutionContextTaskSupport
   */

  // Atomic Operation and References
  val atomic = new AtomicReference[Int]()
  val currentValue = atomic.get() // Thread-safe read
  atomic.set(1) // thread safe write
  atomic.getAndSet(5) // thread safe read and write
  atomic.compareAndSet(42, 45)
  // if value is 42 then set value to 45
  atomic.updateAndGet((x) => x + 1)
  atomic.getAndUpdate((x) => x + 1)
  atomic.accumulateAndGet(12, _ + _)
  atomic.getAndAccumulate(10, _ + _)
}
