package pro.xite.dev.vkgram;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

    @BindView(R.id.recycler_view) RecyclerView recyclerView;
    TextView tvVkUserName;
    private LinearLayoutManager layoutManager;
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
        if (getArguments() != null) {
            vkUser = getArguments().getParcelable(ARG_VKUSER);
        }
    }

    private void initRecycler(View view) {
        layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);
        if(vkFollowers != null) {
            recyclerView.setAdapter(new FollowersAdapter(vkFollowers));
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_main, container, false);
        ButterKnife.bind(this, view);
        initRecycler(view);
        loadFollowers();
        return view;
    }

    private void loadFollowers() {
        if (vkUser != null) {
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
        return;
    }
}
