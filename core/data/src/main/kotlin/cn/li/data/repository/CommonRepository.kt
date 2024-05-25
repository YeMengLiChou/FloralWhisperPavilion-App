package cn.li.data.repository

import android.graphics.Bitmap
import android.net.Uri
import cn.li.network.dto.ApiResult
import cn.li.network.retrofit.datasource.RetrofitCommonDataSource
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

interface CommonRepository {

    suspend fun uploadFile(file: File): ApiResult<String>

    suspend fun uploadFile(bitmap: Bitmap): ApiResult<String>

    suspend fun uploadFile(uri: Uri): ApiResult<String>
}

