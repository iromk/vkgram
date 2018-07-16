package pro.xite.dev.vkgram.followers.model

import com.vk.sdk.api.model.VKUsersArray
import io.reactivex.Observable

/**
 * Created by Roman Syrchin on 7/3/18.
 */
class FollowersRepo internal constructor(private val remote: FollowersDataSource, private val local: FollowersDataSource) : FollowersDataSource {

    override val followers: Observable<VKUsersArray> get() = remote.followers

}
