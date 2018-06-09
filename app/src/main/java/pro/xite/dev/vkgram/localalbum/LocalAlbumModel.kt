package pro.xite.dev.vkgram.localalbum

import android.content.Context
import android.os.Environment
import java.io.File

/**
 * Created by Roman Syrchin on 6/3/18.
 */

class LocalAlbumModel(private val context: Context?) {

    val px: Array<File>?
        get() = context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.listFiles()

    val count: Int get() = px?.size ?: 0

    fun getPicture(index: Int): String? =
            if(px?.get(index)?.exists() == true)
                px?.get(index)?.absolutePath
            else null

}

