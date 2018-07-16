package pro.xite.dev.vkgram.followers.model

import com.vk.sdk.api.model.VKUsersArray
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import pro.xite.dev.vkgram.main.model.vkapi.VkApiService

/**
 * Created by Roman Syrchin on 7/17/18.
 */
class FollowersVkRepo internal constructor(private val vkApi: VkApiService)  : FollowersDataSource {

    override val followers: Observable<VKUsersArray> get() = randomFollowersPack

    private val randomFollowersPack: Observable<VKUsersArray>
        get() = vkApi.getFollowers("1", 2345, 100,"id,first_name,last_name,sex,bdate,city,photo_200,photo_100")
                .subscribeOn(Schedulers.io())
                .onErrorReturn { VKUsersArray() }

}