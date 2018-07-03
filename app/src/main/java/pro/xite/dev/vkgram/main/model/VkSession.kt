package pro.xite.dev.vkgram.main.model

import com.vk.sdk.VKCallback
import com.vk.sdk.VKSdk
import pro.xite.dev.vkgram.main.Application

/**
 * Created by Roman Syrchin on 6/30/18.
 */
class VkSession {

    companion object {
        @JvmStatic
        fun init(callback: VKCallback<VKSdk.LoginState>) {
            VKSdk.wakeUpSession(Application.getInstance().applicationContext, callback)
        }
    }
}
