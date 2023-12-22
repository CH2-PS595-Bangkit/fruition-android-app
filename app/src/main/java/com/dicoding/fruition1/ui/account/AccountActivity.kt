package com.dicoding.fruition1.ui.account

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.dicoding.fruition1.R
import com.dicoding.fruition1.api.ApiService
import com.dicoding.fruition1.api.Injection
import com.dicoding.fruition1.data.UserRepository
import com.dicoding.fruition1.data.pref.UserPreference
import com.dicoding.fruition1.data.pref.dataStore
import com.dicoding.fruition1.data.pref.restartApp
import com.dicoding.fruition1.ui.navigation.NavigationItem
import com.dicoding.fruition1.ui.navigation.Screen
import com.dicoding.fruition1.ui.theme.Fruition1Theme
import kotlinx.coroutines.launch

class AccountActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val apiService: ApiService = ApiService.create("https://backend-dot-fruition-api-408523.et.r.appspot.com/")
        val userPreference: UserPreference = UserPreference.getInstance(dataStore)

        setContent {
            Fruition1Theme {
                // A surface container using the 'background' color from the theme
                val navController = rememberNavController()
                AccountOverall(navController)
            }
        }
    }
}

@Composable
fun AccountOverall(navController: NavHostController) {


    AccountScreen(navController = navController)

}

@Composable
fun AccountScreen(navController: NavHostController) {

    Scaffold(
        bottomBar = {
            BottomBar(navController)
        },
    ) { innerPadding ->

        Column {
            AccountTopBar()
            InfoScreen(navController)
            //LogoutButton(navController)
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
fun AccountTopBar() {
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
            text = "Informasi Akun",
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
private fun InfoScreen(navController: NavHostController) {
    // Get the UserRepository instance
    val userRepository = UserRepository.getInstance(
        UserPreference.getInstance(LocalContext.current.dataStore),
        ApiService.create("https://backend-dot-fruition-api-408523.et.r.appspot.com/")
    )

    // Collect the user data from the data store using Flow
    val user by userRepository.getSession().collectAsState(null)

    Spacer(modifier = Modifier.height(16.dp))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Username
        Text(
            text = "Username",
            color = Color.LightGray,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(10.dp)
                .align(Alignment.Start)
        )
        user?.let {
            Text(
                text = it.username ?: "",
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
            text = "Email",
            color = Color.LightGray,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(10.dp)
                .align(Alignment.Start)
        )
        user?.let {
            Text(
                text = it.email ?: "Not Found",
                color = Color.Black,
                fontSize = 21.sp,
                modifier = Modifier
                    .padding(5.dp)
                    .align(Alignment.Start)

            )
        }
        Column {
        Spacer(modifier = Modifier.width(32.dp))
        LogoutButton(navController = navController)
        }
    }
}


@Composable
private fun LogoutButton(navController: NavHostController) {
     val context = LocalContext.current
    Button(
        onClick = {
            (context as? ComponentActivity)?.lifecycleScope?.launch {
                val repository = Injection.provideRepository(context)
                repository.logout()

                navController.navigate(Screen.Login.route){
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                }
                context.restartApp()
            }
        },
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFdbdbdb)),
        modifier = Modifier
            .heightIn(min = 48.dp)
            .widthIn(min = 334.dp)
            //.align(Alignment.CenterHorizontally)
            .background((MaterialTheme.colorScheme.primary), shape = RoundedCornerShape(15.dp)),
        shape = RoundedCornerShape(15.dp),
        //border = BorderStroke(1.dp, Color.Gray),
        content = {
            Spacer(modifier = Modifier.width(8.dp))
            Text("Logout", fontWeight = FontWeight.Bold, color = Color.Gray, fontSize = 16.sp)
        }
    )
}