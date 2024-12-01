package ms.homemonitor.domain.eneco.rest

import ms.homemonitor.domain.eneco.model.EnecoConsumption
import ms.homemonitor.domain.eneco.model.EnecoDataModel
import ms.homemonitor.domain.eneco.model.EnecoUsageEntry
import ms.homemonitor.tools.getForEntityWithHeader
import org.slf4j.LoggerFactory
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.time.LocalDate

@Service
class Eneco(
    private val enecoSecretsService: EnecoSecretsService) {

    private val restTemplate = RestTemplate()
    private val log = LoggerFactory.getLogger(Eneco::class.java)

    private fun getHeaders(apiKey: String, accessToken: String): HttpHeaders {
        val headers = HttpHeaders()
        headers.set("apikey", apiKey)   //41ff1058fc7f4446b80db84e8857c347
        headers.set("authorization", accessToken)
        headers.set("accept", "application/json")
        headers.set("accept-language", "nl-NL")
//        headers.set("request-id", "|b4399e4bb92e453883bb884cd3d4d90b.538724f27f7145ee")
        headers.set("origin", "https://www.eneco.nl")
        headers.set("priority", "u=1, i")
        headers.set("referer", "https://www.eneco.nl/mijn-eneco/verbruik/?product=total&unit=currency&interval=Day")
        headers.set("sec-ch-ua", "\"Not)A;Brand\";v=\"99\", \"Google Chrome\";v=\"127\", \"Chromium\";v=\"127\"")
        headers.set("sec-ch-ua-mobile", "?0")
        headers.set("sec-ch-ua-platform", "\"macOS\"")
        headers.set("sec-fetch-dest", "empty")
        headers.set("sec-fetch-mode", "cors")
        headers.set("sec-fetch-site", "cross-site")
//        headers.set("traceparent", "00-a698b1ec03024812ab1025d6beb8a23f-e7aa3de2aa5c496b-01")
        headers.set("user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/127.0.0.0 Safari/537.")
        return headers
    }

    fun getNewDataFromEneco(fromDate: LocalDate): List<EnecoConsumption> {
        val now = LocalDate.now()
        val response = getEnecoHourDataFromEneco(fromDate, now.plusDays(1))
        return response
            .map{ EnecoConsumption(it.actual.date, it.actual.warmth.high) }
    }

    private fun getEnecoHourDataFromEneco(start: LocalDate, end: LocalDate): List<EnecoUsageEntry> {
        val secrets = enecoSecretsService.getEnecoSecretsOrNull()
        if (secrets == null) {
            log.error("Could not retrieve the secrets from Eneco")
            return emptyList()
        } else {
            val result = mutableListOf<EnecoUsageEntry>()
            var dayDate = start
            while (dayDate != end) {
                val response = getEnecoHourDataByAccessToken(secrets.apiKey, secrets.accessToken, dayDate)
                if (response != null)
                    result.addAll(response.data.usages[0].entries)
                dayDate = dayDate.plusDays(1)
            }
            log.info("eneco data read  from $start to $end, ${result.size} hours, ${result.sumOf { it.actual.warmth.high }} GJ")
            return result
        }
    }

    private fun getEnecoHourDataByAccessToken(apiKey: String,
                                          accessToken: String,
                                          dayDate: LocalDate): EnecoDataModel? {
        val headers = getHeaders(apiKey, accessToken)
        val url = "https://api-digital.enecogroup.com/dxpweb/nl/eneco/customers/54687058/accounts/1/usages" +
                "?aggregation=Day" +
                "&interval=Hour" +
                "&start=${dayDate}" +
                "&end=${dayDate.plusDays(1)}" +
                "&addBudget=false" +
                "&addWeather=true" +
                "&extrapolate=false"
        val response = restTemplate.getForEntityWithHeader<EnecoDataModel>(url, HttpEntity<Any?>(headers))
        return response.body
    }


}



//curl 'https://api-digital.enecogroup.com/dxpweb/nl/eneco/customers/54687058/accounts/1/usages?aggregation=Week&interval=Day&start=2024-08-05&end=&addBudget=false&addWeather=true&extrapolate=false' \
//-H 'accept: application/json' \
//-H 'accept-language: nl-NL' \
//-H 'apikey: 41ff1058fc7f4446b80db84e8857c347' \
//-H 'authorization: eyJraWQiOiJrZjB3dVlwRlYwYkoxU2tnaHJpTWo5aFZOSHZUaXhVbTNlRlN5NDRKZ2ZBIiwiYWxnIjoiUlMyNTYifQ.eyJzdWIiOiIwMHViZGtyODhpYVg5YVB1bjBpNyIsIm5hbWUiOiJtc2NoaWpmQGljbG91ZC5jb20iLCJlbWFpbCI6Im1zY2hpamZAaWNsb3VkLmNvbSIsInZlciI6MSwiaXNzIjoiaHR0cHM6Ly9pbmxvZ2dlbi5lbmVjby5ubC9vYXV0aDIvYXVzMjh5MnBocmRXNTh5SVowaTciLCJhdWQiOiIwb2E2cmw5dnJ6ZlpjZDhkTDBpNyIsImlhdCI6MTcyMzI3Mjg3OSwiZXhwIjoxNzIzMjc2NDc5LCJqdGkiOiJJRC5xbmZsazF2SzRFY2lpT2JuZEE3LW16MTFaX2hfakhZTFhwMXVkTE1sYks4IiwiYW1yIjpbInB3ZCJdLCJpZHAiOiIwMG82aDM2cjhma200SlZHcjBpNiIsIm5vbmNlIjoiM1F0RVY3emdtVW54dnBLZzJCaVRuYVVTdDRJazJXM3JnWnpXYVJuQzhCbyIsInByZWZlcnJlZF91c2VybmFtZSI6Im1zY2hpamZAaWNsb3VkLmNvbSIsImF1dGhfdGltZSI6MTcyMzI2OTE4NSwiYXRfaGFzaCI6IjQzQ1RDV3lyM092bWJuVFdNM2hnR3ciLCJrbGFudG51bW1lciI6NTQ2ODcwNTgsImN1c3RvbWVySWQiOjU0Njg3MDU4fQ.IlMeCU2Ksg8CqTXbWabSR8JxQr2upBQfdsMe1Ct05sHRPkIn4FGT9MdBojJDZS8uOGQ1l1AaEQAHVmJL3cmLytm5MUySrO5VPdCQsLJ6wCu03qHVGnX4wakM4-GVDrUSbKIO3x-w-4yqge1H0PsIv3uUGLHXgz3q7O1qoGbf0c44V7zoBq_rWWGWzAoa7nzw0GY9KbN4BrK10QtVWxT3bmQoS7emakoj-AwAlzxLvIoNfrP5fTQu5SKqy1jilAJOxF0buGETFOTnhY738o8e5UUKHGPpze7SfjI1gF8y27Yemm0kdkq6IUEWI5W71tbGK46f5ggC3uQjldXKlpE2MQ' \
//-H 'origin: https://www.eneco.nl' \
//-H 'priority: u=1, i' \
//-H 'referer: https://www.eneco.nl/mijn-eneco/verbruik/?product=total&unit=currency&interval=Day' \
//-H 'request-id: |b4399e4bb92e453883bb884cd3d4d90b.538724f27f7145ee' \
//-H 'sec-ch-ua: "Not)A;Brand";v="99", "Google Chrome";v="127", "Chromium";v="127"' \
//-H 'sec-ch-ua-mobile: ?0' \
//-H 'sec-ch-ua-platform: "macOS"' \
//-H 'sec-fetch-dest: empty' \
//-H 'sec-fetch-mode: cors' \
//-H 'sec-fetch-site: cross-site' \
//-H 'traceparent: 00-b4399e4bb92e453883bb884cd3d4d90b-538724f27f7145ee-01' \
//-H 'user-agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/127.0.0.0 Safari/537.36'

//Per keer anders:
//-H 'authorization: eyJraWQiOiJrZjB3dVlwRlYwYkoxU2tnaHJpTWo5aFZOSHZUaXhVbTNlRlN5NDRKZ2ZBIiwiYWxnIjoiUlMyNTYifQ.eyJzdWIiOiIwMHViZGtyODhpYVg5YVB1bjBpNyIsIm5hbWUiOiJtc2NoaWpmQGljbG91ZC5jb20iLCJlbWFpbCI6Im1zY2hpamZAaWNsb3VkLmNvbSIsInZlciI6MSwiaXNzIjoiaHR0cHM6Ly9pbmxvZ2dlbi5lbmVjby5ubC9vYXV0aDIvYXVzMjh5MnBocmRXNTh5SVowaTciLCJhdWQiOiIwb2E2cmw5dnJ6ZlpjZDhkTDBpNyIsImlhdCI6MTcyMzQzOTQyOSwiZXhwIjoxNzIzNDQzMDI5LCJqdGkiOiJJRC5ISEIzZmtwSEstSXRYbktMNWNjR1FkTkNEWmQwbE93dUNYcV9qN3oxRG5zIiwiYW1yIjpbInB3ZCJdLCJpZHAiOiIwMG82aDM2cjhma200SlZHcjBpNiIsIm5vbmNlIjoiLTlaS2ZQWWp1aDFmTkxRcGo0LWhDVmJfMWR1dXJBc1Jsc2ZRNEgzRy1BQSIsInByZWZlcnJlZF91c2VybmFtZSI6Im1zY2hpamZAaWNsb3VkLmNvbSIsImF1dGhfdGltZSI6MTcyMzQzOTQyOCwiYXRfaGFzaCI6IkJOZlFhTldSWG9OS3JyS3doNm14dnciLCJrbGFudG51bW1lciI6NTQ2ODcwNTgsImN1c3RvbWVySWQiOjU0Njg3MDU4fQ.inl3JKFx48WG1hiorByOIYt0-4BP4WW-5W1KN74mGou4LqEDQQEnh9vabrfGHotA2rXTN366TjmmMAbLg3D_FyFmPbYuto5-6aDXzWD94Vr3Xqtd_biv-xvBiQJj3JLH4ktJHjaIUJrBw8NibZFr-wFz0Ou2KJOoI8BG9Kxl8j0ujl_FbL3ZxXyv1hQtmU5zrglquLH8QVaTAwvf7rVqPoBRR0lBvQbJGoQYGtvs5ZAIISI814dPjoeBcVVk6irzXsINZtTZU5tI-v62Xsdv50B8z2hVYL1xiefhaoduoWC5hLqzJ93qNt4LFWf5kyCqkXqO_hLZVEngpu0XyJrcdw' \
//-H 'request-id: |53bbb062d28e41768e5e4c79a9632617.0cf21c82811c4562' \
//-H 'traceparent: 00-53bbb062d28e41768e5e4c79a9632617-0cf21c82811c4562-01' \

