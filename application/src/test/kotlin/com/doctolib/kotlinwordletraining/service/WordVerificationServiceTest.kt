package com.doctolib.kotlinwordletraining.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class WordVerificationServiceTest {

    private val service = WordVerificationService()

    @Test
    fun `all correct - same word`() {
        val result = service.verifyWord("HELLO", "HELLO")
        assertThat(result).isEqualTo("CCCCC")
    }

    @Test
    fun `all absent - no matching letters`() {
        val result = service.verifyWord("HELLO", "FARTS")
        assertThat(result).isEqualTo("AAAAA")
    }

    @Test
    fun `mixed correct and absent`() {
        // Target: HELLO, Guess: HELPS
        // H=C, E=C, L=C (pos 2 matches), P=A, S=A
        val result = service.verifyWord("HELLO", "HELPS")
        assertThat(result).isEqualTo("CCCAA")
    }

    @Test
    fun `letter present but wrong position`() {
        val result = service.verifyWord("HELLO", "OELLH")
        assertThat(result).isEqualTo("PCCCP")
    }

    @Test
    fun `duplicate letter in guess - only one in target`() {
        // Target: HELPS (E in pos 1), Guess: EEEEE
        // E in pos 1 is CORRECT, all others are ABSENT (no more E's)
        val result = service.verifyWord("HELPS", "EEEEE")
        assertThat(result).isEqualTo("ACAAA")
    }

    @Test
    fun `duplicate letter in guess - consumes available letters`() {
        // Target: CREEP (C-R-E-E-P), Guess: EERIE (E-E-R-I-E)
        // No exact matches in first pass
        // Second pass: E(0)→P (consumes E@2), E(1)→P (consumes E@3), R(2)→P (consumes R@1), I(3)→A,
        // E(4)→A (no more E's)
        val result = service.verifyWord("CREEP", "EERIE")
        assertThat(result).isEqualTo("PPPAA")
    }

    @Test
    fun `duplicate letter in target`() {
        // Target: GEESE (has 3 E's)
        // Guess:  EAGLE
        // E pos 0: present (there's E but not in pos 0)
        // A pos 1: absent
        // G pos 2: present (G is in pos 0)
        // L pos 3: absent
        // E pos 4: correct
        val result = service.verifyWord("GEESE", "EAGLE")
        assertThat(result).isEqualTo("PAPAC")
    }

    @Test
    fun `case insensitive - lowercase target`() {
        val result = service.verifyWord("hello", "HELLO")
        assertThat(result).isEqualTo("CCCCC")
    }

    @Test
    fun `case insensitive - lowercase guess`() {
        val result = service.verifyWord("HELLO", "hello")
        assertThat(result).isEqualTo("CCCCC")
    }

    @Test
    fun `case insensitive - mixed case`() {
        val result = service.verifyWord("HeLLo", "hElLO")
        assertThat(result).isEqualTo("CCCCC")
    }

    @Test
    fun `correct letter consumes before present - ABBEY BABES`() {
        // Target: ABBEY (A pos 0, B pos 1 and 2, E pos 3, Y pos 4)
        // Guess:  BABES
        // B pos 0: present (B exists at pos 1,2 but not pos 0)
        // A pos 1: present (A exists at pos 0 but not pos 1)
        // B pos 2: correct
        // E pos 3: correct
        // S pos 4: absent
        val result = service.verifyWord("ABBEY", "BABES")
        assertThat(result).isEqualTo("PPCCA")
    }

    @Test
    fun `no double counting - ROBOT ROOTS`() {
        // Target: ROBOT (R pos 0, O pos 1 and 3, B pos 2, T pos 4)
        // Guess:  ROOTS
        // R pos 0: correct
        // O pos 1: correct
        // O pos 2: present (O exists at pos 3)
        // T pos 3: present (T exists at pos 4)
        // S pos 4: absent
        val result = service.verifyWord("ROBOT", "ROOTS")
        assertThat(result).isEqualTo("CCPPA")
    }
}
