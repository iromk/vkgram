package pro.xite.dev.vkgram.localalbum

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import pro.xite.dev.vkgram.R
import java.io.File

/**
 * Created by Roman Syrchin on 6/3/18.
 */

class LocalAlbumModel(private val context: Context) {

    val px: Array<File>? get() = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.listFiles()

    val count: Int get() = px?.size ?: 0

    private val imageStubSandler get() = BitmapFactory.decodeResource(context.resources, R.drawable.icons8_adam_sandler_filled_100)
    private val imageStubKim get() = BitmapFactory.decodeResource(context.resources, R.drawable.icons8_kim_kardashian_filled_100)

    fun getPicture(index: Int): Bitmap {
        px ?: return imageStubSandler
        if (index >= count) return imageStubKim
        return BitmapFactory.decodeFile(px?.get(index)?.absolutePath)
    }

}

