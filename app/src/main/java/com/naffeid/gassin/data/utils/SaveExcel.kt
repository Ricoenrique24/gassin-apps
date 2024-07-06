package com.naffeid.gassin.data.utils

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.naffeid.gassin.BuildConfig
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val FILENAME_FORMAT = "yyyyMMdd_HHmmss"
private val timeStamp: String = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(Date())
private val fileName: String = "laporan_transaksi_gas_$timeStamp.xlsx"
fun getFileUri(context: Context, directory: String): Uri {
    var uri: Uri? = null
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "application/vnd.ms-excel")
            put(MediaStore.MediaColumns.RELATIVE_PATH, directory)
        }
        uri = context.contentResolver.insert(
            MediaStore.Files.getContentUri("external"),
            contentValues
        )
    }
    return uri ?: getFileUriForPreQ(context, directory)
}

private fun getFileUriForPreQ(context: Context, directory: String): Uri {
    val filesDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
    val file = File(filesDir, "/$directory/$fileName")
    if (file.parentFile?.exists() == false) file.parentFile?.mkdir()
    return FileProvider.getUriForFile(
        context,
        "${BuildConfig.APPLICATION_ID}.fileprovider",
        file
    )
}