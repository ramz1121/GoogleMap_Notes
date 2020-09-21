package com.googlemaps.googlemapnotes.data.repository


import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.googlemaps.googlemapnotes.data.local.prefs.UserPreferences
import com.googlemaps.googlemapnotes.data.model.Notes
import com.googlemaps.googlemapnotes.data.model.User
import com.googlemaps.googlemapnotes.data.model.UserID
import com.googlemaps.googlemapnotes.data.remote.FirebaseSource
import com.googlemaps.googlemapnotes.utils.common.Resource
import com.googlemaps.googlemapnotes.utils.network.NetworkResult
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Singleton
class FirebaseRepository @Inject constructor(
    private val firebase: FirebaseSource,
    private val userPreferences: UserPreferences,
    private val firebaseAuth: FirebaseAuth,
    private val firebaseDatabase: FirebaseDatabase

) {
    private val usersPath = "users"
    private val nameKey = "name"

    fun registerAuth(email: String, password: String) = Completable.create { emitter ->
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (!emitter.isDisposed) {
                if (it.isSuccessful) {
                    (Resource.success(UserID(it.result?.user?.uid!!)))
                    userPreferences.setUserId(it.result?.user?.uid!!)
                    emitter.onComplete()
                } else
                    emitter.onError(it.exception!!)
            }
        }
    }

    fun changeUIdUserName(user: UserID, name: String) = Completable.create { emitter ->
        val usersRef = firebaseDatabase.getReference(usersPath)
        usersRef.child(user.uid).setValue(hashMapOf(nameKey to name))
    }

    fun login(email: String, password: String) = Completable.create { emitter ->

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {

            if (!emitter.isDisposed) {
                if (it.isSuccessful) {
                    (Resource.success(UserID(it.result?.user?.uid!!)))
                    userPreferences.setUserId(it.result?.user?.uid!!)
                    emitter.onComplete()
                } else
                    emitter.onError(it.exception!!)
                NetworkResult.Error(Exception("User not authenticated"))
            }
        }
    }


    fun logout() = Completable.create { emitter ->
        firebaseAuth.signOut()
    }


    fun saveCurrentUser(user: User) {
        userPreferences.setUserEmail(user.email)
        userPreferences.setUserName(user.userName)
    }

    fun getCurrentUser(): Single<Boolean> {

        val user = firebaseAuth.currentUser
        if (user != null) {
            UserID(user.uid)
            userPreferences.setUserId(user.uid)
            Resource.success(user.uid)
            return Single.just(true)

        } else {
            return Single.just(false)
        }
    }

    suspend fun getUserIdFromActualName(userName: String): NetworkResult<String> = withContext(Executors.newFixedThreadPool(3).asCoroutineDispatcher()) {
        suspendCoroutine<NetworkResult<String>> { continuation ->
            firebaseDatabase.getReference(usersPath)
                .orderByChild(nameKey)
                .equalTo(userName)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {}

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()&& dataSnapshot.hasChildren()) {
                            continuation.resume(NetworkResult.Success(dataSnapshot.children.first().key.toString()))
                        }
                        else{
                            continuation.resume(NetworkResult.Error(Exception("User not authenticated")))
                        }
                    }
                })
        }
    }

    suspend fun getUserReadableName(userId: String): NetworkResult<String> =
        withContext(Executors.newFixedThreadPool(3).asCoroutineDispatcher()) {
            suspendCoroutine<NetworkResult<String>> {
                firebaseDatabase.getReference(usersPath).child(userId)
                    .addValueEventListener(object : ValueEventListener {
                        override fun onCancelled(databaseError: DatabaseError) {
                           it.resume(NetworkResult.Error(Exception("User not authenticated")))
                        }

                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            if (dataSnapshot.exists()&& dataSnapshot.hasChildren()) {
                                it.resume(NetworkResult.Success(dataSnapshot.children.first().value.toString()))
                            }
                            else{
                                NetworkResult.Error(Exception("User not authenticated"))
                            }
                        }
                    })
            }
        }
}