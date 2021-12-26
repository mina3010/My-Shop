package com.mina_myshop.myshop.ui.Registration

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.mina_myshop.myshop.R
import com.mina_myshop.myshop.firestore.FirestoreClass
import com.mina_myshop.myshop.models.User
import com.mina_myshop.myshop.ui.BaseActivity
import com.mina_myshop.myshop.ui.DashboardActivity
import com.mina_myshop.myshop.ui.UserProfileActivity
import com.mina_myshop.myshop.utils.Constants
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*

class LoginActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        btn_login.setOnClickListener {
            login()
        }

        tv_signUp.setOnClickListener { startActivity(Intent(this@LoginActivity, RegisterActivity::class.java)) }

        tv_forgetPassword.setOnClickListener { startActivity(Intent(this@LoginActivity, ForgetPasswordActivity::class.java)) }

    }

    private fun validateRegisterDetails(): Boolean {
        if (ed_login_userName.text.toString().trim().isEmpty() || ed_login_password.text.toString()
                .trim().isEmpty()
        ) {

            if (ed_login_userName.text.toString().trim().isEmpty()) {
                showErrorSnackBar(resources.getString(R.string.err_password), true)
                return false
            }
            if (ed_login_password.text.toString().trim().isEmpty()) {
                showErrorSnackBar(resources.getString(R.string.err_phone), true)
                return false
            }
        } else {
            // showErrorSnackBar(resources.getString(R.string.success_login), false)
        }
        return true
    }

    private fun login() {

        if (validateRegisterDetails()) {

            showDialog("loading")

            val email: String = ed_login_userName.text.toString().trim() { it <= ' ' }
            val password: String = ed_login_password.text.toString().trim() { it <= ' ' }

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(
                    OnCompleteListener<AuthResult> { task ->

                        if (task.isSuccessful) {

                            FirestoreClass().getUserDetails(this@LoginActivity)

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

    fun userLoggedInSuccess(user: User){
        hideDialog()

        if (user.profileCompleted == 0 ){
            val intent = Intent(this@LoginActivity, UserProfileActivity::class.java)
            intent.putExtra(Constants.EXTRA_USER_DETAILS, user)
            startActivity(intent)

        }else {
            startActivity(Intent(this@LoginActivity, DashboardActivity::class.java))
        }
        finish()

    }
}