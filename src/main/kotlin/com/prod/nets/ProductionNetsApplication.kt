package com.prod.nets

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import

@SpringBootApplication
@Import(
	MainConfiguration::class,
	SoapConfiguration::class,
	GrpcConfiguration::class,
)
class ProductionNetsApplication

fun main(args: Array<String>) {
	runApplication<ProductionNetsApplication>(*args)
}
