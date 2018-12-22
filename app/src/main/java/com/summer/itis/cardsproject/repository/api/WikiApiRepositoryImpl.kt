package com.summer.itis.summerproject.repository.api


import com.summer.itis.summerproject.api.ApiFactory
import com.summer.itis.summerproject.model.pojo.opensearch.Item
import com.summer.itis.summerproject.model.pojo.opensearch.SearchSuggestion
import com.summer.itis.summerproject.model.pojo.opensearch.Section
import com.summer.itis.summerproject.model.pojo.query.Api
import com.summer.itis.summerproject.model.pojo.query.Page
import com.summer.itis.summerproject.model.pojo.query.Pages
import com.summer.itis.summerproject.model.pojo.query.Query
import com.summer.itis.summerproject.utils.RxUtils

import io.reactivex.Single

import com.summer.itis.summerproject.utils.Const.ACTION_QUERY
import com.summer.itis.summerproject.utils.Const.ACTION_SEARCH
import com.summer.itis.summerproject.utils.Const.EXINTRO
import com.summer.itis.summerproject.utils.Const.EXPLAINTEXT
import com.summer.itis.summerproject.utils.Const.FORMAT
import com.summer.itis.summerproject.utils.Const.NAMESPACE
import com.summer.itis.summerproject.utils.Const.PILICENSE
import com.summer.itis.summerproject.utils.Const.PIPROP
import com.summer.itis.summerproject.utils.Const.PROP

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
