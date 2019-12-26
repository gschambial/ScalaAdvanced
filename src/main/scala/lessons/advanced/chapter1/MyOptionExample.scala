package lessons.advanced.chapter1

object MyOptionExample extends App {

  abstract class MyOption[+A] {

    def isEmpty: Boolean

    def map[B](f: A => B): MyOption[B]

    def flatMap[B](f: A => MyOption[B]): MyOption[B]
  }

  case class MySome[+A](value: A) extends MyOption[A] {

    override def map[B](f: A => B): MyOption[B] = MySome(f(value))

    override def flatMap[B](f: A => MyOption[B]): MyOption[B] =
      if (isEmpty) MyNone else f(value)

    override def isEmpty: Boolean = this eq MyNone
  }

  object MyNone extends MyOption[Nothing] {

    override def map[B](f: Nothing => B): MyOption[B] = MyNone

    override def flatMap[B](f: Nothing => MyOption[B]): MyOption[B] = MyNone

    override def isEmpty: Boolean = this eq MyNone
  }

  val someValue : MyOption[Int] = MySome(10)

  val doubleSome = someValue.map( (x: Int) => x * 2)

  println(doubleSome)

}
