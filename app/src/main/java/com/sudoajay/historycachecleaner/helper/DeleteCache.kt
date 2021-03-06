package com.sudoajay.historycachecleaner.helper

import android.content.Context
import com.sudoajay.historyprivacycleaner.R
import java.io.File

internal object DeleteCache {
    fun deleteCache(context: Context) {
        try {

            deleteWithFile(context.cacheDir)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP)
                deleteWithFile(context.codeCacheDir)

            deleteWithFile(context.externalCacheDir!!)
            CustomToast.toastIt(
                context,
                context.getString(R.string.successfully_cache_data_is_deleted_text)
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun deleteWithFile(dir: File): Boolean {
        try {
            return when {
                dir.isDirectory -> {
                    val children = dir.listFiles()
                    for (i in children!!.indices) {
                        deleteWithFile(children[i])
                    }
                    dir.delete()
                }
                dir.isFile -> {
                    dir.delete()
                }
                else -> {
                    return false
                }
            }
        } catch (e: Exception) {
            return false
        }
    }

}