package pro.xite.dev.vkgram.followers.model

import com.vk.sdk.api.model.VKUsersArray
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import pro.xite.dev.vkgram.main.model.vkapi.VkApiService

/**
 * Created by Roman Syrchin on 7/3/18.
 */
class FollowersRepo internal constructor(internal val vkApi: VkApiService) {

    val paolosFollowers: Observable<VKUsersArray>
        get() = vkApi.getFollowers("1", 10, "id,first_name,last_name,sex,bdate,city,photo_100")
                     .subscribeOn(Schedulers.io())
                .onErrorReturn { VKUsersArray() }
//            Observable.create { emitter ->
//            vkDataSource.executeGetFollowers( object : VKRequest.VKRequestListener() {
//                override fun onComplete(response: VKResponse?) {
//                    emitter.onNext(response?.parsedModel as VKUsersArray)
//                    emitter.onComplete()
//                    super.onComplete(response)
//                }
//            })
//        }

}
