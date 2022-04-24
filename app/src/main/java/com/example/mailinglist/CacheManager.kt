package com.example.mailinglist

import android.content.Context
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class CacheManager {
    fun cacheData(context: Context, data: ByteArray, name: String) {
        val cacheDir: File = context.cacheDir
        val file = File(cacheDir, name)
        val outputStream = FileOutputStream(file)

        outputStream.use {
            it.write(data)
        }
    }

    fun retrieveData(context: Context, name: String): ByteArray? {
        val cacheDir: File = context.cacheDir
        val file = File(cacheDir, name)

        if (!file.exists()) {
            return null
        }

        val inputStream = FileInputStream(file)
        val data: ByteArray
        inputStream.use {
            data = it.readBytes()
        }

        return data
    }

    fun cleanDir(context: Context) {
        val cacheDir: File = context.cacheDir
        val files = cacheDir.listFiles()

        if (files != null) {
            for (file in files) {
                file.delete()
            }
        }
    }
}
