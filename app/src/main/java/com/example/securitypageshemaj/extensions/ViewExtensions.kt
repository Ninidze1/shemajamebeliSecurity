package com.example.securitypageshemaj.extensions

import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.view.children
import com.example.securitypageshemaj.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun ViewGroup.changeByStep(position: Int, resource: Int) {
    getChildAt(position)
        .setBackgroundResource(resource)
}

fun ViewGroup.changeAll(resource: Int, withDelay: Boolean = false) {
        children.forEach { child ->
            child.setBackgroundResource(resource)
        }
}

fun ViewGroup.shake(resource: Int) {
    CoroutineScope(Dispatchers.Main).launch {
        startAnimation(AnimationUtils.loadAnimation(context, R.anim.shake))
        delay(900)
        changeAll(resource)
    }
}