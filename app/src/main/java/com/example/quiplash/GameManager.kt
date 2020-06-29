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
        const val startSecondsAnswer = 90L
        const val startSecondsVoting = 15L
        const val startSecondsIdle = 60L

        //getter
        fun getUserInfo(): UserQP {
            return this.user
        }

        // setter
        fun setUserinfo(userinfo: UserQP) {
            this.user = userinfo
        }

    }

}