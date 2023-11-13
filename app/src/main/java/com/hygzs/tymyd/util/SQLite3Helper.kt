package com.hygzs.tymyd.util

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


/*
* Created by xyz on 2023/11/11
* 所以你瞅啥？
*/

class SQLite3Helper(context: Context, pathName: String) :
    SQLiteOpenHelper(context, pathName, null, 3) {
    //读取数据库
    override fun onCreate(p0: SQLiteDatabase?) {
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
    }

    //获取所有表名
    fun getAllTableNames(): List<String> {
        val tableNames = mutableListOf<String>()
        val db = readableDatabase
        var cursor = db.rawQuery(
            "SELECT name FROM sqlite_master WHERE type='table' AND name != 'android_metadata' ORDER BY name;",
            null
        )
        while (cursor.moveToNext()) {
            tableNames.add(cursor.getString(0))
        }
        dropAndroidMetadata(db)
        return tableNames
    }
    //删除指定表
    fun dropTable(tableName: String) {
        val db = writableDatabase
        db.execSQL("DROP TABLE $tableName")
        dropAndroidMetadata(db)
    }

    //删除android_metadata表
    private fun dropAndroidMetadata(db: SQLiteDatabase) {
        val cursor = db.rawQuery(
            "SELECT name FROM sqlite_master WHERE type='table' AND name='android_metadata'",
            null
        )
        if (cursor.moveToFirst()) {
            db.execSQL("DROP TABLE android_metadata")
        }
        db.close()
    }

}