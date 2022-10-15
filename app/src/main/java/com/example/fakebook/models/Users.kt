package com.example.fakebook.models

class Users {
    private var uid: String = ""
    private var username: String = ""
    private var avatar: String = ""
    private var cover: String = ""
    private var status: String = ""
    private var search: String = ""
    private var facebook: String = ""
    private var instagram: String = ""
    private var tiktok: String = ""
    private var email: String = ""
    private var password: String = ""
    private var phone: String = ""

    constructor()

    constructor(
        uid: String,
        username: String,
        avatar: String,
        cover: String,
        status: String,
        search: String,
        facebook: String,
        instagram: String,
        tiktok: String,
        email: String,
        password: String,
        phone: String
    ) {
        this.uid = uid
        this.username = username
        this.avatar = avatar
        this.cover = cover
        this.status = status
        this.search = search
        this.facebook = facebook
        this.instagram = instagram
        this.tiktok = tiktok
        this.email = email
        this.password = password
        this.phone = phone
    }

    fun getUID(): String? {
        return uid
    }
    fun setUID(uid: String) {
        this.uid = uid
    }

    fun getUsername(): String? {
        return username
    }
    fun setUsername(username: String) {
        this.username = username
    }

    fun getAvatar(): String? {
        return avatar
    }
    fun setAvatar(avatar: String) {
        this.avatar = avatar
    }

    fun getCover(): String? {
        return cover
    }
    fun setCover(cover: String) {
        this.cover = cover
    }

    fun getStatus(): String? {
        return status
    }
    fun setStatus(status: String) {
        this.status = status
    }

    fun getSearch(): String? {
        return search
    }
    fun setSearch(search: String) {
        this.search = search
    }

    fun getFacebook(): String? {
        return facebook
    }
    fun setFacebook(facebook: String) {
        this.facebook = facebook
    }

    fun getInstagram(): String? {
        return instagram
    }
    fun setInstagram(instagram: String) {
        this.instagram = instagram
    }

    fun getTiktok(): String? {
        return tiktok
    }
    fun setTiktok(tiktok: String) {
        this.tiktok = tiktok
    }

    fun getEmail(): String? {
        return email
    }
    fun setEmail(email: String) {
        this.email = email
    }

    fun getPassword(): String? {
        return password
    }
    fun setPassword(password: String) {
        this.password = password
    }

    fun getPhone(): String? {
        return phone
    }
    fun setPhone(phone: String) {
        this.phone = phone
    }
}