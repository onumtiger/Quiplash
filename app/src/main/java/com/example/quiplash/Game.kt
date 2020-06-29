package com.example.quiplash

/**
 * Game:
 * The Game-Object-class contains all game-relevant data,
 * which will be saved in Firestore-DB
 * **/

class Game {
    var activeRound: Int = 1
    var category: String = ""
    var playerNumber: Int = 0
    var rounds: Int = 0
    var users: List<String> = listOf()
    var gameID: String = ""
    var playrounds: HashMap<String, Round> = linkedMapOf()
    var isPublic: Boolean = false
    var hostID: String = ""
    var gameTitle: String = ""
    var questions: ArrayList<Question> = arrayListOf<Question>()


    constructor(
        currentRound: Int,
        gameCategory: String,
        gamePlayerNumber: Int,
        gameRounds: Int,
        gameUsers: List<String>,
        gameid: String,
        hostid: String,
        ispublic: Boolean,
        gametitle: String,
        questions: ArrayList<Question>
    ) {
        this.activeRound = currentRound
        this.category = gameCategory
        this.playerNumber = gamePlayerNumber
        this.rounds = gameRounds
        this.users = gameUsers
        this.gameID = gameid
        this.hostID = hostid
        this.isPublic = ispublic
        this.gameTitle = gametitle
        this.questions = questions
    }


    constructor(
        gameid: String,
        gamePlayerNumber: Int,
        ispublic: Boolean,
        currentRound: Int,
        gameplayrounds: HashMap<String,Round>,
        gameHostid: String,
        gametitle: String,
        gameCategory: String,
        gameRounds: Int,
        gameUsers: List<String>,
        questions: ArrayList<Question>
    ) {
        this.activeRound = currentRound
        this.category = gameCategory
        this.playerNumber = gamePlayerNumber
        this.rounds = gameRounds
        this.users = gameUsers
        this.gameID = gameid
        this.playrounds = gameplayrounds
        this.isPublic = ispublic
        this.hostID = gameHostid
        this.gameTitle = gametitle
        this.questions = questions
    }

    constructor()

}

