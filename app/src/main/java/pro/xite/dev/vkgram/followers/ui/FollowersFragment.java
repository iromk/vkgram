package pro.xite.dev.vkgram.followers.ui;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arellomobile.mvp.MvpAppCompatFragment;
import com.vk.sdk.api.model.VKApiUserFull;

import butterknife.BindView;
import butterknife.ButterKnife;
import pro.xite.dev.vkgram.R;
import timber.log.Timber;

public class FollowersFragment extends MvpAppCompatFragment {//} implements FollowersView {

    //private static final String TAG = String.format("%s/%s", Application.APP_TAG, FollowersFragment.class.getSimpleName());

    private static final String ARG_VKUSER = "vkUser";

    @BindView(R.id.recycler_view) RecyclerView recyclerView;
    private VKApiUserFull vkUser;

//    @InjectPresenter
//    FollowersPresenter p;
//

/*
    @ProvidePresenter//(type = PresenterType.GLOBAL)
    public FollowersPresenter provideFollowersPresenter() {
        Timber.v("provideFollowersPresenter");
        return new FollowersPresenter(
                AndroidSchedulers.mainThread(),
                ViewModelProviders.of(this).get(VkApiViewModel.class));
    }
*/


    public FollowersFragment() {
    }

    public static FollowersFragment getInstance(VKApiUserFull vkUser) {
        Timber.d("getInstance:");
        FollowersFragment ff = new FollowersFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_VKUSER, vkUser);
        ff.setArguments(args);
        return ff;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.d("onCreate fragment");
        if (getArguments() != null) {
            vkUser = getArguments().getParcelable(ARG_VKUSER);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Timber.d("onCreateView: ");
        final View view = inflater.inflate(R.layout.content_recycler, container, false);

        ButterKnife.bind(this, view);

        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(
                new FollowersAdapter(
                        getMvpDelegate()));
//                ,
//                        AndroidSchedulers.mainThread(),
//                        ViewModelProviders.of(this).get(VkApiViewModel.class)));
//        recyclerView.setAdapter(new FollowersAdapter(p));
//        final VkApiViewModel model = ViewModelProviders.of(this).get(VkApiViewModel.class);
//        model.getFollowersLiveData().observe(this,
//                followers -> recyclerView.setAdapter(new FollowersAdapter(followers)) );
//
        return view;
    }

}
