package ms.homemonitor.controller

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springdoc.core.customizers.OpenApiCustomizer
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
            .addOpenApiCustomizer(OpenApiCustomizer { openApi: OpenAPI? ->
                openApi!!.info = Info()
                    .title("Summary API")
                    .description("Public API fore retrieving summaries")
                    .version("1.0")
            }
            )
            .pathsToMatch("/**/summary")
            .build()
    }

    @Bean
    fun verifyGroup(): GroupedOpenApi? {
        return GroupedOpenApi.builder()
            .group("verify")
            .displayName("Verify")
            .addOpenApiCustomizer(OpenApiCustomizer { openApi: OpenAPI? ->
                openApi!!.info = Info()
                    .title("Verify (Rest)Clients APIs")
                    .description("verify the working of retrieving current data from several data providers")
                    .version("1.0")
            }
            )
            .pathsToMatch("/verify/**")
            .build()
    }

    @Bean
    fun logGroup(): GroupedOpenApi? {
        return GroupedOpenApi.builder()
            .group("log")
            .displayName("log")
            .addOpenApiCustomizer(OpenApiCustomizer { openApi: OpenAPI? ->
                openApi!!.info = Info()
                    .title("v1")
                    .description("Log API")
                    .version("1.0")
            }
            )
            .pathsToMatch("/log/**")
            .build()
    }

    @Bean
    fun testGroup(): GroupedOpenApi? {
        return GroupedOpenApi.builder()
            .group("test")
            .displayName("Test")
            .addOpenApiCustomizer(OpenApiCustomizer { openApi: OpenAPI? ->
                openApi!!.info = Info()
                    .title("Test API")
                    .description("test out new functionality")
                    .version("1.0")
            }
            )
            .pathsToMatch("/test/**")
            .build()
    }
}
