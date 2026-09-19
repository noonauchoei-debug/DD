package com.example

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddBusiness
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.ToggleOff
import androidx.compose.material.icons.filled.ToggleOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import java.util.UUID

private const val ACCESS_FILE = "dd_talacom_access.txt"

private data class AccessState(
    val approved: Boolean,
    val email: String,
    val role: AppRole
)

private class OwnerAccessStore(private val context: Context) {
    fun read(): AccessState {
        val file = context.getFileStreamPath(ACCESS_FILE)
        if (!file.exists()) return AccessState(false, "", AppRole.PENDING)
        val fields = file.readText().split("|")
        return AccessState(
            approved = fields.getOrNull(0) == "approved",
            email = fields.getOrNull(1).orEmpty(),
            role = fields.getOrNull(2)?.let { runCatching { AppRole.valueOf(it) }.getOrNull() }
                ?: AppRole.PENDING
        )
    }

    fun save(email: String, role: AppRole, approved: Boolean) {
        context.openFileOutput(ACCESS_FILE, Context.MODE_PRIVATE).use {
            it.write("${if (approved) "approved" else "pending"}|$email|${role.name}".toByteArray())
        }
    }

    fun clear() = context.deleteFile(ACCESS_FILE)
}

/** First-run local gate. Replace the demo approval action with Firebase/Auth backend approval before release. */
@Composable
fun OwnerManagedApp(activity: Activity) {
    val store = remember { OwnerAccessStore(activity) }
    var access by remember { mutableStateOf(store.read()) }

    if (!access.approved) {
        LoginScreen(
            onRequestAccess = { email ->
                store.save(email, AppRole.PENDING, approved = false)
                access = store.read()
            }
        )
        return
    }

    OwnerDashboard(
        email = access.email,
        onLogout = {
            store.clear()
            access = store.read()
        },
        onOpenApp = { App(activity) }
    )
}

@Composable
private fun LoginScreen(onRequestAccess: (String) -> Unit) {
    var email by remember { mutableStateOf("") }
    var requested by remember { mutableStateOf(false) }

    Scaffold { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Filled.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(12.dp))
            Text("DD Talacom", style = MaterialTheme.typography.headlineMedium)
            Text("เข้าสู่ระบบสำหรับเจ้าของและผู้ได้รับอนุญาต", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(24.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("อีเมลเจ้าของ/ผู้ใช้งาน") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            Button(
                onClick = { if (email.contains("@")) { onRequestAccess(email.trim()); requested = true } },
                enabled = email.contains("@"),
                modifier = Modifier.fillMaxWidth()
            ) { Text("ส่งคำขออนุญาตใช้งาน") }
            if (requested) {
                Spacer(Modifier.height(12.dp))
                Text("ส่งคำขอแล้ว กรุณารอเจ้าของระบบอนุมัติ", color = MaterialTheme.colorScheme.primary)
            }
            Spacer(Modifier.height(16.dp))
            Text("ระบบตัวอย่างจะยังไม่เปิดใช้งานจนกว่าจะมีการอนุมัติจากเจ้าของ", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun OwnerDashboard(email: String, onLogout: () -> Unit, onOpenApp: () -> Unit) {
    val branches = remember {
        mutableStateListOf(
            BranchProfile("branch-main", "สาขาหลัก", true, true, AppRole.OWNER)
        )
    }
    var showAddBranch by remember { mutableStateOf(false) }
    var branchName by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("DD Talacom • Owner Dashboard") },
                actions = { IconButton(onClick = onLogout) { Icon(Icons.Filled.Logout, "ออกจากระบบ") } }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                    Column(Modifier.padding(16.dp)) {
                        Text("ยินดีต้อนรับเจ้าของระบบ", style = MaterialTheme.typography.titleLarge)
                        Text(email)
                        Text("สิทธิ์: ${AppRole.OWNER.label}")
                        Spacer(Modifier.height(10.dp))
                        Button(onClick = onOpenApp, modifier = Modifier.fillMaxWidth()) {
                            Icon(Icons.Filled.Business, null)
                            Text("เปิดระบบร้าน DD Talacom", modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                }
            }
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("จัดการสาขา (${branches.size})", style = MaterialTheme.typography.titleLarge)
                    OutlinedButton(onClick = { showAddBranch = true }) {
                        Icon(Icons.Filled.AddBusiness, null)
                        Text("เพิ่มสาข")
                    }
                }
            }
            items(branches, key = { it.branchId }) { branch ->
                BranchCard(
                    branch = branch,
                    canDelete = branches.size > 1,
                    onToggle = {
                        val i = branches.indexOfFirst { it.branchId == branch.branchId }
                        if (i >= 0) branches[i] = branch.copy(isActive = !branch.isActive)
                    },
                    onDelete = { branches.removeAll { it.branchId == branch.branchId } }
                )
            }
        }
    }

    if (showAddBranch) {
        AlertDialog(
            onDismissRequest = { showAddBranch = false },
            title = { Text("เพิ่มสาขาใหม่") },
            text = { OutlinedTextField(value = branchName, onValueChange = { branchName = it }, label = { Text("ชื่อสาขา") }, singleLine = true) },
            confirmButton = {
                TextButton(onClick = {
                    if (branchName.isNotBlank()) {
                        branches.add(BranchProfile("branch-${UUID.randomUUID()}", branchName.trim(), true, true, AppRole.BRANCH_MANAGER))
                        branchName = ""
                        showAddBranch = false
                    }
                }) { Text("บันทึก") }
            },
            dismissButton = { TextButton(onClick = { showAddBranch = false }) { Text("ยกเลิก") } }
        )
    }
}

@Composable
private fun BranchCard(branch: BranchProfile, canDelete: Boolean, onToggle: () -> Unit, onDelete: () -> Unit) {
    Card {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.ManageAccounts, null, tint = MaterialTheme.colorScheme.primary)
                Column(Modifier.weight(1f).padding(start = 10.dp)) {
                    Text(branch.branchName, style = MaterialTheme.typography.titleMedium)
                    Text("ID: ${branch.branchId}", style = MaterialTheme.typography.bodySmall)
                    Text(if (branch.isActive) "เปิดใช้งาน" else "ปิดใช้งาน", color = if (branch.isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
                }
                IconButton(onClick = onToggle) { Icon(if (branch.isActive) Icons.Filled.ToggleOn else Icons.Filled.ToggleOff, "สลับสถานะ") }
                if (canDelete) IconButton(onClick = onDelete) { Icon(Icons.Filled.DeleteOutline, "ลบสาขา") }
            }
            Text("ผู้จัดการ: ${branch.role.label} • อนุมัติแล้ว", style = MaterialTheme.typography.bodySmall)
        }
    }
}
