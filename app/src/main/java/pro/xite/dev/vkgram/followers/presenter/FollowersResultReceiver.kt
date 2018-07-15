package pro.xite.dev.vkgram.followers.presenter

import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver
import com.vk.sdk.api.model.VKUsersArray
import timber.log.Timber

/**
 * Created by Roman Syrchin on 7/15/18.
 */
class FollowersResultReceiver (handler: Handler?, private val callback: Callback) : ResultReceiver(handler) {

    override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {
        super.onReceiveResult(resultCode, resultData)
        Timber.v("FollowersResultReceiver.onReceiveResult")


        when(resultCode) {
            23 -> { callback.onSuccess(VKUsersArray()) }
        }
    }

    interface Callback {
        fun onSuccess(data: VKUsersArray)
        fun onError(e: Exception)
    }
}