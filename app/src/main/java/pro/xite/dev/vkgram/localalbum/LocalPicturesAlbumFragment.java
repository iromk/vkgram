package pro.xite.dev.vkgram.localalbum;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arellomobile.mvp.MvpAppCompatFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;

import butterknife.BindView;
import butterknife.ButterKnife;
import pro.xite.dev.vkgram.R;
import timber.log.Timber;

public class LocalPicturesAlbumFragment extends MvpAppCompatFragment implements AlbumView {

    @InjectPresenter
    LocalAlbumPresenter p;

    @BindView(R.id.recycler_view) RecyclerView recyclerView;

    public LocalPicturesAlbumFragment() {}

    public static LocalPicturesAlbumFragment newInstance() {
        return new LocalPicturesAlbumFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Timber.d("onCreateView: ");
        return inflater.inflate(R.layout.content_recycler, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Timber.d("onViewCreated");
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        loadPictures();
    }

    @ProvidePresenter
    public LocalAlbumPresenter providePresenter() {
        Timber.d("providePresenter");
        return new LocalAlbumPresenter();
    }

    private void loadPictures() {

    }

    @Override
    public void initRecyclerView() {
        Timber.d("initRecycler: ");
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        recyclerView.setAdapter(new LocalAlbumAdapter());
    }
}
