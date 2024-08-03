package ms.homemonitor.infra.resttools

import com.sun.jdi.request.InvalidRequestStateException
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange

inline fun <reified T : Any> RestTemplate.getForObjectWithHeader(endPoint: String, headerRequest: HttpEntity<Any?>): T {
    return this.exchange<T>(
        url=endPoint,
        method= HttpMethod.GET,
        requestEntity=headerRequest,
        T::class.java
    ).body
        ?: throw InvalidRequestStateException("result is null while fetching $endPoint")
}

