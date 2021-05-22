package com.diplomaproject.neristbuddy.model

data class MessageModel(
    val name:String,
    val message:String?,
    val time:String,
    val image:String?,
    val msgKey:String,
    val uid:String
)