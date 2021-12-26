package com.mina_myshop.myshop.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class User (
    val id : String = "",
    val fullname : String = "",
    val email : String = "",
    val password : String = "",
    val phoneNumber : String = "",
    val image : String = "",
    val gender : String = "",
    val userType : String = "",
    val profileCompleted : Int = 0) :Parcelable