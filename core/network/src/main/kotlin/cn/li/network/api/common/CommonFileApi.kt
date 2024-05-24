package cn.li.network.api.common

import android.graphics.Bitmap
import android.net.Uri
import cn.li.network.dto.ApiResult
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import java.io.File


interface CommonFileApi {
    @POST("admin/common/upload")
    @Multipart
    suspend fun uploadFile(
        @Part file: MultipartBody.Part,
    ): ApiResult<String>
}

interface CommonDataSource {
    suspend fun uploadFile(file: File): ApiResult<String>

    suspend fun uploadFile(bitmap: Bitmap): ApiResult<String>

    suspend fun uploadFile(uri: Uri): ApiResult<String>

}