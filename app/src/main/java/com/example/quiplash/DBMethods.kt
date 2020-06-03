package com.example.quiplash

import android.widget.Toast
import java.util.*
import kotlin.collections.HashMap

class DBMethods {

    var question: String? = null
    var type: String? = null
    var ID: String? = null

    companion object {

        fun saveQuestion() {
            var ID = createID().toString()
            /*
            val attributes = HashMap<String, Any>()
            attributes.put("text", question_text)
            attributes.put("ID", ID)
            attributes.put("Type", question_type)

            var qustn = Question(ID, question_text, question_type)

     */
        }

            @Throws(Exception::class)
            fun createID(): String? {
                return UUID.randomUUID().toString()
            }


        }

    }
