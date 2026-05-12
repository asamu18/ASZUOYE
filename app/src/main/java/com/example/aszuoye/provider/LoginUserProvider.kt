package com.example.aszuoye.provider

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import com.example.aszuoye.data.UserDb

class LoginUserProvider : ContentProvider() {
    override fun onCreate(): Boolean = true

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        val ctx = context ?: return null
        val db = UserDb(ctx).readableDatabase
        val (sel, args) = when (matcher.match(uri)) {
            CODE_USERS -> selection to selectionArgs
            CODE_USER_ID -> {
                val id = ContentUris.parseId(uri).toString()
                val extraSel = "${UserDb.COL_ID}=?"
                val extraArgs = arrayOf(id)
                if (selection.isNullOrBlank()) {
                    extraSel to extraArgs
                } else {
                    val mergedArgs = (selectionArgs?.toList().orEmpty() + extraArgs.toList()).toTypedArray()
                    "($selection) AND ($extraSel)" to mergedArgs
                }
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }

        val cursor = db.query(
            UserDb.TABLE_USERS,
            projection,
            sel,
            args,
            null,
            null,
            sortOrder ?: "${UserDb.COL_LAST_LOGIN} DESC"
        )
        cursor.setNotificationUri(ctx.contentResolver, uri)
        return cursor
    }

    override fun getType(uri: Uri): String {
        return when (matcher.match(uri)) {
            CODE_USERS -> "vnd.android.cursor.dir/vnd.$AUTHORITY.${UserDb.TABLE_USERS}"
            CODE_USER_ID -> "vnd.android.cursor.item/vnd.$AUTHORITY.${UserDb.TABLE_USERS}"
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val ctx = context ?: return null
        if (matcher.match(uri) != CODE_USERS) throw IllegalArgumentException("Invalid URI: $uri")

        val db = UserDb(ctx).writableDatabase
        val rowId = db.insertWithOnConflict(
            UserDb.TABLE_USERS,
            null,
            values,
            android.database.sqlite.SQLiteDatabase.CONFLICT_REPLACE
        )
        if (rowId == -1L) return null
        val inserted = ContentUris.withAppendedId(CONTENT_URI, rowId)
        ctx.contentResolver.notifyChange(uri, null)
        return inserted
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        val ctx = context ?: return 0
        val db = UserDb(ctx).writableDatabase
        val (sel, args) = when (matcher.match(uri)) {
            CODE_USERS -> selection to selectionArgs
            CODE_USER_ID -> "${UserDb.COL_ID}=?" to arrayOf(ContentUris.parseId(uri).toString())
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
        val count = db.delete(UserDb.TABLE_USERS, sel, args)
        if (count > 0) ctx.contentResolver.notifyChange(uri, null)
        return count
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        val ctx = context ?: return 0
        val db = UserDb(ctx).writableDatabase
        val (sel, args) = when (matcher.match(uri)) {
            CODE_USERS -> selection to selectionArgs
            CODE_USER_ID -> "${UserDb.COL_ID}=?" to arrayOf(ContentUris.parseId(uri).toString())
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
        val count = db.update(UserDb.TABLE_USERS, values, sel, args)
        if (count > 0) ctx.contentResolver.notifyChange(uri, null)
        return count
    }

    companion object {
        const val AUTHORITY = "com.example.aszuoye.loginusers"
        val CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY/${UserDb.TABLE_USERS}")

        private const val CODE_USERS = 1
        private const val CODE_USER_ID = 2

        private val matcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(AUTHORITY, UserDb.TABLE_USERS, CODE_USERS)
            addURI(AUTHORITY, "${UserDb.TABLE_USERS}/#", CODE_USER_ID)
        }
    }
}
