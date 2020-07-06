package com.example.quiplash.game


class Round {
    var voters: HashMap<String, Voter> = linkedMapOf()
    var opponents: HashMap<String, Opponent> = linkedMapOf()
    var question: String = ""

    constructor(votersArr: HashMap<String, Voter>, opponentsArr: HashMap<String, Opponent>) {
        voters = votersArr
        opponents = opponentsArr
    }

    constructor(votersArr: HashMap<String, Voter>, opponentsArr: HashMap<String, Opponent>, roundQuestion:String) {
        voters = votersArr
        opponents = opponentsArr
        question = roundQuestion
    }

    constructor()

}