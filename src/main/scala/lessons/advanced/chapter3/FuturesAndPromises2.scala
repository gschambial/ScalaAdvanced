package lessons.advanced.chapter3

import lessons.advanced.chapter3.FuturesAndPromises2.SocialNetwork.fetchProfile

import scala.concurrent.{Await, Future, Promise}
import scala.util.{Failure, Random, Success}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

object FuturesAndPromises2 extends App {

  // Mini Social Network

  case class Profile(id: String, name: String) {

    def poke(anotherProfile: Profile): Unit = {
      println(s"${this.name} poking ${anotherProfile.name}")
    }
  }

  object SocialNetwork {

    // Database of Profiles
    val names: Map[String, String] = Map(
      "fb.id.1-zuck" -> "Mark",
      "fb.id.1-bill" -> "Bill",
      "fb.id.1-jeff" -> "Jeff"
    )

    val friends: Map[String, String] = Map(
      "fb.id.1-zuck" -> "fb.id.1-bill"
    )

    val random = new Random()

    def fetchProfile(id: String): Future[Profile] = Future {
      Thread.sleep(random.nextInt(100))
      Profile(id, names(id))
    }

    def fetchBestFriendProfile(profile: Profile): Future[Profile] = Future {
      Thread.sleep(random.nextInt(100))
      val bfId = friends(profile.id)
      Profile(bfId, names(bfId))
    }
  }

  // client: mark to poke bill
  val mark = fetchProfile("fb.id.1-zuck")
  mark.onComplete {
    case Success(markProfile) => {
      val bill = fetchProfile("fb.id.1-bill")
      bill.onComplete {
        case Success(billProfile) => markProfile poke billProfile
        case Failure(exception) => exception.printStackTrace()
      }
    }
    case Failure(exception) => exception.printStackTrace()
  }

  // Function Composition
  // better client: bill poking mark

  val nameOnTheWall = mark.map(profile => profile.name)
  val marksBestFriend = mark.flatMap(profile => SocialNetwork.fetchProfile("fb.id.1-bill"))
  val zucksBestFriend = marksBestFriend.filter(profile => profile.name.startsWith("z"))

  for {
    mark <- SocialNetwork.fetchProfile("fb.id.1-zuck")
    bill <- SocialNetwork.fetchProfile("fb.id.1-bill")
  } mark.poke(bill)

  SocialNetwork.fetchProfile("fb.id.1-zuck")
    .flatMap(mark =>
      SocialNetwork.fetchProfile("fb.id.1-bill")
        .map(bill =>
          mark poke bill)
    )

  Thread.sleep(2000)

  // Fallbacks

  val aProfileNoMatterWhat = SocialNetwork.fetchProfile("fb.id.1-unknown").recover {
    case e: Throwable => Profile("fb.id.1-unknown", "Unknown")
  }

  val aFetchedProfileNoMatterWhat = SocialNetwork.fetchProfile("fb.id.1-unknown1").recoverWith {
    case e: Throwable => SocialNetwork.fetchProfile("fb.id.1-unknown")
  }

  val aFallbackResult = SocialNetwork.fetchProfile("fb.id.1-unknown1").fallbackTo(SocialNetwork.fetchProfile("fb.id.1-unknown"))

  // Online Banking App
  case class User(name: String)
  case class Transaction(sender: String, receiver: String, amount: Double, status: String)
  object BankingApp {

    val name = "Banking App"

    def fetchUsers(name: String): Future[User] = Future {
      Thread.sleep(1000)
      User(name)
    }

    def createTransaction(user: User, merchantName: String, amount: Double): Future[Transaction] = Future {
      Thread.sleep(1000)
      Transaction(user.name, merchantName, amount, "Success")
    }

    def purchase(username: String, item: String, merchantName: String, cost: Double) : String = {
      val transactionStatus = for {
        user <- fetchUsers(username)
        transaction <- createTransaction(user, merchantName, cost)
      } yield transaction.status
      Await.result(transactionStatus, 2.seconds) // , implicit conversions -> pimp my library
    }
  }
  println(BankingApp.purchase("Gourav", "iPhone 11", "Apple Store", 1000))

  // Promises
  val promise = Promise[Int]()
  val future = promise.future

  // Thread 1 - consumer
  future.onComplete({
    case Success(r) => println(s"[Consumer]: I got $r")
  })

  val producer = new Thread( () => {
    println("[Producer]: Crunching numbers...")
    Thread.sleep(500)
    println("[Producer]: Done")
    promise.success(42)
  })

  producer.start()
}
