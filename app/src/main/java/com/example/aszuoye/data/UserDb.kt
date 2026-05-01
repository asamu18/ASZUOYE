package com.example.aszuoye.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

data class LoginUser(
    val username: String,
    val lastLoginAt: Long
)

class UserDb(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE $TABLE_USERS (" +
                "$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COL_USERNAME TEXT NOT NULL UNIQUE," +
                "$COL_LAST_LOGIN INTEGER NOT NULL" +
                ")"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    fun upsertLoginUser(username: String) {
        val values = ContentValues().apply {
            put(COL_USERNAME, username)
            put(COL_LAST_LOGIN, System.currentTimeMillis())
        }
        writableDatabase.insertWithOnConflict(
            TABLE_USERS,
            null,
            values,
            SQLiteDatabase.CONFLICT_REPLACE
        )
    }

    fun getAllLoginUsers(): List<LoginUser> {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_USERS,
            arrayOf(COL_USERNAME, COL_LAST_LOGIN),
            null,
            null,
            null,
            null,
            "$COL_LAST_LOGIN DESC"
        )
        cursor.use {
            return buildList {
                while (it.moveToNext()) {
                    add(it.toLoginUser())
                }
            }
        }
    }

    private fun Cursor.toLoginUser(): LoginUser {
        val username = getString(getColumnIndexOrThrow(COL_USERNAME))
        val lastLogin = getLong(getColumnIndexOrThrow(COL_LAST_LOGIN))
        return LoginUser(username = username, lastLoginAt = lastLogin)
    }

    companion object {
        private const val DB_NAME = "users.db"
        private const val DB_VERSION = 1

        private const val TABLE_USERS = "login_users"
        private const val COL_ID = "_id"
        private const val COL_USERNAME = "username"
        private const val COL_LAST_LOGIN = "last_login_at"
    }
}

