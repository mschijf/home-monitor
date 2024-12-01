package ms.homemonitor.domain.eneco.model

import org.slf4j.LoggerFactory

// data class containing the two 'secrets' necessary to make the final call to get data from eneco
// input is a html page (that is retrieved after login in on 'mijn eneco'.
//
// when logged in, you can find the secrets on that page
//    apiKey - ususally a fixed value, but can be found after FE_DC_API_KEY
//    accessToken
//
data class EnecoOAuth(
    val apiKey: String,
    val accessToken: String) {

    companion object {
        private val log = LoggerFactory.getLogger(EnecoOAuth::class.java)

        fun of(htmlPage: String): EnecoOAuth? {
            val apiKey = getValueForKey(htmlPage, "FE_DC_API_KEY")
            val accessToken = getValueForKey(htmlPage, "accessToken")
            return if (apiKey.isEmpty() || accessToken.isEmpty())
                null
            else
                EnecoOAuth(apiKey= apiKey, accessToken = accessToken)
        }

        private fun getValueForKey(htmlPage: String, key: String): String {
            return if (htmlPage.contains(key)) {
                htmlPage
                    .substringAfter("\"$key\":")
                    .substringBefore(",")
                    .trim()
                    .removeSurrounding("\"")
            } else {
                log.error("cannot find $key on htmlPage")
                ""
            }
        }
    }
}
