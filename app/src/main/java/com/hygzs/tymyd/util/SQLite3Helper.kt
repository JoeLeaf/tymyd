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
        //安卓会自动增加android_metadata和sqlite_sequence两个表，所以要去掉
        cursor = db.rawQuery(
            "SELECT name FROM sqlite_master WHERE type='table' AND name='android_metadata'",
            null
        )
        if (cursor.moveToFirst()) {
            // 表格存在
            db.execSQL("DROP TABLE android_metadata")
        }
        cursor.close()
        return tableNames
    }

}