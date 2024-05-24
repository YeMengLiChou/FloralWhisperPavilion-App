package cn.li.network.retrofit.datasource

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.net.Uri
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import androidx.core.net.toFile
import cn.li.network.api.common.CommonDataSource
import cn.li.network.api.common.CommonFileApi
import cn.li.network.dto.ApiResult
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okio.BufferedSink
import okio.use
import retrofit2.Retrofit
import retrofit2.create
import java.io.ByteArrayOutputStream
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RetrofitCommonDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    retrofit: Retrofit
) : CommonDataSource {

    private val commonFileApi = retrofit.create<CommonFileApi>()
    override suspend fun uploadFile(file: File): ApiResult<String> {
        return commonFileApi.uploadFile(
            MultipartBody.Part.createFormData(
                name = "file",
                filename = file.name,
                body = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
            )
        )
    }

    override suspend fun uploadFile(bitmap: Bitmap): ApiResult<String> {
        return commonFileApi.uploadFile(
            MultipartBody.Part.createFormData(
                name = "file",
                filename = "${System.currentTimeMillis()}.jpeg",
                body = bitmap.asRequestBody(
                    CompressFormat.JPEG,
                    contentType = "multipart/form-data".toMediaTypeOrNull()
                )
            )
        )
    }

    override suspend fun uploadFile(uri: Uri): ApiResult<String> {
        return commonFileApi.uploadFile(
            MultipartBody.Part.createFormData(
                name = "file",
                filename = uri.name(context),
                body = uri.asRequestBody(context, "multipart/form-data".toMediaTypeOrNull())
            )
        )
    }
}

fun Bitmap.toByteArray(format: CompressFormat, quality: Int = 100): ByteArray {
    return ByteArrayOutputStream().let {
        compress(format, quality, it)
        return@let it.toByteArray()
    }
}

fun Bitmap.asRequestBody(
    format: CompressFormat,
    quality: Int = 100,
    contentType: MediaType? = null
): RequestBody {
    return object : RequestBody() {
        override fun contentType() = contentType

        override fun writeTo(sink: BufferedSink) {
            this@asRequestBody.compress(format, quality, sink.outputStream())
        }
    }
}

fun Uri.asRequestBody(
    context: Context,
    contentType: MediaType? = null
): RequestBody {
    return object : RequestBody() {
        override fun contentType() = contentType

        override fun writeTo(sink: BufferedSink) {
            context.contentResolver.openInputStream(this@asRequestBody).use { ins ->
                val buffer = ByteArray(1024)
                var bytesRead: Int
                while (ins!!.read(buffer).also { bytesRead = it } != -1) {
                    sink.write(buffer, 0, bytesRead)
                }
            }
        }
    }
}

/**
 * 返回 [Uri] 对应的名字，如果无法获取，则返回 [System.currentTimeMillis].扩展名
 * */
fun Uri.name(context: Context): String? {
    return when (scheme) {
        ContentResolver.SCHEME_FILE -> toFile().name
        ContentResolver.SCHEME_CONTENT -> {
            val cursor = context.contentResolver.query(this, null, null, null, null, null)
            cursor?.let {
                it.moveToFirst()
                val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                val displayName = it.getString(index)
                it.close()
                displayName
            } ?: "${System.currentTimeMillis()}.${
                MimeTypeMap.getSingleton()
                    .getExtensionFromMimeType(context.contentResolver.getType(this))
            }"
        }

        else -> "${System.currentTimeMillis()}.${
            MimeTypeMap.getSingleton()
                .getExtensionFromMimeType(context.contentResolver.getType(this))
        }"

    }
}
