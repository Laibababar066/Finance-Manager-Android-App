package com.smd.penni.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class FirestoreHelper {
    private val db = FirebaseFirestore.getInstance()

    suspend fun saveData(collection: String, documentId: String, data: Any) {
        db.collection(collection).document(documentId).set(data).await()
    }

    suspend fun getData(collection: String): List<Map<String, Any>> {
        val snapshot = db.collection(collection).get().await()
        return snapshot.documents.map { it.data ?: emptyMap() }
    }

    fun syncUserData(userId: String): Flow<Map<String, Any>?> {
        return db.collection("users").document(userId).snapshots().map { snapshot ->
            snapshot.data
        }
    }

    fun syncTransactions(userId: String): Flow<List<Map<String, Any>>> {
        return db.collection("users").document(userId)
            .collection("transactions")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .snapshots()
            .map { snapshot ->
                snapshot.documents.map { it.data ?: emptyMap() }
            }
    }

    suspend fun addTransaction(userId: String, transaction: Map<String, Any>) {
        val dataWithTimestamp = transaction.toMutableMap()
        dataWithTimestamp["timestamp"] = com.google.firebase.Timestamp.now()
        db.collection("users").document(userId)
            .collection("transactions").add(dataWithTimestamp).await()
    }
}
