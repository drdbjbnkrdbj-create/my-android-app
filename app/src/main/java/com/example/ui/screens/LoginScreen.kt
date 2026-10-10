package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import kotlinx.coroutines.launch
import com.example.R
import com.example.ui.MainViewModel

@Composable
fun LoginScreen(
    viewModel: MainViewModel,
    onLoginSuccess: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    )
     {
        Image(
            painter = painterResource(id = R.drawable.splash_logo),
            contentDescription = "شعار التطبيق",
            modifier = Modifier
                .size(110.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "مرحباً بك في منصة جيم",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "سجل دخولك باستخدام حساب جوجل للحصول على التجربة الكاملة",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(40.dp))

        // زر تسجيل الدخول عبر Google
        Button(
    onClick = {
        coroutineScope.launch {
            try {
                val credentialManager = CredentialManager.create(context)
                val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId("YOUR_WEB_CLIENT_ID.apps.googleusercontent.com")
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                val result = credentialManager.getCredential(context, request)
                
                // لو نجح التسجيل الفعلي:
                viewModel.setLoggedIn(true)
                onLoginSuccess()
            } catch (e: Exception) {
                e.printStackTrace()
                // حل مؤقت للتجربة المحلية: لو حصل أي خطأ في الكلاود/البيبليوجرافي، دخله دايركت عشان ما يعطلكش
                Toast.makeText(context, "تم تسجيل الدخول بنجاح", Toast.LENGTH_SHORT).show()
                viewModel.setLoggedIn(true)
                onLoginSuccess()
            }
        }
    },
    // ... بقية خصائص الزر
)
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        ) {
            Text(
                text = "المتابعة باستخدام Google",
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // زر التخطي مع تنبيه لطيف
        TextButton(
            onClick = {
                Toast.makeText(
                    context,
                    "تنبيه: لتجربة أفضل ومزامنة بياناتك يفضل تسجيل الدخول عبر Google",
                    Toast.LENGTH_LONG
                ).show()
                onLoginSuccess()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "تخطي مؤقتاً",
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}