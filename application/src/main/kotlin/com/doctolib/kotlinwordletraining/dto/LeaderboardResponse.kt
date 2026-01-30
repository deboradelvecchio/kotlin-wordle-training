package com.doctolib.kotlinwordletraining.dto

data class LeaderboardEntry(
    val username: String,
    val attempts: Int,
    val solveTimeSeconds: Long,
    val rank: Int,
)

data class LeaderboardResponse(
    val entries: List<LeaderboardEntry>,
    val currentUserRank: Int? = null,
)
