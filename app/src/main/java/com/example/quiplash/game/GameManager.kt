package com.example.quiplash.game

import com.example.quiplash.user.UserQP

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
            return user
        }

        // setter
        fun setUserinfo(userinfo: UserQP) {
            user = userinfo
        }

    }

}