package com.summer.itis.cardsproject.ui.start.registration

import android.text.TextUtils
import android.util.Log
import android.widget.Toast


import com.google.firebase.auth.FirebaseUser
import com.summer.itis.cardsproject.R
import com.summer.itis.cardsproject.model.User
import com.summer.itis.cardsproject.repository.RepositoryProvider
import com.summer.itis.cardsproject.utils.AppHelper
import com.summer.itis.cardsproject.utils.Const
import com.summer.itis.cardsproject.utils.Const.AVATAR

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

import com.summer.itis.cardsproject.utils.Const.TAG_LOG

class RegistrationPresenter(private val regView: RegistrationActivity) {

    internal var myFormat = "dd.MM.yyyy" //In which you need put here
    internal var sdf = SimpleDateFormat(myFormat, Locale.getDefault())

    internal fun createAccount(email: String, password: String) {
        Log.d(TAG_LOG, "createAccount:$email")
        if (!validateForm()) {
            return
        }

        regView.showProgressDialog()

        regView.fireAuth!!.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(regView) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG_LOG, "createUserWithEmail:success")
                        val user = regView.fireAuth!!.currentUser
                        createInDatabase(user!!)
                        updateUI(user)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG_LOG, "createUserWithEmail:failure", task.exception)
                        Toast.makeText(regView, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                        updateUI(null)
                    }
                    regView.hideProgressDialog()
                }
    }

    private fun createInDatabase(firebaseUser: FirebaseUser) {
        val user = User()

        val username = regView.etUsername!!.text.toString()
        val email = regView.etEmail!!.text.toString()

        user.email = email
        user.username = username
        user.lowerUsername = username.toLowerCase()

        user.id = firebaseUser.uid

        user.isStandartPhoto = regView.isStandartPhoto

       /* val uri = regView.imageUri
        val path: String
        if (uri != null) {
            path = (IMAGE_START_PATH + user.id + SEP
                    + uri.lastPathSegment)
        } else {
            path = STUB_PATH
        }
*/
        user.photoUrl = regView.photoUrl

        regView.user = user

        if (!regView.isStandartPhoto) {
            user.photoUrl = (Const.IMAGE_START_PATH + user.id + Const.SEP
                    + AVATAR)
            val childRef = AppHelper.storageReference.child(user.photoUrl!!)

            //uploading the image
            val uploadTask = childRef.putFile(regView.imageUri!!)

            uploadTask.addOnSuccessListener { Toast.makeText(regView, "Upload successful", Toast.LENGTH_SHORT).show() }.addOnFailureListener { e ->
                //                pd.dismiss();
                Toast.makeText(regView, "Upload Failed -> $e", Toast.LENGTH_SHORT).show()
            }
        }

        RepositoryProvider.userRepository?.createUser(user)
    }

    private fun validateForm(): Boolean {
        var valid = true

        val email = regView.etEmail!!.text.toString()
        if (TextUtils.isEmpty(email)) {
            regView.tiEmail!!.error = regView.getString(R.string.enter_correct_name)
            valid = false
        } else {
            regView.tiEmail!!.error = null
        }

        val password = regView.etPassword!!.text.toString()
        if (TextUtils.isEmpty(password)) {
            regView.tiPassword!!.error = regView.getString(R.string.enter_correct_password)
            valid = false
        } else {
            regView.tiPassword!!.error = null
        }

        return valid
    }

    private fun updateUI(firebaseUser: FirebaseUser?) {
        regView.hideProgressDialog()
        if (firebaseUser != null) {
            regView.user?.let {
                AppHelper.currentUser = it
                AppHelper.userInSession = true
            }
            regView.goToBookList()
        }
    }

    fun formatDate(calendar: Calendar): String {
        return sdf.format(calendar.time)
    }
}
