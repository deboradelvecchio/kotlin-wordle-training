export type LeaderboardEntry = {
  username: string
  attempts: number
  solveTimeSeconds: number
  rank: number
}

export type LeaderboardResponse = {
  entries: LeaderboardEntry[]
  currentUserRank?: number // Only if authenticated
}
