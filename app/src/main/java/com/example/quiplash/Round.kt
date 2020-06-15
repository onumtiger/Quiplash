package com.example.quiplash


class Round {
    var voters: List<String> = listOf()
    var opponents: List<RoundUser> = listOf()
    var question: String = ""
    //var winner: RoundUser?= null
    //var loser: RoundUser?= null

    constructor(votersArr: List<String>, opponentsArr: List<RoundUser>) {
        voters = votersArr
        opponents = opponentsArr
    }

    constructor(votersArr: List<String>, opponentsArr: List<RoundUser>, roundQuestion:String) {
        voters = votersArr
        opponents = opponentsArr
        question = roundQuestion
    }

    constructor()


    /*fun setResult(userW: RoundUser, userL: RoundUser){
        winner = userW
        loser = userL
    }*/

}