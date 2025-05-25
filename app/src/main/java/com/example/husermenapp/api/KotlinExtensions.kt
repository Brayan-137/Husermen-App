package com.example.husermenapp.api

import com.google.android.gms.tasks.Task
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@Throws(InterruptedException::class)
fun <T> Task<T>.await(): T {
    val latch = CountDownLatch(1)
    this.addOnCompleteListener { latch.countDown() }
    latch.await(10, TimeUnit.SECONDS)

    if (!isComplete) {
        throw InterruptedException("Timeout waiting for Firebase task")
    }

    return result ?: throw Exception("Firebase task returned null")
}