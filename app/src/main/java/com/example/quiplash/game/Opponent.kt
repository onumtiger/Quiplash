package com.example.quiplash.game

class Opponent {
    var userID: String? =null
    var answer: String = ""
    var answerScore: Int = 0

    constructor(userid: String) {
        this.userID = userid
    }

    constructor(userid: String, answer:String) {
        this.answer = answer
        this.userID = userid
    }

    constructor(answer:String, score:Int, userid: String) {
        this.answer = answer
        this.answerScore = score
        this.userID = userid
    }

    constructor()

    fun setRoundScore(roundscore: Int){
        this.answerScore = roundscore
    }

    fun setRoundAnswer(answer: String){
        this.answer = answer
    }
}