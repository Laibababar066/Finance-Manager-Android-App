package com.smd.penni.data

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: AuthRepository = AuthRepository()) : ViewModel() {
    private val _userState = MutableStateFlow<FirebaseUser?>(null)
    val userState: StateFlow<FirebaseUser?> = _userState

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val analytics = Firebase.analytics

    init {
        _userState.value = repository.getCurrentUser()
    }

    fun login(email: String, password: String, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val user = repository.signInWithEmailPassword(email, password)
                if (user != null) {
                    _userState.value = user
                    // NF2: Log Custom Analytics Event
                    val bundle = Bundle()
                    bundle.putString(FirebaseAnalytics.Param.METHOD, "email")
                    analytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle)
                    
                    onSuccess(user.displayName ?: user.email ?: "User")
                } else {
                    onError("Login failed")
                }
            } catch (e: Exception) {
                // NF2: Record non-fatal exception in Crashlytics
                FirebaseCrashlytics.getInstance().recordException(e)
                onError(e.localizedMessage ?: "Error")
            } finally {
                _loading.value = false
            }
        }
    }

    fun register(email: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val user = repository.registerWithEmail(email, password)
                if (user != null) {
                    // NF2: Log sign up event
                    analytics.logEvent(FirebaseAnalytics.Event.SIGN_UP, null)
                    onSuccess()
                }
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                onError(e.localizedMessage ?: "Error")
            } finally {
                _loading.value = false
            }
        }
    }

    fun signInWithGoogle(credential: AuthCredential, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val user = repository.signInWithGoogle(credential)
                if (user != null) {
                    _userState.value = user
                    val bundle = Bundle()
                    bundle.putString(FirebaseAnalytics.Param.METHOD, "google")
                    analytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle)
                    onSuccess(user.displayName ?: user.email ?: "User")
                }
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                onError(e.localizedMessage ?: "Error")
            }
        }
    }
}
