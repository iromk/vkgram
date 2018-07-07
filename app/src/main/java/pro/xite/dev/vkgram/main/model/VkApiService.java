package pro.xite.dev.vkgram.main.model;

import com.vk.sdk.api.model.VKUsersArray;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Roman Syrchin on 7/7/18.
 */
public interface VkApiService {

    @GET("/method/users.getFollowers")
    Observable<VKUsersArray> getFollowers(@Query("user_id") String uid);

    @GET("/method/users.getFollowers")
    Observable<VKUsersArray> getFollowers(@Query("user_id") String uid, @Query("count") int count);

    @GET("/method/users.getFollowers")
    Observable<VKUsersArray> getFollowers(@Query("user_id") String uid, @Query("count") int count, @Query("token") String token);
}
