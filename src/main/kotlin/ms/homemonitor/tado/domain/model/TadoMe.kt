package ms.homemonitor.tado.domain.model

data class TadoMe(
    val name: String,
    val email: String,
    val username: String,
    val id: String,
    val homes: List<TadoHome>)
