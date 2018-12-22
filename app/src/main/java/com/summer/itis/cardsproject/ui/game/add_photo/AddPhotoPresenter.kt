package com.summer.itis.cardsproject.ui.game.add_photo

import android.system.Os.remove
import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.summer.itis.cardsproject.model.db_dop_models.PhotoItem
import com.summer.itis.cardsproject.repository.RepositoryProvider
import com.summer.itis.cardsproject.repository.RepositoryProvider.Companion.abstractCardRepository
import com.summer.itis.cardsproject.ui.tests.add_test.AddTestView
import com.summer.itis.cardsproject.utils.Const.TAG_LOG
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer

@InjectViewState
class AddPhotoPresenter : MvpPresenter<AddPhotoView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        Log.d(TAG_LOG, "attach listPresenter")
    }

    fun loadPhotos(userId: String) {
        abstractCardRepository
                .findMyAbstractCards(userId)
                .doOnSubscribe(Consumer<Disposable> { viewState.showLoading(it) })
                .doAfterTerminate(Action { viewState.hideLoading() })
                .map { it ->
                    val list: MutableList<PhotoItem> =  ArrayList()
                    for(item in it) {
                        if(item.photoUrl == null) {
                            continue
                        }
                        item.photoUrl?.let{list.add(PhotoItem(it))}
                    }
                    list
                }
                .subscribe({ viewState.changeDataSet(it) }, { viewState.handleError(it) })
    }
}