package com.smd.penni.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.GoogleAuthProvider
import com.smd.penni.NavExtras
import com.smd.penni.R
import com.smd.penni.data.AuthViewModel

class LoginActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val isLoading by authViewModel.loading.collectAsState()

            MaterialTheme(colorScheme = darkColorScheme()) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF121212)
                ) {
                    LoginScreenContent(
                        isLoading = isLoading,
                        onLoginClick = { email, password ->
                            if (email.isBlank() || password.isBlank()) {
                                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                            } else {
                                authViewModel.login(
                                    email.trim(),
                                    password,
                                    onSuccess = { name -> navigateToMain(name) },
                                    onError = { error -> Toast.makeText(this, error, Toast.LENGTH_LONG).show() }
                                )
                            }
                        },
                        onRegisterClick = {
                            startActivity(Intent(this, RegisterActivity::class.java))
                        },
                        onGoogleSignIn = {
                            signInWithGoogle()
                        }
                    )
                }
            }
        }
    }

    private fun signInWithGoogle() {
        val clientId = getString(R.string.default_web_client_id)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(clientId)
            .requestEmail()
            .build()
        val googleSignInClient = GoogleSignIn.getClient(this, gso)
        startActivityForResult(googleSignInClient.signInIntent, 101)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(Exception::class.java)!!
                val credential = GoogleAuthProvider.getCredential(account.idToken!!, null)
                authViewModel.signInWithGoogle(
                    credential,
                    onSuccess = { name -> navigateToMain(name) },
                    onError = { error -> Toast.makeText(this, error, Toast.LENGTH_LONG).show() }
                )
            } catch (e: Exception) {
                Toast.makeText(this, "Google sign in failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateToMain(name: String) {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra(NavExtras.EXTRA_DISPLAY_NAME, name)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }
}

@Composable
fun LoginScreenContent(
    isLoading: Boolean,
    onLoginClick: (String, String) -> Unit,
    onRegisterClick: () -> Unit,
    onGoogleSignIn: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF121212))) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Penni Login",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFF4CAF50),
                    unfocusedBorderColor = Color.Gray
                )
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password", color = Color.Gray) },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFF4CAF50),
                    unfocusedBorderColor = Color.Gray
                )
            )
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { onLoginClick(email, password) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
            ) {
                Text("Sign In", color = Color.White)
            }
            Spacer(modifier = Modifier.height(16.dp))
            TextButton(onClick = onRegisterClick, enabled = !isLoading) {
                Text("Don't have an account? Register", color = Color.White)
            }
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onGoogleSignIn,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                enabled = !isLoading
            ) {
                Text("Sign In with Google", color = Color.Black)
            }
        }
        
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = Color(0xFF4CAF50)
            )
        }
    }
}
