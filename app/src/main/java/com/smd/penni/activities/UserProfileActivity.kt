package com.smd.penni.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.smd.penni.data.FirestoreHelper
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class UserProfileActivity : ComponentActivity() {
    private val auth = FirebaseAuth.getInstance()
    private val firestoreHelper = FirestoreHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UserProfileScreen(
                onLogout = {
                    auth.signOut()
                    finishAffinity()
                    startActivity(android.content.Intent(this, LoginActivity::class.java))
                },
                onCrashTest = {
                    // Feature B: Test crash
                    throw RuntimeException("Test Crash")
                }
            )
        }
    }
}

@Composable
fun UserProfileScreen(onLogout: () -> Unit, onCrashTest: () -> Unit) {
    val user = FirebaseAuth.getInstance().currentUser
    var userData by remember { mutableStateOf<Map<String, Any>?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(user?.uid) {
        user?.uid?.let { uid ->
            FirestoreHelper().syncUserData(uid).collectLatest { data ->
                userData = data
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("User Profile", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))
        
        Text("Email: ${user?.email ?: "N/A"}")
        Text("UID: ${user?.uid ?: "N/A"}")
        
        userData?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Text("Extra Data from Firestore:", style = MaterialTheme.typography.titleMedium)
            it.forEach { (key, value) ->
                Text("$key: $value")
            }
        }

        Spacer(modifier = Modifier.weight(1f))
        
        Button(
            onClick = onCrashTest,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Trigger Test Crash")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Logout")
        }
    }
}
