package com.example.securitypageshemaj.fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.securitypageshemaj.R
import com.example.securitypageshemaj.databinding.TestFragmentBinding

class TestFragment : Fragment() {

    private lateinit var binding: TestFragmentBinding
    private lateinit var viewModel: TestViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = TestFragmentBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    private fun init() {

    }

}