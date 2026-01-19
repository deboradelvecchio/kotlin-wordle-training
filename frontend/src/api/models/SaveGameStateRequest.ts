import type { Attempt } from './WordOfTheDayResponse'

export type SaveGameStateRequest = {
  attempts: Attempt[]
  date: string
}
