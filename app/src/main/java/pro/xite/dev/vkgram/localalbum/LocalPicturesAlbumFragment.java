package pro.xite.dev.vkgram.localalbum;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import pro.xite.dev.vkgram.Application;
import pro.xite.dev.vkgram.R;

public class LocalPicturesAlbumFragment extends Fragment {

    private static final String TAG = String.format("%s/%s", Application.APP_TAG, LocalPicturesAlbumFragment.class.getSimpleName());

    @BindView(R.id.recycler_view) RecyclerView recyclerView;

    public LocalPicturesAlbumFragment() {}

    public static LocalPicturesAlbumFragment newInstance() {
        return new LocalPicturesAlbumFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        final View view = inflater.inflate(R.layout.content_recycler, container, false);
        ButterKnife.bind(this, view);
        initRecycler(view);
        loadPictures();
        return view;
    }

    private void loadPictures() {

    }

    private void initRecycler(View view) {
        Log.d(TAG, "initRecycler: ");
        recyclerView.setLayoutManager(new GridLayoutManager(view.getContext(),2));
        recyclerView.setAdapter(new LocalAlbumAdapter());
    }

}
