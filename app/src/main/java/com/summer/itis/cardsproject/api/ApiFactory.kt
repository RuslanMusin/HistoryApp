package com.summer.itis.cardsproject.api


import android.util.Log
import com.summer.itis.cardsproject.BuildConfig
import com.summer.itis.cardsproject.repository.json.GamesRepository
import com.summer.itis.cardsproject.utils.Const.TAG_LOG

import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory

class ApiFactory {

    companion object {
        /*@JvmField
        var wikiService: WikiService? = null

        @JvmStatic fun getWikiService(): WikiService? {
            if (wikiService == null) {
                synchronized(ApiFactory::class.java) {
                    if (wikiService == null) {
                        wikiService = buildRetrofit().create(WikiService::class.java)
                    }
                }
            }
            return wikiService
        }
*/

        val wikiService: WikiService by lazy {
            Log.d(TAG_LOG,"build service")
            buildRetrofit().create(WikiService::class.java)
        }

        private fun buildRetrofit(): Retrofit {
            return Retrofit.Builder()
                    .baseUrl(BuildConfig.API_ENDPOINT)
                    .client(OkHttpProvider.provideClient())
                    .addConverterFactory(SimpleXmlConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build()
        }
    }

}
