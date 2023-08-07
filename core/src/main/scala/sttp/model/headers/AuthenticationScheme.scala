package sttp.model.headers

import scala.collection.immutable.ListMap

sealed trait AuthenticationScheme {
  def name: String
}

object AuthenticationScheme {
  private[headers] val supported: List[AuthenticationScheme] = List(Basic, Bearer, Digest)
  private[headers] val supportedNames: List[String] = supported.map(_.name)

  object Basic extends AuthenticationScheme {
    override val name = "Basic"

    private[headers] val maxParametersCount = 2

    private val realm: String = "realm"
    private val charset: String = "charset"

    private[headers] def getParams(params: Map[String, String]): ListMap[String, String] =
      ListMap(
        realm -> params.getOrElse(realm, ""),
        charset -> params.getOrElse(charset, "")
      ).filter(_._2.nonEmpty)
  }

  object Bearer extends AuthenticationScheme {
    override val name = "Bearer"

    private[headers] val maxParametersCount = 5

    private val realm: String = "realm"
    private val scope: String = "scope"
    private val error: String = "error"
    private val errorDescription: String = "error_description"
    private val errorUri: String = "error_uri"

    private[headers] def getParams(params: Map[String, String]): ListMap[String, String] =
      ListMap(
        realm -> params.getOrElse(realm, ""),
        scope -> params.getOrElse(scope, ""),
        error -> params.getOrElse(error, ""),
        errorDescription -> params.getOrElse(errorDescription, ""),
        errorUri -> params.getOrElse(errorUri, "")
      ).filter(_._2.nonEmpty)
  }

  object Digest extends AuthenticationScheme {
    override val name = "Digest"

    private val realm: String = "realm"
    private val domain: String = "domain"
    private val nonce: String = "nonce"
    private val opaque: String = "opaque"
    private val stale: String = "stale"
    private val algorithm: String = "algorithm"
    private val qop: String = "qop"
    private val qopValues = List("auth", "auth-int")
    private val charset: String = "charset"
    private val userhash: String = "userhash"

    private[headers] def paramsValid(params: Map[String, String]): Either[String, Unit] = {
      val containsNonce = params.contains(nonce)
      val containsOpaque = params.contains(opaque)
      val qopValue = params.getOrElse(qop, "")
      val qopValueMatch = qopValues.exists(_.equals(qopValue))
      if (!containsNonce) Left(s"Missing nonce parameter in: $params")
      else if (!containsOpaque) Left(s"Missing opaque parameter in: $params")
      else if (!qopValueMatch) Left(s"qop value incorrect in: $params")
      else Right(())
    }

    private[headers] def getParams(params: Map[String, String]): ListMap[String, String] =
      ListMap(
        realm -> params.getOrElse(realm, ""),
        domain -> params.getOrElse(domain, ""),
        nonce -> params.getOrElse(nonce, ""),
        opaque -> params.getOrElse(opaque, ""),
        stale -> params.getOrElse(stale, ""),
        algorithm -> params.getOrElse(algorithm, ""),
        qop -> params.getOrElse(qop, ""),
        charset -> params.getOrElse(charset, ""),
        userhash -> params.getOrElse(userhash, "")
      ).filter(_._2.nonEmpty)
  }
}
