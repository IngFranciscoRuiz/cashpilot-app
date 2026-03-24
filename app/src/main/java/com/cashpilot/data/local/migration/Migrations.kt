package com.cashpilot.data.local.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE expense ADD COLUMN sortOrderMillis INTEGER NOT NULL DEFAULT 0")
        db.execSQL("UPDATE expense SET sortOrderMillis = id * 1000 WHERE sortOrderMillis = 0")
    }
}
