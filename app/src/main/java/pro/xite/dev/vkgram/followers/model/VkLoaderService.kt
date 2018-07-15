package pro.xite.dev.vkgram.followers.model

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.ResultReceiver
import pro.xite.dev.vkgram.followers.presenter.FollowersResultReceiver
import pro.xite.dev.vkgram.main.Application
import pro.xite.dev.vkgram.main.model.vkapi.VkApiService
import timber.log.Timber
import javax.inject.Inject

// TODO: Rename actions, choose action names that describe tasks that this
// IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
private const val ACTION_FOO = "pro.xite.dev.vkgram.followers.model.action.FOO"
private const val ACTION_BAZ = "pro.xite.dev.vkgram.followers.model.action.BAZ"

// TODO: Rename parameters
private const val EXTRA_PARAM1 = "pro.xite.dev.vkgram.followers.model.extra.PARAM1"
private const val EXTRA_PARAM2 = "pro.xite.dev.vkgram.followers.model.extra.PARAM2"

/**
 * An [IntentService] subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
class VkLoaderService : IntentService("VkLoaderService") {

    private lateinit var resultCallback: ResultReceiver
    @Inject
    lateinit var vkApi: VkApiService

    override fun onCreate() {
        Timber.v("VkLoaderService.onCreate")
        super.onCreate()
        Application.getAppComponent().inject(this)
    }

    override fun onHandleIntent(intent: Intent?) {
        resultCallback = intent?.getParcelableExtra(EXTRA_PARAM2) ?: return
        when (intent?.action) {
            ACTION_FOO -> {
                val param1 = intent.getStringExtra(EXTRA_PARAM1)
                handleActionFoo(param1, resultCallback)
            }
            ACTION_BAZ -> {
                val param1 = intent.getStringExtra(EXTRA_PARAM1)
                handleActionBaz(param1, resultCallback)
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private fun handleActionFoo(param1: String, resultReceiver: ResultReceiver) {
        Timber.v("handleActionFoo")
        val cursor = contentResolver.query(VkContentProvider.uri, null, null, null, null)
        val bundle = Bundle().apply { putString("A", "cursor") }
        resultReceiver.send(23, bundle)
//        TODO("Handle action Foo")
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private fun handleActionBaz(param1: String, param2: ResultReceiver) {
        TODO("Handle action Baz")
    }

    companion object {
        /**
         * Starts this service to perform action Foo with the given parameters. If
         * the service is already performing a task this action will be queued.
         *
         * @see IntentService
         */
        // TODO: Customize helper method
        @JvmStatic
        fun startActionFoo(context: Context, param1: String, callback: FollowersResultReceiver.Callback) {
            val resultReceiver = FollowersResultReceiver(null, callback)
            val intent = Intent(context, VkLoaderService::class.java).apply {
                action = ACTION_FOO
                putExtra(EXTRA_PARAM1, param1)
                putExtra(EXTRA_PARAM2, resultReceiver)
            }
            context.startService(intent)
        }

        /**
         * Starts this service to perform action Baz with the given parameters. If
         * the service is already performing a task this action will be queued.
         *
         * @see IntentService
         */
        // TODO: Customize helper method
        @JvmStatic
        fun startActionBaz(context: Context, param1: String, param2: String) {
            val intent = Intent(context, VkLoaderService::class.java).apply {
                action = ACTION_BAZ
                putExtra(EXTRA_PARAM1, param1)
                putExtra(EXTRA_PARAM2, param2)
            }
            context.startService(intent)
        }
    }
}
