package com.dicoding.fruition1.ui.login

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dicoding.fruition1.R
import com.dicoding.fruition1.api.ApiService
import com.dicoding.fruition1.api.LoginRequestBody
import com.dicoding.fruition1.api.LoginResponse
import com.dicoding.fruition1.data.UserRepository
import com.dicoding.fruition1.data.pref.UserPreference
import com.dicoding.fruition1.data.pref.dataStore
import com.dicoding.fruition1.ui.account.AccountScreen
import com.dicoding.fruition1.ui.account.AccountViewModel
import com.dicoding.fruition1.ui.detect.DetectScreen
import com.dicoding.fruition1.ui.home.HomeScreen
import com.dicoding.fruition1.ui.navigation.Screen
import com.dicoding.fruition1.ui.register.RegisterScreen
import com.dicoding.fruition1.ui.theme.Fruition1Theme
import com.dicoding.fruition1.ui.theme.defaultTosca
import kotlinx.coroutines.launch


class LoginActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val apiService: ApiService = ApiService.create("https://backend-dot-fruition-api-408523.et.r.appspot.com/")
        val userPreference: UserPreference = UserPreference.getInstance(dataStore)
        val userRepository: UserRepository = UserRepository( userPreference, apiService)
        val accountViewModel: AccountViewModel by viewModels()




        setContent {
            Fruition1Theme {
                // A surface container using the 'background' color from the theme
                val navController = rememberNavController()

                val loginViewModelFactory = LoginViewModelFactory(application, userRepository)
                val loginViewModel: LoginViewModel by viewModels {
                    loginViewModelFactory
                }
                loginViewModel.getSession().observe(this) { user ->
                    if (user.isLogin) {
                        navController.navigate(Screen.Home.route)
                    }
                }

                NavHost(navController = navController, startDestination = Screen.Login.route) {
                    composable(Screen.Login.route) {
                        LoginScreen(navController, loginViewModel)
                    }
                    composable(Screen.Register.route) {
                        RegisterScreen(navController)
                    }
                    composable(Screen.Home.route) {
                        HomeScreen(navController)
                    }
                    composable(Screen.Detect.route) {
                        DetectScreen(navController)
                    }
                    composable(Screen.Account.route) {
                        AccountScreen(navController)
                    }
                }
            }
        }
    }
}


@Composable
fun LoginScreen(navController: NavController, viewModel: LoginViewModel){
    var usernameOrEmail by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }



    Box {
        Image(
            painter = painterResource(R.drawable.bg_login),
            contentDescription = "",
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (-33).dp)
                .clip(RoundedCornerShape(20.dp))
                ,
            contentScale = ContentScale.Crop
        )
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ){
        Spacer(modifier = Modifier.height(100.dp))
        Image(
            painter = painterResource(R.drawable.logo),
            contentDescription = "Fruition Logo",
            modifier = Modifier
                .height(139.dp)
                .clip(RoundedCornerShape(20.dp)),
            contentScale = ContentScale.Fit
        )
        Text(
            text = "Fruition",
            fontSize = 35.sp,
            color = Color.White,
            textAlign = TextAlign.Start,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(8.dp)
        )
        Spacer(modifier = Modifier.height(27.dp))
        Text(
            text = "Selamat datang!",
            fontSize = 22.sp,
            color = Color.White,
            textAlign = TextAlign.Start,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(8.dp)
        )
        Spacer(modifier = Modifier.height(50.dp))

        Text(
            text = "Username",
            fontSize = 15.sp,
            fontWeight = FontWeight.Normal,
            color = Color.White,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .width(280.dp)
                .offset(x=4.dp)
        )
         LoginTextEditor(value = usernameOrEmail, onValueChange = { usernameOrEmail = it })
          Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Password",
            fontSize = 15.sp,
            fontWeight = FontWeight.Normal,
            color = Color.White,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .width(280.dp)
                .offset(x=4.dp)
        )
        PasswordTextEditor(value = password, onValueChange = { password = it })
          Spacer(modifier = Modifier.height(26.dp))
          LoginButton(navController, usernameOrEmail, password, viewModel)

          Spacer(modifier = Modifier.height(115.dp))
        ToRegister(navController = navController)
    }
}

@Composable
fun LoginTextEditor(value: String, onValueChange: (String) -> Unit, placeholder: String = "Enter Username or Email") {

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = TextStyle(
            fontSize = 15.sp,
            color = Color.DarkGray
        ),

        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .width(282.dp)
                    .height(41.dp)
                    .border(
                        width = 2.dp,
                        color = Color(0xFFFAFAFA),
                        shape = RoundedCornerShape(size = 16.dp)
                    )
                    .background(color = Color(0xFFFAFAFA), shape = RoundedCornerShape(size = 15.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            ) {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.Gray
                    )
                }
                innerTextField()
            }
        }
    )
}

@Composable
fun PasswordTextEditor(value: String, onValueChange: (String) -> Unit, placeholder: String = "Enter Password") {
    var passwordVisible by remember { mutableStateOf(false) }

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = TextStyle(
            fontSize = 15.sp,
            color = Color.DarkGray
        ),
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .width(282.dp)
                    .height(41.dp)
                    .border(
                        width = 2.dp,
                        color = Color(0xFFFAFAFA),
                        shape = RoundedCornerShape(size = 16.dp)
                    )
                    .background(color = Color(0xFFFAFAFA), shape = RoundedCornerShape(size = 15.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterEnd) // Align items to the end (right side)
                ) {
                    Box(
                        modifier = Modifier.weight(1f)
                    ) {
                        if (value.isEmpty()) {
                            Text(
                                text = placeholder,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color.Gray
                            )
                        }
                        innerTextField()
                    }

                    IconButton(
                        onClick = { passwordVisible = !passwordVisible },
                        modifier = Modifier
                            .clickable { passwordVisible = !passwordVisible }
                    ) {
                        Icon(
                            painter = painterResource(if (passwordVisible) R.drawable.ic_visibility else R.drawable.ic_visibility_off),
                            contentDescription = if (passwordVisible) "Hide Password" else "Show Password",
                            tint = Color.Gray
                        )
                    }
                }
            }
        }
    )

}


@Composable
fun LoginButton(navController: NavController, usernameOrEmail: String, password: String, viewModel: LoginViewModel) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val loginResponse: LoginResponse? = null
    val navigateToHome by viewModel.navigateToHome.observeAsState()

    Button(
        onClick = {
            if (usernameOrEmail.isNotEmpty() && password.isNotEmpty()) {
                try {
                    viewModel.viewModelScope.launch {
                        val isEmail = isEmail(usernameOrEmail)
                        viewModel.login(
                            LoginRequestBody(
                                email = if (isEmail) usernameOrEmail else "",
                                username = if (!isEmail) usernameOrEmail else "",
                                password = password
                            )
                        )
                        if (loginResponse != null) {
                            val success = loginResponse.success
                            val message = loginResponse.message
                            val token = loginResponse.token


                        }

                    }


                } catch(e: Exception) {
                    Toast.makeText(
                        context,
                        "Unable to logging in now. Please try again later",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    context,
                    "Please fill in all fields",
                    Toast.LENGTH_SHORT
                ).show()
            }
            if (navigateToHome == true) {
                navController.navigate(Screen.Home.route)
            }
        },
        Modifier
            .width(282.dp)
            .height(41.dp)
            .clip(RoundedCornerShape(size = 15.dp)),
        colors = ButtonDefaults.buttonColors(containerColor = defaultTosca),
    ) { Text(text = "Login", color = Color.White) }
}


sealed class LoginRequest {
    data class Email(val email: String, val password: String) : LoginRequest()
    data class Username(val username: String, val password: String) : LoginRequest()
}



@Composable
fun ToRegister(navController: NavController){
    Row {
        Text(
            text = "Belum punya akun?",
            fontSize = 20.sp,
            color = Color.Black,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .padding(2.dp)
        )
        Text(
            text = "Daftar",
            fontSize = 20.sp,
            color = Color.Black,
            textAlign = TextAlign.Start,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(2.dp)
                .clickable {
                    navController.navigate(Screen.Register.route)
                }
        )
    }
}

fun isEmail(input: String): Boolean {
    val emailRegex = Regex("^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})")
    return emailRegex.matches(input)
}
