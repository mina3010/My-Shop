package com.mina_myshop.myshop.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap

object  Constants {
    const val USERS: String = "users"
    const val MYSHOP_PREFERENCES: String = "MyShopPrefs"
    const val LOGGED_IN_USERNAME: String = "logged_in_username"
    const val EXTRA_USER_DETAILS: String = "extra_user_details"
    const val Read_STORAGE_PERMISSION_CODE = 2
    const val PICK_IMAGE_REQUEST_CODE = 1
    const val FULLNAME: String = "fullname"
    const val MALE: String = "male"
    const val FEMALE: String = "female"
    const val IMAGE: String = "image"
    const val MOBILE: String = "mobile"
    const val GENDER: String = "gender"
    const val USER_PROFILE_IMAGE: String = "user_profile_image"
    const val COMPLETE_PROFILE: String = "profileCompleted"

    fun showImageChooser (activity: Activity){
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activity.startActivityForResult(galleryIntent,Constants.PICK_IMAGE_REQUEST_CODE)
    }

    fun getFileExtension (activity: Activity, uri:Uri?): String? {
        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }
}