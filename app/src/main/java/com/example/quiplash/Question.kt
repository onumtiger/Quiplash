package com.example.quiplash

import com.google.firebase.firestore.IgnoreExtraProperties

/*
@IgnoreExtraProperties
class Question {
    var question: String? = null
    var type: Int? = null

    constructor() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    constructor(question: String?, type: Int?) {
        this.question = question
        this.type = type
    }
}
*/

class Question {

    var question: String? = null
    var type: String? = null
    var ID: String? = null

    constructor(ID: String?, question: String?, type: String?) {
        this.ID = ID
        this.question = question
        this.type = type
    }

}
