package com.dicoding.fruition1.ui.detect

import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts

class DetectViewModel : ComponentActivity() {
    val takePicture = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { /* ... */ }
    val pickFromGallery = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { /* ... */ }
}