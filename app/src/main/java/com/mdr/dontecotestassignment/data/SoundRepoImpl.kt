package com.mdr.dontecotestassignment.data

import android.content.Context
import android.provider.MediaStore
import android.provider.OpenableColumns
import androidx.core.net.toUri
import com.mdr.dontecotestassignment.domain.Sound
import com.mdr.dontecotestassignment.domain.SoundRepo
import com.mdr.dontecotestassignment.domain.StringUri
import java.lang.Exception

class SoundRepoImpl(private val context: Context) : SoundRepo {
    override fun provideSound(uri: StringUri): Sound? {
        val fileName = tryRetrieveFileName(uri)
        return fileName?.let { Sound(fileName, uri) }
    }

    private fun tryRetrieveFileName(uri: StringUri): String? {
        try {
            context.contentResolver.query(
                uri.toUri(),
                arrayOf(OpenableColumns.DISPLAY_NAME),
                null,
                null,
                null
            )?.use { cursor ->
                cursor.moveToFirst()
                val fileName = cursor
                    .getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME)
                    .let(cursor::getString)
                return fileName
            }
        } catch (e: Exception) {}

        return null
    }
}