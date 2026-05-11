package com.smd.penni.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.smd.penni.NavExtras
import com.smd.penni.data.RemoteConfigHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        lifecycleScope.launch {
            try { RemoteConfigHelper.fetchAndActivate() } catch (e: Exception) {}
            
            // FORCED LOGOUT FOR TESTING: This ensures you see the Login/Register screens
            FirebaseAuth.getInstance().signOut()
            
            delay(2000) // 2 second delay to see the splash

            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                val intent = Intent(this@SplashActivity, MainActivity::class.java).apply {
                    putExtra(NavExtras.EXTRA_DISPLAY_NAME, user.displayName ?: user.email ?: "User")
                }
                startActivity(intent)
            } else {
                startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
            }
            finish()
        }
    }
}
