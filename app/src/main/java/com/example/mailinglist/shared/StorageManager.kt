package com.example.mailinglist.shared

import android.content.Context
import jakarta.mail.Part
import org.joda.time.DateTime
import org.joda.time.Duration
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class StorageManager(private val context: Context) {
    fun cacheData(data: ByteArray, name: String) {
        val cacheDir: File = context.filesDir
        val file = File(cacheDir, name)

        if (file.exists()) {
            file.setLastModified(DateTime().millis)
        } else {
            val outputStream = FileOutputStream(file)

            outputStream.use {
                it.write(data)
            }
        }

        cleanDir()
    }

    fun cacheData(part: Part, name: String) {
        val cacheDir: File = context.filesDir
        val path = cacheDir.absolutePath

        val file = File(cacheDir, name)

        if (file.exists()) {
            file.setLastModified(DateTime().millis)
        } else {
            File("$path/$name").outputStream().use {
                part.inputStream.copyTo(it)
            }
        }

        cleanDir()
    }

    fun retrieveData(name: String): ByteArray? {
        val cacheDir: File = context.filesDir
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

    private fun cleanDir() {
        val cacheDir: File = context.filesDir
        val files = cacheDir.listFiles()

        val today = DateTime()

        if (files != null) {
            for (file in files) {
                val lastModified = DateTime(file.lastModified())
                val duration = Duration(lastModified, today)

                if (duration.standardDays > 10) {
                    file.delete()
                }
            }
        }
    }
}
