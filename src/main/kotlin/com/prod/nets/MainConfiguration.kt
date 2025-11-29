package com.prod.nets

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import


@Configuration
@Import(
    SoapConfiguration::class,
    GrpcConfiguration::class,
)
class MainConfiguration