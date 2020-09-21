package com.googlemaps.googlemapnotes.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.googlemaps.googlemapnotes.data.local.prefs.UserPreferences
import com.googlemaps.googlemapnotes.data.model.Notes
import com.googlemaps.googlemapnotes.utils.common.Resource
import com.googlemaps.googlemapnotes.utils.network.NetworkResult
import io.reactivex.Single
import kotlinx.coroutines.Job
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


@Singleton
class FirebaseNotesRepository @Inject constructor(
    private val userPreferences: UserPreferences,
    private val firebaseAuth: FirebaseAuth,
    private val firebaseDatabase: FirebaseDatabase

) {
    private val notesPath = "notes"
    private val usersPath = "users"
    private val textPath = "text"
    private val userKey = "user"

    fun addNotes(note: Notes): Single<Boolean> {

        val notesRef = firebaseDatabase.getReference(notesPath)
        val newNoteRef = notesRef.push()
        newNoteRef.setValue(note)
        return Single.just(true)
    }


    suspend fun getNotes(replaceUserName: (Notes) -> Job): NetworkResult<List<Notes>> =
        withContext(
            Executors.newFixedThreadPool(3).asCoroutineDispatcher()
        ) {
            suspendCoroutine<NetworkResult<List<Notes>>> {
                firebaseDatabase.getReference(notesPath)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(databaseError: DatabaseError) {
                            it.resume(NetworkResult.Error(databaseError.toException()))
                        }

                        override fun onDataChange(dataSnapshot: DataSnapshot) {

                            launch(Executors.newFixedThreadPool(3).asCoroutineDispatcher()) {

                                if (dataSnapshot.exists()&& dataSnapshot.hasChildren())
                                {
                                    val noteResults = mutableListOf<Notes>()
                                    dataSnapshot.children.forEach { childrenDataSnapshot ->
                                        val note =
                                            childrenDataSnapshot.getValue(Notes::class.java)!!
                                        replaceUserName(note).join()
                                        noteResults.add(note)
                                    }
                                    it.resume(NetworkResult.Success(noteResults))
                                }
                                else{
                                    it.resume( NetworkResult.Error(Exception("User not authenticated")))
                                }
                            }
                        }
                    })
            }
        }

    suspend fun searchNotesByNote(
        text: String,
        changeUserName: (Notes) -> Job
    ): NetworkResult<List<Notes>> =
        withContext(Executors.newFixedThreadPool(3).asCoroutineDispatcher()) {
            suspendCoroutine<NetworkResult<List<Notes>>> {
                firebaseDatabase.getReference(notesPath)
                    .orderByChild(textPath)
                    .equalTo(text)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(databaseError: DatabaseError) {
                            it.resume(NetworkResult.Error(databaseError.toException()))
                        }

                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            launch(Executors.newFixedThreadPool(3).asCoroutineDispatcher()) {
                                if (dataSnapshot.exists()&& dataSnapshot.hasChildren()) {
                                    val noteResults = mutableListOf<Notes>()
                                    dataSnapshot.children.forEach { childrenDataSnapshot ->
                                        val note =
                                            childrenDataSnapshot.getValue(Notes::class.java)!!
                                        changeUserName(note).join()
                                        noteResults.add(note)
                                    }
                                    it.resume(NetworkResult.Success(noteResults))
                                }
                                else{
                                    it.resume(NetworkResult.Error(Exception("User not authenticated")))
                                }
                            }
                        }
                    })
            }
        }
    suspend fun searchNotesByUser(userId: String, userReadableName: String): NetworkResult<List<Notes>> = withContext(Executors.newFixedThreadPool(3).asCoroutineDispatcher()) {
        suspendCoroutine<NetworkResult<List<Notes>>> {
            firebaseDatabase.getReference(notesPath)
                .orderByChild(userKey)
                .equalTo(userId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(databaseError: DatabaseError) {
                        it.resume(NetworkResult.Error(databaseError.toException()))
                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()&& dataSnapshot.hasChildren()) {
                            val noteResults = mutableListOf<Notes>()
                            dataSnapshot.children.forEach { childrenDataSnapshot ->
                                val note = childrenDataSnapshot.getValue(Notes::class.java)!!
                                note.user = userReadableName
                                noteResults.add(note)
                            }
                            it.resume(NetworkResult.Success(noteResults))
                        }
                        else{
                            it.resume(NetworkResult.Error(Exception("No users found")))
                        }
                    }
                })
        }
    }


}