package com.example.toolshedd.app

import android.app.Application
import com.example.toolshedd.data.UserInfo

class CustomApplication : Application() {
    private var userInfo: UserInfo? = null

    override fun onCreate() {
        super.onCreate()
    }

    fun setUserInfo(info: UserInfo) {
        this.userInfo = info
    }

    fun getUserInfo(): UserInfo? {
        return userInfo
    }
}