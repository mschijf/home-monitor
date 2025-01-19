package ms.homemonitor.shared.tools.rest

import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange

inline fun <reified T : Any> RestTemplate.getForEntityWithHeader(endPoint: String, headerRequest: HttpEntity<Any?>): ResponseEntity<T> {
    try {
        return this.exchange<T>(
            url = endPoint,
            method = HttpMethod.GET,
            requestEntity = headerRequest,
            T::class.java
        )
    } catch(e: HttpClientErrorException) {
        return ResponseEntity.status(e.statusCode).build()
    }
}

