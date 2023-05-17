package com.edimitre.handyapp.data.service

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.FileUtils
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import com.edimitre.handyapp.HandyAppEnvironment
import com.edimitre.handyapp.HandyAppEnvironment.TAG
import com.edimitre.handyapp.data.model.FileAsByte
import com.edimitre.handyapp.data.model.FileObject
import com.edimitre.handyapp.data.model.MemeTemplate
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream


class FileService {


    private val storageDirectory =
        File("${Environment.getExternalStorageDirectory()}/${HandyAppEnvironment.FILES_STORAGE_DIRECTORY}")

    private val memeStorageDirectory =
        File("$storageDirectory/${HandyAppEnvironment.MEME_STORAGE_DIRECTORY}")

    private val tempStorageDirectory =
        File("$storageDirectory/${HandyAppEnvironment.TEMP_STORAGE_DIRECTORY}")

    fun createStorageDirectory() {

        val folderName = HandyAppEnvironment.FILES_STORAGE_DIRECTORY

        val file = File("${Environment.getExternalStorageDirectory()}/$folderName")

        var folderCreated = false

        if (!file.exists()) {
            folderCreated = file.mkdir()
        }
        if (folderCreated) {
            Log.e(HandyAppEnvironment.TAG, "folder created")
        } else {

            Log.e(HandyAppEnvironment.TAG, "folder not created because it exists")
        }
    }

    private fun convertToBase64(attachment: File): String {
        return Base64.encodeToString(attachment.readBytes(), Base64.NO_WRAP)
    }

    fun getAllFiles(): ArrayList<FileObject> {

        val folderName = HandyAppEnvironment.FILES_STORAGE_DIRECTORY
        val directory = File("${Environment.getExternalStorageDirectory()}/$folderName")

        val files = directory.listFiles()


        val listFile = arrayListOf<FileObject>()


        if (files != null) {
            for (i in files.indices) {

                listFile.add(FileObject(files[i].name, files[i]))

            }
        }

        listFile.forEach { file ->
            if(file.actualFile!!.isDirectory){
                listFile.remove(file)
            }
        }

        return listFile
    }

    fun getFilesAsBytesList(): List<FileAsByte> {

        val allFiles = getAllFiles()

        val filesAsBytesList = arrayListOf<FileAsByte>()

        allFiles.forEach { file ->


            if (!file.actualFile!!.isDirectory) {
                val fileAsByte = FileAsByte(file.name, convertToBase64(file.actualFile))

                filesAsBytesList.add(fileAsByte)


            }
//
        }

        return filesAsBytesList
    }

    fun createLocalFiles(fileAsByteList: List<FileAsByte>) {

        fileAsByteList.forEach { file ->

            val localFile = File("${storageDirectory}/${file.name}")

            if (!localFile.exists()) {
                val bytes = Base64.decode(file.fileStringBase64 , Base64.NO_WRAP)
                localFile.writeBytes(bytes)

            }

        }

    }


    fun getBitmap(contentResolver: ContentResolver, fileUri: Uri?): Bitmap? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver, fileUri!!))
            } else {
                MediaStore.Images.Media.getBitmap(contentResolver, fileUri)
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun saveMemeTemplate(finalBitmap: Bitmap, templateName: String) {
        if (!memeStorageDirectory.exists()) {
            memeStorageDirectory.mkdirs()
        }
        val file = File(memeStorageDirectory, templateName)
        if (file.exists()) {
            file.delete()
        }
        try {
            val out = FileOutputStream(file)
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            out.flush()
            out.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun getBitmapFromBase64(base64Str: String): Bitmap? {
        val decodedBytes = Base64.decode(
            base64Str.substring(base64Str.indexOf(",") + 1),
            Base64.DEFAULT
        )
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }

    fun getBase64FromBitmap(bitmap: Bitmap): String? {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
    }

    fun getResizedBitmap(bm: Bitmap, newWidth: Int, newHeight: Int): Bitmap? {
        val width = bm.width
        val height = bm.height
        val scaleWidth = newWidth.toFloat() / width
        val scaleHeight = newHeight.toFloat() / height
        // CREATE A MATRIX FOR THE MANIPULATION
        val matrix = Matrix()
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight)

        // "RECREATE" THE NEW BITMAP
        return Bitmap.createBitmap(
            bm, 0, 0, width, height, matrix, false
        )
    }


    fun createTempFile(bitmap: Bitmap) {

        if (!tempStorageDirectory.exists()) {
            tempStorageDirectory.mkdirs()
        }
        val file = File(tempStorageDirectory, "temp_file.jpeg")
        if (file.exists()) {
            file.delete()
        }
        val out = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
        out.flush()
        out.close()
    }

    fun getUriFromTempFile(finalBitmap: Bitmap): Uri? {

        if (!tempStorageDirectory.exists()) {
            tempStorageDirectory.mkdirs()
        }
        val file = File(tempStorageDirectory, "temp_file.jpeg")
        if (file.exists()) {
            file.delete()
        }
        return try {
            val out = FileOutputStream(file)
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            out.flush()
            out.close()
            Uri.fromFile(file)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getTempFile(): File? {

        val file = File(tempStorageDirectory, "temp_file.jpeg")

        return if (file.exists()) {
            file
        } else {
            null
        }

    }

    fun clearTempFile() {
        val file = File(tempStorageDirectory, "temp_file.jpeg")
        if (file.exists()) {
            file.delete()
        }
    }

}

