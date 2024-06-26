package cn.li.data.repository

import android.graphics.Bitmap
import android.net.Uri
import cn.li.network.dto.ApiResult
import cn.li.network.retrofit.datasource.RetrofitCommonDataSource
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiCommonRepository @Inject constructor(
    private val dataSource: RetrofitCommonDataSource
) : CommonRepository {

    override suspend fun uploadFile(file: File): ApiResult<String> {
        return dataSource.uploadFile(file)
    }

    override suspend fun uploadFile(bitmap: Bitmap): ApiResult<String> {
        return dataSource.uploadFile(bitmap)
    }

    override suspend fun uploadFile(uri: Uri): ApiResult<String> {
        return dataSource.uploadFile(uri)
    }
}