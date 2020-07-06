package com.example.quiplash.game

/**
 * Game:
 * The Game-Object-class contains all game-relevant data,
 * which will be saved in Firestore-DB
 * **/

class Game {
    var activeRound: Int = 0
    var category: String = ""
    var playerNumber: Int = 0
    var rounds: Int = 0
    var users: List<String> = listOf()
    var gameID: String = ""
    var playrounds: HashMap<String, Round> = linkedMapOf()
    var isPublic: Boolean = false
    var hostID: String = ""
    var gameTitle: String = ""
    var invitations: ArrayList<String> = arrayListOf<String>()


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
        invitations: ArrayList<String>
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
        this.invitations = invitations
    }


    constructor(
        gameid: String,
        gamePlayerNumber: Int,
        ispublic: Boolean,
        currentRound: Int,
        gameplayrounds: HashMap<String, Round>,
        gameHostid: String,
        gametitle: String,
        gameCategory: String,
        gameRounds: Int,
        gameUsers: List<String>,
        invitations: ArrayList<String>
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
        this.invitations = invitations
    }

    constructor()

}

