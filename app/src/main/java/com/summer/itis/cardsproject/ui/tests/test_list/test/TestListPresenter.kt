package com.summer.itis.cardsproject.ui.tests.test_list.test

import android.annotation.SuppressLint
import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.summer.itis.cardsproject.model.Test
import com.summer.itis.cardsproject.model.User
import com.summer.itis.cardsproject.model.UserEpoch
import com.summer.itis.cardsproject.repository.RepositoryProvider
import com.summer.itis.cardsproject.utils.AppHelper
import com.summer.itis.cardsproject.utils.Const.AFTER_TEST
import com.summer.itis.cardsproject.utils.Const.TAG_LOG
import com.summer.itis.cardsproject.utils.RxUtils
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer

@InjectViewState
class TestListPresenter : MvpPresenter<TestListView>() {

    @SuppressLint("CheckResult")
    fun loadOfficialTestsByQUery(query: String) {
        AppHelper.currentUser?.id?.let {
            RepositoryProvider.testRepository
                .findOfficialTestsByQuery(query, it)
                .doOnSubscribe(Consumer<Disposable> { viewState.showLoading(it) })
                .doAfterTerminate(Action { viewState.hideLoading() })
                .subscribe({ viewState.changeDataSet(it) }, { viewState.handleError(it) })
        }
    }

    @SuppressLint("CheckResult")
    fun loadUserTestsByQUery(query: String, userId: String) {
        RepositoryProvider.testRepository!!
                .findUserTestsByQuery(query, userId)
                .doOnSubscribe(Consumer<Disposable> { viewState.showLoading(it) })
                .doAfterTerminate(Action { viewState.hideLoading() })
                .subscribe({ viewState.changeDataSet(it) }, { viewState.handleError(it) })
    }

    @SuppressLint("CheckResult")
    fun loadMyTestsByQUery(query: String, userId: String) {
        RepositoryProvider.testRepository!!
                .findMyTestsByQuery(query, userId)
                .doOnSubscribe({ viewState.showLoading(it) })
                .doAfterTerminate({ viewState.hideLoading() })
                .subscribe({ viewState.changeDataSet(it) }, { viewState.handleError(it) })
    }

    @SuppressLint("CheckResult")
    fun loadUserTests(userId: String) {
        RepositoryProvider.testRepository!!
                .findUserTests(userId)
                .doOnSubscribe(Consumer<Disposable> { viewState.showLoading(it) })
                .doAfterTerminate(Action { viewState.hideLoading() })
                .subscribe({ viewState.changeDataSet(it) }, { viewState.handleError(it) })
    }

    @SuppressLint("CheckResult")
    fun loadMyTests(userId: String) {
        RepositoryProvider.testRepository!!
                .findMyTests(userId)
                .doOnSubscribe(Consumer<Disposable> { viewState.showLoading(it) })
                .doAfterTerminate(Action { viewState.hideLoading() })
                .subscribe({ viewState.changeDataSet(it) }, { viewState.handleError(it) })
    }

    @SuppressLint("CheckResult")
    fun loadOfficialTests() {
        Log.d(TAG_LOG, "load books")
        AppHelper.currentUser?.id?.let {
            RepositoryProvider.testRepository
                .findOfficialTests(it)
                .doOnSubscribe({ viewState.showLoading(it) })
                .subscribe { tests ->
                    sortTest(tests)
                        .doAfterTerminate({ viewState.hideLoading() })
                        .doAfterTerminate({ viewState.setNotLoading() })
                        .subscribe({ viewState.changeDataSet(it) }, { viewState.handleError(it) })
                }

        }
    }

    fun sortTest(tests: List<Test>): Single<List<Test>> {
        val single: Single<List<Test>> = Single.create { e ->
            val finishedTests = findTestCount(tests)
            var isRec = false
            if (finishedTests.size >= 3) {
                isRec = true
            }
            if (isRec) {
                 sortByRec(tests, finishedTests).subscribe { tests ->
                     e.onSuccess(tests)
                 }
            } else {
                sortByKe(tests).subscribe {tests ->
                    e.onSuccess(tests)
                }
            }
        }
        return single.compose(RxUtils.asyncSingle())
    }

    fun sortByRec(tests: List<Test>, finishedTests: MutableList<Test>): Single<List<Test>> {
        val single: Single<List<Test>> = Single.create { e ->
            val notFinished = findNotFinished(tests, finishedTests)
            var i = 1
            for (test in notFinished) {
                findLikes(AppHelper.currentUser, test, notFinished).subscribe { likes ->
                    test.likes = likes
                    if (i == notFinished.size) {
                        val sorted = notFinished.sortedWith(compareBy(Test::likes))
                        e.onSuccess(sorted)
                    }
                }
            }
        }
        return single.compose(RxUtils.asyncSingle())
    }

    fun findLikes(
        currentUser: User,
        test: Test,
        notFinished: MutableList<Test>
    ): Single<Double> {
        val single: Single<Double> = Single.create { e ->
            RepositoryProvider.userRepository.findUsers(test.usersIds).subscribe { users ->
                var sumSim = 0.0
                var i = 1
                for (user in users) {
                    findSim(currentUser, user).subscribe { sim ->
                        sumSim += sim
                        if(i == users.size) {
                            val likes = sumSim / users.size
                            e.onSuccess(likes)
                        }
                        i++
                    }
                }
            }
        }
        return single.compose(RxUtils.asyncSingle())

    }

    fun findSim(currentUser: User, user: User): Single<Double> {
        val single: Single<Double> = Single.create { e ->
            RepositoryProvider.testRepository.findUserFinishedTests(user.id).subscribe { userTests ->
                RepositoryProvider.testRepository.findUserFinishedTests(currentUser.id).subscribe { finishedTests ->
                    val set: MutableSet<Test> = HashSet()
                    set.addAll(userTests)
                    set.addAll(finishedTests)
                    val common = set.size
                    val count = finishedTests.size + userTests.size
                    val sim = common.toDouble() / count
                    e.onSuccess(sim)
                }
            }
        }
        return single.compose(RxUtils.asyncSingle())
    }

    fun findNotFinished(tests: List<Test>, finishedTests: MutableList<Test>): MutableList<Test> {
        val notFinished: MutableList<Test> = ArrayList()
        for(test in tests) {
            if(!finishedTests.contains(test)) {
                notFinished.add(test)
            }
        }
        return notFinished
    }

    fun sortByKe(tests: List<Test>): Single<List<Test>> {
        val single: Single<List<Test>> = Single.create { e ->
            val sortTests: MutableList<Test> = ArrayList()
            RepositoryProvider.userEpochRepository.findUserEpoches(AppHelper.currentUser.id).subscribe { epoches ->
                val sortEps = epoches.sortedWith(compareBy(UserEpoch::ke))
                for (ep in sortEps) {
                    val testPart = findTestByEp(ep, tests)
                    sortTests.addAll(testPart)
                }
                e.onSuccess(sortTests)
            }
        }
        return single.compose(RxUtils.asyncSingle())
    }

    fun findTestByEp(
        userEpoch: UserEpoch,
        tests: List<Test>
    ): MutableList<Test> {
        val part: MutableList<Test> = ArrayList()
        for(test in tests) {
            if(test.epochId.equals(userEpoch.epochId)) {
                part.add(test)
            }
        }
        return part
    }

    fun findTestCount(tests: List<Test>): MutableList<Test> {
        val finishedTests: MutableList<Test> = ArrayList()
        for(test in tests) {
            if(test.testRelation?.equals(AFTER_TEST)!!) {
                finishedTests.add(test)
            }
        }
        return finishedTests
    }

    /*@SuppressLint("CheckResult")
    fun loadNextElements(page: Int) {
        Log.d(TAG, "load books")
        RepositoryProvider.userRepository!!
                .loadDefaultUsers()
                .doOnSubscribe(Consumer<Disposable> { viewState.showLoading(it) })
                .doAfterTerminate(Action { viewState.hideLoading() })
                .doAfterTerminate(Action { viewState.setNotLoading() })
                .subscribe({ this.setReaders(it) }, { viewState.handleError(it) })

    }*/


    fun onItemClick(comics: Test) {
        viewState.showDetails(comics)
    }
}
