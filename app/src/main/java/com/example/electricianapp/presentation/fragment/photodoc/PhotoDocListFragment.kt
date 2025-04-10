package com.example.electricianapp.presentation.fragment.photodoc

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.electricianapp.R
import com.example.electricianapp.databinding.FragmentPhotoDocListBinding
import com.example.electricianapp.domain.model.photodoc.PhotoDocument
import com.example.electricianapp.presentation.adapter.photodoc.PhotoDocAdapter
import com.example.electricianapp.presentation.viewmodel.photodoc.PhotoDocUiState
import com.example.electricianapp.presentation.viewmodel.photodoc.PhotoDocViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class PhotoDocListFragment : Fragment() {

    private var _binding: FragmentPhotoDocListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PhotoDocViewModel by viewModels()
    private lateinit var photoAdapter: PhotoDocAdapter

    // Permission request launcher
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val cameraGranted = permissions[Manifest.permission.CAMERA] ?: false
        val storageGranted = permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE] ?: true // Not needed for API 29+

        if (cameraGranted && storageGranted) {
            openCamera()
        } else {
            Toast.makeText(
                requireContext(),
                "Camera and storage permissions are required to take photos",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // Camera launcher
    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            viewModel.tempPhotoUri.value?.let { uri ->
                showAddPhotoDialog(uri)
            }
        }
    }

    // Gallery launcher
    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            showAddPhotoDialog(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPhotoDocListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupListeners()
        observeViewModel()

        // Load all photos initially
        viewModel.loadAllPhotoDocuments()
    }

    private fun setupRecyclerView() {
        photoAdapter = PhotoDocAdapter(
            onItemClick = { photo ->
                navigateToPhotoDetail(photo)
            }
        )

        binding.recyclerViewPhotos.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = photoAdapter
        }
    }

    private fun setupListeners() {
        binding.fabAddPhoto.setOnClickListener {
            showPhotoSourceDialog()
        }

        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    if (it.isNotEmpty()) {
                        viewModel.searchPhotoDocuments(it)
                    } else {
                        viewModel.loadAllPhotoDocuments()
                    }
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Only search when the user stops typing
                return false
            }
        })

        binding.chipGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.chipAll -> viewModel.loadAllPhotoDocuments()
                R.id.chipRecent -> viewModel.loadPhotoDocumentsByTags(listOf("recent"))
                R.id.chipFavorites -> viewModel.loadPhotoDocumentsByTags(listOf("favorite"))
                // Add more chips as needed
            }
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.photoDocUiState.collectLatest { state ->
                when (state) {
                    is PhotoDocUiState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.recyclerViewPhotos.visibility = View.GONE
                        binding.textViewEmpty.visibility = View.GONE
                    }
                    is PhotoDocUiState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        
                        if (state.photos.isEmpty()) {
                            binding.recyclerViewPhotos.visibility = View.GONE
                            binding.textViewEmpty.visibility = View.VISIBLE
                        } else {
                            binding.recyclerViewPhotos.visibility = View.VISIBLE
                            binding.textViewEmpty.visibility = View.GONE
                            photoAdapter.submitList(state.photos)
                        }
                    }
                    is PhotoDocUiState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        binding.recyclerViewPhotos.visibility = View.GONE
                        binding.textViewEmpty.visibility = View.VISIBLE
                        binding.textViewEmpty.text = state.message
                        
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun showPhotoSourceDialog() {
        val options = arrayOf("Take Photo", "Choose from Gallery")
        
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Add Photo")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> checkCameraPermissions()
                    1 -> openGallery()
                }
            }
            .show()
    }

    private fun checkCameraPermissions() {
        val cameraPermission = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        )
        
        val storagePermission = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        
        val permissionsToRequest = mutableListOf<String>()
        
        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.CAMERA)
        }
        
        if (storagePermission != PackageManager.PERMISSION_GRANTED && android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.Q) {
            permissionsToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        
        if (permissionsToRequest.isNotEmpty()) {
            requestPermissionLauncher.launch(permissionsToRequest.toTypedArray())
        } else {
            openCamera()
        }
    }

    private fun openCamera() {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_${timeStamp}_"
        
        val photoFile = File.createTempFile(
            imageFileName,
            ".jpg",
            requireContext().cacheDir
        )
        
        val photoUri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.fileprovider",
            photoFile
        )
        
        viewModel.setTempPhotoUri(photoUri)
        takePictureLauncher.launch(photoUri)
    }

    private fun openGallery() {
        pickImageLauncher.launch("image/*")
    }

    private fun showAddPhotoDialog(photoUri: Uri) {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_add_photo, null)
        
        // TODO: Set up the dialog view with the photo and input fields
        
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Add Photo")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                // TODO: Get input values from dialog and save the photo
                lifecycleScope.launch {
                    val photoId = viewModel.savePhotoDocument(
                        photoUri = photoUri,
                        title = "Test Photo", // Replace with actual input
                        description = "Test Description", // Replace with actual input
                        tags = listOf("recent") // Replace with actual input
                    )
                    
                    if (photoId > 0) {
                        Toast.makeText(requireContext(), "Photo saved", Toast.LENGTH_SHORT).show()
                        viewModel.loadAllPhotoDocuments() // Refresh the list
                    } else {
                        Toast.makeText(requireContext(), "Failed to save photo", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun navigateToPhotoDetail(photo: PhotoDocument) {
        viewModel.selectPhotoDocument(photo.id)
        // TODO: Navigate to photo detail fragment
        // findNavController().navigate(PhotoDocListFragmentDirections.actionPhotoDocListFragmentToPhotoDocDetailFragment())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
