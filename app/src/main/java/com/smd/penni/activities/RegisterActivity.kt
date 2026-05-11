package com.smd.penni.activities

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.smd.penni.data.AuthViewModel

class RegisterActivity : ComponentActivity() {
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val isLoading by viewModel.loading.collectAsState()

            MaterialTheme(colorScheme = darkColorScheme()) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF121212)
                ) {
                    RegisterScreenContent(
                        isLoading = isLoading,
                        onRegisterClick = { email, password ->
                            viewModel.register(email.trim(), password,
                                onSuccess = {
                                    Toast.makeText(this, "Success! Please Login", Toast.LENGTH_SHORT).show()
                                    finish()
                                },
                                onError = { Toast.makeText(this, it, Toast.LENGTH_LONG).show() }
                            )
                        },
                        onBackToLogin = { finish() }
                    )
                }
            }
        }
    }
}

@Composable
fun RegisterScreenContent(
    isLoading: Boolean, 
    onRegisterClick: (String, String) -> Unit, 
    onBackToLogin: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF121212))) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "Create Penni Account", 
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
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = confirmPassword, 
                onValueChange = { confirmPassword = it }, 
                label = { Text("Confirm Password", color = Color.Gray) }, 
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
                onClick = {
                    if (email.isBlank() || password.isBlank()) {
                        Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                    } else if (password == confirmPassword) {
                        onRegisterClick(email, password)
                    } else {
                        Toast.makeText(context, "Passwords don't match", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
            ) {
                Text("Register", color = Color.White)
            }
            Spacer(modifier = Modifier.height(16.dp))
            TextButton(onClick = onBackToLogin, enabled = !isLoading) {
                Text("Already have an account? Login", color = Color.White)
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
