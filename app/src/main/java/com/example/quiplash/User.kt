package com.example.quiplash

class User {
    var userID: String? = null
    var userName: String? = null
    var guest: Boolean?= null
    var score: Int?= null

    constructor(userID: String?, userName: String?, guest: Boolean?, score: Int?) {
        this.userID = userID
        this.userName = userName
        this.guest = guest
        this.score = score
    }

    constructor(userID: String?) {
        this.userID = userID
    }

    constructor() {}

}