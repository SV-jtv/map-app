package com.example.mapsapp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mapsapp.utils.AuthState
import com.example.mapsapp.utils.SharedPreferencesHelper
import com.example.mapsapp.viewmodels.AuthViewModel
import com.example.mapsapp.viewmodels.AuthViewModelFactory

@Composable
fun LoginScreen(navigateToSignUp: () -> Unit, navigateToHome: () -> Unit) {
    val context = LocalContext.current
    val viewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(SharedPreferencesHelper(context)))

    val authState by viewModel.authState.observeAsState()
    val showError by viewModel.showError.observeAsState(false)
    val email by viewModel.email.observeAsState("")
    val password by viewModel.password.observeAsState("")

    // Navegar si ya está autenticado
    if (authState == AuthState.Authenticated) {
        navigateToHome()
        return
    }

    // Mostrar errores si hay
    if (showError) {
        val errorMessage = (authState as? AuthState.Error)?.message
        val message = if (errorMessage?.contains("invalid_credentials") == true) {
            "Credenciales inválidas"
        } else {
            "Ha ocurrido un error"
        }
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        viewModel.errorMessageShowed()
    }

    // UI
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val darkBlue = Color(0xFF4D8AC9) // Azul oscuro personalizado

        Text("Iniciar sesión", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { viewModel.editEmail(it) },
            label = { Text("Email") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = darkBlue,
                focusedLabelColor = darkBlue,
                unfocusedLabelColor = Color.Gray,
                cursorColor = darkBlue,
                focusedTextColor = darkBlue,
                unfocusedTextColor = Color.Gray
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { viewModel.editPassword(it) },
            label = { Text("Contraseña") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = darkBlue,
                focusedLabelColor = darkBlue,
                unfocusedLabelColor = Color.Gray,
                cursorColor = darkBlue,
                focusedTextColor = darkBlue,
                unfocusedTextColor = Color.Gray
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { viewModel.signIn() },
            modifier = Modifier.fillMaxWidth(),
            enabled = email.isNotEmpty() && password.isNotEmpty()
        ) {
            Text("Iniciar sesión")
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = navigateToSignUp) {
            Text("¿No tienes cuenta? Regístrate", color =  darkBlue)
        }
    }
}
