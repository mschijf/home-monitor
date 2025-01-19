package ms.homemonitor.eneco.restclient

import io.github.bonigarcia.wdm.WebDriverManager
import ms.homemonitor.eneco.restclient.model.EnecoOAuth
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxOptions
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class EnecoAccessToken(
    @Value("\${eneco.userName}") private val userName: String,
    @Value("\${eneco.password}") private val password: String) {

    private val log = LoggerFactory.getLogger(EnecoAccessToken::class.java)

    fun getEnecoSecretsOrNull(): EnecoOAuth? {
        val sourcePage = scrapeEnecoPage()
        return EnecoOAuth.of(sourcePage)
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