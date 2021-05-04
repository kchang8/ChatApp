package com.bignerdranch.android.chatapp2.modelClasses

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Users (val uid: String, val username: String, val email: String, val profileImageUrl: String): Parcelable{

    constructor() : this("", "", "", "")


}