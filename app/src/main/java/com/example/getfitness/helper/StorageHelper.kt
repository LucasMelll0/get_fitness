package com.example.getfitness.helper

import android.net.Uri
import android.util.Log
import com.google.firebase.storage.StorageReference

const val TAG = "StorageHelp"

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

    fun deleteImage(image: String) {
        val imageRef = storageReference.child(image)
        imageRef.delete()
            .addOnSuccessListener { Log.i(TAG, "deleteImage: Image Deleted successfully") }
            .addOnFailureListener { Log.w(TAG, "deleteImage: ", it) }
    }
}