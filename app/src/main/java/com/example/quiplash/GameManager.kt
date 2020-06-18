package com.example.quiplash

/**
 * GAMEMANAGER:
 * In the GameManager-class all game-relevant data,
 * which will be called up frequently is stored here.
 * Like the user-information.
 * **/

class GameManager {

    companion object {
        var user = UserQP()
        var game = Game()
        val startSeconds = 60L
        //var score: Int?= null

        //getter
        fun getUserInfo(): UserQP {
            return this.user
        }

        fun getGameInfo(): Game {
            return this.game
        }

        // setter
        fun setUserinfo(userinfo: UserQP) {
            this.user = userinfo
        }

        fun setGameinfo(gameinfo: Game) {
            this.game = gameinfo
        }
    }

}