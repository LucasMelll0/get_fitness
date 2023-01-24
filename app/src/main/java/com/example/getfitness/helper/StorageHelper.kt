package com.example.getfitness.helper

import android.net.Uri
import com.google.firebase.storage.StorageReference

class StorageHelper(
    private val storageReference: StorageReference
) {

    fun saveImage(image: Uri, onSuccess: (uri: Uri) -> Unit = {}, onError: () -> Unit = {}) {
        val imageRef = storageReference.child("images/${image.lastPathSegment}")
        val uploadTask = imageRef.putFile(image)
        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful){
                task.exception?.let {
                    throw it
                }
            }
            imageRef.downloadUrl
        }.addOnCompleteListener { task ->
        if (task.isSuccessful) {
            val downloadUri = task.result
            onSuccess(downloadUri)
        }else {
            onError()
        }

        }
    }
}