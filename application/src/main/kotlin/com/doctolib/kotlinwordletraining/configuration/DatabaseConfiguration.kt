package com.doctolib.kotlinwordletraining.configuration

import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

/**
 * Configuration class for database and JPA repositories setup. Enables JPA repositories scanning
 * and configuration.
 */
@EnableJpaRepositories(basePackages = ["com.doctolib.kotlinwordletraining.repository"])
@EntityScan(basePackages = ["com.doctolib.kotlinwordletraining.entity"])
@Configuration
class DatabaseConfiguration
