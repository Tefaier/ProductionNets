package com.prod.nets

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate
import java.time.Duration

@Configuration
open class RestTemplateConfiguration {
    @Bean
    open fun restTemplate(
        @Value("\${timeout.connection}") connectionTimeout: Long,
        @Value("\${timeout.response}") responseTimeout: Long,
    ): RestTemplate {
        return RestTemplateBuilder()
            .connectTimeout(Duration.ofSeconds(connectionTimeout))
            .readTimeout(Duration.ofSeconds(responseTimeout))
            .errorHandler { false }
            .build()
    }
}