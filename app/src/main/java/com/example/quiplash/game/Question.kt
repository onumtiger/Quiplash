package com.example.quiplash.game

class Question {

    var question: String? = null
    var type: String? = null
    var ID: String? = null
    var category: String? = null

    constructor(ID: String?, question: String?, type: String?, category: String?) {
        this.ID = ID
        this.question = question
        this.type = type
        this.category = category
    }

    constructor(){}

}
