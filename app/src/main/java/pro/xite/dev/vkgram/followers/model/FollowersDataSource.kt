package pro.xite.dev.vkgram.followers.model

import com.vk.sdk.api.model.VKUsersArray
import io.reactivex.Observable

/**
 * Created by Roman Syrchin on 7/16/18.
 */
interface FollowersDataSource {

    val followers : Observable<VKUsersArray>
}