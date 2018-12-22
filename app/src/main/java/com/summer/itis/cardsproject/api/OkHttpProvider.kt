package com.summer.itis.cardsproject.api

import okhttp3.OkHttpClient

object OkHttpProvider {

    @Volatile
    private var sClient: OkHttpClient? = null

    fun provideClient(): OkHttpClient? {
        var client = sClient
        if (client == null) {
            synchronized(ApiFactory::class.java) {
                client = sClient
                if (client == null) {
                    sClient = buildClient()
                    client = sClient
                }
            }
        }
        return client
    }

    fun recreate() {
        sClient = null
        sClient = buildClient()
    }

    private fun buildClient(): OkHttpClient {
        return OkHttpClient.Builder().build()
    }
}
