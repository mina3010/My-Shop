package com.mina_myshop.myshop.ui

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowInsets
import android.view.WindowManager
import com.mina_myshop.myshop.R
import com.mina_myshop.myshop.ui.Registration.LoginActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)


        // set fullscreen in android r
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }


        @Suppress("DEPRECATION")
        Handler().postDelayed(
            {
                //launch the main activity
                startActivity(Intent(this@SplashActivity, DashboardActivity::class.java))
                finish()
            }, 1500
        )
    }
}