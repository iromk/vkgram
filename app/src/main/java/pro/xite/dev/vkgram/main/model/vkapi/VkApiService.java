package pro.xite.dev.vkgram.main.model.vkapi;

import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.model.VKUsersArray;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Roman Syrchin on 7/7/18.
 */
public interface VkApiService {

    @GET("/method/users.getFollowers")
    Observable<VKUsersArray> getFollowers(@Query(VKApiConst.USER_ID) String uid);

    @GET("/method/users.getFollowers")
    Observable<VKUsersArray> getFollowers(@Query(VKApiConst.USER_ID) String uid, @Query(VKApiConst.COUNT) int count);

    @GET("/method/users.getFollowers")
    Observable<VKUsersArray> getFollowers(@Query(VKApiConst.USER_ID) String uid, @Query(VKApiConst.COUNT) int count, @Query(VKApiConst.FIELDS) String fields);
}
