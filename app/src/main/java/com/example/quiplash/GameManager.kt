package com.example.quiplash

/**
 * GAMEMANAGER:
 * In the GameManager-class all game-relevant data,
 * which will be called up frequently is stored here.
 * Like the user-information.
 * **/

class GameManager {

    var user = User()
    //var score: Int?= null

    //getter
    fun getUserInfo(): User {
        return this.user
    }

    // setter
    fun setUserinfo(userinfo:User) {
        this.user = userinfo
    }

}