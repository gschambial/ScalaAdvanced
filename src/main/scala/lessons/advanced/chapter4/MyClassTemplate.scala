package lessons.advanced.chapter4

/*
  Type Classes
    1.
   */

// type class
trait MyClassTemplate[T] {

  // action
  def action(value: T): String
}