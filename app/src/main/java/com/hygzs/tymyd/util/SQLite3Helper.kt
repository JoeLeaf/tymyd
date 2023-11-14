package com.hygzs.tymyd.util

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.blankj.utilcode.util.FileIOUtils
import com.hygzs.tymyd.Data


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

    /*
CREATE TABLE F2306125583703156160 (
    SrcId          INTEGER,
    DstId          INTEGER,
    SrcName        TEXT,
    DstChatGroupId INTEGER,
    Content        TEXT,
    Timestamp      INTEGER,
    Channel        INTEGER,
    Addition       TEXT,
    MsgRid         INTEGER,
    Client_Tag     INTEGER
);
     */
    //根据表名获取表的所有数据
    fun getTableData(tableName: String): ArrayList<Map<String, Any>> {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $tableName", null)
        val tableData = arrayListOf<Map<String, Any>>()
        while (cursor.moveToNext()) {
            val map = mutableMapOf<String, Any>()
            for (i in 0 until cursor.columnCount) {
                map[cursor.getColumnName(i)] = cursor.getString(i)
            }
            tableData.add(map)
        }
        dropAndroidMetadata(db)
        return tableData
    }

    //根据表名获取表的分页数据
    fun getTableData(tableName: String, page: Int, pageSize: Int): ArrayList<Map<String, Any>> {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $tableName LIMIT $pageSize OFFSET ${(page - 1) * pageSize}",
            null
        )
        val tableData = arrayListOf<Map<String, Any>>()
        while (cursor.moveToNext()) {
            val map = mutableMapOf<String, Any>()
            for (i in 0 until cursor.columnCount) {
                map[cursor.getColumnName(i)] = cursor.getString(i)
            }
            tableData.add(map)
        }
        dropAndroidMetadata(db)
        return tableData
    }

    //根据Timestamp删除表中的数据
    fun deleteTableData(tableName: String, timestamp: Long) {
        val db = writableDatabase
        db.execSQL("DELETE FROM $tableName WHERE Timestamp = $timestamp")
        dropAndroidMetadata(db)

    }
    //根据Timestamp修改表中Content的值
    fun updateTableData(tableName: String, timestamp: Long, content: String) {
        val db = writableDatabase
        db.execSQL("UPDATE $tableName SET Content = '$content' WHERE Timestamp = $timestamp")
        dropAndroidMetadata(db)
    }

}