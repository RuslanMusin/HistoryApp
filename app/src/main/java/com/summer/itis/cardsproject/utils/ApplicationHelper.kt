/*
 * Copyright 2017 Rozdoum
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.summer.itis.summerproject.utils

import android.content.Context
import android.util.Log
import android.widget.ImageView

import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.summer.itis.summerproject.Application
import com.summer.itis.summerproject.model.User
import com.summer.itis.summerproject.repository.RepositoryProvider
import com.summer.itis.summerproject.repository.json.UserRepository
import com.summer.itis.summerproject.ui.start.login.LoginActivity

import com.summer.itis.summerproject.utils.Const.TAG_LOG
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import android.util.TypedValue
import com.summer.itis.summerproject.ui.member.member_item.PersonalActivity
import com.summer.itis.summerproject.utils.Const.STUB_PATH
import android.app.Activity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.summer.itis.summerproject.utils.Const.MAX_LENGTH
import com.summer.itis.summerproject.utils.Const.MORE_TEXT
import com.summer.itis.summerproject.utils.Const.OFFLINE_STATUS


//ОСНОВНОЙ КЛАСС HELPER приложения. ОТСЮДА БЕРЕМ ТЕКУЩЕГО ЮЗЕРА ИЗ БД, ГРУЗИМ ФОТКУ ЮЗЕРА В ПРОФИЛЬ,
//ПОЛУЧАЕМ ССЫЛКУ НА ПУТЬ ФАЙЛОГО ХРАНИЛИЩА И СОЗДАЕМ СЕССИЮ. ПОКА ТАК ПУСТЬ БУДЕТ
class ApplicationHelper {

    companion object {

        lateinit var currentUser: User

        var userInSession: Boolean = false

        var userStatus: String = OFFLINE_STATUS

        var onlineFunction: (() -> Unit)? = null

        var offlineFunction: (() -> Unit)? = null

        val storageReference: StorageReference
            get() = FirebaseStorage.getInstance().reference

        fun loadUserPhoto(photoView: ImageView) {
            if(!currentUser?.photoUrl.equals(STUB_PATH)) {
                val storageReference = currentUser!!.photoUrl?.let { FirebaseStorage.getInstance().reference.child(it) }

                Glide.with(photoView.context)
                        .load(storageReference)
                        .into(photoView)
            }
        }


        fun initUserState(application: Application) {
            val authListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
                val user = firebaseAuth.currentUser
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    Log.d(TAG_LOG, "logout")
                    LoginActivity.start(application)
                } else {
                    Log.d(TAG_LOG, "try to login")
                    val reference = RepositoryProvider.userRepository?.readUser(UserRepository.currentId)
                    reference?.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val user = dataSnapshot.getValue(User::class.java)
                            user?.let {
                                currentUser = it
                                userInSession = true
                            }
                            Log.d(TAG_LOG,"user in session = ${currentUser?.username}")
                            LoginActivity.start(application.applicationContext)
                            PersonalActivity.start(application.applicationContext, currentUser)
                        }

                        override fun onCancelled(databaseError: DatabaseError) {

                        }
                    })
                }
            }
        }

        fun readFileFromAssets(fileName: String, context: Context): List<String> {
            var reader: BufferedReader? = null
            var names: MutableList<String> = ArrayList()
            try {
                reader = BufferedReader(
                        InputStreamReader(context.assets.open(fileName), "UTF-8"))
                var mLine: String? = reader.readLine()
                while (mLine != null && !"".equals(mLine)) {
                    names.add(mLine)
                    mLine = reader.readLine()
                }
                return names
            } catch (e: IOException) {
                //log the exception
            } finally {
                if (reader != null) {
                    try {
                        reader.close()
                    } catch (e: IOException) {
                        //log the exception
                    }

                }
            }
            return names
        }

        fun convertDpToPx(dp: Float, context: Context): Int {
            val r = context.getResources()
            val px = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    dp,
                    r.getDisplayMetrics()
            ).toInt()
            return px
        }

        fun hideKeyboardFrom(context: Context, view: View) {
            Log.d(TAG_LOG,"hide keyboard")
            val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0)
        }

        fun showKeyboard(context: Context, editText: EditText) {
            val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        }

        fun cutLongDescription(description: String, maxLength: Int): String {
            return if (description.length < MAX_LENGTH) {
                description
            } else {
                description.substring(0, MAX_LENGTH - MORE_TEXT.length) + MORE_TEXT
            }
        }
    }
}
