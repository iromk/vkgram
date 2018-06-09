package pro.xite.dev.vkgram.localalbum

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import timber.log.Timber

/**
 * Created by Roman Syrchin on 6/3/18.
 */
@InjectViewState
class LocalAlbumPresenter(private val m: LocalAlbumModel) : MvpPresenter<AlbumView>() {

    val count: Int
        get() = m.count

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        viewState.initRecyclerView()
    }

    fun showCard(itemView: AlbumItem, position: Int) {
        val pictureFile : String? = m.getPicture(position)
        pictureFile ?: Timber.w("No picture file found")
        itemView.setImage(pictureFile)
    }


}
