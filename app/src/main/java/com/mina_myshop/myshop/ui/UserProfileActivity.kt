package com.mina_myshop.myshop.ui

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.mina_myshop.myshop.MainActivity
import com.mina_myshop.myshop.R
import com.mina_myshop.myshop.firestore.FirestoreClass
import com.mina_myshop.myshop.models.User
import com.mina_myshop.myshop.utils.Constants
import com.mina_myshop.myshop.utils.GlideLoader
import kotlinx.android.synthetic.main.activity_user_profile.*
import kotlinx.android.synthetic.main.loading.*
import java.io.IOException
import java.util.jar.Manifest

class UserProfileActivity : BaseActivity() {
    private lateinit var mUserDetails: User
    private var mSelectedImageUri: Uri? = null
    private var mUserProfileImageURL: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)


        if (intent.hasExtra(Constants.EXTRA_USER_DETAILS)) {
            mUserDetails = intent.getParcelableExtra(Constants.EXTRA_USER_DETAILS)!!
        }

        ed_fullname.setText(mUserDetails.fullname)
        ed_email.isEnabled = false
        ed_email.setText(mUserDetails.email)
        if (mUserDetails.profileCompleted == 0){
            tv_title.text = "complete profile"
            ed_fullname.isEnabled = false

        }else{
            setupActionBar()
            tv_title.text = "edit profile"
            GlideLoader(this@UserProfileActivity).loadImage(mUserDetails.image,iv_userImg)
            ed_email.isEnabled = false
            ed_email.setText(mUserDetails.email)

//            if (mUserDetails.phoneNumber != 0L){
//                ed_phone.setText(mUserDetails.phoneNumber.toString())
//            }
            if (mUserDetails.gender == Constants.MALE){
                rd_male.isChecked = true
            }else{
                rd_female.isChecked = true
            }
        }
        
        iv_userImg.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                )
                == PackageManager.PERMISSION_GRANTED
            ) {
                //showErrorSnackBar("you already have storage permission.", false)
                Constants.showImageChooser(this@UserProfileActivity)
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.Read_STORAGE_PERMISSION_CODE
                )
            }
        }

        btn_submit.setOnClickListener {

            if (validateUserProfileDetails()) {
                showDialog("Loading")

                if (mSelectedImageUri != null) {
                    FirestoreClass().uploadImageToCloudStorage(this, mSelectedImageUri)
                } else {
                    updateUserProfileDetails()
                }
            }
        }
    }

    private fun setupActionBar(){
        setSupportActionBar(toolbar_user_profile)

        val actionBar = supportActionBar
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back)
        }
        toolbar_user_profile.setNavigationOnClickListener { onBackPressed() }
    }
    private fun updateUserProfileDetails() {
        val userHashMap = HashMap<String, Any>()
        val fullname = ed_fullname.text.toString().trim() { it <= ' ' }
        if (fullname!= mUserDetails.fullname){
            userHashMap[Constants.FULLNAME] = fullname
        }
        val mobileNumber = ed_phone.text.toString().trim() { it <= ' ' }
        val gender = if (rd_male.isChecked) {
            Constants.MALE
        } else {
            Constants.FEMALE
        }


        if (mUserProfileImageURL.isNotEmpty()) {
            userHashMap[Constants.IMAGE] = mUserProfileImageURL
        }
        if (mobileNumber.isNotEmpty() && mobileNumber != mUserDetails.phoneNumber.toString()) {
            userHashMap[Constants.MOBILE] = mobileNumber.toLong()
        }

        if (gender.isNotEmpty() && gender != mUserDetails.gender) {
            userHashMap[Constants.GENDER] = gender
        }
        userHashMap[Constants.GENDER] = gender

        userHashMap[Constants.COMPLETE_PROFILE] = 1

        FirestoreClass().updateUserProfileData(this, userHashMap)
        //showErrorSnackBar("your details are valid",false)

        ed_phone.isEnabled = false
    }

    fun userProfileUpdatedSuccess() {
        hideDialog()
        Toast.makeText(this, "profile update successful", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this@UserProfileActivity, DashboardActivity::class.java))
        finish()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == Constants.Read_STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //showErrorSnackBar(" the storage permission is granted",false)
                Constants.showImageChooser(this@UserProfileActivity)
            } else {
                Toast.makeText(
                    this,
                    "permission is denied, you can also try again",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.PICK_IMAGE_REQUEST_CODE) {
                if (data != null) {
                    try {
                        mSelectedImageUri = data.data!!
                        // iv_userImg.setImageURI(Uri.parse(selectImageUri.toString()))
                        GlideLoader(this).loadImage(mSelectedImageUri!!, iv_userImg)

                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(this, "image selected failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.d("userProfile", "image canceled")
        }
    }

    private fun validateUserProfileDetails(): Boolean {
        if (ed_phone.text.isEmpty()) {
            showErrorSnackBar(resources.getString(R.string.err_phone), true)
            return false
        } else {
            return true
        }
    }

    fun imageUploadSuccess(imageUri: String) {
       // hideDialog()
        mUserProfileImageURL = imageUri
        updateUserProfileDetails()

        Toast.makeText(this, "Image update successful", Toast.LENGTH_SHORT).show()

    }
}