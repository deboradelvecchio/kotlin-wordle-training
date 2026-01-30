package com.doctolib.kotlinwordletraining

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
@EnableJpaRepositories(basePackages = ["com.doctolib.kotlinwordletraining.repository"])
@EntityScan(basePackages = ["com.doctolib.kotlinwordletraining.entity"])
class Application

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}
