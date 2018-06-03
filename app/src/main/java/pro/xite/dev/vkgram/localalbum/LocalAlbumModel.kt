package pro.xite.dev.vkgram.localalbum

/**
 * Created by Roman Syrchin on 6/3/18.
 */

class LocalAlbumModel {

    lateinit var x: String

    var count: Int = 0
        get() = 15
        private set(value) {  field = value }

    fun getPicture(index: Int): String = "/storage/emulated/0/Android/data/pro.xite.dev.vkgram/files/Pictures/images-3.jpeg"

}