package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ui.theme.MyApplicationTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class FirebaseAuthActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val auth = Firebase.auth

        setContent {
            MyApplicationTheme {
                FirebaseAuthScreen(
                    auth = auth,
                    onSignedIn = {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                )
            }
        }
    }
}

@Composable
fun FirebaseAuthScreen(auth: FirebaseAuth, onSignedIn: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isRegister by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (auth.currentUser != null) {
            onSignedIn()
        }
    }

    fun ensureUserProfile(uid: String, email: String) {
        val db = FirebaseFirestore.getInstance()
        val role = if (email.lowercase().contains("owner") || email.lowercase().contains("admin")) {
            "OWNER"
        } else {
            "PENDING"
        }

        val profile = hashMapOf(
            "email" to email,
            "role" to role,
            "approved" to (role == "OWNER"),
            "createdAt" to System.currentTimeMillis(),
            "branchId" to "",
            "displayName" to email.substringBefore('@')
        )

        db.collection("users").document(uid).set(profile)
            .addOnFailureListener { /* no-op: profile will be retried later */ }
    }

    fun submit() {
        val trimmedEmail = email.trim()
        if (trimmedEmail.isBlank() || password.length < 6) {
            error = "กรุณากรอกอีเมลและรหัสผ่านอย่างน้อย 6 ตัว"
            return
        }

        isLoading = true
        error = null

        val task = if (isRegister) {
            auth.createUserWithEmailAndPassword(trimmedEmail, password)
        } else {
            auth.signInWithEmailAndPassword(trimmedEmail, password)
        }

        task.addOnCompleteListener { result ->
            isLoading = false
            if (result.isSuccessful) {
                val uid = auth.currentUser?.uid ?: return@addOnCompleteListener
                ensureUserProfile(uid, trimmedEmail)
                onSignedIn()
            } else {
                error = result.exception?.message ?: "Authentication failed"
            }
        }
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("DD Talacom", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(8.dp))
            Text(
                if (isRegister) "สร้างบัญชีผู้ใช้" else "เข้าสู่ระบบ",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(Modifier.height(24.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("อีเมล") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("รหัสผ่าน") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            if (error != null) {
                Spacer(Modifier.height(12.dp))
                Text(error ?: "", color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = { submit() },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isRegister) "สร้างบัญชี" else "เข้าสู่ระบบ")
            }

            Spacer(Modifier.height(12.dp))

            TextButton(
                onClick = {
                    isRegister = !isRegister
                    error = null
                }
            ) {
                Text(
                    if (isRegister) "มีบัญชีแล้ว? เข้าสู่ระบบ" else "ยังไม่มีบัญชี? สร้างบัญชี"
                )
            }
        }
    }
}
