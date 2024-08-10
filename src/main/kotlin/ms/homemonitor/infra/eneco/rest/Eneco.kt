package ms.homemonitor.infra.eneco.rest

import ms.homemonitor.infra.eneco.model.EnecoDataModel
import ms.homemonitor.infra.resttools.getForEntityWithHeader
import org.slf4j.LoggerFactory
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.time.LocalDate


@Service
class Eneco {
    private val restTemplate = RestTemplate()
    private val log = LoggerFactory.getLogger(Eneco::class.java)

    private fun getValueForKey(htmlPage: String, key: String): String {
        val value = htmlPage.substringAfter("\"$key\":").substringBefore(",").trim()
        return value.removeSurrounding("\"")
    }

    private fun getHeaders(apiKey: String, accessToken: String): HttpHeaders {
        val headers = HttpHeaders()
        headers.set("apikey", apiKey)   //41ff1058fc7f4446b80db84e8857c347
        headers.set("authorization", accessToken)
        headers.set("accept", "application/json")
        headers.set("accept-language", "nl-NL")

//        headers.set("request-id", "|b4399e4bb92e453883bb884cd3d4d90b.538724f27f7145ee")
//        headers.set("origin", "https://www.eneco.nl")
//        headers.set("priority", "u=1, i")
//        headers.set("referer", "https://www.eneco.nl/mijn-eneco/verbruik/?product=total&unit=currency&interval=Day")
//        headers.set("sec-ch-ua", "\"Not)A;Brand\";v=\"99\", \"Google Chrome\";v=\"127\", \"Chromium\";v=\"127\"")
//        headers.set("sec-ch-ua-mobile", "?0")
//        headers.set("sec-ch-ua-platform", "\"macOS\"")
//        headers.set("sec-fetch-dest", "empty")
//        headers.set("sec-fetch-mode", "cors")
//        headers.set("sec-fetch-site", "cross-site")
//        headers.set("traceparent", "00-a698b1ec03024812ab1025d6beb8a23f-e7aa3de2aa5c496b-01")
//        headers.set("user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/127.0.0.0 Safari/537.")
        return headers
    }

    fun getEnecoData(sourcePage: String,
                     start: LocalDate = LocalDate.now().minusDays(30),
                     end: LocalDate = LocalDate.now().plusDays(1)): EnecoDataModel? {

        val page = sourcePage  //readPage()
        val apiKey = getValueForKey(page, "FE_DC_API_KEY")
        val accessToken = getValueForKey(page, "accessToken")

        val headers = getHeaders(apiKey, accessToken)

        val url = "https://api-digital.enecogroup.com/dxpweb/nl/eneco/customers/54687058/accounts/1/usages" +
                "?aggregation=Year" +
                "&interval=Day" +
                "&start=${start.toString()}" +
                "&end=${end.toString()}" +
                "&addBudget=false" +
                "&addWeather=true" +
                "&extrapolate=false"
        val response = restTemplate.getForEntityWithHeader<EnecoDataModel>(url, HttpEntity<Any?>(headers))
        log.info("reading eneco data from $start to $end, with statuscode: " + response.statusCode)
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