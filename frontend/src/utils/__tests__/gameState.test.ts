import { describe, it, expect } from 'vitest'
import {
  GAME_CONSTANTS,
  isGameActive,
  isGameFinished,
  canSubmitWord,
  canInputLetter,
} from '../gameState'

describe('gameState utilities', () => {
  describe('GAME_CONSTANTS', () => {
    it('has correct values', () => {
      expect(GAME_CONSTANTS.MAX_ATTEMPTS).toBe(6)
      expect(GAME_CONSTANTS.WORD_LENGTH).toBe(5)
    })
  })

  describe('isGameActive', () => {
    it('returns true for in_progress state', () => {
      expect(isGameActive('in_progress')).toBe(true)
    })

    it('returns true for not_started state', () => {
      expect(isGameActive('not_started')).toBe(true)
    })

    it('returns false for won state', () => {
      expect(isGameActive('won')).toBe(false)
    })

    it('returns false for lost state', () => {
      expect(isGameActive('lost')).toBe(false)
    })
  })

  describe('isGameFinished', () => {
    it('returns true for won state', () => {
      expect(isGameFinished('won')).toBe(true)
    })

    it('returns true for lost state', () => {
      expect(isGameFinished('lost')).toBe(true)
    })

    it('returns false for in_progress state', () => {
      expect(isGameFinished('in_progress')).toBe(false)
    })

    it('returns false for not_started state', () => {
      expect(isGameFinished('not_started')).toBe(false)
    })
  })

  describe('canSubmitWord', () => {
    it('returns true when word is 5 letters and game is active', () => {
      expect(canSubmitWord('HELLO', 'in_progress')).toBe(true)
      expect(canSubmitWord('WORLD', 'not_started')).toBe(true)
    })

    it('returns false when word is not 5 letters', () => {
      expect(canSubmitWord('HELL', 'in_progress')).toBe(false)
      expect(canSubmitWord('HELLOW', 'in_progress')).toBe(false)
    })

    it('returns false when game is finished', () => {
      expect(canSubmitWord('HELLO', 'won')).toBe(false)
      expect(canSubmitWord('HELLO', 'lost')).toBe(false)
    })
  })

  describe('canInputLetter', () => {
    it('returns true when word is less than 5 letters and game is active', () => {
      expect(canInputLetter('HELL', 'in_progress')).toBe(true)
      expect(canInputLetter('', 'not_started')).toBe(true)
    })

    it('returns false when word is 5 letters', () => {
      expect(canInputLetter('HELLO', 'in_progress')).toBe(false)
    })

    it('returns false when game is finished', () => {
      expect(canInputLetter('HELL', 'won')).toBe(false)
      expect(canInputLetter('HELL', 'lost')).toBe(false)
    })
  })
})
