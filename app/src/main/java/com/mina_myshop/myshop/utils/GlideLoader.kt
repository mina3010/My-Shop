package com.mina_myshop.myshop.utils

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.mina_myshop.myshop.R
import java.io.IOException

class GlideLoader(val context: Context) {

    fun loadImage(imageURI: Any, imageView: ImageView) {
        try {
            Glide.with(context)
                .load(Uri.parse(imageURI.toString()))
                .centerCrop()
                .placeholder(R.drawable.ic_user)
                .into(imageView)
        } catch (e: IOException){
            e.printStackTrace()
        }
    }
}