package com.urbancrew.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.urbancrew.app.data.model.User
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    @OptIn(ExperimentalCoroutinesApi::class)
    override val currentUser: Flow<User?> = callbackFlow {
        // Initial emission
        trySend(firebaseAuth.currentUser?.uid)
        
        val authStateListener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser?.uid)
        }
        firebaseAuth.addAuthStateListener(authStateListener)
        awaitClose { firebaseAuth.removeAuthStateListener(authStateListener) }
    }.distinctUntilChanged()
    .flatMapLatest { uid ->
        if (uid == null) {
            flowOf(null)
        } else {
            callbackFlow {
                val fbUser = firebaseAuth.currentUser
                
                // 1. Initial emission from FirebaseAuth to avoid blank screen/guest state
                val initialName = if (!fbUser?.displayName.isNullOrBlank()) {
                    fbUser?.displayName
                } else {
                    fbUser?.email?.substringBefore("@") ?: "User"
                }
                
                trySend(User(id = uid, email = fbUser?.email ?: "", name = initialName))

                // 2. Continuous updates from Firestore for full profile (role, etc.)
                val subscription = firestore.collection("users").document(uid)
                    .addSnapshotListener { snapshot, error ->
                        val currentFbUser = firebaseAuth.currentUser
                        
                        if (error != null || snapshot == null || !snapshot.exists()) {
                            val name = if (!currentFbUser?.displayName.isNullOrBlank()) {
                                currentFbUser?.displayName
                            } else {
                                currentFbUser?.email?.substringBefore("@") ?: "User"
                            }
                            trySend(User(id = uid, email = currentFbUser?.email ?: "", name = name))
                            return@addSnapshotListener
                        }
                        
                        val firestoreUser = snapshot.toObject(User::class.java)
                        if (firestoreUser != null) {
                            val finalName = if (firestoreUser.name.isNullOrBlank()) {
                                if (!currentFbUser?.displayName.isNullOrBlank()) currentFbUser.displayName else currentFbUser?.email?.substringBefore("@") ?: "User"
                            } else {
                                firestoreUser.name
                            }
                            
                            trySend(firestoreUser.copy(
                                name = finalName,
                                email = if (firestoreUser.email.isBlank()) currentFbUser?.email ?: "" else firestoreUser.email
                            ))
                        }
                    }
                awaitClose { subscription.remove() }
            }
        }
    }

    override suspend fun loginWithGoogle(idToken: String, role: String): Result<User> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = firebaseAuth.signInWithCredential(credential).await()
            val firebaseUser = result.user ?: throw Exception("Google login failed")
            
            val userDoc = firestore.collection("users").document(firebaseUser.uid).get().await()
            val user = if (userDoc.exists()) {
                userDoc.toObject(User::class.java)!!
            } else {
                val newUser = User(
                    id = firebaseUser.uid,
                    email = firebaseUser.email ?: "",
                    name = firebaseUser.displayName,
                    role = role
                )
                firestore.collection("users").document(firebaseUser.uid).set(newUser).await()
                newUser
            }
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun loginWithEmail(email: String, password: String): Result<User> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user ?: throw Exception("Login failed")
            
            if (!firebaseUser.isEmailVerified) {
                firebaseAuth.signOut()
                throw Exception("EMAIL_NOT_VERIFIED")
            }
            
            firebaseUser.reload().await()
            
            val userDoc = firestore.collection("users").document(firebaseUser.uid).get().await()
            val user = if (userDoc.exists()) {
                val fetchedUser = userDoc.toObject(User::class.java)!!
                if (fetchedUser.name.isNullOrBlank() && !firebaseUser.displayName.isNullOrBlank()) {
                    val updatedUser = fetchedUser.copy(name = firebaseUser.displayName)
                    firestore.collection("users").document(firebaseUser.uid).set(updatedUser).await()
                    updatedUser
                } else {
                    fetchedUser
                }
            } else {
                val newUser = User(
                    id = firebaseUser.uid, 
                    email = email,
                    name = firebaseUser.displayName ?: email.substringBefore("@"),
                    role = "Customer"
                )
                firestore.collection("users").document(firebaseUser.uid).set(newUser).await()
                newUser
            }
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signUp(name: String, email: String, password: String, role: String): Result<User> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user ?: throw Exception("Sign up failed")
            
            val profileUpdates = userProfileChangeRequest {
                displayName = name
            }
            firebaseUser.updateProfile(profileUpdates).await()

            val user = User(
                id = firebaseUser.uid,
                email = email,
                name = name,
                role = role
            )
            firestore.collection("users").document(firebaseUser.uid).set(user).await()

            firebaseUser.sendEmailVerification().await()
            firebaseAuth.signOut()

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        firebaseAuth.signOut()
    }

    override suspend fun isUserLoggedIn(): Boolean {
        // Just check if user exists at SDK level. 
        // Verification is handled during actual login flow.
        return firebaseAuth.currentUser != null
    }
}
