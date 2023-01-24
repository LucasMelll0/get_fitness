package com.example.getfitness.helper

import android.util.Log
import com.example.getfitness.model.Exercise
import com.example.getfitness.model.Training
import com.example.getfitness.repository.*
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore

class FireStoreHelper(
    private val db: FirebaseFirestore,
    private val storage: StorageHelper
) {

    fun getAllTrainings(
        userId: String,
        onSuccess: (trainings: List<Training>) -> Unit = {},
        onError: () -> Unit = {}
    ) {
        db.collection(TRAININGS_COLLECTION)
            .whereEqualTo(AUTHOR_FIELD, userId)
            .addSnapshotListener { value, e ->
                e?.let {
                    Log.w(TAG, "Listen Failed: ", e)
                    onError()
                } ?: run {
                    val trainings = mutableListOf<Training>()
                    for (doc in value!!) {
                        val name = doc.getLong(NAME_FIELD) ?: 0
                        val description = doc.getString(DESCRIPTION_FIELD) ?: ""
                        val date = doc.getTimestamp(DATE_FIELD) ?: Timestamp.now()
                        val training = Training(
                            name = name,
                            description = description,
                            date = date
                        )
                        trainings.add(training)
                    }
                    onSuccess(trainings)
                }
            }

    }

    fun saveTraining(
        training: Training,
        exercises: List<Exercise>,
        onSuccess: () -> Unit,
        onError: () -> Unit

    ) {
        val trainingRef = db.collection(TRAININGS_COLLECTION)
            .document(training.name.toString())
        trainingRef.set(training).addOnSuccessListener {
            for (exercise in exercises) {
                try {
                    exercise.image?.let {
                        storage.saveImage(
                            it,
                            onSuccess = { storageUri ->
                                exercise.image = storageUri
                                trainingRef
                                    .collection(EXERCISES_COLLECTION)
                                    .document(exercise.name.toString())
                                    .set(exercise).addOnFailureListener { exception ->
                                        throw exception
                                    }
                            },
                        onError = onError)
                    } ?: run {
                        trainingRef
                            .collection(EXERCISES_COLLECTION)
                            .document(exercise.name.toString())
                            .set(exercise).addOnFailureListener { exception ->
                                throw exception
                            }
                    }
                    onSuccess()
                } catch (e: Exception) {
                    onError()
                }
            }
        }.addOnFailureListener {
            Log.w(TAG, "addTraining: ", it)
            onError()
        }

    }
}