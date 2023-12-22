package com.dicoding.fruition1.ui.home

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.dicoding.fruition1.R
import com.dicoding.fruition1.ui.account.AccountScreen
import com.dicoding.fruition1.ui.account.AccountViewModel
import com.dicoding.fruition1.ui.detect.DetectScreen
import com.dicoding.fruition1.ui.navigation.NavigationItem
import com.dicoding.fruition1.ui.navigation.Screen
import com.dicoding.fruition1.ui.theme.Fruition1Theme
import java.io.File


class HomeActivity : ComponentActivity() {
    private val homeViewModel: HomeViewModel by viewModels()
    private val accountViewModel: AccountViewModel by viewModels()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Fruition1Theme {
                // A surface container using the 'background' color from the theme
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = Screen.Home.route,
                    modifier = Modifier.padding()
                ) {
                    composable(Screen.Home.route) {
                        HomeScreen(navController, )
                    }
                    composable(Screen.Detect.route) {
                        DetectScreen(navController)
                    }
                    composable(Screen.Account.route) {
                        AccountScreen(navController)
                    }
                }
                HomeScreen(navController)
            }
        }
    }
}


@Composable
fun HomeScreen(
    navController: NavHostController,
) {
    var selectedImageUri: Uri? by remember { mutableStateOf(null) }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val context = LocalContext.current
    val imageFilenames = context.fileList().filter { it.endsWith(".jpg") }

    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        bottomBar = {
            BottomBar(navController)
        },
    ) { innerPadding ->
        Column {
            Box {
                ImageTopWithText()
                Column {
                Spacer(modifier = Modifier.height(57.dp))
                SearchBar(query = searchQuery) { newQuery ->
                    searchQuery = newQuery
                    }
                }
            }

            if (imageFilenames.isNotEmpty()) {
                val filteredFilenames = imageFilenames
                    .filter { filename ->
                        val (predictedClass, _) = getPredictionData(context, filename)
                        predictedClass?.contains(searchQuery, ignoreCase = true) == true
                    }

                LazyColumn(
                    modifier = Modifier

                        .fillMaxSize()

                        .padding(innerPadding)

                ) {
                    items(filteredFilenames) { filename ->
                        val imageUri = getImageUri(filename)
                        val imageBitmap = loadBitmapFromUri(imageUri)
                        // Ambil metadata dari SharedPreferences
                        val (predictedClass, accuracy) = getPredictionData(context, filename)

                        Log.d("ImageTag", "Image displayed: $filename")

                        // Gunakan Card untuk membungkus setiap item
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)

                        ) {
                            // Gunakan Column di dalam Card untuk menyusun kontennya
                            Column {
                                // Display metadata
                                Text(
                                    text = " $predictedClass",
                                    color = Color.Black,
                                    modifier = Modifier
                                        .padding(7.dp),

                                )
                                Text("Accuracy: $accuracy%",
                                    color = Color.Black,
                                    modifier = Modifier
                                        .padding(7.dp),)

                                // Display each image in the LazyColumn
                                imageBitmap?.asImageBitmap()?.let {
                                    Image(
                                        bitmap = it,
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(250.dp)
                                            .clip(MaterialTheme.shapes.medium)
                                            .background(MaterialTheme.colorScheme.onSurfaceVariant)
                                    )
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }
                    }
                }

            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "No detection history yet",
                        color = Color.Gray,
                        fontSize = 18.sp,
                        //fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(40.dp)
                            .align(Alignment.CenterHorizontally)
                            .offset(y = (15).dp)
                    )
                    Image(
                        painter = painterResource(R.drawable.emptyhome),
                        contentDescription = null,
                        modifier = Modifier
                            .width(200.dp)
                            .height(200.dp)
                            .clip(MaterialTheme.shapes.medium)
                        //.background(MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }
            }
        }
    }
}



// Ambil data hasil prediksi dan akurasi dari SharedPreferences
private fun getPredictionData(context: Context, filename: String): Pair<String?, Float> {

    val sharedPreferences = context.getSharedPreferences("prediction_data", Context.MODE_PRIVATE)
    val predictedClass = sharedPreferences.getString("${filename}_predicted_class", null)
    val accuracy = sharedPreferences.getFloat("${filename}_prediction_accuracy", 0f)

    return Pair(predictedClass, accuracy)
}



@Composable
private fun getImageUri(filename: String): Uri {
    val context = LocalContext.current
    return File(context.filesDir, filename).toUri()
}

@Composable
private fun loadBitmapFromUri(uri: Uri?): Bitmap? {
    return uri?.let { nonNullUri ->
        val context = LocalContext.current
        return try {
            context.contentResolver.openInputStream(nonNullUri)?.use { inputStream ->
                BitmapFactory.decodeStream(inputStream)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}


@Composable
private fun BottomBar(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier,
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


@Composable
fun ImageTopWithText() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(152.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.topbar),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                //.offset(y = (-36).dp),
        )
        Text(
            text = " Beranda",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(20.dp)
                .align(Alignment.TopStart)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(query: String, onQueryChange: (String) -> Unit) {
    TextField(
        value = query,
        onValueChange = { newQuery ->
            onQueryChange(newQuery)
        },
        textStyle = LocalTextStyle.current.copy(
            color = Color.Black
        ),
        placeholder = { Text(stringResource(R.string.search_hint)) },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            disabledContainerColor = Color.White,
        ),
        shape = (MaterialTheme.shapes.large),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)

    )
}





