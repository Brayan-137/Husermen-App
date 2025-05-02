package com.example.husermenapp

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

object FragmentUtils {

    fun replaceFragment(
        manager: FragmentManager,
        containerViewId: Int,
        fragment: Fragment,
        isAddToBackStack: Boolean = true,
    ) {
        val fragmentTransition = manager.beginTransaction()
            .replace(containerViewId, fragment)

        if (isAddToBackStack) {
            fragmentTransition.addToBackStack(null)
        }

        fragmentTransition.commit()
    }

    fun applyTextViewFormat(value: String): String {
        return value.replaceFirstChar { it.uppercaseChar() }
    }
}