package com.example.closet

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.closet.data.database.AppDatabase
import com.example.closet.databinding.ActivityMainBinding
import com.example.closet.data.repository.AttributeRepository
import com.example.closet.data.repository.ClothingItemRepository
import com.example.closet.data.repository.ColorRepository
import com.example.closet.data.repository.OutfitClothingItemRepository
import com.example.closet.data.repository.OutfitRepository
import com.example.closet.data.repository.TypeRepository
import com.example.closet.ui.viewmodels.OutfitCreationViewModel
import com.example.closet.ui.viewmodels.ViewModelFactory

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    val outfitCreationVM: OutfitCreationViewModel by viewModels {
        val database = AppDatabase.getDatabase(applicationContext)
        ViewModelFactory {
            OutfitCreationViewModel(
                outfitRepo = OutfitRepository(database.outfitDao()),
                clothingRepo = ClothingItemRepository(database.clothingItemDao()),
                joinRepo = OutfitClothingItemRepository(database.outfitClothingItemDao()),
                typeRepo = TypeRepository(database.typeDao()),
                colorRepository = ColorRepository(database.colorDao()),
                attributeRepository = AttributeRepository(database.attributeDao())
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set status bar and navigation bar colors to black
        window.statusBarColor = android.graphics.Color.BLACK
        window.navigationBarColor = android.graphics.Color.BLACK

        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "upload_channel",
                "Upload Progress",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows upload progress"
            }

            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
}
