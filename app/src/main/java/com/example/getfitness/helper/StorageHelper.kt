package com.example.getfitness.helper

import android.net.Uri
import android.util.Log
import com.google.firebase.storage.StorageReference

const val TAG = "StorageHelp"

class StorageHelper(
    private val storageReference: StorageReference
) {

    fun saveImage(image: Uri, onSuccess: (uri: Uri) -> Unit = {}, onError: () -> Unit = {}) {
        try {
            val imageRef = storageReference.child("images/${image.lastPathSegment}")
            val uploadTask = imageRef.putFile(image)
            uploadTask
                .onSuccessTask {
                    uploadTask.continueWithTask { task ->
                        if (!task.isSuccessful) {
                            task.exception?.let {
                                Log.w(TAG, "saveImage: ", it)
                            }
                        }
                        imageRef.downloadUrl
                    }
                        .addOnSuccessListener {
                            onSuccess(it)
                        }
                        .addOnFailureListener {
                            onError()
                            Log.w(TAG, "saveImage: ", it)
                        }
                }.addOnFailureListener {
                    Log.w(TAG, "saveImage: ", it)
                    onError()
                }


        } catch (e: Exception) {
            Log.w(TAG, "saveImage: ", e)
        }

    }

}