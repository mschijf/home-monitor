package ms.homemonitor.controller

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springdoc.core.models.GroupedOpenApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ApiDefinitions {

    @Bean
    fun summaryGroup(): GroupedOpenApi? {
        return GroupedOpenApi.builder()
            .group("summary")
            .displayName("Summary")
            .addOpenApiCustomizer { openApi: OpenAPI? ->
                openApi!!.info = Info()
                    .title("Summary API")
                    .description("Public API fore retrieving summaries")
                    .version("1.0")
            }
            .pathsToMatch("/**/summary")
            .build()
    }

    @Bean
    fun adminGroup(): GroupedOpenApi? {
        return GroupedOpenApi.builder()
            .group("admin")
            .displayName("Admin")
            .addOpenApiCustomizer { openApi: OpenAPI? ->
                openApi!!.info = Info()
                    .title("Admin or verify (Rest)Clients APIs")
                    .description("verify the working of retrieving current data from several data providers")
                    .version("1.0")
            }
            .pathsToMatch("/admin/**")
            .build()
    }

    @Bean
    fun logGroup(): GroupedOpenApi? {
        return GroupedOpenApi.builder()
            .group("log")
            .displayName("log")
            .addOpenApiCustomizer { openApi: OpenAPI? ->
                openApi!!.info = Info()
                    .title("v1")
                    .description("Log API")
                    .version("1.0")
            }
            .pathsToMatch("/log/**")
            .build()
    }

    @Bean
    fun testGroup(): GroupedOpenApi? {
        return GroupedOpenApi.builder()
            .group("test")
            .displayName("Test")
            .addOpenApiCustomizer { openApi: OpenAPI? ->
                openApi!!.info = Info()
                    .title("Test API")
                    .description("test out new functionality")
                    .version("1.0")
            }
            .pathsToMatch("/test/**")
            .build()
    }
}
