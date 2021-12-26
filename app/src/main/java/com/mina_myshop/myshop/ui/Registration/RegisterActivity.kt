package com.mina_myshop.myshop.ui.Registration

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.mina_myshop.myshop.R
import com.mina_myshop.myshop.firestore.FirestoreClass
import com.mina_myshop.myshop.models.User
import com.mina_myshop.myshop.ui.BaseActivity
import com.mina_myshop.myshop.ui.DashboardActivity
import kotlinx.android.synthetic.main.activity_register.*
import kotlin.math.E

class RegisterActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        tv_register_login.setOnClickListener {
            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
            finish()
        }

        btn_register.setOnClickListener {
            registerUser()
        }
    }

    private fun validateRegisterDetails(): Boolean {
        if (ed_register_userName.text.toString().trim()
                .isEmpty() || ed_register_email.text.toString().trim().isEmpty() ||
            ed_register_password.text.toString().trim()
                .isEmpty() || ed_register_number.text.toString().trim().isEmpty()
        ) {

            if (ed_register_userName.text.toString().trim().isEmpty()) {
                showErrorSnackBar(resources.getString(R.string.err_name), true)
                return false
            }
            if (ed_register_email.text.toString().trim().isEmpty()) {
                showErrorSnackBar(resources.getString(R.string.err_email), true)
                return false
            }
            if (ed_register_password.text.toString().trim().isEmpty()) {
                showErrorSnackBar(resources.getString(R.string.err_password), true)
                return false
            }
            if (ed_register_password.text.toString().trim().length <= 6) {
                showErrorSnackBar(resources.getString(R.string.err_len_password), true)
                return false
            }
            if (ed_register_number.text.toString().trim().isEmpty()) {
                showErrorSnackBar(resources.getString(R.string.err_phone), true)
                return false
            }
        } else {
            //showErrorSnackBar(resources.getString(R.string.success_register), false)
        }
        return true
    }

    private fun registerUser() {

        if (validateRegisterDetails()) {

            showDialog("loading")

            val email: String = ed_register_email.text.toString().trim() { it <= ' ' }
            val password: String = ed_register_password.text.toString().trim() { it <= ' ' }

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(
                    OnCompleteListener<AuthResult> { task ->

                        if (task.isSuccessful) {
                            val firebaseUser: FirebaseUser = task.result!!.user!!

                            val user = User(
                                firebaseUser.uid,
                                ed_register_userName.text.toString().trim() { it <= ' ' },
                                ed_register_email.text.toString().trim() { it <= ' ' },
                                ed_register_password.text.toString().trim() { it <= ' ' },
                                ed_register_number.text.toString().trim() { it <= ' ' }
                            )

                            //to save user data in firestore
                            FirestoreClass().registerUser(this, user)

                            showErrorSnackBar(
                                resources.getString(R.string.success_register) + " ${firebaseUser.uid}",
                                false
                            )
                        } else {
                            hideDialog()
                            showErrorSnackBar(task.exception!!.message.toString(), true)
                        }
                    }).addOnFailureListener {
                    showErrorSnackBar("error happened", true)
                    hideDialog()
                }

        }
    }

    fun userSuccessRegistration() {
        hideDialog()
        Toast.makeText(this@RegisterActivity, R.string.success_register, Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, DashboardActivity::class.java))
        finish()
    }
// back toolbar
//    private fun setupActionBar(){
//         setSupportActionBar(toolbar_name)
//
//        val actionBar = supportActionBar
//
//        if (actionBar != null){
//            actionBar.setDisplayHomeAsUpEnabled(true)
//            actionBar.setHomeAsUpIndicator(R.drawable.ic_back)
//        }
//        toolbar_name.setNavigationOnClickListener { onBackPressed() }
//    }

}