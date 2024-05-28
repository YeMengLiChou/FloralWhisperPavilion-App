package cn.li.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import cn.li.datastore.proto.CachedPreferences
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FwpCachedSerializer @Inject constructor() : Serializer<CachedPreferences> {
    override val defaultValue: CachedPreferences
        get() = CachedPreferences.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): CachedPreferences {
        try {
            return CachedPreferences.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }

    }

    override suspend fun writeTo(t: CachedPreferences, output: OutputStream) {
        t.writeTo(output)
    }

}