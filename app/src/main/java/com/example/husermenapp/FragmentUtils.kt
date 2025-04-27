package com.example.husermenapp

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

object FragmentUtils {

    fun replaceFragment(manager: FragmentManager, containerViewId: Int, fragment: Fragment) {
        val fragmentTransition = manager.beginTransaction()
        fragmentTransition.replace(containerViewId, fragment)
        fragmentTransition.commit()
    }

}