package pro.xite.dev.vkgram.followers.model

import android.app.Activity.RESULT_OK
import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.ResultReceiver
import com.vk.sdk.api.model.VKUsersArray
import pro.xite.dev.vkgram.di.anno.LocalDataSource
import pro.xite.dev.vkgram.di.anno.RemoteDataSource
import pro.xite.dev.vkgram.main.Application
import timber.log.Timber
import javax.inject.Inject

private const val ACTION_GET_FOLLOWERS = "pro.xite.dev.vkgram.followers.model.action.get_followers"
private const val ACTION_BAZ = "pro.xite.dev.vkgram.followers.model.action.BAZ"

private const val EXTRA_ID = "pro.xite.dev.vkgram.followers.model.extra.ID"
private const val EXTRA_CALLBACK = "pro.xite.dev.vkgram.followers.model.extra.CALLBACK"

class VkLoaderService : IntentService("VkLoaderService") {

    private lateinit var resultCallback: ResultReceiver
//    @Inject @RemoteDataSource lateinit var followersRepo : FollowersRepo
    @Inject @field:RemoteDataSource
    lateinit var remoteRepo: FollowersDataSource

    @Inject @field:LocalDataSource
    lateinit var localRepo: FollowersDataSource

    override fun onCreate() {
        Timber.v("VkLoaderService.onCreate")
        super.onCreate()
        Application.getAppComponent().inject(this)
    }

    override fun onHandleIntent(intent: Intent?) {
        resultCallback = intent?.getParcelableExtra(EXTRA_CALLBACK) ?: return
        when (intent?.action) {
            ACTION_GET_FOLLOWERS -> {
                val id = intent.getStringExtra(EXTRA_ID)
                handleActionGetFollowers(id, resultCallback)
            }
            ACTION_BAZ -> {
                val param1 = intent.getStringExtra(EXTRA_ID)
                handleActionBaz(param1, resultCallback)
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private fun handleActionGetFollowers(id: String, resultReceiver: ResultReceiver) {
        Timber.v("handleActionGetFollowers")
        val cursor = contentResolver.query(VkContentProvider.uri, null, null, null, null)
        if(cursor == null || cursor.count == 0) {
            remoteRepo.followers.subscribe {
                val bundle = Bundle().apply { putParcelable(VKUsersArray::class.java.simpleName, it) }
                resultReceiver.send(RESULT_OK, bundle)
            }
        }
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
        @JvmStatic
        fun startActionGetFollowers(context: Context, id: String, callback: FollowersResultReceiver.Callback) {
            val resultReceiver = FollowersResultReceiver(null, callback)
            val intent = Intent(context, VkLoaderService::class.java).apply {
                action = ACTION_GET_FOLLOWERS
                putExtra(EXTRA_ID, id)
                putExtra(EXTRA_CALLBACK, resultReceiver)
            }
            context.startService(intent)
        }

        /**
         * Starts this service to perform action Baz with the given parameters. If
         * the service is already performing a task this action will be queued.
         *
         * @see IntentService
         */
        @JvmStatic
        fun startActionBaz(context: Context, param1: String, param2: String) {
            val intent = Intent(context, VkLoaderService::class.java).apply {
                action = ACTION_BAZ
                putExtra(EXTRA_ID, param1)
                putExtra(EXTRA_CALLBACK, param2)
            }
            context.startService(intent)
        }
    }
}
