package com.summer.itis.summerproject.ui.start.registration

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v7.widget.AppCompatButton
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.bumptech.glide.Glide


import com.google.firebase.auth.FirebaseAuth
import com.summer.itis.summerproject.R
import com.summer.itis.summerproject.model.User
import com.summer.itis.summerproject.model.db_dop_models.PhotoItem
import com.summer.itis.summerproject.ui.base.BaseActivity
import com.summer.itis.summerproject.ui.cards.add_card.AddCardActivity
import com.summer.itis.summerproject.ui.game.add_photo.AddPhotoActivity
import com.summer.itis.summerproject.ui.member.member_item.PersonalActivity
import com.summer.itis.summerproject.ui.start.login.LoginActivity
import com.summer.itis.summerproject.utils.Const
import com.summer.itis.summerproject.utils.Const.BOT_ID
import com.summer.itis.summerproject.utils.Const.STUB_PATH
import com.summer.itis.summerproject.utils.Const.USER_ID
import kotlinx.android.synthetic.main.activity_registration.*
import kotlinx.android.synthetic.main.dialog_pick_image.*
import java.io.InputStream


class RegistrationActivity : BaseActivity(), View.OnClickListener {

    var btnRegistrNewReader: AppCompatButton? = null
    var tvAddPhoto: TextView? = null
    var liAddPhoto: LinearLayout? = null
    var tvLogin: TextView? = null
    var tiEmail: TextInputLayout? = null
    var tiPassword: TextInputLayout? = null
    var tiUsername: TextInputLayout? = null
    var etEmail: EditText? = null
    var etPassword: EditText? = null
    var etUsername: EditText? = null

    var user: User? = null
    //set-get

    var fireAuth: FirebaseAuth? = null

    var imageUri: Uri? = null

    var photoUrl: String = STUB_PATH
    var isStandartPhoto: Boolean = true

    lateinit var photoDialog: MaterialDialog

    private var presenter: RegistrationPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        etEmail = findViewById(R.id.et_email)
        etPassword = findViewById(R.id.et_password)
        etUsername = findViewById(R.id.et_username)

        tiEmail = findViewById(R.id.ti_email)
        tiPassword = findViewById(R.id.ti_password)
        tiUsername = findViewById(R.id.ti_username)

        btnRegistrNewReader = findViewById(R.id.btn_sign_up)
        liAddPhoto = findViewById(R.id.li_add_photo)
        tvAddPhoto = findViewById(R.id.tv_add_photo)
        tvLogin = findViewById(R.id.link_login)

        btnRegistrNewReader!!.setOnClickListener(this)
        liAddPhoto!!.setOnClickListener(this)
        tvLogin!!.setOnClickListener(this)

        presenter = RegistrationPresenter(this)

        fireAuth = FirebaseAuth.getInstance()

    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_sign_up -> {
                val username = etEmail!!.text.toString()
                val password = etPassword!!.text.toString()
                presenter!!.createAccount(username, password)
            }

            R.id.li_add_photo -> addPhoto()

            R.id.link_login -> LoginActivity.start(this)
        }
    }

    private fun addPhoto() {

    /*    val photoPickerIntent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        photoPickerIntent.addCategory(Intent.CATEGORY_OPENABLE)
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent, GALLERY_PHOTO)*/
        */
        photoDialog = MaterialDialog.Builder(this)
                .customView(R.layout.dialog_pick_image, false).build()

        photoDialog.btn_choose_gallery.setOnClickListener{ showGallery() }
        photoDialog.btn_choose_standart.setOnClickListener{ showStandarts()}

        photoDialog.show()

    }

    override fun onActivityResult(reqCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(reqCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if(reqCode == GALLERY_PHOTO) {
                imageUri = data.data
                isStandartPhoto = false
                val imageStream: InputStream = getContentResolver().openInputStream(imageUri)
                val selectedImage: Bitmap = BitmapFactory.decodeStream(imageStream)
                iv_cover.setImageBitmap(selectedImage)
            }
            if(reqCode == STANDART_PHOTO) {
                val photoItem = Const.gsonConverter.fromJson(data.getStringExtra(AddCardActivity.ITEM_JSON), PhotoItem::class.java)
                photoUrl = photoItem.photoUrl
                Glide.with(this)
                        .load(photoUrl)
                        .into(iv_cover)
            }
            tvAddPhoto!!.setText(R.string.photo_uploaded)
        } else {
            imageUri = null
            Toast.makeText(this@RegistrationActivity, "You haven't picked Image", Toast.LENGTH_LONG).show()
        }
    }

    fun showGallery() {
        photoDialog.hide()
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY_PHOTO)
    }

    fun showStandarts() {
        photoDialog.hide()
        val intent = Intent(this, AddPhotoActivity::class.java)
        intent.putExtra(USER_ID, BOT_ID)
        startActivityForResult(intent, STANDART_PHOTO)
    }

    internal fun goToBookList() {
        PersonalActivity.start(this)
    }

    companion object {

        private val GALLERY_PHOTO = 0

        private val STANDART_PHOTO = 1

        fun start(activity: Activity) {
            val intent = Intent(activity, RegistrationActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            activity.startActivity(intent)
        }
    }
}
