package com.example.quiplash.game

class Question {

    var question: String? = null
    var type: String? = null
    var ID: String? = null

    constructor(ID: String?, question: String?, type: String?) {
        this.ID = ID
        this.question = question
        this.type = type
    }

    constructor(){}

}
