package pro.xite.dev.vkgram.followers.model

import com.vk.sdk.api.VKRequest
import com.vk.sdk.api.VKResponse
import com.vk.sdk.api.model.VKUsersArray

import io.reactivex.Observable
import pro.xite.dev.vkgram.main.model.VkApiDataSource

/**
 * Created by Roman Syrchin on 7/3/18.
 */
class FollowersRepo internal constructor(internal var vkDataSource: VkApiDataSource) {

    val paolosFollowers: Observable<VKUsersArray>
        get() = Observable.create { emitter ->
            vkDataSource.executeGetFollowers( object : VKRequest.VKRequestListener() {
                override fun onComplete(response: VKResponse?) {
                    emitter.onNext(response?.parsedModel as VKUsersArray)
                    emitter.onComplete()
                    super.onComplete(response)
                }
            })
        }

}
