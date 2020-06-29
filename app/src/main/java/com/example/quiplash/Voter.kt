package com.example.quiplash

class Voter {
    var userID: String = ""
    var voteUserID: String = ""

    constructor(userid: String) {
        userID = userid
        voteUserID = ""
    }

    constructor(userid: String, vote: String) {
        userID = userid
        voteUserID = vote
    }

    constructor()


}