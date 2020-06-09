package com.example.quiplash

class RoundUser {
    var userID: String? =null
    var score: Int = 0

    constructor(userid: String) {
        this.userID = userid
    }

    fun setRoundScore(roundscore: Int){
        this.score = roundscore
    }
}