package pro.xite.dev.vkgram.main.presenter

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.vk.sdk.VKAccessToken
import com.vk.sdk.VKCallback
import com.vk.sdk.VKSdk
import com.vk.sdk.api.VKError
import com.vk.sdk.api.model.VKApiUserFull
import io.reactivex.Scheduler
import pro.xite.dev.vkgram.main.model.ApplicationModel
import pro.xite.dev.vkgram.main.model.VkDataSource
import pro.xite.dev.vkgram.main.model.VkSession
import pro.xite.dev.vkgram.main.view.MainView
import timber.log.Timber

/**
 * Created by Roman Syrchin on 6/30/18.
 */
@InjectViewState
class MainViewPresenter(private val mainThreadScheduler: Scheduler,
                        private val vkDataSource: VkDataSource) : MvpPresenter<MainView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        initVkSession()
    }

    val m = ApplicationModel()

    fun onThemeChange(theme : Int ) {
        m.theme = theme
    }

    val theme : Int get() = m.theme


    fun getLoggedInUser() : VKApiUserFull {
        return vkDataSource.getLoggedInUser()
    }

    fun clearLoggedInUser() {
        vkDataSource.clearLoggedInUser()
    }

    private fun initVkSession() {
        if (VKSdk.isLoggedIn()) {
            Timber.v("U r logged in already")
            viewState.setUiStateLoggedIn()
            viewState.setActiveUser()
        } else
        VkSession.init(object : VKCallback<VKSdk.LoginState> {
            override fun onResult(res: VKSdk.LoginState) {
                when (res) {
                    VKSdk.LoginState.LoggedOut -> {
                        Timber.d("wakeUpSession: invoke login")
                        viewState.invokeVkLoginActivity()
                    }
                    VKSdk.LoginState.LoggedIn -> Timber.d("wakeUpSession: already logged in")
                    VKSdk.LoginState.Pending  -> Timber.d("wakeUpSession: pending state")
                    VKSdk.LoginState.Unknown  -> Timber.d("wakeUpSession: unknown state")
                }

            }

            override fun onError(error: VKError) {
                Timber.e("wakeUpSession.onError: %s", error.toString())
            }
        })
    }

    fun getVkDataSource() : VkDataSource = vkDataSource

    fun onLogoutVk() : Boolean {
        VKSdk.logout()
        viewState.setUiStateLoggedOut()
        return true
    }

    val onLogin = object : VKCallback<VKAccessToken>  {
        override fun onResult(res: VKAccessToken?) {
            if(VKSdk.isLoggedIn())
                viewState.setUiStateLoggedIn()
            else
                viewState.setUiStateLoggedOut()
            Timber.v("Access token %s", res?.accessToken)
        }

        override fun onError(error: VKError?) {
            Timber.e(error.toString())
        }

    }

}
