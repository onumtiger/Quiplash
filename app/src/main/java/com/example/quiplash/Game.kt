package com.example.quiplash

/**
 * GAMEMANAGER:
 * In the GameManager-class all game-relevant data,
 * which will be called up frequently is stored here.
 * Like the user-information.
 * **/

class Game(val active_round: Int, val category: String, val game_id: String, val player_number: Int, val rounds: Int, val users: HashMap<String, String>) {

    constructor() : this(0, "", "", 0, 0, HashMap())

}