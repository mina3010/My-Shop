package com.mina_myshop.myshop.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.mina_myshop.myshop.R
import com.mina_myshop.myshop.firestore.FirestoreClass
import com.mina_myshop.myshop.models.User
import com.mina_myshop.myshop.ui.Registration.LoginActivity
import com.mina_myshop.myshop.utils.Constants
import com.mina_myshop.myshop.utils.GlideLoader
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : BaseActivity() {
    private lateinit var mUserDetails: User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        setupActionBar()

        btn_logout_setting.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this@SettingActivity,LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        btn_edit.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this@SettingActivity,UserProfileActivity::class.java)
            intent.putExtra(Constants.EXTRA_USER_DETAILS,mUserDetails)
            startActivity(intent)
        }

        ed_address_setting.setOnClickListener {  }
    }

    private fun setupActionBar() {
        setSupportActionBar(toolbar_setting)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back)
        }

        toolbar_setting.setNavigationOnClickListener { onBackPressed() }
    }

    private fun getUserDetails(){
        showDialog("please wait ...")
        FirestoreClass().getUserDetails(this)
    }

    fun userDetailsSuccess(user: User){
        mUserDetails = user
        hideDialog()
        GlideLoader(this).loadImage(user.image,iv_userImg);
        txt_name_setting.text = user.fullname
        txt_gender_setting.text = user.gender
        txt_email_setting.text = user.email
        txt_phone_setting.text = user.phoneNumber
        txt_address_setting
    }

    override fun onResume() {
        super.onResume()
        getUserDetails()
    }
}