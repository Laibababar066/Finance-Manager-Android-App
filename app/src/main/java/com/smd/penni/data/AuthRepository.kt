package com.smd.penni.data

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

class AuthRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    suspend fun signInWithEmailPassword(email: String, password: String): FirebaseUser? {
        return auth.signInWithEmailAndPassword(email, password).await().user
    }

    suspend fun registerWithEmail(email: String, password: String): FirebaseUser? {
        return auth.createUserWithEmailAndPassword(email, password).await().user
    }

    suspend fun signInWithGoogle(credential: AuthCredential): FirebaseUser? {
        return auth.signInWithCredential(credential).await().user
    }

    fun signOut() {
        auth.signOut()
    }

    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }
}
