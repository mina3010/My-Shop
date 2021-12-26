package com.mina_myshop.myshop.ui.Registration

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.mina_myshop.myshop.MainActivity
import com.mina_myshop.myshop.R
import com.mina_myshop.myshop.ui.BaseActivity
import kotlinx.android.synthetic.main.activity_foreget_password.*
import kotlinx.android.synthetic.main.activity_login.*

class ForgetPasswordActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_foreget_password)

        btn_forgetPassword.setOnClickListener { resetPassword() }
        btn_back.setOnClickListener { onBackPressed() }

    }

    private fun validateEmailDetails(): Boolean {

        if (ed_email_forgetPassword.text.toString().trim().isEmpty()) {
            showErrorSnackBar(resources.getString(R.string.err_email), true)
            return false
        }

        return true
    }

    private fun resetPassword() {

        if (validateEmailDetails()) {

            showDialog("loading")

            val email: String = ed_email_forgetPassword.text.toString().trim() { it <= ' ' }

            FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener(
                OnCompleteListener { task ->
                    hideDialog()
                    if (task.isSuccessful){
                        showErrorSnackBar("successful submit .. please check your email .. this progress may be take some of time", false)
                    }
                    else{
                        showErrorSnackBar(task.exception!!.message.toString(),true)
                    }
                }).addOnFailureListener {
                showErrorSnackBar("error happened",true)
                hideDialog()
                }
        }
    }
}