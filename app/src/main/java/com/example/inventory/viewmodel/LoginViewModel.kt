package com.example.inventory.viewmodel

import android.app.Application
import android.content.Intent
import android.provider.Settings
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class Event<T>(private val content: T) {
    private var hasBeenHandled = false

    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }
}

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    enum class AuthState {
        SUCCESS,
        FAILED,
        ERROR
    }

    private val _authState = MutableLiveData<Event<AuthState>>()
    val authState: LiveData<Event<AuthState>> = _authState

    private val _errorMessage = MutableLiveData<Event<String>>()
    val errorMessage: LiveData<Event<String>> = _errorMessage
    private val _enrollmentIntent = MutableLiveData<Event<Intent>>()
    val enrollmentIntent: LiveData<Event<Intent>> = _enrollmentIntent

    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    fun startAuthentication(activity: Fragment) {
        val biometricManager = BiometricManager.from(getApplication())
        val authenticators = BIOMETRIC_STRONG

        when (biometricManager.canAuthenticate(authenticators)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                setupBiometricPrompt(activity)
                biometricPrompt.authenticate(promptInfo)
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                    putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED, authenticators)
                }
                _enrollmentIntent.value = Event(enrollIntent)
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> _errorMessage.value = Event("No biometric features available on this device.")
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> _errorMessage.value = Event("Biometric features are currently unavailable.")
            else -> _errorMessage.value = Event("An unknown biometric error occurred.")
        }
    }

    private fun setupBiometricPrompt(activity: Fragment) {
        val executor = ContextCompat.getMainExecutor(getApplication())

        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                _authState.value = Event(AuthState.SUCCESS)
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                _authState.value = Event(AuthState.FAILED)
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                if (errorCode != BiometricPrompt.ERROR_NEGATIVE_BUTTON && errorCode != BiometricPrompt.ERROR_USER_CANCELED) {
                    _errorMessage.value = Event("Authentication error: $errString")
                    _authState.value = Event(AuthState.ERROR)
                }
            }
        }

        biometricPrompt = BiometricPrompt(activity, executor, callback)

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric login")
            .setSubtitle("Log in using your finger")
            .setNegativeButtonText("Cancel")
            .build()
    }
}