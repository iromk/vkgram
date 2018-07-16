package pro.xite.dev.vkgram.followers.model

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import android.util.Log
import io.realm.Realm
import io.realm.RealmConfiguration
import pro.xite.dev.vkgram.di.AppComponent
import pro.xite.dev.vkgram.followers.model.db.realm.FollowersMigration
import timber.log.Timber

class VkContentProvider : ContentProvider() {

    companion object {
        const val CONTENT_SCHEME = "content"
        const val AUTHORTITY = "pro.xite.dev.vkgram"
        const val VK_FOLLOWERS = "followers"
        const val REQUEST_FOLLOWERS = 1900
        val uri = Uri.Builder().scheme(CONTENT_SCHEME).authority(AUTHORTITY).build()
    }

    val uriMatcher = UriMatcher(UriMatcher.NO_MATCH)


    init {
        uriMatcher.addURI(AUTHORTITY, VK_FOLLOWERS, REQUEST_FOLLOWERS)
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        TODO("Implement this to handle requests to delete one or more rows")
    }

    override fun getType(uri: Uri): String? {
        TODO("Implement this to handle requests for the MIME type of the data" +
                "at the given URI")
    }

    override fun insert(uri: Uri, values: ContentValues): Uri? {
        return ContentUris.withAppendedId(uri, 11)
    }

    private lateinit var appComponent: AppComponent

    override fun onCreate(): Boolean {
        Realm.init(context)
        val config = RealmConfiguration.Builder()
                .name("vk.followers.realm")
                .schemaVersion(0)
                .migration(FollowersMigration())
                .build()
        Realm.setDefaultConfiguration(config)

        val realm = Realm.getDefaultInstance()
        Log.v("Provider", "VkContentProvider onCreate")
        return false
    }

    override fun query(uri: Uri, projection: Array<String>?, selection: String?,
                       selectionArgs: Array<String>?, sortOrder: String?): Cursor? {
        Timber.v("VkContentProvider.query")

        when(uriMatcher.match(uri)) {
            REQUEST_FOLLOWERS -> { Timber.v("Got followers request") }
        }
        return null
//        TODO("Implement this to handle query requests from clients.")
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?,
                        selectionArgs: Array<String>?): Int {
        TODO("Implement this to handle requests to update one or more rows.")
    }
}
