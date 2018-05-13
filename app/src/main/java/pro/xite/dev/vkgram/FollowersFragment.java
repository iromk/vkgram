package pro.xite.dev.vkgram;


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

import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiUserFull;
import com.vk.sdk.api.model.VKUsersArray;

import java.io.UnsupportedEncodingException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FollowersFragment extends Fragment {

    private static final String TAG = String.format("%s/%s", Application.APP_TAG, FollowersFragment.class.getSimpleName());

    private static final String ARG_VKUSER = "vkUser";

    public static final String KEY_VK_FOLLOWERS = "VK_FOLLOWERS";

    @BindView(R.id.recycler_view) RecyclerView recyclerView;
    private VKUsersArray vkFollowers;
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

    private void initRecycler(View view) {
        Log.d(TAG, "initRecycler: ");
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        if(vkFollowers != null) {
            recyclerView.setAdapter(new FollowersAdapter(vkFollowers));
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        Log.d(TAG, "onSaveInstanceState");
        if(vkFollowers != null) outState.putParcelable(KEY_VK_FOLLOWERS, vkFollowers);
        super.onSaveInstanceState(outState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_recycler, container, false);
        if(savedInstanceState != null && savedInstanceState.containsKey(KEY_VK_FOLLOWERS)) {
            vkFollowers = savedInstanceState.getParcelable(KEY_VK_FOLLOWERS);
        } else {
            loadFollowers();
        }
        ButterKnife.bind(this, view);
        initRecycler(view);
        return view;
    }

    private void loadFollowers() {
        Log.d(TAG, "loadFollowers");
        if (vkUser != null) {
            Log.d(TAG, "loadFollowers (vkUser != null)");
            VKRequest request = new VKRequest("users.getFollowers");
            request.addExtraParameters(
                    VKParameters.from(VKApiConst.USER_ID, 1,
                            VKApiConst.FIELDS, "id,first_name,last_name,sex,bdate,city,photo_200",
                            VKApiConst.COUNT, 333,
                            VKApiConst.OFFSET, 0
                    ));
            request.setModelClass(VKUsersArray.class);
            try {
                Log.i(TAG, String.format("loadFollowers:\n%s", request.getPreparedRequest().getQuery().toString()));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            request.executeWithListener(new VKRequest.VKRequestListener() {
                @Override
                public void onComplete(VKResponse response) {
                    vkFollowers = (VKUsersArray) (response.parsedModel);
                    Log.d(TAG, "onComplete: loadFollowers " + vkFollowers.size());
                    recyclerView.setAdapter(new FollowersAdapter(vkFollowers));
                    super.onComplete(response);
                }

                @Override
                public void onError(VKError error) {
                    Log.e(TAG, "onError: loadFollowers " + error);
                    super.onError(error);
                }
            });
        }
    }
}
