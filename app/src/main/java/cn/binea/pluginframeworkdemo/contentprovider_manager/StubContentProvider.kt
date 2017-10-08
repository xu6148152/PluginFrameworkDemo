package cn.binea.pluginframeworkdemo.contentprovider_manager

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri

/**
 * Created by binea on 8/10/2017.
 */
class StubContentProvider : ContentProvider() {

    companion object {
        val TAG: String = StubContentProvider::class.java.canonicalName
        val AUTHORITY: String = "cn.binea.pluginframeworkdemo.contentprovider_manager.StubContentProvider"
    }

    override fun insert(uri: Uri?, values: ContentValues?): Uri {
        return context.contentResolver.insert(getRealUri(uri), values)
    }

    override fun query(uri: Uri?, projection: Array<out String>?, selection: String?, selectionArgs: Array<out String>?, sortOrder: String?): Cursor {
        return context.contentResolver.query(getRealUri(uri), projection, selection, selectionArgs, sortOrder)
    }

    override fun onCreate(): Boolean {
        return true
    }

    override fun update(uri: Uri?, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int {
        return 0
    }

    override fun delete(uri: Uri?, selection: String?, selectionArgs: Array<out String>?): Int {
        return 0
    }

    override fun getType(uri: Uri?): String? {
        return null
    }

    private fun getRealUri(raw: Uri?): Uri {
        val rawAuth = raw?.authority

        val uriString: String = raw.toString().replace(rawAuth + "/", "")
        return Uri.parse(uriString)
    }

}