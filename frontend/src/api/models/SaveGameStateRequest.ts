import type { Attempt } from './types'

export type SaveGameStateRequest = {
  attempts: Attempt[]
  date: string
}
