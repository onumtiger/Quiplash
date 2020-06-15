package com.example.quiplash

/**
 * GAMEMANAGER:
 * In the GameManager-class all game-relevant data,
 * which will be called up frequently is stored here.
 * Like the user-information.
 * **/

class Game {
    var activeRound: Int = 1
    var category: String = ""
    var playerNumber: Int = 0
    var rounds: Int = 0
    var users: List<String> = listOf()
    var gameID: String = ""
    var playrounds: List<Round> = listOf()
    var isPublic: Boolean = false

    constructor(
        currentRound: Int,
        gameCategory: String,
        gamePlayerNumber: Int,
        gameRounds: Int,
        gameUsers: List<String>,
        gameid: String
    ) {
        this.activeRound = currentRound
        this.category = gameCategory
        this.playerNumber = gamePlayerNumber
        this.rounds = gameRounds
        this.users = gameUsers
        this.gameID = gameid
    }

    constructor(
        currentRound: Int,
        gameCategory: String,
        gamePlayerNumber: Int,
        gameRounds: Int,
        gameUsers: List<String>,
        gameid: String,
        ispublic: Boolean
    ) {
        this.activeRound = currentRound
        this.category = gameCategory
        this.playerNumber = gamePlayerNumber
        this.rounds = gameRounds
        this.users = gameUsers
        this.gameID = gameid
        this.isPublic = ispublic
    }

    constructor(
        currentRound: Int,
        gameCategory: String,
        gamePlayerNumber: Int,
        gameRounds: Int,
        gameUsers: List<String>,
        gameid: String,
        gameplayrounds: List<Round>
    ) {
        this.activeRound = currentRound
        this.category = gameCategory
        this.playerNumber = gamePlayerNumber
        this.rounds = gameRounds
        this.users = gameUsers
        this.gameID = gameid
        this.playrounds = gameplayrounds
    }

    constructor(
        currentRound: Int,
        gameCategory: String,
        gamePlayerNumber: Int,
        gameRounds: Int,
        gameUsers: List<String>,
        gameid: String,
        gameplayrounds: List<Round>,
        ispublic: Boolean
    ) {
        this.activeRound = currentRound
        this.category = gameCategory
        this.playerNumber = gamePlayerNumber
        this.rounds = gameRounds
        this.users = gameUsers
        this.gameID = gameid
        this.playrounds = gameplayrounds
        this.isPublic = ispublic
    }

    constructor()

}

