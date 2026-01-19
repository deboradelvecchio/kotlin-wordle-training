package com.doctolib.kotlinwordletraining;

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

/**
 * Database migration application entry point.
 */
@SpringBootApplication class Application

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}
