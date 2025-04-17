package com.example.closet.ui.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.closet.R
import com.example.closet.data.database.AppDatabase
import com.example.closet.data.model.ClothingItem
import com.example.closet.repository.ClothingItemRepository
import com.example.closet.repository.TypeRepository
import com.example.closet.ui.viewmodels.ClothingAddViewModel
import com.example.closet.ui.viewmodels.ViewModelFactory
import com.example.closet.utils.FileUtils
import java.util.UUID

class ClothingAddFragment : Fragment() {

    private var clothingType: Long? = null
    private var clothingId: Long? = null
    private var imagePath: String? = null
    private lateinit var imageViewClothing: ImageView
    private lateinit var clothingItemRepository: ClothingItemRepository
    private lateinit var typeRepository: TypeRepository
    private lateinit var viewModel: ClothingAddViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            clothingType = it.getLong("clothingType")
            clothingId = it.getLong("clothingId")
        }

        val db = AppDatabase.getDatabase(requireContext())
        clothingItemRepository = ClothingItemRepository(db.clothingItemDao())
        typeRepository = TypeRepository(db.typeDao())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_clothing_add, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imageViewClothing = view.findViewById(R.id.imageViewClothing)

        val viewModelFactory = ViewModelFactory {
            ClothingAddViewModel(clothingItemRepository, typeRepository)
        }

        viewModel = ViewModelProvider(this, viewModelFactory)[ClothingAddViewModel::class.java]

        // Fetch the clothing item if ID is not null
        clothingId?.let {
            viewModel.getClothingItemById(it)
        }

        // Observe clothing item
        viewModel.currentItem.observe(viewLifecycleOwner) { item ->
            item?.let {
                imagePath = it.imageUrl

                Glide.with(requireContext())
                    .load(it.imageUrl)
                    .centerCrop()
                    .into(imageViewClothing)
            }
        }

        imageViewClothing.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE)
        }

        view.findViewById<View>(R.id.back_button).setOnClickListener {
            requireActivity().onBackPressed()
        }

        view.findViewById<View>(R.id.save_button).setOnClickListener {
            val clothingItem = ClothingItem(
                clothingItemId = clothingId ?: 0L,
                typeOwnerId = clothingType ?: 0L,
                imageUrl = imagePath
                    ?: "file:///android_asset/clothingImages/default_clothingItem.jpg"
            )

            viewModel.insertClothingItem(clothingItem)
            requireActivity().onBackPressed()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == Activity.RESULT_OK) {
            val imageUri: Uri? = data?.data
            imageUri?.let {
                val imageName = "clothing_image_${UUID.randomUUID()}.jpg"
                imagePath = FileUtils.saveImageToInternalStorage(requireContext(), imageName, data)

                Glide.with(requireContext())
                    .load(imagePath)
                    .centerCrop()
                    .into(imageViewClothing)
            }
        }
    }

    companion object {
        private const val REQUEST_CODE_SELECT_IMAGE = 100
    }
}
