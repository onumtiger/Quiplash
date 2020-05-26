package com.example.quiplash

class User {
    var userID: String? = null
    var userName: String? = null

    constructor(userID: String?, userName: String?) {
        this.userID = userID
        this.userName = userName
    }

    constructor(userID: String?) {
        this.userID = userID
    }

    constructor() {}

}