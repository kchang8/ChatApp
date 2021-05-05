package com.bignerdranch.android.chatapp2.modelClasses

class Message {
    private var id: String = ""
    private var text: String = ""
    private var fromUid: String = ""
    private var toUid: String = ""
    private var type: String = ""

    constructor()

    constructor(id: String, text: String, fromUid: String, toUid: String, type: String) {
        this.id = id
        this.text = text
        this.fromUid = fromUid
        this.toUid = toUid
        this.type = type
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

    fun getType(): String{
        return type
    }

    fun setType(type: String){
        this.type = type
    }
}