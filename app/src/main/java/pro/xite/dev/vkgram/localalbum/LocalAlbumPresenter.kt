package pro.xite.dev.vkgram.localalbum

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import timber.log.Timber

/**
 * Created by Roman Syrchin on 6/3/18.
 */
@InjectViewState
class LocalAlbumPresenter : MvpPresenter<AlbumView>() {

    override fun onFirstViewAttach() {
        Timber.v("onFirstViewAttach")
        super.onFirstViewAttach()

        viewState.initRecyclerView()
    }

}
