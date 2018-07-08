package pro.xite.dev.vkgram.main.model.vkapi;

import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiUserFull;
import com.vk.sdk.api.model.VKUsersArray;

import org.jetbrains.annotations.NotNull;

import java.io.UnsupportedEncodingException;
import java.util.Locale;

import pro.xite.dev.vkgram.main.Application;
import pro.xite.dev.vkgram.main.model.VkApiDataSource;
import timber.log.Timber;

public class VkApiViewModel extends AndroidViewModel implements VkApiDataSource {

    private static final String TAG = String.format("%s/%s", Application.APP_TAG, VkApiViewModel.class.getSimpleName());

    private MutableLiveData<VKUsersArray> vkFollowers;
    private MutableLiveData<VKApiUserFull> vkLoggedInUser;

    public VkApiViewModel(@NonNull android.app.Application application) {
        super(application);
    }

    public LiveData<VKUsersArray> getFollowersLiveData() {
        if (vkFollowers == null) {
            vkFollowers = new MutableLiveData<>();
            loadFollowers();
        }
        return vkFollowers;
    }

    public VKApiUserFull getLoggedInUser() {
        return getLoggedInUserLiveData().getValue();
    }

    @NonNull
    public LiveData<VKApiUserFull> getLoggedInUserLiveData() {
        if (vkLoggedInUser == null)
            vkLoggedInUser = new MutableLiveData<>();
        if (VKAccessToken.currentToken() != null)
            loadVkUser(VKAccessToken.currentToken().userId);
        return vkLoggedInUser;
    }

    private void loadVkUser(String userId) {
        VKRequest request = new VKRequest("users.get",
                VKParameters.from(
                        VKApiConst.USER_ID, userId,
                        VKApiConst.FIELDS, "id,first_name,last_name,sex,bdate,city,photo_200"),
                        VKUsersArray.class);
        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            @SuppressWarnings("unchecked")
            public void onComplete(VKResponse response) {
                try {
                    VKUsersArray users = (VKUsersArray) (response.parsedModel);
                    vkLoggedInUser.setValue(users.get(0));
                } catch (ClassCastException e) {
                    Timber.tag(TAG).e(String.format(Locale.US, "onComplete: %s", e));
                }
            }
        });

    }

    private void loadFollowers() {
        Timber.tag(TAG).d("loadFollowers");
        Timber.tag(TAG).d("loadFollowers (vkUser != null)");
        VKRequest request = new VKRequest("users.getFollowers");
        request.addExtraParameters(
                VKParameters.from(VKApiConst.USER_ID, 1,
                        VKApiConst.FIELDS, "id,first_name,last_name,sex,bdate,city,photo_200",
                        VKApiConst.COUNT, 333,
                        VKApiConst.OFFSET, 0
                ));
        request.setModelClass(VKUsersArray.class);
        try {
            Timber.tag(TAG).i(String.format("loadFollowers:\n%s", request.getPreparedRequest().getQuery()));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                vkFollowers.setValue((VKUsersArray) (response.parsedModel));
                Timber.tag(TAG).d("onComplete: loadFollowers %s", vkFollowers.getValue().size());
                super.onComplete(response);
            }

            @Override
            public void onError(VKError error) {
                Timber.tag(TAG).e("onError: loadFollowers " + error);
                super.onError(error);
            }
        });
    }

    @Override
    public void executeGetFollowers(VKRequest.VKRequestListener listener) {
        VKRequest request = new VKRequest("users.getFollowers");
        request.addExtraParameters(
                VKParameters.from(VKApiConst.USER_ID, 1,
                        VKApiConst.FIELDS, "id,first_name,last_name,sex,bdate,city,photo_200",
                        VKApiConst.COUNT, 333,
                        VKApiConst.OFFSET, 0
                ));
        request.setModelClass(VKUsersArray.class);
        request.executeWithListener(listener);
    }

    @Override
    public void clearLoggedInUser() {
        vkLoggedInUser.setValue(null);
    }

    @NotNull
    @Override
    public VKApiUserFull getUser() {
        return null;
    }
}
