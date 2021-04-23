package com.bignerdranch.android.chatapp2.modelClasses

class Users {

    private var profileImageUrl: String = ""
    private var uid: String = ""
    private var username: String = ""

    constructor(profileImageUrl: String, uid: String, username: String) {
        this.profileImageUrl = profileImageUrl
        this.uid = uid
        this.username = username
    }

    fun getImageURL(): String?{
        return profileImageUrl
    }

    fun setImageURL(profileImageUrl: String){
        this.profileImageUrl = profileImageUrl
    }

    fun getUID(): String?{
        return uid
    }

    fun setUID(uid: String){
        this.uid = uid
    }

    fun getUsername(): String?{
        return username
    }

    fun setUsername(username: String){
        this.username = username
    }

}