package lessons.advanced.chapter4

object TypeClasses1 extends App {

  case class User(name: String, age: Double, email: String)
  val john = User("Jack", 20, "jack@gmail.com")

  /*
  1. Works for types we write
  2. One Implementation
   */

  // Option 2: Pattern Matching
  object HtmlSerializer {
    def serialize(value: Any): String = {
      value match {
        case User(n, a, e) => s"<div> $n, $a, $e </div>"
      }
    }
  }
  /*
    1. No type safety
    2. need to modify code everytime we add a new type
    3. Still one implementation
   */

  trait BetterHtmlSerializer[T] {
    def serialize(value: T): String
  }

  implicit object UserSerializer extends BetterHtmlSerializer[User] {

    def serialize(user: User): String = s"<div> ${user.name}, ${user.age}, ${user.email} </div>"
  }

  implicit object IntSerializer extends BetterHtmlSerializer[Int] {

    def serialize(n: Int): String = s"<div> $n </div>"
  }

  println(UserSerializer.serialize(john))

  /*
  Benefits
  1. We can define serializer for other Types
  2. Multiple implementation
   */

  // 1
  import java.util.Date
  object DateSerializer extends BetterHtmlSerializer[Date]{

    override def serialize(date: Date): String = s"<div> ${date.toString} </div>"
  }

  // 2
  object PartialUserSerializer extends BetterHtmlSerializer[User] {

    def serialize(user: User): String = s"<div> ${user.name}, ${user.age} </div>"
  }

  // Part 3
  implicit class HtmlEnrichment[T](value: T) {

    def toHtml(implicit serializer: BetterHtmlSerializer[T]): String = serializer.serialize(value)
  }

  println(john.toHtml(UserSerializer)) // println( new HtmlEnrichment[User](john).toHtml(UserSerializer) )
  println(john.toHtml)

  /*
   - extend functionality to new types
   - choose implementation
   - Super Expressive
   */

  println(2.toHtml(IntSerializer))

  /*
  - type class - HtmlSerializer
  - type class instances - UserSerializer, IntSerializer ...
  - type conversions implicit classes - HtmlEnrichment
   */

  // Context bounds

  def htmlpBoilerPlate[T](context: T)(implicit serializer: BetterHtmlSerializer[T]): String =
    s"<Body> ${context.toHtml(serializer)}  </body>"

  // Bounding implicit to context of method
  def htmlSugar[T: BetterHtmlSerializer](context: T): String = {
    val serializer = implicitly[BetterHtmlSerializer[T]]
    context.toHtml(serializer)
  }

  case class Permission(mask: String)
  implicit val defaultPermissions: Permission= Permission("0744")

  // implicitly
  val standardPermission = implicitly[Permission]
}
