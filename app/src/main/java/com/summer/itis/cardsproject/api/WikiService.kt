package com.summer.itis.cardsproject.api


import com.summer.itis.cardsproject.model.pojo.opensearch.SearchSuggestion
import com.summer.itis.cardsproject.model.pojo.query.Api

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface WikiService {

    @GET("./")
    fun query(@Query("format") format: String,
              @Query("action") action: String,
              @Query("prop") prop: String,
              @Query("exintro") exintro: String,
              @Query("explaintext") explaintext: String,
              @Query("piprop") piprop: String,
              @Query("pilicense") pilicense: String,
              @Query("titles") titles: String
    ): Single<Api>

    @GET("./")
    fun opensearch(@Query("format") format: String,
                   @Query("action") action: String,
                   @Query("search") search: String,
                   @Query("namespace") namespace: String
    ): Single<SearchSuggestion>

    //    format=json&action=query&prop=extracts&exintro=&explaintext=&titles=Толстой,%20Лев%20Николаевич&formatversion=2

    //format=json&action=opensearch&format=jsonfm&search=%D0%A2%D0%BE%D0%BB%D1%81%D1%82%D0%BE%D0%B9&namespace=0&suggest=1&redirects=resolve&utf8=1&formatversion=2
}
