package com.example.inventory.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.inventory.R
import com.example.inventory.databinding.FragmentLoginAndRegistreBinding
import com.example.inventory.ui.MainActivity
import com.example.inventory.ui.widget.EXTRA_FROM_WIDGET   // 👈 IMPORTANTE
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginRegistreFragment : Fragment() {

    private var _binding: FragmentLoginAndRegistreBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginAndRegistreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogin.setOnClickListener {
            val email = binding.tietEmail.text.toString().trim()
            val password = binding.tietPassword.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(requireActivity()) { task ->
                        if (task.isSuccessful) {
                            Log.d("Login", "signInWithEmail:success")
                            goAfterAuthSuccess()
                        } else {
                            Log.w("Login", "signInWithEmail:failure", task.exception)
                            Toast.makeText(
                                context, "Authentication failed.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            } else {
                Toast.makeText(context, "Please enter email and password.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvRegistrarse.setOnClickListener {
            val email = binding.tietEmail.text.toString().trim()
            val password = binding.tietPassword.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(requireActivity()) { task ->
                        if (task.isSuccessful) {
                            Log.d("Register", "createUserWithEmail:success")
                            goAfterAuthSuccess()
                        } else {
                            Log.w("Register", "createUserWithEmail:failure", task.exception)
                            Toast.makeText(
                                context, "Registration failed.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            } else {
                Toast.makeText(context, "Please enter email and password.", Toast.LENGTH_SHORT).show()
            }
        }

        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val email = binding.tietEmail.text.toString().trim()
                val password = binding.tietPassword.text.toString().trim()
                val enabled = email.isNotEmpty() && password.isNotEmpty()
                binding.btnLogin.isEnabled = enabled
                binding.tvRegistrarse.isEnabled = enabled
                binding.btnLogin.alpha = if (enabled) 1.0f else 0.4f
                binding.tvRegistrarse.alpha = if (enabled) 1.0f else 0.4f
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }
        binding.tietEmail.addTextChangedListener(textWatcher)
        binding.tietPassword.addTextChangedListener(textWatcher)
    }

    /**
     * Lógica común después de un login o registro exitoso.
     * Si vino desde el widget → se cierra la Activity y vuelve al escritorio.
     * Si NO vino del widget → navega al Home normal.
     */
    private fun goAfterAuthSuccess() {
        val fromWidget = requireActivity()
            .intent
            .getBooleanExtra(EXTRA_FROM_WIDGET, false)

        if (fromWidget) {
            // ✅ Criterio 10: volver al widget (cerrar la activity)
            requireActivity().finish()
        } else {
            // Caso normal: ir al Home de la app
            (activity as? MainActivity)?.showHome()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

