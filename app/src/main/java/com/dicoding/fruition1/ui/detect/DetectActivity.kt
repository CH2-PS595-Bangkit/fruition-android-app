package com.dicoding.fruition1.ui.detect

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.dicoding.fruition1.R
import com.dicoding.fruition1.api.ApiService
import com.dicoding.fruition1.api.Injection
import com.dicoding.fruition1.data.pref.UserPreference
import com.dicoding.fruition1.data.pref.dataStore
import com.dicoding.fruition1.ui.navigation.NavigationItem
import com.dicoding.fruition1.ui.navigation.Screen
import com.dicoding.fruition1.ui.theme.Fruition1Theme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.tensorflow.lite.Interpreter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel

//private lateinit var takePicture: ActivityResultLauncher<Intent>
private var imageFromCamera: Bitmap? = null
private var imageFromGallery: Bitmap? = null
private lateinit var tflite: Interpreter
var fileName = ""
class DetectActivity : ComponentActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Fruition1Theme {
                // A surface container using the 'background' color from the theme
                val navController = rememberNavController()
                loadTFLiteModel()
                DetectScreen(navController)
            }
        }

    }

    private fun loadTFLiteModel() {
        try {
            val assetManager = assets
            val model = assetManager.openFd("fruition_model.tflite").createInputStream().readBytes()
            val tfliteOptions = Interpreter.Options()
            tflite = Interpreter(ByteBuffer.wrap(model), tfliteOptions)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadBitmapFromUri(uri: Uri?): Bitmap? {
        return try {
            if (uri != null) {
                val inputStream = contentResolver.openInputStream(uri)
                BitmapFactory.decodeStream(inputStream)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

@Composable
private fun loadTFLiteModel() {

    val assetManager = LocalContext.current.assets
    val modelFileDescriptor = assetManager.openFd("fruition_model.tflite")
    val inputStream = FileInputStream(modelFileDescriptor.fileDescriptor)
    val startOffset = modelFileDescriptor.startOffset
    val declaredLength = modelFileDescriptor.declaredLength

    // Create a MappedByteBuffer
    val modelBuffer = inputStream.channel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)

    val tfliteOptions = Interpreter.Options()
    tflite = Interpreter(modelBuffer, tfliteOptions)
}

@Composable
fun DetectScreen(navController: NavHostController) {
    //var imageUri: Uri?= null


    var startUploading by rememberSaveable { mutableStateOf(false) }

    val takePicture = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Handle gambar yang diambil dari kamera (result.data)
            val data: Intent? = result.data
            // Ambil gambar dari intent
            @Suppress("DEPRECATION")
            imageFromCamera = data?.getParcelableExtra("data")
            navController.navigate(Screen.Detect.route)
        }

    }

    var selectedImageUri: Uri? = null
    val pickFromGallery = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data

            // Memeriksa apakah data tidak null dan tipe datanya adalah URI (gambar)
            if (data != null && data.data != null) {
                // Mengambil URI gambar yang dipilih dari galeri
                selectedImageUri = data.data
            }
        }
    }
    if(selectedImageUri != null){
        val imageBitmap = loadBitmapFromUri(selectedImageUri)

        imageFromGallery = imageBitmap
        navController.navigate(Screen.Detect.route)
    }

    Scaffold(
        bottomBar = {
            BottomBar(navController)
        },
    ) { innerPadding ->

        Column {
            DetectTopBar()
            AddScreen(
                takePicture = takePicture,
                pickFromGallery = pickFromGallery,
                navController = navController,
                startUploading = startUploading
            )
        }
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
        ) {
        }
    }
}

@Composable
fun DetectTopBar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.detectopbar),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .offset(y = (-38).dp),
        )
        Text(
            text = "Deteksi Kematangan",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(20.dp)
                .align(Alignment.TopCenter)
                .offset(y = (15).dp)
        )
    }
}

@Composable
private fun BottomBar(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier
            .zIndex(4f)
            .background(Color.White),
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        val navigationItems = listOf(
            NavigationItem(
                title = stringResource(R.string.menu_home),
                icon = Icons.Default.Home,
                screen = Screen.Home
            ),
            NavigationItem(
                title = stringResource(R.string.menu_detect),
                icon = ImageVector.vectorResource(R.drawable.cam),
                screen = Screen.Detect
            ),
            NavigationItem(
                title = stringResource(R.string.menu_account),
                icon = Icons.Default.AccountCircle,
                screen = Screen.Account
            ),
        )
        navigationItems.map { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title
                    )
                },
                label = { Text(item.title) },
                selected = currentRoute == item.screen.route,
                onClick = {
                    navController.navigate(item.screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        restoreState = true
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AddScreen(
    navController: NavHostController,
    takePicture: ActivityResultLauncher<Intent>,
    pickFromGallery: ActivityResultLauncher<Intent>,
    startUploading: Boolean
) {
    loadTFLiteModel()
    var description by remember { mutableStateOf(TextFieldValue()) }
    val context = LocalContext.current

    val uriHandler = LocalUriHandler.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val view = LocalView.current
    val configuration = LocalViewConfiguration.current
    val density = LocalDensity.current.density

    var imageToShow by remember { mutableStateOf<Bitmap?>(null) }
    LaunchedEffect(imageFromGallery, imageFromCamera) {

        // Mengambil gambar dari URI
        imageToShow = imageLoadFromUri(imageFromGallery, imageFromCamera)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(45.dp)
    ) {
        // Image Preview
        if (imageToShow != null) {
            Image(
                bitmap = imageToShow!!.asImageBitmap(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.onSurfaceVariant)
            )
        } else {
            // Jika tidak ada gambar dari galeri atau kamera, tampilkan gambar default

            Box(
                modifier = Modifier
                    .padding(11.dp)
                    .fillMaxWidth()
                    .height(220.dp)
                    .background(
                        Color(0xFFdbdbdb),
                        RoundedCornerShape(16.dp)
                    )
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_place_holder),
                    contentDescription = "Place Holder Icon",
                    tint = Color.Gray,
                    modifier = Modifier
                        .size(48.dp)
                        .align(Alignment.Center)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Button(
                onClick = {
                    // Handle camera button click
                    val intent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
                    takePicture.launch(intent)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 48.dp)
                    .background(Color.White)
                    .border(1.dp, Color(0xFF39CDD6), shape = RoundedCornerShape(15.dp)),
                shape = RoundedCornerShape(15.dp),
                content = {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.cam),
                        contentDescription = null,
                        tint = Color(0xFF39CDD6),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Kamera", fontWeight = FontWeight.Bold, color = Color(0xFF39CDD6))
                }
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    // Handle gallery button click
                    val intent = Intent(Intent.ACTION_PICK)
                    intent.type = "image/*"
                    pickFromGallery.launch(intent)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 48.dp)
                    .background(
                        (MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(15.dp)
                    ),
                shape = RoundedCornerShape(15.dp),
                border = BorderStroke(1.dp, Color(0xFF39CDD6)),
                content = {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_place_holder),
                        contentDescription = null,
                        tint = Color(0xFF39CDD6)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Galeri", fontWeight = FontWeight.Bold, color = Color(0xFF39CDD6))
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        var timeStamp: Long by remember { mutableStateOf(0L) }
        // Upload Button
        Button(
            onClick = {
                // Handle upload button click
                if (imageToShow != null) {
                    var img = imageFromCamera ?: imageFromGallery




                    timeStamp = System.currentTimeMillis()
                    val sharedPreferences = context.getSharedPreferences("prediction_data", Context.MODE_PRIVATE)
                    sharedPreferences.edit().putLong("$timeStamp", timeStamp).apply()
                    /*fileName = "$timeStamp.jpg"

                    val fileOutputStream: FileOutputStream
                    try {
                        fileOutputStream =
                            context.openFileOutput(fileName, Context.MODE_PRIVATE)
                        img?.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
                        fileOutputStream.close()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }*/

                    imageFromCamera = null
                    imageFromGallery = null
                    CoroutineScope(Dispatchers.Default).launch{
                        processImageWithTFLite(context, timeStamp.toString(), imageToShow!!, tflite, navController )

                    }




                    //navController.navigate(Screen.Home.route))
                    navController.navigate(Screen.Home.route) {
                        // Optionally, you can use the following flags to control backstack behavior
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                    }




                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF39CDD6)),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(Color(0xFF39CDD6), shape = RoundedCornerShape(15.dp)),
            shape = RoundedCornerShape(15.dp),
            border = BorderStroke(3.dp, Color(0xFF39CDD6)),
            content = {
                Spacer(modifier = Modifier.width(8.dp))
                Text("Detect", fontWeight = FontWeight.Bold, color = Color.White)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))
    }

}

private fun processImageWithTFLite(context: Context, filename: String,image: Bitmap, tflite: Interpreter, navController: NavHostController) {
    // Preprocess the image
    val inputImage = preprocessImage(image)

    // Run inference
    val outputProbabilityBuffer = Array(1) { FloatArray(12) }
    tflite.run(inputImage, outputProbabilityBuffer)

    // Post-process the results
    val predictedClass = findIndexOfMaxValue(outputProbabilityBuffer[0])

    val classes = arrayOf(
        "Apple Fresh", "Apple Rotten", "Apple Semifresh", "Apple Semirotten",
        "Banana Fresh", "Banana Rotten", "Banana Semifresh", "Banana Semirotten",
        "Orange Fresh", "Orange Rotten", "Orange Semifresh", "Orange Semirotten"
    )

    val accuracy = outputProbabilityBuffer[0][predictedClass] * 100.0f
    Log.d("TFLite", "Predicted class: ${classes[predictedClass]}")
    Log.d("TFLite", "Prediction accuracy: $accuracy%")

    // Display the image (Assuming you have the necessary UI components)
    // Example: imageView.setImageBitmap(image)

    val coroutineScope = CoroutineScope(Dispatchers.Main)
    coroutineScope.launch {
        saveImageWithMetadata(context, filename, image, classes[predictedClass], accuracy, navController)

    }
}

private fun findIndexOfMaxValue(array: FloatArray): Int {
    var maxIndex = 0
    var maxValue = Float.MIN_VALUE

    for (i in array.indices) {
        if (array[i] > maxValue) {
            maxIndex = i
            maxValue = array[i]
        }
    }

    return maxIndex
}



private fun preprocessImage(image: Bitmap): ByteBuffer {
    val modelInputSize = 224
    val inputBuffer = ByteBuffer.allocateDirect(4 * modelInputSize * modelInputSize * 3)
    inputBuffer.order(ByteOrder.nativeOrder())

    // Resize the image to the model input size
    val resizedBitmap = Bitmap.createScaledBitmap(image, modelInputSize, modelInputSize, true)

    // Normalize pixel values to range [0.0, 1.0]
    val pixels = IntArray(modelInputSize * modelInputSize)
    resizedBitmap.getPixels(pixels, 0, modelInputSize, 0, 0, modelInputSize, modelInputSize)

    for (pixelValue in pixels) {
        // Extract RGB channels
        val red = (pixelValue shr 16) and 0xFF
        val green = (pixelValue shr 8) and 0xFF
        val blue = pixelValue and 0xFF

        // Normalize and put the values into the input buffer
        inputBuffer.putFloat(red.toFloat() / 255.0f)
        inputBuffer.putFloat(green.toFloat() / 255.0f)
        inputBuffer.putFloat(blue.toFloat() / 255.0f)
    }

    return inputBuffer
}

@Composable
private fun loadBitmapFromUri(uri: Uri?): Bitmap? {
    return if (uri != null) {
        val context = LocalContext.current
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    } else {
        null
    }
}

private fun imageLoadFromUri(imageFromGallery: Bitmap?, imageFromCamera: Bitmap?): Bitmap? {
    return imageFromGallery ?: imageFromCamera
}

private suspend fun saveImageWithMetadata(context: Context, timeStamp: String, image: Bitmap, predictedClass: String, accuracy: Float, navController: NavHostController) {
    // Simpan gambar ke penyimpanan lokal internal storage
    val fileName = "$timeStamp.jpg"
    val fileOutputStream: FileOutputStream
    try {
        fileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE)
        image.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
        fileOutputStream.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }

    // Simpan metadata ke SharedPreferences
    savePredictionData(context, timeStamp, predictedClass, accuracy)

    val userPreference = UserPreference.getInstance(context.dataStore)
    val userSession = userPreference.getSession().first()

// Get token from the user session
    val token = userSession.token

    // Convert Bitmap to ByteArray
    val byteArrayOutputStream = ByteArrayOutputStream()
    image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
    val byteArray = byteArrayOutputStream.toByteArray()

// Create a RequestBody from the ByteArray
    val requestBody = byteArray.toRequestBody("image/jpeg".toMediaTypeOrNull())

// Create a MultipartBody.Part from the RequestBody
    val photo = MultipartBody.Part.createFormData("photo", fileName, requestBody)

    try {
        val apiService: ApiService =
            Injection.provideApiServiceUp(UserPreference.getInstance(context.dataStore))

        // Pass the token and photo in headers
        apiService.uploadHistory("Bearer $token", photo, requestBody)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful) {
                        // Handle successful response
                        Log.d("Upload", "Upload berhasil")
                        // navController.navigate() // Add your navigation logic here
                    } else {
                        // Handle unsuccessful response
                        Log.e("Upload", "Upload gagal: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    // Handle failure
                    Log.e("Upload", "Gagal melakukan permintaan: ${t.message}")
                }
            })
    } catch (e: Exception) {
        e.printStackTrace()
    }}





    // Simpan hasil prediksi dan akurasi ke SharedPreferences
    fun savePredictionData(
        context: Context,
        filename: String,
        predictedClass: String,
        accuracy: Float
    ) {
        val sharedPreferences =
            context.getSharedPreferences("prediction_data", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()


        // Perhatikan perubahan di baris berikut
        editor.putString("${filename}.jpg_predicted_class", predictedClass)
        editor.putFloat("${filename}.jpg_prediction_accuracy", accuracy)
        editor.apply()
    }




