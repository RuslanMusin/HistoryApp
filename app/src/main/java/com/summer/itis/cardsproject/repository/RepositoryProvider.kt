package com.summer.itis.cardsproject.repository

import android.util.Log
import com.summer.itis.cardsproject.repository.api.WikiApiRepository
import com.summer.itis.cardsproject.repository.api.WikiApiRepositoryImpl
import com.summer.itis.cardsproject.repository.json.*
import com.summer.itis.cardsproject.utils.Const.TAG_LOG


class RepositoryProvider {

    companion object {
        val testRepository: TestRepository by lazy {
            TestRepository()
        }

        val testCommentRepository: TestCommentRepository by lazy {
            TestCommentRepository()
        }

        val cardRepository: CardRepository by lazy {
            CardRepository()
        }

        val cardCommentRepository: CardCommentRepository by lazy {
            CardCommentRepository()
        }

        val userRepository: UserRepository by lazy {
            UserRepository()
        }


        val gamesRepository: GamesRepository by lazy {
            GamesRepository()
        }

        val abstractCardRepository: AbstractCardRepository by lazy {
            AbstractCardRepository()
        }

        val wikiApiRepository: WikiApiRepository by lazy {
            Log.d(TAG_LOG,"wikiRepo")
            WikiApiRepositoryImpl()
        }


    }
}