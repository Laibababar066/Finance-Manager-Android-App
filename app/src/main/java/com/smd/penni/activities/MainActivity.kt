package com.smd.penni.activities

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import com.smd.penni.NavExtras
import com.smd.penni.R
import com.smd.penni.data.FirestoreHelper
import com.smd.penni.databinding.ActivityMainBinding
import com.smd.penni.fragments.MainNavFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            val name = intent.getStringExtra(NavExtras.EXTRA_DISPLAY_NAME) ?: "Guest"
            supportFragmentManager.commit {
                replace(R.id.fragment_root, MainNavFragment.newInstance(name))
            }
        }

        // Part 3: Fetch and store FCM token
        fetchAndStoreFcmToken()
    }

    private fun fetchAndStoreFcmToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("MainActivity", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }

            val token = task.result
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            if (userId != null) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        FirestoreHelper().saveData("users", userId, mapOf("fcmToken" to token))
                    } catch (e: Exception) {
                        Log.e("MainActivity", "Error saving token to Firestore", e)
                    }
                }
            }
        }
    }
}
