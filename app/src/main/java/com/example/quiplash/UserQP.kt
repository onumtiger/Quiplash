package com.example.quiplash

class UserQP {
    var userID: String? = null
    var userName: String? = null
    var guest: Boolean?= null
    var score: Int?= null
    var photo: String? = null
    var friends: List<String> = emptyList<String>()

    constructor(userID: String?, userName: String?, guest: Boolean?, score: Int?, photo: String?, friends: List<String>) {
        this.userID = userID
        this.userName = userName
        this.guest = guest
        this.score = score
        this.photo = photo
        this.friends = friends
    }

    constructor(userID: String?) {
        this.userID = userID
    }

    constructor() {}

}