package com.example.quiplash

/**
 * GAMEMANAGER:
 * In the GameManager-class all game-relevant data,
 * which will be called up frequently is stored here.
 * Like the user-information.
 * **/

class Game(val activeRound: Int, val category: String, val playerNumbers: Int, val rounds: Int, val users: HashMap<String, String>) {

    constructor() : this(0, "", 0, 0, HashMap())

}