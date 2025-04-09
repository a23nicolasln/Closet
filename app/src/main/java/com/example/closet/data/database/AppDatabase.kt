package com.example.closet.data.database

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.closet.data.dao.ClothingItemDao
import com.example.closet.data.dao.OutfitClothingItemDao
import com.example.closet.data.dao.OutfitDao
import com.example.closet.data.model.ClothingItem
import com.example.closet.data.model.Outfit
import com.example.closet.data.model.OutfitClothingItemCrossRef


@Database(entities = [Outfit::class, ClothingItem::class, OutfitClothingItemCrossRef::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun outfitDao(): OutfitDao
    abstract fun clothingItemDao(): ClothingItemDao
    abstract fun outfitClothingItemDao(): OutfitClothingItemDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "closetDB"
                )   .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        // This callback is used to pre-populate the database with a dummy outfit and a dummy clothing item
        val roomCallback = object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                db.execSQL("INSERT INTO Outfit (outfitId, name, imageUrl) VALUES (1, 'add', '')")
                db.execSQL("INSERT INTO ClothingItem (clothingItemId,type,brand,size,imageUrl,color) VALUES (1, 'add', '', '','','')")
            }
        }

    }
}