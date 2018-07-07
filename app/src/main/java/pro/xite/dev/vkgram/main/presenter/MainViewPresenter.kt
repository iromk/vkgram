package pro.xite.dev.vkgram.main.presenter

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.vk.sdk.VKAccessToken
import com.vk.sdk.VKCallback
import com.vk.sdk.VKSdk
import com.vk.sdk.api.VKError
import io.reactivex.Scheduler
import pro.xite.dev.vkgram.main.Application
import pro.xite.dev.vkgram.main.model.ApplicationModel
import pro.xite.dev.vkgram.main.model.VkSession
import pro.xite.dev.vkgram.main.view.MainView
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by Roman Syrchin on 6/30/18.
 */
@InjectViewState
class MainViewPresenter() : MvpPresenter<MainView>() {

    init {
        Application.getAppComponent().inject(this)
    }
    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        initVkSession()
    }

    @Inject lateinit var mainThreadScheduler: Scheduler
    val modelApp = ApplicationModel()

    fun onThemeChange(theme : Int ) {
        modelApp.theme = theme
    }

    val theme : Int get() = modelApp.theme


/*    fun getLoggedInUser() : VKApiUserFull {
        return vkDataSource.getLoggedInUser()
    }

    fun clearLoggedInUser() {
        vkDataSource.clearLoggedInUser()
    }*/

    private fun initVkSession() {
        if (VKSdk.isLoggedIn()) {
            Timber.v("U r logged in already, your token == %s", VKAccessToken.currentToken().accessToken)
            viewState.setUiStateLoggedIn()
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

//    fun getVkDataSource() : VkApiDataSource = vkDataSource

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
