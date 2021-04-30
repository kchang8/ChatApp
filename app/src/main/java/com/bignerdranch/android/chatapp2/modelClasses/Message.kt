package com.bignerdranch.android.chatapp2.modelClasses

class Message {
    private var id: String = ""
    private var text: String = ""
    private var fromUid: String = ""
    private var toUid: String = ""

    constructor()

    constructor(id: String, text: String, fromUid: String, toUid: String){
        this.id = id
        this.text = text
        this.fromUid = fromUid
        this.toUid = toUid
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
}