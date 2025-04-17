package com.example.closet.data.database

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.closet.data.dao.*
import com.example.closet.data.model.*
import com.example.closet.data.relations.*


@Database(
    entities = [
        Outfit::class,
        ClothingItem::class,
        OutfitClothingItemCrossRef::class,
        Type::class,
        Color::class,
        OutfitColorCrossRef::class,
        ClothingItemColorCrossRef::class,
        Attribute::class,
        ClothingItemAttributeCrossRef::class,
        OutfitAttributeCrossRef::class
    ],
    version = 1
)

abstract class AppDatabase : RoomDatabase() {
    abstract fun outfitDao(): OutfitDao
    abstract fun clothingItemDao(): ClothingItemDao
    abstract fun outfitClothingItemDao(): OutfitClothingItemDao
    abstract fun typeDao(): TypeDao
    abstract fun colorDao(): ColorDao
    abstract fun attributeDao(): AttributeDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "closetDB"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        // This callback is used to pre-populate the database with a dummy outfit and a dummy clothing item
        val roomCallback = object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
            }
        }

    }
}