package com.example.getfitness.helper

import android.net.Uri
import android.util.Log
import com.example.getfitness.model.Exercise
import com.example.getfitness.model.Training
import com.example.getfitness.repository.*
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions

class FireStoreHelper(
    private val db: FirebaseFirestore,
    private val storage: StorageHelper
) {

    private var allTrainingsObserver: ListenerRegistration? = null

    fun getAllTrainings(
        userId: String,
        onSuccess: (trainings: List<Training>) -> Unit = {},
        onError: () -> Unit = {}
    ) {
        allTrainingsObserver = db.collection(TRAININGS_COLLECTION)
            .whereEqualTo(AUTHOR_FIELD, userId)
            .addSnapshotListener { value, e ->
                e?.let {
                    it.message?.let { message ->
                        Log.w(TAG, "Listen Failed: ", e)
                        if (!message.contains("Not Found")) {
                            onError()
                            return@addSnapshotListener
                        }
                    }
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

    fun removeAllTrainingsObserver() {
        allTrainingsObserver?.remove()
    }

    fun getTrainingByName(
        userId: String,
        name: Number,
        onSuccess: (training: Training, exercises: List<Exercise>) -> Unit,
        onError: () -> Unit = {}
    ) {
        val trainingRef = db.collection(TRAININGS_COLLECTION)
            .document(name.toString()).get()
        trainingRef.addOnCompleteListener { trainingTask ->
            if (trainingTask.isSuccessful) {
                trainingTask.result?.let {
                    val trainingName = it.getLong(NAME_FIELD) ?: 0
                    val trainingDescription = it.getString(DESCRIPTION_FIELD) ?: ""
                    val trainingDate = it.getTimestamp(DATE_FIELD) ?: Timestamp.now()
                    val training = Training(trainingName, trainingDescription, trainingDate, userId)
                    it.reference
                        .collection(EXERCISES_COLLECTION)
                        .whereEqualTo(AUTHOR_FIELD, userId)
                        .get()
                        .addOnCompleteListener { exercisesTask ->
                            if (exercisesTask.isSuccessful) {
                                exercisesTask.result?.let { exerciseDocs ->
                                    val exerciseList = mutableListOf<Exercise>()
                                    for (exerciseDoc in exerciseDocs) {
                                        val exerciseName = exerciseDoc.getLong(NAME_FIELD) ?: 0
                                        val exerciseObservation =
                                            exerciseDoc.getString(OBSERVATIONS_FIELD) ?: ""
                                        val exerciseImage =
                                            exerciseDoc.getString(IMAGE_FIELD)?.let { uri ->
                                                Uri.parse(uri)
                                            }
                                        val exerciseAuthor =
                                            exerciseDoc.getString(AUTHOR_FIELD) ?: ""
                                        val exercise = Exercise(
                                            exerciseName,
                                            exerciseImage,
                                            exerciseObservation,
                                            exerciseAuthor
                                        )
                                        exerciseList.add(exercise)
                                    }
                                    onSuccess(training, exerciseList)
                                }

                            } else {
                                Log.w(TAG, "getTrainingByName: ", trainingTask.exception)
                                onError()
                            }
                        }
                }
            } else {
                Log.w(TAG, "getTrainingByName: ", trainingTask.exception!!)
                onError()
            }
        }
    }

    fun getTrainingByNameRealTime(
        userId: String,
        name: Number,
        onSuccess: (training: Training, exercises: List<Exercise>) -> Unit,
        onError: () -> Unit = {}
    ) {
        val trainingRef = db.collection(TRAININGS_COLLECTION)
            .document(name.toString())
        trainingRef.addSnapshotListener { trainingDoc, e ->
            e?.let {
                Log.w(TAG, "getTrainingByName: ", it)
                onError()
                return@addSnapshotListener
            } ?: run {
                trainingDoc?.let {
                    val trainingName = trainingDoc.getLong(NAME_FIELD) ?: 0
                    val trainingDescription = trainingDoc.getString(DESCRIPTION_FIELD) ?: ""
                    val trainingDate = trainingDoc.getTimestamp(DATE_FIELD) ?: Timestamp.now()
                    val training = Training(trainingName, trainingDescription, trainingDate, userId)
                    trainingDoc.reference
                        .collection(EXERCISES_COLLECTION)
                        .whereEqualTo(AUTHOR_FIELD, userId)
                        .addSnapshotListener { exerciseDocs, e ->
                            e?.let {
                                Log.w(TAG, "getTrainingByName: ", e)
                                onError()
                            } ?: run {
                                val exerciseList = mutableListOf<Exercise>()
                                for (exerciseDoc in exerciseDocs!!) {
                                    val exerciseName = exerciseDoc.getLong(NAME_FIELD) ?: 0
                                    val exerciseObservation =
                                        exerciseDoc.getString(OBSERVATIONS_FIELD) ?: ""
                                    val exerciseImage =
                                        exerciseDoc.getString(IMAGE_FIELD)?.let { uri ->
                                            Uri.parse(uri)
                                        }
                                    val exerciseAuthor = exerciseDoc.getString(AUTHOR_FIELD) ?: ""
                                    val exercise = Exercise(
                                        exerciseName,
                                        exerciseImage,
                                        exerciseObservation,
                                        exerciseAuthor
                                    )
                                    exerciseList.add(exercise)
                                }

                                onSuccess(training, exerciseList)
                            }

                        }
                }

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
        trainingRef.set(training, SetOptions.merge()).addOnSuccessListener {
            if (exercises.isNotEmpty()) {
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
                                        .set(exercise, SetOptions.merge())
                                        .addOnFailureListener { exception ->
                                            throw exception
                                        }
                                },
                                onError = onError
                            )
                        } ?: run {
                            trainingRef
                                .collection(EXERCISES_COLLECTION)
                                .document(exercise.name.toString())
                                .set(exercise).addOnFailureListener { exception ->
                                    Log.w(TAG, "saveTraining: ", exception)
                                    onError()
                                }
                        }
                        onSuccess()
                    } catch (e: Exception) {
                        onError()
                    }
                }
            } else {
                onSuccess()
            }

        }.addOnFailureListener {
            Log.w(TAG, "addTraining: ", it)
            onError()
        }
    }

    fun removeTraining(
        userId: String,
        trainingName: String,
        onSuccess: () -> Unit = {},
        onError: () -> Unit = {}
    ) {
        val trainingRef = db.collection(TRAININGS_COLLECTION).document(trainingName)
        val exercisesCollection = trainingRef.collection(EXERCISES_COLLECTION)
        exercisesCollection
            .whereEqualTo(AUTHOR_FIELD, userId).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        document.reference.delete()
                    }
                    trainingRef.delete()
                        .addOnSuccessListener { onSuccess() }
                        .addOnFailureListener { onError() }
                } else {
                    onError()
                }
            }
    }

    fun updateTraining(
        userId: String,
        training: Training,
        exercises: List<Exercise>,
        onSuccess: () -> Unit = {},
        onError: () -> Unit = {}
    ) {
        val trainingRef = db.collection(TRAININGS_COLLECTION).document(training.name.toString())
        val exercisesCollection = trainingRef.collection(EXERCISES_COLLECTION)
        exercisesCollection.whereEqualTo(AUTHOR_FIELD, userId).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        val name = document.getLong(NAME_FIELD) ?: 0
                        if (exercises.none { it.name == name }) {
                            document.reference.delete()
                        }

                    }
                    trainingRef.set(training, SetOptions.merge()).addOnSuccessListener {
                        for (exercise in exercises) {
                            exercise.image?.let {
                                storage.saveImage(
                                    it,
                                    onSuccess = { storageUri ->
                                        exercise.image = storageUri
                                        trainingRef
                                            .collection(EXERCISES_COLLECTION)
                                            .document(exercise.name.toString())
                                            .set(exercise)
                                            .addOnFailureListener { exception ->
                                                throw exception
                                            }
                                    },
                                    onError = onError
                                )
                            } ?: run {
                                trainingRef
                                    .collection(EXERCISES_COLLECTION)
                                    .document(exercise.name.toString())
                                    .set(exercise).addOnFailureListener { exception ->
                                        throw exception
                                    }
                            }
                        }
                        onSuccess()
                    }
                }
            }
    }
}