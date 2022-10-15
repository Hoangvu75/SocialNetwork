package com.example.fakebook.models

class Posts {
    private var avatar: String = ""
    private var username: String = ""
    private var time: String = ""
    private var content: String = ""
    private var image: String = ""
    private var id: String = ""
    private var userId: String = ""

    constructor()

    constructor(avatar: String, username: String, time: String, content: String, image: String, id: String, userId: String) {
        this.avatar = avatar
        this.username = username
        this.time = time
        this.content = content
        this.image = image
        this.id = id
        this.userId = userId
    }

    fun getAvatar(): String {
        return avatar
    }
    fun setAvatar(avatar: String) {
        this.avatar = avatar
    }

    fun getUsername(): String {
        return username
    }
    fun setUsername(username: String) {
        this.username = username
    }

    fun getTime(): String {
        return time
    }
    fun setTime(time: String) {
        this.time = time
    }

    fun getContent(): String {
        return content
    }
    fun setContent(content: String) {
        this.content = content
    }

    fun getImage(): String {
        return image
    }
    fun setImage(image: String) {
        this.image = image
    }

    fun getId(): String {
        return id
    }
    fun setId(id: String) {
        this.id = id
    }

    fun getUserId(): String {
        return userId
    }
    fun setUserId(userId: String) {
        this.userId = userId
    }
}