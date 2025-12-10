package com.example.inventory.fragments
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import com.example.inventory.viewmodel.LoginViewModel
import java.util.concurrent.Executor
import kotlin.getValue
import com.example.inventory.R
import com.example.inventory.sessions.SessionManager
import com.example.inventory.utils.Constants

class LoginFragment : Fragment() {

    private val loginViewModel: LoginViewModel by viewModels()
    private lateinit var sessionManager: SessionManager



    interface BiometricAuthListener {
        fun onBiometricAuthSuccess()
        fun onBiometricAuthError(error: String)
        fun onBiometricAuthFailed()
        fun onBiometricEnrollmentRequested(enrollIntent: Intent)
    }

    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    private var authListener: BiometricAuthListener? = null

    fun setBiometricAuthListener(listener: BiometricAuthListener) {
        this.authListener = listener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // enableEdgeToEdge()
        // setContentView(R.layout.activity_main)

        sessionManager = SessionManager(requireContext())

        executor = ContextCompat.getMainExecutor(requireContext())
        setupBiometricPrompt()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState);

        val mainLayout = view.findViewById<View>(R.id.login_layout)
        ViewCompat.setOnApplyWindowInsetsListener(mainLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val fingerprintIcon = view.findViewById<ImageView>(R.id.iv_fingerprint_login)
        fingerprintIcon.setOnClickListener {
            loginViewModel.startAuthentication(this)
        }
        setupObservers()

    }

    private fun setupBiometricPrompt() {
        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    if (errorCode != BiometricPrompt.ERROR_NEGATIVE_BUTTON && errorCode != BiometricPrompt.ERROR_USER_CANCELED) {
                        authListener?.onBiometricAuthError("Authentication error: $errString")
                    }
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    authListener?.onBiometricAuthSuccess()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    authListener?.onBiometricAuthFailed()
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric login")
            .setSubtitle("Log in using your finger")
            .setNegativeButtonText("Cancel")
            .build()
    }

    fun startAuthentication() {
        if (!isBiometricSupported()) {
            return
        }
        biometricPrompt.authenticate(promptInfo)
    }

    private fun isBiometricSupported(): Boolean {
        val biometricManager = BiometricManager.from(requireContext())
        val authenticators = BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.BIOMETRIC_WEAK

        when (biometricManager.canAuthenticate(authenticators)) {
            BiometricManager.BIOMETRIC_SUCCESS -> return true

            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                    putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED, authenticators)
                }
                authListener?.onBiometricEnrollmentRequested(enrollIntent)
                return false
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                authListener?.onBiometricAuthError("No biometric features available on this device.")
                return false
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                authListener?.onBiometricAuthError("Biometric features are currently unavailable.")
                return false
            }
            else -> {
                authListener?.onBiometricAuthError("An unknown biometric error occurred.")
                return false
            }
        }
    }

    private fun setupObservers() {
        loginViewModel.authState.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { state ->
                when (state) {
                    LoginViewModel.AuthState.SUCCESS -> {
                        Toast.makeText(requireContext(), Constants.AUTHENTICATED, Toast.LENGTH_SHORT).show()

                        sessionManager.saveLoginState(true)
                        navigateToHome()
                    }
                    LoginViewModel.AuthState.FAILED -> {
                        Toast.makeText(requireContext(), Constants.AUTHENTICATED_FAILED, Toast.LENGTH_SHORT).show()
                    }
                    LoginViewModel.AuthState.ERROR -> {
                    }
                }
            }
        }
        loginViewModel.errorMessage.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { message ->
                Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
            }
        }

        loginViewModel.enrollmentIntent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { intent ->
                Toast.makeText(requireContext(), Constants.ADD_FINGERPRINT, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun navigateToHome() {
        parentFragmentManager.commit {
            setReorderingAllowed(true)
            replace(R.id.main_fragment_container, HomeInventoryFragment())
        }
    }

    override fun onDetach() {
        super.onDetach()
        authListener = null
    }

    companion object {
        const val TAG = "LoginFragment"
        fun newInstance() = LoginFragment()
    }
}