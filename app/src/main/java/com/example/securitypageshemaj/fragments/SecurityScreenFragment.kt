package com.example.securitypageshemaj.fragments

import android.content.Intent
import android.content.SharedPreferences
import android.hardware.biometrics.BiometricManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log.d
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.securitypageshemaj.App
import com.example.securitypageshemaj.R
import com.example.securitypageshemaj.adapters.RecyclerViewAdapter
import com.example.securitypageshemaj.databinding.SecurityScreenFragmentBinding
import com.example.securitypageshemaj.extensions.changeAll
import com.example.securitypageshemaj.extensions.changeByStep
import com.example.securitypageshemaj.extensions.shake
import com.example.securitypageshemaj.extensions.showToast
import com.example.securitypageshemaj.models.ButtonModel
import java.util.concurrent.Executor

class SecurityScreenFragment : Fragment() {

    private val items = mutableListOf<ButtonModel>()

    private lateinit var binding: SecurityScreenFragmentBinding
    private lateinit var adapter: RecyclerViewAdapter

    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    private var passcode = mutableListOf<Int>()
    private val retryPasscode = mutableListOf<Int>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SecurityScreenFragmentBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    private fun init() {

        biometricAuth()
        encryptedSharedPref()
        recyclerInit()

        if (read() == "not_registered") {
            registerPasscode()
        }
        else {
            passcode = read().split(',').map { it.toInt() }.toMutableList()
            loginPasscode()
        }

        binding.resetButton.setOnClickListener {
            resetPasscode()
        }

    }

    private fun encryptedSharedPref() {
        val masterKey = MasterKey.Builder(requireContext()).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()

        val specificFileToStore = "passcode_state"
        sharedPreferences = EncryptedSharedPreferences
            .create(requireContext(), specificFileToStore, masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM)
    }

    private fun registerPasscode() {
        adapter.itemClick = { position ->

            if (passcode.size < 4)
                items[position].number?.let { digit -> addDigit(passcode, digit) }
            if (passcode.size == 4) {
                d("tagtag", "$passcode")
                write(passcode)
                binding.indicatortitle.text = App.context.getString(R.string.retry)
                loginPasscode()
            }
            if (position == 11 && passcode.isNotEmpty())
                deleteDigit(passcode)
            if (position == 9)
                requireContext().showToast("Enter passcode once")
        }
    }

    private fun loginPasscode() {
        binding.indicatorHolder.changeAll(R.drawable.number_button_shape)
        adapter.itemClick = { position ->

            if (retryPasscode.size != 4)
                items[position].number?.let { digit -> addDigit(retryPasscode, digit) }
            else retryPasscode.clear()
        if (retryPasscode.size == 4)
            matchingCheck()
        if (position == 11 && retryPasscode.isNotEmpty())
            deleteDigit(retryPasscode)
        if (position == 9)
            biometricPrompt.authenticate(promptInfo)
        }
    }

    private fun resetPasscode() {
        binding.indicatortitle.text = App.context.getString(R.string.register)
        binding.indicatorHolder.changeAll(R.drawable.number_button_shape)
        delete()
        passcode.clear()
        retryPasscode.clear()
        registerPasscode()
        requireContext().showToast("Password reseted")
    }


    private fun deleteDigit(list: MutableList<Int>) {
        list.removeLast()
        binding.indicatorHolder.changeByStep(list.size, R.drawable.number_button_shape)
    }

    private fun addDigit(list: MutableList<Int>, digit: Int) {
        list.add(digit)
        binding.indicatorHolder.changeByStep(list.size - 1, R.drawable.dot_active_item)
    }


    private fun matchingCheck() {
        if (passcode == retryPasscode)
            findNavController().navigate(R.id.action_securityScreenFragment_to_testFragment)
        else {
            requireContext().showToast("Passcode doesn't match")
            retryPasscode.clear()
            binding.indicatorHolder.changeAll(R.drawable.dot_incorrect_item, false)
            binding.indicatorHolder.shake(R.drawable.number_button_shape)
        }
    }


    private fun biometricAuth() {

        executor = ContextCompat.getMainExecutor(App.context)
        biometricPrompt = BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                d("loglog", "$errorCode $errString")

                when (errorCode) {
                    ERROR_HW_UNAVAILABLE -> requireActivity().showToast(errString as String)
                    ERROR_LOCKOUT -> requireActivity().showToast(errString as String)
                    ERROR_NO_BIOMETRICS -> {
                        startActivity(Intent(Settings.ACTION_SECURITY_SETTINGS))
                        requireActivity().showToast("Enable biometric authentication from Settings", Toast.LENGTH_LONG)
                    }
                }
            }

            override fun onAuthenticationSucceeded(result: AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                findNavController().navigate(R.id.action_securityScreenFragment_to_testFragment)
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                retryPasscode.clear()
                binding.indicatorHolder.changeAll(R.drawable.dot_incorrect_item, false)
                binding.indicatorHolder.shake(R.drawable.number_button_shape)
            }
        })

        promptInfo = PromptInfo.Builder()
            .setTitle("Simple authentication")
            .setSubtitle("Log in using your fingerprint")
            .setNegativeButtonText("Use passcode")
            .build()

    }


    private fun addButtons() {
        (1..9).forEach { each ->
            items.add(ButtonModel(each))
        }
        items.add(ButtonModel(null, R.drawable.ic_tuch_id))
        items.add(ButtonModel(0))
        items.add(ButtonModel(null, R.drawable.ic_back))
    }

    private fun recyclerInit() {
        addButtons()
        adapter = RecyclerViewAdapter(items)
        binding.recycler.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.recycler.adapter = adapter
    }


    private fun write(passcode: MutableList<Int>) {
        val passcodeToStr = passcode.joinToString(",")
        val editable = sharedPreferences.edit()
        editable.putString("passcode", passcodeToStr)
        editable.apply()
    }

    private fun read(): String {
        return sharedPreferences.getString("passcode", "not_registered").toString()
    }

    private fun delete() {
        val editable = sharedPreferences.edit()
        editable.clear()
        editable.apply()
    }



}