package pro.xite.dev.vkgram.followers.model

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.util.Log
import io.realm.Realm

class VkContentProvider : ContentProvider() {

    companion object {
        val uri = Uri.Builder().scheme("content").authority("pro.xite.dev.vkgram").build()

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

    override fun onCreate(): Boolean {
        Realm.init(context)
        val realm = Realm.getDefaultInstance()
        Log.v("Provider", "VkContentProvider onCreate")
        return false
    }

    override fun query(uri: Uri, projection: Array<String>?, selection: String?,
                       selectionArgs: Array<String>?, sortOrder: String?): Cursor? {
        TODO("Implement this to handle query requests from clients.")
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?,
                        selectionArgs: Array<String>?): Int {
        TODO("Implement this to handle requests to update one or more rows.")
    }
}
