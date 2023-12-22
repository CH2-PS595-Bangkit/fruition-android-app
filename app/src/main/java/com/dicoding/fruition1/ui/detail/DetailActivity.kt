package com.dicoding.fruition1.ui.detail


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.dicoding.fruition1.R
import com.dicoding.fruition1.api.ApiService
import com.dicoding.fruition1.data.UserRepository
import com.dicoding.fruition1.data.pref.UserPreference
import com.dicoding.fruition1.data.pref.dataStore
import com.dicoding.fruition1.ui.theme.Fruition1Theme

/*
class DetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Fruition1Theme {
                // A surface container using the 'background' color from the theme
                val navController = rememberNavController()
                DetailScreen(navController)
            }
        }
    }
}


@Composable
fun DetailScreen(navController: NavHostController) {

    Scaffold { innerPadding ->

        Column {
            InfoScreen()
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
private fun InfoScreen() {

    Spacer(modifier = Modifier.height(160.dp))

        Text(
            text = "Kematangan",
            color = Color.Gray,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(16.dp)
        )
}

@Composable
private fun InfoScreen(navController: NavHostController) {
    // Get the UserRepository instance
    val userRepository = UserRepository.getInstance(
        UserPreference.getInstance(LocalContext.current.dataStore),
        ApiService.create("https://backend-dot-fruition-api-408523.et.r.appspot.com/")
    )

    // Collect the user data from the data store using Flow
    val user by userRepository.getSession().collectAsState(null)

    Spacer(modifier = Modifier.height(16.dp))

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
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Username
        Text(
            text = "Detection Class",
            color = Color.LightGray,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(10.dp)
                .align(Alignment.Start)
        )
        user?.let {
            Text(
                text = it. ?: "",
                color = Color.Black,
                fontSize = 21.sp,
                modifier = Modifier
                    .padding(5.dp)
                    .align(Alignment.Start)
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        // Email
        Text(
            text = "Accuracy",
            color = Color.LightGray,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(10.dp)
                .align(Alignment.Start)
        )
        user?.let {
            Text(
                text = it. ?: "Not Found",
                color = Color.Black,
                fontSize = 21.sp,
                modifier = Modifier
                    .padding(5.dp)
                    .align(Alignment.Start)

            )
        }
        Column {
            Spacer(modifier = Modifier.width(32.dp))
        }
    }
}*/