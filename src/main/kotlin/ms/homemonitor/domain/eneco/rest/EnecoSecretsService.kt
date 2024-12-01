package ms.homemonitor.domain.eneco.rest

import io.github.bonigarcia.wdm.WebDriverManager
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxOptions
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class EnecoSecretsService(
    @Value("\${eneco.userName}") private val userName: String,
    @Value("\${eneco.password}") private val password: String) {

    private val log = LoggerFactory.getLogger(EnecoSecretsService::class.java)

    fun getEnecoSecretsOrNull(): EnecoSecrets? {
        val sourcePage = scrapeEnecoPage()
        return EnecoSecrets.of(sourcePage)
    }

    private fun scrapeEnecoPage():String {
        try {

            log.info("start reading new Eneco data")

            WebDriverManager.firefoxdriver().setup()

            val options = FirefoxOptions()
            options.addArguments("--no-sandbox")
            options.addArguments("--headless")

            val driver: WebDriver = FirefoxDriver(options)

            val url = "https://inloggen.eneco.nl/"

            driver.get(url)
            driver.manage().timeouts().implicitlyWait(Duration.ofMillis(20_000))

            Thread.sleep(3_000)
            val inlog = driver.findElement(By.name("identifier"))

            Thread.sleep(1_000)
            inlog.sendKeys(userName)
            inlog.submit()

            Thread.sleep(3_000)
            val pw = driver.findElement(By.name("credentials.passcode"))

            Thread.sleep(1_000)
            pw.sendKeys(password)
            pw.submit()

            Thread.sleep(5_000)

            val pageSource = driver.pageSource

            driver.quit()

            log.info("Finish reading new Eneco data")
            return pageSource

        } catch (e: Exception) {
            log.error("Some error occurred", e)
            return ""
        }
    }
}

data class EnecoSecrets(val apiKey: String, val accessToken: String) {

    companion object {
        private val log = LoggerFactory.getLogger(EnecoSecrets::class.java)

        fun of(htmlPage: String): EnecoSecrets? {
            val apiKey = getValueForKey(htmlPage, "FE_DC_API_KEY")
            val accessToken = getValueForKey(htmlPage, "accessToken")
            return if (apiKey.isEmpty() || accessToken.isEmpty())
                null
            else
                EnecoSecrets(apiKey= apiKey, accessToken = accessToken)
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