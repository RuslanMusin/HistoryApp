package com.summer.itis.cardsproject.repository.api


import com.summer.itis.cardsproject.api.ApiFactory
import com.summer.itis.cardsproject.model.pojo.opensearch.Item
import com.summer.itis.cardsproject.model.pojo.opensearch.SearchSuggestion
import com.summer.itis.cardsproject.model.pojo.opensearch.Section
import com.summer.itis.cardsproject.model.pojo.query.Api
import com.summer.itis.cardsproject.model.pojo.query.Page
import com.summer.itis.cardsproject.model.pojo.query.Pages
import com.summer.itis.cardsproject.model.pojo.query.Query
import com.summer.itis.cardsproject.utils.RxUtils

import io.reactivex.Single

import com.summer.itis.cardsproject.utils.Const.ACTION_QUERY
import com.summer.itis.cardsproject.utils.Const.ACTION_SEARCH
import com.summer.itis.cardsproject.utils.Const.EXINTRO
import com.summer.itis.cardsproject.utils.Const.EXPLAINTEXT
import com.summer.itis.cardsproject.utils.Const.FORMAT
import com.summer.itis.cardsproject.utils.Const.NAMESPACE
import com.summer.itis.cardsproject.utils.Const.PILICENSE
import com.summer.itis.cardsproject.utils.Const.PIPROP
import com.summer.itis.cardsproject.utils.Const.PROP

class WikiApiRepositoryImpl : WikiApiRepository {

    override fun opensearch(query: String): Single<List<Item>> {
        return ApiFactory.wikiService
                .opensearch(FORMAT, ACTION_SEARCH, query, NAMESPACE)
                .map<Section>(SearchSuggestion::section)
                .map<List<Item>>(Section::items)
                .compose(RxUtils.asyncSingle())
    }

    override fun query(query: String): Single<List<Page>> {
        return ApiFactory.wikiService
                .query(FORMAT, ACTION_QUERY, PROP, EXINTRO, EXPLAINTEXT, PIPROP, PILICENSE, query)
                .map<Query>(Api::query)
                .map<Pages>(Query::pages)
                .map<List<Page>>(Pages::pages)
                .compose(RxUtils.asyncSingle())
    }
}
