package com.summer.itis.summerproject.repository.json

import android.util.Log
import com.google.android.gms.common.util.ArrayUtils.contains
import com.google.firebase.database.*
import com.summer.itis.summerproject.model.*
import com.summer.itis.summerproject.model.db_dop_models.ElementId
import com.summer.itis.summerproject.model.db_dop_models.Relation
import com.summer.itis.summerproject.repository.RepositoryProvider.Companion.abstractCardRepository
import com.summer.itis.summerproject.repository.RepositoryProvider.Companion.cardRepository
import com.summer.itis.summerproject.repository.RepositoryProvider.Companion.testRepository
import com.summer.itis.summerproject.utils.Const
import com.summer.itis.summerproject.utils.Const.ADMIN_ROLE
import com.summer.itis.summerproject.utils.Const.AFTER_TEST
import com.summer.itis.summerproject.utils.Const.BEFORE_TEST
import com.summer.itis.summerproject.utils.Const.LOSE_GAME
import com.summer.itis.summerproject.utils.Const.OFFICIAL_TYPE
import com.summer.itis.summerproject.utils.Const.QUERY_END

import java.util.ArrayList
import java.util.HashMap

import io.reactivex.Single

import com.summer.itis.summerproject.utils.Const.SEP
import com.summer.itis.summerproject.utils.Const.TAG_LOG
import com.summer.itis.summerproject.utils.Const.USER_TYPE
import com.summer.itis.summerproject.utils.Const.WIN_GAME
import com.summer.itis.summerproject.utils.RxUtils
import io.reactivex.Observable
import java.util.regex.Pattern

class TestRepository {


    private val databaseReference: DatabaseReference

    private val TABLE_NAME = "tests"
    private val USERS_TESTS = "users_tests"
    val USERS_CARDS = "users_cards"
    val USERS_ABSTRACT_CARDS = "users_abstract_cards"

    private val TEST_QUESTIONS = "test_questions"
    private val TEST_CARDS = "test_cards"
    private val ABSTRACT_CARDS = "abstract_cards"


    private val FIELD_ID = "id"
    private val FIELD_TITLE = "title"
    private val FIELD_LOWER_TITLE = "lowerTitle"
    private val FIELD_CARD_ID = "cardId"
    private val FIELD_AUTHOR_ID = "authorId"
    private val FIELD_AUTHOR_NAME = "authorName"
    private val FIELD_QUESTIONS = "questions"
    private val FIELD_DESC = "desc"
    private val FIELD_TYPE = "type"
    private val FIELD_IMAGE_URL = "imageUrl"


    private val FIELD_RELATION = "relation"


    init {
        this.databaseReference = FirebaseDatabase.getInstance().reference.child(TABLE_NAME)
    }

    fun toMap(test: Test): Map<String, Any?> {
        val result = HashMap<String, Any?>()

        result[FIELD_ID] = test.id
        result[FIELD_DESC] = test.desc
        result[FIELD_TITLE] = test.title
        result[FIELD_LOWER_TITLE] = test.lowerTitle
        result[FIELD_AUTHOR_ID] = test.authorId
        result[FIELD_AUTHOR_NAME] = test.authorName
        result[FIELD_CARD_ID] = test.cardId
        result[FIELD_QUESTIONS] = test.questions
        result[FIELD_TYPE] = test.type
        result[FIELD_IMAGE_URL] = test.imageUrl



        return result
    }

    fun toMap(id: String?,relation: String?): Map<String, Any?> {
        val result = HashMap<String, Any?>()
        result[FIELD_ID] = id
        result[FIELD_RELATION] = relation
        return result
    }

    fun changeStatus(testId: String, userId: String, relation: String): Single<Relation> {
        Log.d(TAG_LOG,"change test status")
        val query: Query = databaseReference.root.child(USERS_TESTS).child(userId).child(testId)
        val single: Single<Relation> = Single.create { e ->
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(shapshot: DataSnapshot) {
                    var testRelation: Relation? = shapshot.getValue(Relation::class.java)
                    if(testRelation != null) {
                        Log.d(TAG_LOG,"rel not null")
                        when  {
                            WIN_GAME.equals(testRelation.relation) -> {
                                testRelation.relBefore = WIN_GAME
                                if(relation.equals(WIN_GAME)) {
                                    testRelation.relation = WIN_GAME
                                }
                                if(relation.equals(LOSE_GAME)) {
                                    testRelation.relation = BEFORE_TEST
                                }
                                if(relation.equals(AFTER_TEST)) {
                                    testRelation.relation = AFTER_TEST
                                }
                            }
                            AFTER_TEST.equals(testRelation.relation) -> {
                                testRelation.relBefore = AFTER_TEST
                                if(relation.equals(WIN_GAME)) {
                                    testRelation.relation = AFTER_TEST
                                }
                                if(relation.equals(LOSE_GAME)) {
                                    testRelation.relation = LOSE_GAME
                                }
                                if(relation.equals(AFTER_TEST)) {
                                    testRelation.relation = AFTER_TEST
                                }
                            }
                            LOSE_GAME.equals(testRelation.relation) -> {
                                testRelation.relBefore = LOSE_GAME
                                if(relation.equals(WIN_GAME)) {
                                    testRelation.relation = WIN_GAME
                                }
                                if(relation.equals(LOSE_GAME)) {
                                    testRelation.relation = LOSE_GAME
                                }
                                if(relation.equals(AFTER_TEST)) {
                                    testRelation.relation = AFTER_TEST
                                }
                            }
                        }
                    } else {
                        Log.d(TAG_LOG,"rel == null")
                        testRelation = Relation()
                        testRelation.relBefore = BEFORE_TEST
                    }
                    e.onSuccess(testRelation)

                }

            })
        }
        return single.compose(RxUtils.asyncSingle())
    }

    fun createTest(test: Test, user: User): Single<Boolean> {

        val single: Single<Boolean> = Single.create { e ->
            val card = test.card
            val abstractCard = card?.abstractCard
            /* if(type.equals("read")) {
             Log.d(TAG_LOG,"read")
             abstractCardRepository.findAbstractCard(test, user,this)
         } else {*/
            abstractCardRepository
                    .findAbstractCardId(abstractCard?.wikiUrl)
                    .subscribe { cardId ->
                        val childUpdates = HashMap<String, Any>()
                        Log.d(TAG_LOG, "create")
                        val crossingKey = databaseReference.push().key
                        test.id = crossingKey
                        if (ADMIN_ROLE.equals(user.role)) {
                            test.type = OFFICIAL_TYPE
                            card?.type = OFFICIAL_TYPE
                        } else {
                            test.type = USER_TYPE
                            card?.type = USER_TYPE
                        }

                        Log.d(TAG_LOG, "abstract")
                        card?.testId = test.id
                        if (cardId.equals("null")) {
                            Log.d(TAG_LOG, "createAbs")
                            val abstractCardValues = abstractCardRepository.toMap(abstractCard)
                            childUpdates[ABSTRACT_CARDS + SEP + abstractCard?.id] = abstractCardValues

                        } else {
                            abstractCard?.id = cardId
                            Log.d(TAG_LOG, "no create abs")
                        }
                        card?.cardId = abstractCard?.id

                        Log.d(TAG_LOG, "after abstract")
                        val cardRepository = CardRepository()
                        val crossingIdValues = cardRepository.toMap(card)
                        childUpdates[TEST_CARDS + SEP + card?.id] = crossingIdValues

                        test.lowerTitle = test.title?.toLowerCase()
                        test.authorId = user.id
                        test.authorName = user.username
                        test.cardId = card?.id
                        test.imageUrl = abstractCard?.photoUrl

                        val crossingValues = toMap(test)

                        childUpdates["$TABLE_NAME/$crossingKey"] = crossingValues

                        databaseReference.root.updateChildren(childUpdates)

                        e.onSuccess(true)
                    }
        }

        return single.compose(RxUtils.asyncSingle())

    }


    fun finishTest(test: Test, user: User): Single<Boolean> {
        val childUpdates = HashMap<String, Any?>()
        val card = test.card
        val single : Single<Boolean> =  Single.create { e ->
            test.id?.let {
                user.id?.let { userId ->
                    changeStatus(it, userId, AFTER_TEST).subscribe { relation ->
                        if (!relation.relBefore.equals(AFTER_TEST)) {
                            card!!.cardId!!.let {
                                user.id!!.let { userId ->
                                    cardRepository?.findMyAbstractCardStates(it, userId)
                                            ?.subscribe { winnerCards ->
                                                Log.d(TAG_LOG,"add card after test")
                                                if (winnerCards.size == 0) {
                                                    Log.d(TAG_LOG,"add abstract card")
                                                    val addAbstractCardValues = abstractCardRepository.toMapId(it)
                                                    childUpdates[USERS_ABSTRACT_CARDS + Const.SEP + userId + SEP + it] = addAbstractCardValues
                                                }
                                                val addCardValues = cardRepository?.toMapId(card.id)
                                                childUpdates[USERS_CARDS + Const.SEP + userId + SEP + card.id] = addCardValues
                                                val addTestValues = testRepository?.toMap(test.id, AFTER_TEST)
                                                childUpdates[USERS_TESTS + Const.SEP + userId + SEP + test.id] = addTestValues
                                                databaseReference.root.updateChildren(childUpdates)
                                                e.onSuccess(true)
                                            }
                                }
                            }
                        }
                    }


                }
            }
        }
        return single.compose(RxUtils.asyncSingle())
    }

    fun readTest(testId: String?): Single<Test> {
        var test: Test?
        val query: Query = databaseReference.child(testId!!)
        val single : Single<Test> =  Single.create { e ->
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    test = dataSnapshot.getValue(Test::class.java)
                   e.onSuccess(test!!)
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })

        }
        return single.compose(RxUtils.asyncSingle())
    }

    fun findTests(cardsIds: List<String>): Single<List<Test>> {
        val single: Single<List<Test>> = Single.create{e ->
            Observable
                    .fromIterable(cardsIds)
                    .flatMap {
                        this.readTest(it).toObservable()
                    }
                    .toList()
                    .subscribe{cards ->
                        e.onSuccess(cards)
                    }
        }
        return single.compose(RxUtils.asyncSingle())
    }

    fun findOfficialTests(userId: String): Single<List<Test>> {
        return findTestsByType(userId, OFFICIAL_TYPE)
    }

    fun findUserTests(userId: String): Single<List<Test>> {
        return findTestsByType(userId, USER_TYPE)
    }

    fun findOfficialTestsByQuery(query: String, userId: String): Single<List<Test>> {
        return findTestsByTypeByQuery(query, userId, OFFICIAL_TYPE)
    }

    fun findUserTestsByQuery(query: String, userId: String): Single<List<Test>> {
        return findTestsByTypeByQuery(query, userId, USER_TYPE)
    }

    fun findTestsByType(userId: String, type: String): Single<List<Test>> {
        var query: Query = databaseReference.root.child(USERS_TESTS).child(userId)
        val single: Single<List<Test>> = Single.create { e ->
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val relations = HashMap<String,Relation>()
                    for (snapshot in dataSnapshot.children) {
                        val elementId = snapshot.getValue(Relation::class.java)
                        elementId?.let {
                            if(LOSE_GAME.equals(it.relation) || AFTER_TEST.equals(it.relation)) {
                                relations[it.id] = it
                            }
                        }
                    }
                    query = databaseReference
                    query.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val cards: MutableList<Test> = ArrayList()
                            for(snapshot in dataSnapshot.children) {
                                val card = snapshot.getValue(Test::class.java)
                                if(card?.type.equals(type) && !card?.authorId.equals(userId)) {
                                    if (relations.keys.contains(card?.id)) {
                                        if(LOSE_GAME.equals(relations[card?.id]?.relation) || AFTER_TEST.equals(relations[card?.id]?.relation)) {
                                            card?.testDone = true

                                        }
                                        card?.testRelation = relations[card?.id]
                                    } else {
                                        card?.testRelation = Relation()
                                    }
                                    card?.let { cards.add(it) }
                                }

                            }
                           e.onSuccess(cards)
                        }

                        override fun onCancelled(databaseError: DatabaseError) {}
                    })

                }
                override fun onCancelled(databaseError: DatabaseError) {}
            })


        }
        return single.compose(RxUtils.asyncSingle())
    }

    fun findMyTests(userId: String): Single<List<Test>> {
        return Single.create { e ->
            val query: Query = databaseReference.orderByChild(FIELD_AUTHOR_ID).equalTo(userId)
            query.addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val tests: MutableList<Test> = ArrayList()
                    for (snapshot in dataSnapshot.children) {
                        val test = snapshot.getValue(Test::class.java)
                        test?.let {
                            tests.add(test)
                        }
                    }
                    e.onSuccess(tests)

                }

                override fun onCancelled(p0: DatabaseError) {
                }
            })
        }
    }

    fun findTestsByTypeByQuery(userQuery: String, userId: String, type: String): Single<List<Test>> {
        var query: Query = databaseReference.root.child(USERS_TESTS).child(userId)
        val single: Single<List<Test>> = Single.create { e ->
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val elementIds: MutableList<String> = ArrayList()
                    for (snapshot in dataSnapshot.children) {
                        val elementId = snapshot.getValue(Relation::class.java)
                        elementId?.let {
                            if(LOSE_GAME.equals(it.relation) || AFTER_TEST.equals(it.relation)) {
                                elementIds.add(it.id)
                            }
                        }
                    }
                    val queryPart = userQuery.toLowerCase()
                    query = databaseReference.orderByChild(FIELD_LOWER_TITLE).startAt(queryPart).endAt(queryPart + QUERY_END)
                    query.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val cards: MutableList<Test> = ArrayList()
                            for(snapshot in dataSnapshot.children) {
                                val card = snapshot.getValue(Test::class.java)
                                if(card?.type.equals(type) && !card?.authorId.equals(userId)) {
                                    if (elementIds.contains(card?.id)) {
                                        card?.testDone = true
                                    }
                                    card?.let { cards.add(it) }
                                }

                            }
                            e.onSuccess(cards)
                        }

                        override fun onCancelled(databaseError: DatabaseError) {}
                    })

                }
                override fun onCancelled(databaseError: DatabaseError) {}
            })


        }
        return single.compose(RxUtils.asyncSingle())
    }

    fun findMyTestsByQuery(queryPart: String, userId: String): Single<List<Test>> {
        return Single.create { e ->
            val pattern: Pattern = Pattern.compile("${queryPart.toLowerCase()}.*")
            val query: Query = databaseReference.orderByChild(FIELD_AUTHOR_ID).equalTo(userId)
            query.addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val tests: MutableList<Test> = ArrayList()
                    for (snapshot in dataSnapshot.children) {
                        val test = snapshot.getValue(Test::class.java)
                        test?.let {
                            if(pattern.matcher(test.lowerTitle).matches()) {
                                tests.add(test)
                            }
                        }
                    }
                    e.onSuccess(tests)

                }

                override fun onCancelled(p0: DatabaseError) {
                }
            })
        }
    }



    fun deleteCrossing(pointId: String) {
        databaseReference.child(pointId).removeValue()
    }

    /* public Single<Map<String,Query>> loadByBook(Book book) {
        Map<String, Query> queryMap = new HashMap<>();
        DatabaseReference reference = databaseReference.getRoot().child(BOOK_CROSSINGS);
        Query queryId =  reference.child(book.getId());

        reference = databaseReference.getRoot().child(CROSSINGS_QUERY);
        Query queryName;
        List<String> authors = book.getAuthors();
        String bookName = book.getName().trim();
        if(authors.size() > 1) {
            queryName =  reference.orderByChild(FIELD_BOOK_NAME).startAt(bookName).endAt(bookName + QUERY_END);
        } else {
            String[] parts = authors.get(0).trim().split("\\s+");
            String lastPart = parts[parts.length-1];
            queryName = reference.orderByChild(FIELD_BOOK_NAME).startAt(bookName).endAt(bookName + QUERY_END);
//            queryName = queryName.orderByChild(FIELD_BOOK_AUTHOR).endAt(lastPart);
        }
        Log.d(TAG_LOG, "query is null ? " + (queryName == null));
        Log.d(TAG_LOG, "queryId is null ? " + (queryId == null));

        queryMap.put("queryId",queryId);
        queryMap.put("queryName",queryName);
        return Single.just(queryMap);
    }

    public Single<Query> loadByQuery(String query) {
        Log.d(TAG_LOG, "load cross by query = " + query);
        String queryPart = query.trim();
        DatabaseReference reference = databaseReference.getRoot().child(CROSSINGS_QUERY);
        Query queryName = reference.orderByChild(FIELD_BOOK_NAME).startAt(queryPart).endAt(queryPart + QUERY_END);
        Log.d(TAG_LOG, "query is null ? " + (queryName == null));
        return Single.just(queryName);
    }*/

    fun loadByIds(crossingsIds: List<String>): Single<List<Query>> {
        val queries = ArrayList<Query>()
        for (id in crossingsIds) {
            queries.add(databaseReference.child(id))
        }
        return Single.just(queries)

    }
}
