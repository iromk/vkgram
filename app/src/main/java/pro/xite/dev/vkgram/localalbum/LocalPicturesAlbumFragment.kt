package pro.xite.dev.vkgram.localalbum

import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.content_recycler.*
import pro.xite.dev.vkgram.R
/**
 * Created by Roman Syrchin on 6/3/18.
 */
class LocalPicturesAlbumFragment : MvpAppCompatFragment(), AlbumView  {

    @InjectPresenter
    lateinit var p : LocalAlbumPresenter

    companion object {
        @JvmStatic
        fun create(): LocalPicturesAlbumFragment = LocalPicturesAlbumFragment ()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.content_recycler, container, false)
    }

    @ProvidePresenter
    fun provideAlbumPresent() : LocalAlbumPresenter {
        return LocalAlbumPresenter(LocalAlbumModel(context))
    }

    override fun initRecyclerView() {
        recycler_view.setItemViewCacheSize(22)
        recycler_view.layoutManager = GridLayoutManager(context, 2)
        recycler_view.adapter = LocalAlbumAdapter(p)
    }


}

