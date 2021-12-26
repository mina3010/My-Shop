package com.mina_myshop.myshop.firestore

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.mina_myshop.myshop.models.User
import com.mina_myshop.myshop.ui.Registration.LoginActivity
import com.mina_myshop.myshop.ui.Registration.RegisterActivity
import com.mina_myshop.myshop.ui.SettingActivity
import com.mina_myshop.myshop.ui.UserProfileActivity
import com.mina_myshop.myshop.utils.Constants

class FirestoreClass {

    private val mFirestore = FirebaseFirestore.getInstance()

    fun registerUser(activity: RegisterActivity, userInfo: User) {
        mFirestore.collection(Constants.USERS).document(userInfo.id)
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userSuccessRegistration()
            }.addOnFailureListener { e ->
                activity.hideDialog()
                Log.d("firestore", "error is : " + e)
            }
    }

    fun getCurrentUserID(): String {
        val currentUser = FirebaseAuth.getInstance().currentUser

        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }

        return currentUserID
    }

    fun getUserDetails(activity: Activity) {

        mFirestore.collection(Constants.USERS)
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.i(activity.javaClass.simpleName, document.toString())

                val user = document.toObject(User::class.java)!!

                //remember me .... key: logged_in_username / value: fullname
                val sharedPreferences = activity.getSharedPreferences(Constants.MYSHOP_PREFERENCES,Context.MODE_PRIVATE)
                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                editor.putString(Constants.LOGGED_IN_USERNAME,"${user.fullname}")
                editor.apply()

                when (activity) {
                    is LoginActivity -> {
                        activity.userLoggedInSuccess(user)
                    }
                    is SettingActivity -> {
                        activity.userDetailsSuccess(user)
                    }
                }
            }.addOnFailureListener { e ->
                when (activity) {
                    is LoginActivity -> {
                        activity.hideDialog()
                    }
                    is SettingActivity -> {
                        activity.hideDialog()
                    }
                }
            }
    }

    fun updateUserProfileData(activity: Activity, userHashMap: HashMap<String,Any>){
        mFirestore.collection(Constants.USERS)
            .document(getCurrentUserID())
            .update(userHashMap)
            .addOnSuccessListener {
                when (activity){
                    is UserProfileActivity -> {
                        activity.userProfileUpdatedSuccess()
                    }
                }
            }
            .addOnFailureListener { e ->
                when (activity){
                    is UserProfileActivity -> {
                        activity.hideDialog()
                    }
                }

                Log.e(activity.javaClass.simpleName,"error in update",e)
            }
    }

    fun uploadImageToCloudStorage(activity: Activity, imageUri: Uri?){
        val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
            Constants.USER_PROFILE_IMAGE + System.currentTimeMillis()+"."
                        +Constants.getFileExtension(activity,imageUri)
        )

        sRef.putFile(imageUri!!).addOnSuccessListener { taskSnapshot->
            Log.e("Firebase Image URL",taskSnapshot.metadata!!.reference!!.downloadUrl.toString())

            taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                Log.e("Downloadable Image URL",imageUri.toString())
                when (activity){
                    is UserProfileActivity ->{
                        activity.imageUploadSuccess(imageUri.toString())
                    }
                }

            }.addOnFailureListener { e->
                when (activity){
                    is UserProfileActivity -> {
                        activity.hideDialog()
                    }
                }
            }
        }
    }
}