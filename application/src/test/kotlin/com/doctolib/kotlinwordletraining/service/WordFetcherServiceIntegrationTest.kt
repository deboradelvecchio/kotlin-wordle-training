package com.doctolib.kotlinwordletraining.service

import com.doctolib.kotlinwordletraining.repository.WordRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test") // for application-test.yml
class WordFetcherServiceIntegrationTest {
    @Autowired private lateinit var wordFetcherService: WordFetcherService
    @Autowired private lateinit var wordRepository: WordRepository

    @Test
    fun `should fetch and save a new word`() {
        val word = wordFetcherService.fetchAndSaveNewWord()
        assertThat(word).isNotNull()
        assertThat(word.word).isNotEmpty()
        assertThat(word.createdAt).isNotNull()
    }

    @Test
    fun `should return the current word if it exists and is not expired`() {
        val word = wordFetcherService.fetchAndSaveNewWord()
        val currentWord = wordFetcherService.getCurrentWord()
        assertThat(currentWord.id).isEqualTo(word.id)
    }

    @Test
    fun `should fetch and save a new word if it does not exist or is expired`() {
        wordRepository.deleteAll()
        val currentWord = wordFetcherService.getCurrentWord()
        assertThat(currentWord).isNotNull()
        assertThat(currentWord.word).isNotEmpty()
        assertThat(currentWord.createdAt).isNotNull()
    }
}
