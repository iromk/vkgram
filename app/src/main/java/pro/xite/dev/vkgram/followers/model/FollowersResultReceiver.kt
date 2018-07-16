package pro.xite.dev.vkgram.followers.model

import android.app.Activity.RESULT_OK
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
            RESULT_OK -> {
                val vkUsersArray = resultData?.get(VKUsersArray::class.java.simpleName) as VKUsersArray
                callback.onSuccess(vkUsersArray)
            }
        }
    }

    interface Callback {
        fun onSuccess(data: VKUsersArray)
        fun onError(e: Exception)
    }
}