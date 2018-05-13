package pro.xite.dev.vkgram;


import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vk.sdk.api.model.VKApiUserFull;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FollowersFragment extends Fragment {

    private static final String TAG = String.format("%s/%s", Application.APP_TAG, FollowersFragment.class.getSimpleName());

    private static final String ARG_VKUSER = "vkUser";

    @BindView(R.id.recycler_view) RecyclerView recyclerView;
    private VKApiUserFull vkUser;

    public FollowersFragment() {
    }

    public static FollowersFragment newInstance(VKApiUserFull vkUser) {
        FollowersFragment ff = new FollowersFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_VKUSER, vkUser);
        ff.setArguments(args);
        return ff;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate fragment");
        if (getArguments() != null) {
            vkUser = getArguments().getParcelable(ARG_VKUSER);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.content_recycler, container, false);

        ButterKnife.bind(this, view);

        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        final VkViewModel model = ViewModelProviders.of(this).get(VkViewModel.class);
        model.getFollowers().observe(this,
                followers -> recyclerView.setAdapter(new FollowersAdapter(followers)) );

        return view;
    }

}
