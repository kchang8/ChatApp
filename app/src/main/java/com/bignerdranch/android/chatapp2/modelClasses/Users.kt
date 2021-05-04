package com.bignerdranch.android.chatapp2.modelClasses

import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize

@Parcelize
class Users (val uid: String, val username: String, val email: String, val profileImageUrl: String): Parcelable{

    constructor() : this("", "", "", "")

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "uid" to uid,
            "username" to username,
            "email" to email,
            "profileImageUrl" to profileImageUrl
        )
    }
}