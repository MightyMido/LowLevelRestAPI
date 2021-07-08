import java.io.InputStream
import java.security.{KeyStore, SecureRandom}
import akka.actor.ActorSystem
import akka.http.javadsl.{ConnectionContext, HttpsConnectionContext}
import akka.http.scaladsl.model.headers.Connection
import akka.stream.ActorMaterializer

import javax.net.ssl.{KeyManagerFactory, SSLContext, TrustManagerFactory}

object LowLevelHttps extends App {
  implicit val system = ActorSystem("LowLevelHttps")
  implicit val materializer:ActorMaterializer = ActorMaterializer()

  val ks:KeyStore = KeyStore.getInstance("PKCS12")
  val keyStoreFile:InputStream = getClass.getClassLoader.getResourceAsStream("keytore.pkcs12")
  val password = "akka-https".toCharArray
  ks.load(keyStoreFile,password)

  // Step 2: initialize
  val keyManagerFactory = KeyManagerFactory.getInstance("SunX509")
  keyManagerFactory.init(ks,password)

  //step 3 :Trust
  val trustManagerFactory = TrustManagerFactory.getInstance("SunX509")
  trustManagerFactory.init(ks)

  //step 4: initialize an ssl context
  val ssLContext:SSLContext = SSLContext.getInstance("TLS")
  ssLContext.init(keyManagerFactory.getKeyManagers,
    trustManagerFactory.getTrustManagers,new SecureRandom())

  // step five return HttpsConnction Context
  val httpsConnectionContext:HttpsConnectionContext =
    ConnectionContext.https(ssLContext)

}