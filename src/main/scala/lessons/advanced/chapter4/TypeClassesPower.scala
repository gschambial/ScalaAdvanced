package lessons.advanced.chapter4

object TypeClassesPower extends App {

  trait Animal {

    def eat: Unit
  }

  case class Dog() extends Animal {
    override def eat: Unit = println("Dog eating")
  }

  case class Cat() extends Animal {
    override def eat: Unit = println("Cat eating")
  }

  case class Wolf() extends Animal {
    override def eat: Unit = println("Wolf Eating")
  }

  // Add Growl behaviour to dog and Wolf

  // Approach 1

  trait Growl[T] {

    def growl(animal: T): Unit
  }

  object Growl {

    implicit object DogGrowl extends Growl[Dog] {
      override def growl(animal: Dog): Unit = println("Dog growling")
    }

    implicit object WolfGrowl extends Growl[Wolf] {
      override def growl(animal: Wolf): Unit = println("Wolf growling")
    }

    implicit class GrowlTemplate[T](animal: T) {
      def growl(implicit growler: Growl[T]): Unit = growler.growl(animal)
    }

    /*

    // Implicit bound to context

    implicit class GrowlTemplate[T: Growl](animal: T) {

      def growl[T] = implicitly[Growl[T]].growl(animal)
    }

    */
  }

  import Growl._

  val dog = new Dog
  DogGrowl.growl(dog)
  // OR
  dog.growl

  val wolf = new Wolf
  WolfGrowl.growl(wolf)
  // OR
  wolf.growl


  // Approach 2

  object Growl2 {

    implicit object DogGrowl extends Growl[Dog] {
      override def growl(animal: Dog): Unit = println("Dog growling")
    }

    implicit object WolfGrowl extends Growl[Wolf] {
      override def growl(animal: Wolf): Unit = println("Wolf growling")
    }

    implicit class GrowlTemplate[T](animal: T) {

      def growl(implicit growler: Growl[T]): Unit = growler.growl(animal)
    }

    def apply[T](animal: T)(implicit growler: Growl[T]): Unit = growler.growl(animal)
  }

  Growl2(new Dog)
  Growl2(new Wolf)
}
