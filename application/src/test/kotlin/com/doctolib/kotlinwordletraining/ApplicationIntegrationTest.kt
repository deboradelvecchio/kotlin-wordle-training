package com.doctolib.kotlinwordletraining

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles

@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@ActiveProfiles("test")
class ApplicationIntegrationTest {

    @Autowired private lateinit var restTemplate: TestRestTemplate

    @Test
    @SuppressWarnings("squid:S1186")
    fun contextLoads() { // NOSONAR
        // assertion is useless: just interested in context loading
    }

    @Test
    fun actuatorIsEnabled() {
        val response = restTemplate.getForEntity("/actuator", String::class.java)

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).contains("/health")
    }
}
