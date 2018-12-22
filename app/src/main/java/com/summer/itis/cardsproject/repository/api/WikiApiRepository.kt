package com.summer.itis.summerproject.repository.api


import com.summer.itis.summerproject.model.pojo.opensearch.Item
import com.summer.itis.summerproject.model.pojo.query.Page

import io.reactivex.Single

interface WikiApiRepository {

    fun opensearch(query: String): Single<List<Item>>

    fun query(query: String): Single<List<Page>>


    /* @NonNull
    Single<List<Book>> books(String query);

    Single<Book> book(String id);

    Single<List<Book>> loadDefaultBooks();*/
}
