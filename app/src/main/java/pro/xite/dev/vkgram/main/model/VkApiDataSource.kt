package pro.xite.dev.vkgram.main.model

import android.arch.lifecycle.LiveData
import com.vk.sdk.api.model.VKApiUserFull

/**
 * Created by Roman Syrchin on 7/1/18.
 */
interface VkApiDataSource {

    val user: VKApiUserFull
    fun getLoggedInUser() : VKApiUserFull
    fun clearLoggedInUser()
    fun getLoggedInUserLiveData() : LiveData<VKApiUserFull>
//    fun getFollowersLiveData(): LiveData<VKUsersArray>
//    fun executeGetFollowers(listener : VKRequest.VKRequestListener)

}
