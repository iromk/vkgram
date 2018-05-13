package pro.xite.dev.vkgram;

import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKUsersArray;

import java.io.UnsupportedEncodingException;

public class VkViewModel extends AndroidViewModel {

    private static final String TAG = String.format("%s/%s", Application.APP_TAG, VkViewModel.class.getSimpleName());

    private MutableLiveData<VKUsersArray> vkFollowers;

    public VkViewModel(@NonNull android.app.Application application) {
        super(application);
    }

    public LiveData<VKUsersArray> getFollowers() {
            if (vkFollowers == null) {
                vkFollowers = new MutableLiveData<>();
                loadFollowers();
            }
            return vkFollowers;
        }

        private void loadFollowers() {
            Log.d(TAG, "loadFollowers");
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
                        vkFollowers.setValue((VKUsersArray) (response.parsedModel));
                        Log.d(TAG, "onComplete: loadFollowers " + vkFollowers.getValue().size());
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
