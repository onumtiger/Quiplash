package com.example.quiplash


class Round {
    var voters: List<Voter> = listOf()
    var opponents: List<Opponent> = listOf()
    var question: String = ""

    constructor(votersArr: List<Voter>, opponentsArr: List<Opponent>) {
        voters = votersArr
        opponents = opponentsArr
    }

    constructor(votersArr: List<Voter>, opponentsArr: List<Opponent>, roundQuestion:String) {
        voters = votersArr
        opponents = opponentsArr
        question = roundQuestion
    }

    constructor()

}