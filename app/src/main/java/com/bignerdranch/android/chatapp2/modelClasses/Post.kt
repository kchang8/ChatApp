package com.bignerdranch.android.chatapp2.modelClasses

class Post {

    private var postid: String = ""
    private var postImage: String = ""
    private var uid: String = ""
    private var description: String = ""

    constructor()

    constructor(postid: String, postImage: String, uid: String, description: String) {
        this.postid = postid
        this.postImage = postImage
        this.uid = uid
        this.description = description
    }

    fun getPostId(): String {
        return postid
    }

    fun getPostImage(): String {
        return postImage
    }

    fun getPostUid(): String {
        return uid
    }

    fun getPostDescription(): String {
        return description
    }


    fun setPostId(postid: String) {
        this.postid = postid
    }

    fun setPostImage(postImage: String) {
        this.postImage = postImage
    }

    fun setPostUid(uid: String) {
        this.uid = uid
    }

    fun setPostDescription(description: String) {
        this.description = description
    }

}