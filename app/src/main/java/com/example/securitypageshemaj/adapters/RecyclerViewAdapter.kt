package com.example.securitypageshemaj.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.securitypageshemaj.databinding.ButtonLayoutBinding
import com.example.securitypageshemaj.databinding.NumberItemBinding
import com.example.securitypageshemaj.models.ButtonModel

typealias click = (count: Int) -> Unit
class RecyclerViewAdapter(private var items: MutableList<ButtonModel>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    lateinit var itemClick: click

    inner class NumberViewHolder(private var binding: NumberItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind() {
            val model = items[adapterPosition]
            binding.textView.text = model.number.toString()
            binding.root.setOnClickListener {
                itemClick(adapterPosition)
            }
        }
    }

    inner class ActionViewHolder(private var binding: ButtonLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind() {
            val model = items[adapterPosition]
            model.src?.let { binding.buttonImg.setImageResource(it) }
            binding.root.setOnClickListener {
                itemClick(adapterPosition)
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 1)
            NumberViewHolder(
                NumberItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        else
            ActionViewHolder(
                ButtonLayoutBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ActionViewHolder -> holder.bind()
            is NumberViewHolder -> holder.bind()
        }
    }

    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int {
        val model = items[position]
        return if (model.src != null) {
            2
        } else {
            1
        }
    }
}