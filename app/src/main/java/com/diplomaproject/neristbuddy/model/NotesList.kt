package com.diplomaproject.neristbuddy.model

data class NotesList(
        val name: String,
        val notes: String,
        val image: String?,
        val imageName:String?,
        val pdf: String?,
        val pdfName: String?,
        val uploadedBy: String,
        val uid:String
)