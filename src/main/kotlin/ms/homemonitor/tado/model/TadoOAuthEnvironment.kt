package ms.homemonitor.tado.model

data class TadoOAuthEnvironment(
    val clientId: String,
    val clientSecret: String,
    val baseUrl: String ) {

    fun isFilled(): Boolean =
        clientId.isNotEmpty() && clientSecret.isNotEmpty() && baseUrl.isNotEmpty()
}
