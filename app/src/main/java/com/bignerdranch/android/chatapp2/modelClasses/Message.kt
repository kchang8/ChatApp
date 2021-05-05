package com.bignerdranch.android.chatapp2.modelClasses

import com.google.firebase.messaging.Constants

class Message {
    private var id: String = ""
    private var text: String = ""
    private var fromUid: String = ""
    private var toUid: String = ""
<<<<<<< Updated upstream
    private var imageUrl: String = ""
    //private var type: String = ""
=======
    private var type: String = ""
>>>>>>> Stashed changes

    constructor()

    constructor(id: String, text: String, fromUid: String, toUid: String, type: String) {
        this.id = id
        this.text = text
        this.fromUid = fromUid
        this.toUid = toUid
<<<<<<< Updated upstream
        //this.type = type
=======
        this.type = type
>>>>>>> Stashed changes
    }


    fun getId(): String{
        return id
    }

    fun setId(id: String){
        this.id = id
    }

    fun getText(): String{
        return text
    }

    fun setText(text: String){
        this.text = text
    }

    fun getFromUid(): String{
        return fromUid
    }

    fun setFromUid(fromUid: String){
        this.fromUid = fromUid
    }

    fun getToUid(): String{
        return toUid
    }

    fun setToUid(toUid: String){
        this.toUid = toUid
    }

<<<<<<< Updated upstream

=======
    fun getType(): String{
        return type
    }

    fun setType(type: String){
        this.type = type
    }
>>>>>>> Stashed changes
}