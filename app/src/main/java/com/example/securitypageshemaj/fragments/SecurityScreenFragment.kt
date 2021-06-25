package com.example.securitypageshemaj.fragments

import android.os.Bundle
import android.util.Log.d
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.securitypageshemaj.R
import com.example.securitypageshemaj.adapters.RecyclerViewAdapter
import com.example.securitypageshemaj.adapters.click
import com.example.securitypageshemaj.databinding.SecurityScreenFragmentBinding
import com.example.securitypageshemaj.models.ButtonModel

class SecurityScreenFragment : Fragment() {

    private val viewModel: SecurityScreenViewModel by viewModels()
    private lateinit var binding: SecurityScreenFragmentBinding
    private lateinit var adapter: RecyclerViewAdapter

    private val items = mutableListOf<ButtonModel>()
    private val password = mutableListOf<Int>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SecurityScreenFragmentBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    private fun init() {
        recyclerInit()
        adapter.itemClick = { position ->
            if (password.size != 4) {
                items[position].number?.let {
                    password.add(it)
                    d("tagtag", "${binding.indicatorHolder.children}")
                    d("tagtag", "$password")
                }
            } else {
                binding.indicatortitle.text = "Enter passcode again"
            }
        }
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

}