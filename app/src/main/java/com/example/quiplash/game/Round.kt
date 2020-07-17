package com.example.quiplash.game


class Round {
    var voters: List<String> = listOf()
    var opponents: HashMap<String, Opponent> = linkedMapOf()
    var question: String = ""

    constructor(votersArr: List<String>, opponentsArr: HashMap<String, Opponent>, roundQuestion:String) {
        voters = votersArr
        opponents = opponentsArr
        question = roundQuestion
    }

    constructor()

}