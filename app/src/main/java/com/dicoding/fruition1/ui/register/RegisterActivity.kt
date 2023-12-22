package com.dicoding.fruition1.ui.register

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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dicoding.fruition1.R
import com.dicoding.fruition1.api.Injection
import com.dicoding.fruition1.data.UserRepository
import com.dicoding.fruition1.data.pref.UserPreference
import com.dicoding.fruition1.data.pref.dataStore
import com.dicoding.fruition1.ui.login.LoginScreen
import com.dicoding.fruition1.ui.login.LoginViewModel
import com.dicoding.fruition1.ui.navigation.Screen
import com.dicoding.fruition1.ui.theme.Fruition1Theme
import com.dicoding.fruition1.ui.theme.defaultTosca
import kotlinx.coroutines.launch

class RegisterActivity : ComponentActivity() {
    private val registerViewModel: RegisterViewModel by viewModels()
    private val loginViewModel: LoginViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerViewModel.fetchDataFromBackend()
        setContent {
            Fruition1Theme {

                // A surface container using the 'background' color from the theme
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = Screen.Login.route
                ) {
                    composable(Screen.Login.route) {
                        LoginScreen(navController, loginViewModel)
                    }
                    composable(Screen.Register.route) {
                        RegisterScreen(navController)
                    }
                }
            }
        }
    }
}


@Composable
fun RegisterScreen(navController: NavController) {
    val repository = Injection.provideRepository(LocalContext.current)
    val viewModel: RegisterViewModel = viewModel(
        factory = RegisterViewModelFactory(
            repository,
            LocalContext.current,
            UserPreference.getInstance(LocalContext.current.dataStore)
        )
    )
    DisposableEffect(Unit) {
        viewModel.fetchDataFromBackend()
        onDispose { /* Cleanup logic if needed */ }
    }

    Box {
        Image(
            painter = painterResource(R.drawable.bg_login),
            contentDescription = "",
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (-33).dp)
                .clip(RoundedCornerShape(20.dp)),
            contentScale = ContentScale.Crop
        )
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(40.dp))
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
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Selamat datang!",
            fontSize = 22.sp,
            color = Color.White,
            textAlign = TextAlign.Start,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(8.dp)
        )
        Spacer(modifier = Modifier.height(20.dp))

        var username by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var confirmPassword by remember { mutableStateOf("") }


        Text(
            text = "Username",
            fontSize = 15.sp,
            fontWeight = FontWeight.Normal,
            color = Color.White,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .width(280.dp)
                .offset(x = 4.dp)
        )
        RegUsernameTextEditor(value = username, onValueChange = { username = it })
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Email",
            fontSize = 15.sp,
            fontWeight = FontWeight.Normal,
            color = Color.White,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .width(280.dp)
                .offset(x = 4.dp)
        )
        RegEmailTextEditor(value = email, onValueChange = { email = it })
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
                .offset(x = 4.dp)
        )
        RegPasswordTextEditor(value = password, onValueChange = { password = it })
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Confirm Password",
            fontSize = 15.sp,
            textAlign = TextAlign.Start,
            fontWeight = FontWeight.Normal,
            color = Color.White,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .width(280.dp)
                .offset(x = 4.dp)
        )
        ConfirmPasswordTextEditor(value = confirmPassword, onValueChange = { confirmPassword = it })
        Spacer(modifier = Modifier.height(24.dp))
        val context = LocalContext.current
        RegisterButton(onClick = {

            if (username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
                if (password.length >= 6 ) {
                    if (password == confirmPassword) {
                        try {
                            viewModel.registerUser(email, username, password).let { response ->
                                if (response is UserRepository.RegistrationResult.Success) {
                                    Toast.makeText(
                                        context,
                                        "Register successful",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    navController.navigate(Screen.Login.route)
                                } else {
                                }
                            }
                        } catch (e: Exception) {
                            Toast.makeText(
                                context,
                                "Unable to register right now. Please try again later",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            context,
                            "Please double-check your password",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                        Toast.makeText(
                            context,
                            "Password must be at least 6 characters",
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
        })

        Spacer(modifier = Modifier.height(50.dp))
        ToLogin(navController = navController)
    }


}

@Composable
fun RegUsernameTextEditor(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "Enter Your Name"
) {
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
                    .padding(horizontal = 20.dp) // margin left and right
                    .width(282.dp)
                    .height(41.dp)
                    .border(
                        width = 2.dp,
                        color = Color(0xFFFAFAFA),
                        shape = RoundedCornerShape(size = 16.dp)
                    )
                    .background(color = Color(0xFFFAFAFA), shape = RoundedCornerShape(size = 15.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp), // inner padding
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
fun RegEmailTextEditor(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "Enter Your Email"
) {


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
                    .padding(horizontal = 20.dp) // margin left and right
                    .width(282.dp)
                    .height(41.dp)
                    .border(
                        width = 2.dp,
                        color = Color(0xFFFAFAFA),
                        shape = RoundedCornerShape(size = 16.dp)
                    )
                    .background(color = Color(0xFFFAFAFA), shape = RoundedCornerShape(size = 15.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp), // inner padding
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
fun RegPasswordTextEditor(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "Enter Password"
) {
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
fun ConfirmPasswordTextEditor(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "Confirm Password"
) {

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
fun RegisterButton(onClick: suspend () -> Unit) {
    val viewModel = viewModel<RegisterViewModel>()

    Button(
        onClick = {
            viewModel.viewModelScope.launch {
                onClick()
            }
        },
        Modifier
            .width(282.dp)
            .height(41.dp)
            .clip(RoundedCornerShape(size = 15.dp)),
        colors = ButtonDefaults.buttonColors(containerColor = defaultTosca),
    ) {
        Text(text = "Register", color = Color.White)
    }
}


@Composable
fun ToLogin(navController: NavController) {
    Row {
        Text(
            text = "Sudah punya akun?",
            fontSize = 20.sp,
            color = Color.Black,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .padding(2.dp)
        )
        Text(
            text = "Login",
            fontSize = 20.sp,
            color = Color.Black,
            textAlign = TextAlign.Start,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(2.dp)
                .clickable {
                    navController.navigate(Screen.Login.route)
                }
        )
    }
}

