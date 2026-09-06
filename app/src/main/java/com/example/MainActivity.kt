package com.example

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.automirrored.outlined.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.ui.theme.MyApplicationTheme
import com.google.firebase.firestore.FirebaseFirestore
import java.io.File

// ---------------- VIEWMODEL ----------------
class ShopVM : ViewModel() {

    var storeProfile by mutableStateOf(StoreProfile())
        private set

    val products = mutableStateListOf<Product>()
    val sales = mutableStateListOf<Sale>()
    val repairs = mutableStateListOf<RepairTicket>()
    val warranties = mutableStateListOf<WarrantyCard>()

    var lastSyncStatus by mutableStateOf<String?>(null)
        private set

    init {
        initSampleData()
    }

    private fun initSampleData() {
        if (products.isEmpty()) {
            products.addAll(
                listOf(
                    Product("TEL-001", "ซิมเน็ต 5G Unlimited 30 วัน", 299.0, 45, "https://images.unsplash.com/photo-1596742578443-7682ef5251cd?w=400&q=80", "ซิมการ์ด"),
                    Product("TEL-002", "หัวชาร์จเร็ว 65W GaN Fast Charger", 590.0, 18, "https://images.unsplash.com/photo-1583863788434-e58a36330cf0?w=400&q=80", "อุปกรณ์ชาร์จ"),
                    Product("TEL-003", "สายชาร์จ Type-C to Type-C 100W (2M)", 199.0, 32, "https://images.unsplash.com/photo-1585338107529-13afc5f02586?w=400&q=80", "สายสัญญาณ"),
                    Product("TEL-004", "พาวเวอร์แบงค์ 20,000mAh PD Fast Charge", 790.0, 12, "https://images.unsplash.com/photo-1609091839311-d5365f9ff1c5?w=400&q=80", "พาวเวอร์แบงค์"),
                    Product("TEL-005", "ฟิล์มกระจกนิรภัย 9H Full Cover", 150.0, 50, "https://images.unsplash.com/photo-1541807084-5c52b6b3adef?w=400&q=80", "ฟิล์มและเคส"),
                    Product("TEL-006", "หูฟังบลูทูธ TWS True Wireless Earbuds", 890.0, 8, "https://images.unsplash.com/photo-1590658268037-6bf12165a8df?w=400&q=80", "อุปกรณ์เสียง")
                )
            )
        }

        if (repairs.isEmpty()) {
            repairs.addAll(
                listOf(
                    RepairTicket(
                        ticketNo = "REP-1001",
                        customerName = "คุณสมชาย ใจดี",
                        customerPhone = "089-111-2233",
                        deviceModel = "iPhone 13 Pro Max",
                        imeiOrSerial = "356891234567890",
                        devicePasscode = "123456",
                        symptoms = "หน้าจอแตก สัมผัสไม่ได้",
                        repairDetails = "เปลี่ยนชุดจอแท้ OLED + ฟิล์มกระจก",
                        estimatedCost = 4500.0,
                        totalCost = 4500.0,
                        deposit = 1000.0,
                        status = RepairStatus.READY,
                        warrantyDays = 90
                    ),
                    RepairTicket(
                        ticketNo = "REP-1002",
                        customerName = "คุณวิภาวรรณ สุขสม",
                        customerPhone = "081-444-5566",
                        deviceModel = "Samsung Galaxy S22 Ultra",
                        imeiOrSerial = "359012345678901",
                        devicePasscode = "9988",
                        symptoms = "แบตเตอรี่บวม ชาร์จไม่เข้า",
                        repairDetails = "เปลี่ยนแบตเตอรี่แท้ และกาวกันน้ำ",
                        estimatedCost = 1800.0,
                        totalCost = 1800.0,
                        deposit = 500.0,
                        status = RepairStatus.IN_PROGRESS,
                        warrantyDays = 180
                    ),
                    RepairTicket(
                        ticketNo = "REP-1003",
                        customerName = "คุณอนุชา แสงทอง",
                        customerPhone = "095-777-8899",
                        deviceModel = "iPad Air 5 (M1)",
                        imeiOrSerial = "DMPX987123",
                        devicePasscode = "",
                        symptoms = "เปิดไม่ติด ชาร์จไฟไม่เข้า บอร์ดร้อน",
                        repairDetails = "ตรวจเช็ค IC Power และเปลี่ยน C ช็อต",
                        estimatedCost = 2500.0,
                        totalCost = 2500.0,
                        deposit = 0.0,
                        status = RepairStatus.PENDING,
                        warrantyDays = 90
                    )
                )
            )
        }

        if (warranties.isEmpty()) {
            warranties.addAll(
                listOf(
                    WarrantyCard(
                        warrantyNo = "WAR-5001",
                        customerName = "คุณสมชาย ใจดี",
                        customerPhone = "089-111-2233",
                        itemTitle = "เปลี่ยนชุดจอ OLED iPhone 13 Pro Max",
                        imeiOrSerial = "356891234567890",
                        coverageType = "รับประกันอะไหล่จอแสดงผลและทัชสกรีน",
                        durationDays = 90,
                        startDate = System.currentTimeMillis() - (5L * 24 * 60 * 60 * 1000),
                        expireDate = System.currentTimeMillis() + (85L * 24 * 60 * 60 * 1000)
                    ),
                    WarrantyCard(
                        warrantyNo = "WAR-5002",
                        customerName = "คุณธนากร ศรีสวัสดิ์",
                        customerPhone = "086-333-4455",
                        itemTitle = "หัวชาร์จ 65W GaN Fast Charger",
                        imeiOrSerial = "GAN65W-2024-889",
                        coverageType = "รับประกันเปลี่ยนตัวใหม่ 1 ปี",
                        durationDays = 365,
                        startDate = System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000),
                        expireDate = System.currentTimeMillis() + (335L * 24 * 60 * 60 * 1000)
                    )
                )
            )
        }
    }

    // ---------------- STORE PROFILE ----------------
    fun updateStoreProfile(profile: StoreProfile, context: Context? = null) {
        storeProfile = profile
        context?.let { saveLocal(it) }
    }

    // ---------------- PRODUCTS ----------------
    fun addProduct(p: Product, context: Context? = null) {
        val existingIndex = products.indexOfFirst { it.code.equals(p.code, ignoreCase = true) }
        if (existingIndex >= 0) {
            products[existingIndex] = p
        } else {
            products.add(p)
        }
        syncToFirebase(p)
        context?.let { saveLocal(it) }
    }

    fun sell(p: Product, qty: Int = 1, context: Context? = null): Boolean {
        if (p.stock >= qty && qty > 0) {
            val index = products.indexOfFirst { it.code == p.code }
            if (index >= 0) {
                val updated = p.copy(stock = p.stock - qty)
                products[index] = updated
                sales.add(Sale(p.code, qty, p.price * qty, p.name))
                syncToFirebase(updated)
                context?.let { saveLocal(it) }
                return true
            }
        }
        return false
    }

    fun adjustStock(p: Product, delta: Int, context: Context? = null) {
        val index = products.indexOfFirst { it.code == p.code }
        if (index >= 0) {
            val newStock = (p.stock + delta).coerceAtLeast(0)
            val updated = p.copy(stock = newStock)
            products[index] = updated
            syncToFirebase(updated)
            context?.let { saveLocal(it) }
        }
    }

    fun deleteProduct(p: Product, context: Context? = null) {
        products.removeAll { it.code == p.code }
        context?.let { saveLocal(it) }
    }

    // ---------------- REPAIR SERVICES ----------------
    fun addRepairTicket(ticket: RepairTicket, context: Context? = null) {
        val idx = repairs.indexOfFirst { it.ticketNo == ticket.ticketNo }
        if (idx >= 0) {
            repairs[idx] = ticket
        } else {
            repairs.add(0, ticket)
        }
        context?.let { saveLocal(it) }
    }

    fun updateRepairStatus(ticket: RepairTicket, newStatus: RepairStatus, context: Context? = null) {
        val idx = repairs.indexOfFirst { it.ticketNo == ticket.ticketNo }
        if (idx >= 0) {
            val completedTime = if (newStatus == RepairStatus.DELIVERED || newStatus == RepairStatus.READY) System.currentTimeMillis() else ticket.completedAt
            val updated = ticket.copy(status = newStatus, completedAt = completedTime)
            repairs[idx] = updated

            // Auto issue warranty if delivered
            if (newStatus == RepairStatus.DELIVERED && ticket.warrantyDays > 0) {
                val existingWarranty = warranties.find { it.imeiOrSerial == ticket.imeiOrSerial && it.itemTitle.contains(ticket.deviceModel) }
                if (existingWarranty == null) {
                    val expTime = System.currentTimeMillis() + (ticket.warrantyDays.toLong() * 24 * 60 * 60 * 1000)
                    val newWarranty = WarrantyCard(
                        warrantyNo = "WAR-${ticket.ticketNo.replace("REP-", "")}",
                        customerName = ticket.customerName,
                        customerPhone = ticket.customerPhone,
                        itemTitle = "งานซ่อม ${ticket.deviceModel} (${ticket.repairDetails.ifBlank { ticket.symptoms }})",
                        imeiOrSerial = ticket.imeiOrSerial,
                        coverageType = "ประกันงานซ่อมและอะไหล่",
                        durationDays = ticket.warrantyDays,
                        startDate = System.currentTimeMillis(),
                        expireDate = expTime
                    )
                    warranties.add(0, newWarranty)
                }
            }
            context?.let { saveLocal(it) }
        }
    }

    fun deleteRepairTicket(ticket: RepairTicket, context: Context? = null) {
        repairs.removeAll { it.ticketNo == ticket.ticketNo }
        context?.let { saveLocal(it) }
    }

    // ---------------- WARRANTIES ----------------
    fun addWarrantyCard(card: WarrantyCard, context: Context? = null) {
        val idx = warranties.indexOfFirst { it.warrantyNo == card.warrantyNo }
        if (idx >= 0) {
            warranties[idx] = card
        } else {
            warranties.add(0, card)
        }
        context?.let { saveLocal(it) }
    }

    fun deleteWarrantyCard(card: WarrantyCard, context: Context? = null) {
        warranties.removeAll { it.warrantyNo == card.warrantyNo }
        context?.let { saveLocal(it) }
    }

    // ---------------- LOCAL PERSISTENCE ----------------
    fun saveLocal(context: Context) {
        try {
            // Products
            val prodFile = File(context.filesDir, "products.txt")
            prodFile.writeText(products.joinToString("\n") {
                "${it.code}#${it.name}#${it.price}#${it.stock}#${it.image ?: ""}#${it.category}"
            })

            // Store Profile
            val profileFile = File(context.filesDir, "store_profile.txt")
            profileFile.writeText("${storeProfile.shopName}#${storeProfile.address}#${storeProfile.phone}#${storeProfile.lineId}#${storeProfile.taxId}#${storeProfile.promptPay}#${storeProfile.footerMessage}")

            // Repairs
            val repFile = File(context.filesDir, "repairs.txt")
            repFile.writeText(repairs.joinToString("\n") {
                "${it.ticketNo}#${it.customerName}#${it.customerPhone}#${it.deviceModel}#${it.imeiOrSerial}#${it.devicePasscode}#${it.symptoms}#${it.repairDetails}#${it.estimatedCost}#${it.totalCost}#${it.deposit}#${it.status.name}#${it.warrantyDays}#${it.createdAt}"
            })

            // Warranties
            val warFile = File(context.filesDir, "warranties.txt")
            warFile.writeText(warranties.joinToString("\n") {
                "${it.warrantyNo}#${it.customerName}#${it.customerPhone}#${it.itemTitle}#${it.imeiOrSerial}#${it.coverageType}#${it.durationDays}#${it.startDate}#${it.expireDate}"
            })

            lastSyncStatus = "บันทึกข้อมูลเรียบร้อยแล้ว"
        } catch (e: Exception) {
            Log.e("ShopVM", "Error saving local: ${e.message}")
        }
    }

    fun loadLocal(context: Context) {
        try {
            // Products
            val prodFile = File(context.filesDir, "products.txt")
            if (prodFile.exists()) {
                val list = mutableListOf<Product>()
                prodFile.readLines().forEach { line ->
                    val p = line.split("#")
                    if (p.size >= 4) {
                        val code = p[0].trim()
                        val name = p[1].trim()
                        val price = p[2].trim().toDoubleOrNull() ?: 0.0
                        val stock = p[3].trim().toIntOrNull() ?: 0
                        val img = if (p.size > 4 && p[4].isNotBlank()) p[4].trim() else null
                        val cat = if (p.size > 5 && p[5].isNotBlank()) p[5].trim() else "ทั่วไป"
                        if (code.isNotEmpty() && name.isNotEmpty()) {
                            list.add(Product(code, name, price, stock, img, cat))
                        }
                    }
                }
                if (list.isNotEmpty()) {
                    products.clear()
                    products.addAll(list)
                }
            }

            // Profile
            val profileFile = File(context.filesDir, "store_profile.txt")
            if (profileFile.exists()) {
                val p = profileFile.readText().split("#")
                if (p.size >= 7) {
                    storeProfile = StoreProfile(p[0], p[1], p[2], p[3], p[4], p[5], p[6])
                }
            }

            // Repairs
            val repFile = File(context.filesDir, "repairs.txt")
            if (repFile.exists()) {
                val list = mutableListOf<RepairTicket>()
                repFile.readLines().forEach { line ->
                    val p = line.split("#")
                    if (p.size >= 14) {
                        val st = try { RepairStatus.valueOf(p[11]) } catch (e: Exception) { RepairStatus.PENDING }
                        list.add(
                            RepairTicket(
                                ticketNo = p[0],
                                customerName = p[1],
                                customerPhone = p[2],
                                deviceModel = p[3],
                                imeiOrSerial = p[4],
                                devicePasscode = p[5],
                                symptoms = p[6],
                                repairDetails = p[7],
                                estimatedCost = p[8].toDoubleOrNull() ?: 0.0,
                                totalCost = p[9].toDoubleOrNull() ?: 0.0,
                                deposit = p[10].toDoubleOrNull() ?: 0.0,
                                status = st,
                                warrantyDays = p[12].toIntOrNull() ?: 90,
                                createdAt = p[13].toLongOrNull() ?: System.currentTimeMillis()
                            )
                        )
                    }
                }
                if (list.isNotEmpty()) {
                    repairs.clear()
                    repairs.addAll(list)
                }
            }

            // Warranties
            val warFile = File(context.filesDir, "warranties.txt")
            if (warFile.exists()) {
                val list = mutableListOf<WarrantyCard>()
                warFile.readLines().forEach { line ->
                    val p = line.split("#")
                    if (p.size >= 9) {
                        list.add(
                            WarrantyCard(
                                warrantyNo = p[0],
                                customerName = p[1],
                                customerPhone = p[2],
                                itemTitle = p[3],
                                imeiOrSerial = p[4],
                                coverageType = p[5],
                                durationDays = p[6].toIntOrNull() ?: 90,
                                startDate = p[7].toLongOrNull() ?: System.currentTimeMillis(),
                                expireDate = p[8].toLongOrNull() ?: System.currentTimeMillis()
                            )
                        )
                    }
                }
                if (list.isNotEmpty()) {
                    warranties.clear()
                    warranties.addAll(list)
                }
            }

            lastSyncStatus = "โหลดข้อมูลครบถ้วนแล้ว"
        } catch (e: Exception) {
            Log.e("ShopVM", "Error loading local: ${e.message}")
        }
    }

    private fun syncToFirebase(p: Product) {
        try {
            val db = FirebaseFirestore.getInstance()
            val data = hashMapOf(
                "code" to p.code,
                "name" to p.name,
                "price" to p.price,
                "stock" to p.stock,
                "image" to (p.image ?: ""),
                "category" to p.category,
                "updatedAt" to System.currentTimeMillis()
            )
            db.collection("products").document(p.code).set(data)
        } catch (e: Throwable) {
            Log.w("ShopVM", "Firestore skipped: ${e.message}")
        }
    }
}

// ---------------- MAIN ACTIVITY ----------------
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                App(this)
            }
        }
    }
}

// ---------------- ROOT APP COMPOSABLE ----------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(activity: Activity, vm: ShopVM = viewModel()) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val context = LocalContext.current

    // Print / Preview Dialog States
    var printableHtml by remember { mutableStateOf<String?>(null) }
    var printableJobTitle by remember { mutableStateOf("") }
    var rawTextSlip by remember { mutableStateOf("") }
    var savedFilePath by remember { mutableStateOf("") }

    // Dialog state controllers
    var showNewRepairDialog by remember { mutableStateOf(false) }
    var showNewWarrantyDialog by remember { mutableStateOf(false) }
    var editingRepairTicket by remember { mutableStateOf<RepairTicket?>(null) }
    var sellProductItem by remember { mutableStateOf<Product?>(null) }

    // Load data on launch
    LaunchedEffect(Unit) {
        vm.loadLocal(context)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.PhoneAndroid,
                                contentDescription = "DD Telecom",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Column {
                            Text(
                                text = vm.storeProfile.shopName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "ระบบขาย หน้าร้าน & บิลซ่อม & ใบประกัน",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            vm.saveLocal(activity)
                            Toast.makeText(activity, "💾 บันทึกข้อมูลทั้งหมดแล้ว", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.testTag("top_save_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Save,
                            contentDescription = "บันทึกข้อมูล",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(
                        onClick = {
                            vm.loadLocal(activity)
                            Toast.makeText(activity, "📥 โหลดข้อมูลสำเร็จ", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.testTag("top_load_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CloudDownload,
                            contentDescription = "โหลดข้อมูล",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier.testTag("bottom_nav_bar"),
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == 0) Icons.Filled.Storefront else Icons.Outlined.Storefront,
                            contentDescription = "หน้าหลัก"
                        )
                    },
                    label = { Text("หน้าหลัก", fontSize = 11.sp, fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal) },
                    modifier = Modifier.testTag("nav_home")
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == 1) Icons.Filled.Inventory2 else Icons.Outlined.Inventory2,
                            contentDescription = "คลัง&ขาย"
                        )
                    },
                    label = { Text("คลัง&ขาย", fontSize = 11.sp, fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal) },
                    modifier = Modifier.testTag("nav_stock")
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = {
                        BadgedBox(
                            badge = {
                                val activeRepairs = vm.repairs.count { it.status != RepairStatus.DELIVERED && it.status != RepairStatus.CANCELLED }
                                if (activeRepairs > 0) {
                                    Badge { Text("$activeRepairs") }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (selectedTab == 2) Icons.Filled.Build else Icons.Outlined.Build,
                                contentDescription = "บิลงานซ่อม"
                            )
                        }
                    },
                    label = { Text("บิลงานซ่อม", fontSize = 11.sp, fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal) },
                    modifier = Modifier.testTag("nav_repairs")
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == 3) Icons.Filled.VerifiedUser else Icons.Outlined.VerifiedUser,
                            contentDescription = "ใบประกัน"
                        )
                    },
                    label = { Text("ใบประกัน", fontSize = 11.sp, fontWeight = if (selectedTab == 3) FontWeight.Bold else FontWeight.Normal) },
                    modifier = Modifier.testTag("nav_warranty")
                )
                NavigationBarItem(
                    selected = selectedTab == 4,
                    onClick = { selectedTab = 4 },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == 4) Icons.Filled.Print else Icons.Outlined.Print,
                            contentDescription = "รายงาน/พิมพ์"
                        )
                    },
                    label = { Text("รายงาน&พิมพ์", fontSize = 11.sp, fontWeight = if (selectedTab == 4) FontWeight.Bold else FontWeight.Normal) },
                    modifier = Modifier.testTag("nav_report")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (selectedTab) {
                0 -> HomeScreen(
                    vm = vm,
                    activity = activity,
                    onNavigateToStock = { selectedTab = 1 },
                    onNavigateToRepairs = { selectedTab = 2 },
                    onNavigateToWarranty = { selectedTab = 3 },
                    onNavigateToReports = { selectedTab = 4 },
                    onOpenNewRepair = { showNewRepairDialog = true },
                    onOpenNewWarranty = { showNewWarrantyDialog = true }
                )
                1 -> StockAndPosScreen(
                    vm = vm,
                    activity = activity,
                    onSellProduct = { prod -> sellProductItem = prod },
                    onPrintReceipt = { salesList, total ->
                        val receiptNo = "REC-${System.currentTimeMillis() % 1000000}"
                        val html = PrintHelper.generateSalesReceiptHtml(vm.storeProfile, salesList, total, receiptNo = receiptNo)
                        val filePath = PrintHelper.createSalesBillFile(activity, vm.storeProfile, salesList, total, receiptNo)
                        printableHtml = html
                        printableJobTitle = "ใบเสร็จรับเงิน-$receiptNo"
                        rawTextSlip = File(filePath).readText()
                        savedFilePath = filePath
                    }
                )
                2 -> RepairOrdersScreen(
                    vm = vm,
                    activity = activity,
                    onAddNewRepair = { showNewRepairDialog = true },
                    onEditRepair = { ticket -> editingRepairTicket = ticket },
                    onPrintRepairTicket = { ticket ->
                        val html = PrintHelper.generateRepairTicketHtml(vm.storeProfile, ticket)
                        val filePath = PrintHelper.createRepairBillFile(activity, vm.storeProfile, ticket)
                        printableHtml = html
                        printableJobTitle = "บิลงานซ่อม-${ticket.ticketNo}"
                        rawTextSlip = File(filePath).readText()
                        savedFilePath = filePath
                    }
                )
                3 -> WarrantyScreen(
                    vm = vm,
                    activity = activity,
                    onAddNewWarranty = { showNewWarrantyDialog = true },
                    onPrintWarranty = { warranty ->
                        val html = PrintHelper.generateWarrantyCertificateHtml(vm.storeProfile, warranty)
                        val filePath = PrintHelper.createWarrantySlipFile(activity, vm.storeProfile, warranty)
                        printableHtml = html
                        printableJobTitle = "ใบรับประกัน-${warranty.warrantyNo}"
                        rawTextSlip = File(filePath).readText()
                        savedFilePath = filePath
                    }
                )
                4 -> ReportAndPrintSettingsScreen(
                    vm = vm,
                    activity = activity,
                    onPrintCustomBill = {
                        val sales = vm.sales.toList()
                        val total = sales.sumOf { it.total }
                        val receiptNo = "REC-SUM-${System.currentTimeMillis() % 100000}"
                        val html = PrintHelper.generateSalesReceiptHtml(vm.storeProfile, sales, total, receiptNo = receiptNo)
                        val filePath = PrintHelper.createSalesBillFile(activity, vm.storeProfile, sales, total, receiptNo)
                        printableHtml = html
                        printableJobTitle = "สรุปยอดขาย-$receiptNo"
                        rawTextSlip = File(filePath).readText()
                        savedFilePath = filePath
                    },
                    onPrintRepairReport = { periodTitle, repairsList ->
                        val totalCostSum = repairsList.sumOf { it.totalCost }
                        val completed = repairsList.count { it.status == RepairStatus.DELIVERED }
                        val inProg = repairsList.count { it.status == RepairStatus.IN_PROGRESS || it.status == RepairStatus.READY }
                        val pending = repairsList.count { it.status == RepairStatus.PENDING || it.status == RepairStatus.WAITING_PARTS }
                        val html = PrintHelper.generateRepairSummaryHtml(
                            store = vm.storeProfile,
                            periodTitle = periodTitle,
                            repairsList = repairsList,
                            totalCostSum = totalCostSum,
                            completedCount = completed,
                            inProgressCount = inProg,
                            pendingCount = pending
                        )
                        val safePeriod = if (periodTitle.contains("วัน")) "daily" else "monthly"
                        val filePath = PrintHelper.createRepairSummaryFile(
                            context = activity,
                            store = vm.storeProfile,
                            periodTitle = periodTitle,
                            repairsList = repairsList,
                            totalCostSum = totalCostSum,
                            fileName = "repair_summary_${safePeriod}_${System.currentTimeMillis() % 100000}"
                        )
                        printableHtml = html
                        printableJobTitle = "สรุปงานซ่อม-$periodTitle"
                        rawTextSlip = File(filePath).readText()
                        savedFilePath = filePath
                    }
                )
            }
        }
    }

    // Modal: New/Edit Repair Ticket Dialog
    if (showNewRepairDialog || editingRepairTicket != null) {
        RepairFormDialog(
            initialTicket = editingRepairTicket,
            onDismiss = {
                showNewRepairDialog = false
                editingRepairTicket = null
            },
            onSave = { ticket ->
                vm.addRepairTicket(ticket, activity)
                Toast.makeText(activity, "บันทึกข้อมูลงานซ่อม ${ticket.ticketNo} เรียบร้อย", Toast.LENGTH_SHORT).show()
                showNewRepairDialog = false
                editingRepairTicket = null
            },
            onSaveAndPrint = { ticket ->
                vm.addRepairTicket(ticket, activity)
                showNewRepairDialog = false
                editingRepairTicket = null

                // Open print preview
                val html = PrintHelper.generateRepairTicketHtml(vm.storeProfile, ticket)
                val filePath = PrintHelper.createRepairBillFile(activity, vm.storeProfile, ticket)
                printableHtml = html
                printableJobTitle = "บิลงานซ่อม-${ticket.ticketNo}"
                rawTextSlip = File(filePath).readText()
                savedFilePath = filePath
            }
        )
    }

    // Modal: New Warranty Dialog
    if (showNewWarrantyDialog) {
        WarrantyFormDialog(
            onDismiss = { showNewWarrantyDialog = false },
            onSave = { warranty ->
                vm.addWarrantyCard(warranty, activity)
                Toast.makeText(activity, "ออกใบรับประกัน ${warranty.warrantyNo} เรียบร้อย", Toast.LENGTH_SHORT).show()
                showNewWarrantyDialog = false
            },
            onSaveAndPrint = { warranty ->
                vm.addWarrantyCard(warranty, activity)
                showNewWarrantyDialog = false

                // Open print preview
                val html = PrintHelper.generateWarrantyCertificateHtml(vm.storeProfile, warranty)
                val filePath = PrintHelper.createWarrantySlipFile(activity, vm.storeProfile, warranty)
                printableHtml = html
                printableJobTitle = "ใบรับประกัน-${warranty.warrantyNo}"
                rawTextSlip = File(filePath).readText()
                savedFilePath = filePath
            }
        )
    }

    // Modal: Sell Product Dialog
    sellProductItem?.let { product ->
        SellQuantityDialog(
            product = product,
            onDismiss = { sellProductItem = null },
            onConfirmSell = { qty ->
                val success = vm.sell(product, qty, activity)
                if (success) {
                    Toast.makeText(activity, "✅ ขาย ${product.name} จำนวน $qty ชิ้น", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(activity, "❌ สินค้าคงเหลือไม่พอ", Toast.LENGTH_SHORT).show()
                }
                sellProductItem = null
            }
        )
    }

    // Modal: Live Universal Print & Thermal Slip Preview Dialog
    printableHtml?.let { html ->
        UniversalPrintPreviewDialog(
            jobTitle = printableJobTitle,
            htmlContent = html,
            rawText = rawTextSlip,
            filePath = savedFilePath,
            onDismiss = {
                printableHtml = null
                rawTextSlip = ""
                savedFilePath = ""
            },
            onPrintToPrinter = {
                PrintHelper.printHtml(activity, html, printableJobTitle)
            },
            onShare = {
                PrintHelper.shareText(activity, rawTextSlip, "แชร์ $printableJobTitle")
            }
        )
    }
}

// ---------------- TAB 0: HOME DASHBOARD ----------------
@Composable
fun HomeScreen(
    vm: ShopVM,
    activity: Activity,
    onNavigateToStock: () -> Unit,
    onNavigateToRepairs: () -> Unit,
    onNavigateToWarranty: () -> Unit,
    onNavigateToReports: () -> Unit,
    onOpenNewRepair: () -> Unit,
    onOpenNewWarranty: () -> Unit
) {
    val totalSales = vm.sales.sumOf { it.total }
    val activeRepairs = vm.repairs.count { it.status != RepairStatus.DELIVERED && it.status != RepairStatus.CANCELLED }
    val readyRepairs = vm.repairs.count { it.status == RepairStatus.READY }
    val validWarranties = vm.warranties.count { System.currentTimeMillis() <= it.expireDate }
    val lowStock = vm.products.count { it.stock <= 5 }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.secondary
                            )
                        )
                    )
                    .padding(20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = vm.storeProfile.shopName,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Text(
                                text = "ระบบหน้าร้าน • พิมพ์บิลซ่อม • ใบประกันสินค้า",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Print,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column {
                            Text(
                                text = "ยอดขายรวมวันนี้",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                            Text(
                                text = "฿ ${PrintHelper.formatPrice(totalSales)}",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "🖨️ รองรับเครื่องพิมพ์ Bluetooth / WiFi",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                fontSize = 11.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        // Quick Stats Row (2x2 Grid)
        Text(
            text = "สถานะภาพรวมร้าน",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickStatCard(
                title = "งานซ่อมค้างอยู่",
                value = "$activeRepairs เครื่อง",
                subtext = if (readyRepairs > 0) "ซ่อมเสร็จรอรับ $readyRepairs เครื่อง" else "กำลังดำเนินการ",
                icon = Icons.Filled.Build,
                iconColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f),
                onClick = onNavigateToRepairs
            )
            QuickStatCard(
                title = "ใบรับประกัน Active",
                value = "$validWarranties ใบ",
                subtext = "ยังอยู่ในระยะเวลา",
                icon = Icons.Filled.VerifiedUser,
                iconColor = Color(0xFF2E7D32),
                modifier = Modifier.weight(1f),
                onClick = onNavigateToWarranty
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickStatCard(
                title = "สินค้าในคลัง",
                value = "${vm.products.size} รายการ",
                subtext = if (lowStock > 0) "ใกล้หมด $lowStock แบบ" else "สต็อกปกติ",
                icon = Icons.Filled.Inventory2,
                iconColor = if (lowStock > 0) Color(0xFFD84315) else MaterialTheme.colorScheme.secondary,
                modifier = Modifier.weight(1f),
                onClick = onNavigateToStock
            )
            QuickStatCard(
                title = "พิมพ์บิล / ใบเสร็จ",
                value = "ออกบิลได้ทันที",
                subtext = "Thermal / A4 / PDF",
                icon = Icons.Filled.ReceiptLong,
                iconColor = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.weight(1f),
                onClick = onNavigateToReports
            )
        }

        // Fast Action Buttons for Repair & Warranty
        Text(
            text = "สร้างเอกสารด่วน (Quick Document Creation)",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onOpenNewRepair,
                modifier = Modifier
                    .weight(1f)
                    .height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("ออกบิลรับซ่อม", fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = onOpenNewWarranty,
                modifier = Modifier
                    .weight(1f)
                    .height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00796B)
                )
            ) {
                Icon(Icons.Filled.CardMembership, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("ออกใบประกัน", fontWeight = FontWeight.Bold)
            }
        }

        // Feature Shortcuts
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "รายการระบบทั้งหมด / Functions",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(10.dp))

                ActionRowItem(
                    icon = Icons.Filled.PointOfSale,
                    iconTint = MaterialTheme.colorScheme.primary,
                    title = "ขายหน้าร้าน & พิมพ์ใบเสร็จ",
                    description = "ตัดสต็อกทันทีและพิมพ์สลิปให้ลูกค้า",
                    onClick = onNavigateToStock
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.surfaceVariant)
                ActionRowItem(
                    icon = Icons.Filled.Build,
                    iconTint = Color(0xFF1565C0),
                    title = "ระบบงานซ่อม & พิมพ์ใบรับเครื่อง",
                    description = "บันทึกอาการ อะไหล่ ค่าบริการ และติดตามสถานะ",
                    onClick = onNavigateToRepairs
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.surfaceVariant)
                ActionRowItem(
                    icon = Icons.Filled.VerifiedUser,
                    iconTint = Color(0xFF2E7D32),
                    title = "ระบบใบรับประกัน (Warranty Certificates)",
                    description = "ออกใบรับประกันงานซ่อม/สินค้า กำหนดวันหมดอายุ",
                    onClick = onNavigateToWarranty
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.surfaceVariant)
                ActionRowItem(
                    icon = Icons.Filled.Settings,
                    iconTint = Color(0xFFE65100),
                    title = "ตั้งค่าหัวบิลร้าน & สรุปยอดขาย",
                    description = "แก้ไขชื่อร้าน ที่อยู่ พร้อมเพย์ และข้อความท้ายบิล",
                    onClick = onNavigateToReports
                )
            }
        }
    }
}

// ---------------- TAB 1: STOCK & POS SCREEN ----------------
@Composable
fun StockAndPosScreen(
    vm: ShopVM,
    activity: Activity,
    onSellProduct: (Product) -> Unit,
    onPrintReceipt: (List<Sale>, Double) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("ทั้งหมด") }
    var showAddProductDialog by remember { mutableStateOf(false) }
    var initialAddProductCode by remember { mutableStateOf("") }
    var showBarcodeScanner by remember { mutableStateOf(false) }
    var viewingBarcodeProduct by remember { mutableStateOf<Product?>(null) }
    var checkedProduct by remember { mutableStateOf<Product?>(null) }
    var notFoundScannedCode by remember { mutableStateOf<String?>(null) }

    val categories = remember(vm.products) {
        listOf("ทั้งหมด") + vm.products.map { it.category }.distinct()
    }

    val filteredProducts = vm.products.filter { product ->
        val matchesSearch = searchQuery.isBlank() ||
                product.name.contains(searchQuery, ignoreCase = true) ||
                product.code.contains(searchQuery, ignoreCase = true)
        val matchesCategory = selectedCategory == "ทั้งหมด" || product.category == selectedCategory
        matchesSearch && matchesCategory
    }

    Scaffold(
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Secondary action: Scanner FAB
                FloatingActionButton(
                    onClick = { showBarcodeScanner = true },
                    containerColor = Color(0xFF0F172A),
                    contentColor = Color(0xFF38BDF8),
                    modifier = Modifier.testTag("pos_fab_scan_barcode")
                ) {
                    Icon(
                        imageVector = Icons.Filled.QrCodeScanner,
                        contentDescription = "ยิงสแกนบาร์โค้ดสินค้า"
                    )
                }

                // Primary action: Add new product
                ExtendedFloatingActionButton(
                    onClick = { showAddProductDialog = true },
                    icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                    text = { Text("เพิ่มสินค้าใหม่") },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White,
                    modifier = Modifier.testTag("pos_fab_add_product")
                )
            }
        }
    ) { pad ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(pad)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Search Bar & Scan Barcode Button Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("pos_search_input"),
                    placeholder = { Text("ค้นหารหัสสินค้า หรือ ชื่อสินค้า...") },
                    leadingIcon = {
                        Icon(Icons.Filled.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Filled.Clear, contentDescription = "ล้างการค้นหา")
                            }
                        }
                    },
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true
                )

                // Quick Barcode Scan Button right next to search
                FilledTonalIconButton(
                    onClick = { showBarcodeScanner = true },
                    modifier = Modifier
                        .size(52.dp)
                        .testTag("pos_quick_scan_btn"),
                    shape = RoundedCornerShape(14.dp),
                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                ) {
                    Icon(
                        imageVector = Icons.Filled.QrCodeScanner,
                        contentDescription = "ยิงบาร์โค้ด",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Category filter chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(categories) { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = category },
                        label = { Text(category, fontSize = 12.sp) },
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Header info & Print POS cart button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "รายการสินค้า (${filteredProducts.size})",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedButton(
                        onClick = { showBarcodeScanner = true },
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Filled.QrCodeScanner, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("ยิงบาร์โค้ด", fontSize = 12.sp)
                    }

                    if (vm.sales.isNotEmpty()) {
                        FilledTonalButton(
                            onClick = {
                                onPrintReceipt(vm.sales.toList(), vm.sales.sumOf { it.total })
                            },
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Filled.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("พิมพ์บิลล่าสุด (${vm.sales.size})", fontSize = 12.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            if (filteredProducts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Outlined.Inventory2,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("ไม่พบสินค้าในระบบ", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .testTag("pos_product_list"),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(filteredProducts, key = { it.code }) { product ->
                        ProductPosCard(
                            product = product,
                            onSell = { onSellProduct(product) },
                            onQuickSell1 = {
                                val success = vm.sell(product, 1, activity)
                                if (success) {
                                    Toast.makeText(activity, "ขาย ${product.name} (1 ชิ้น) แล้ว", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(activity, "สินค้าหมดสต็อก!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onAddStock = {
                                vm.adjustStock(product, 5, activity)
                                Toast.makeText(activity, "เติมสต็อก +5 ชิ้น", Toast.LENGTH_SHORT).show()
                            },
                            onDelete = {
                                vm.deleteProduct(product, activity)
                                Toast.makeText(activity, "ลบ ${product.name} แล้ว", Toast.LENGTH_SHORT).show()
                            },
                            onViewBarcodeQr = {
                                viewingBarcodeProduct = product
                            },
                            onPrintLabel = {
                                val html = PrintHelper.generateProductBarcodeLabelHtml(vm.storeProfile, product)
                                PrintHelper.printHtml(activity, html, "label_${product.code}")
                            }
                        )
                    }
                }
            }
        }
    }

    // --- DIALOGS FOR QR & BARCODE INVENTORY CHECKING ---

    // 1. Live Barcode / QR Code Scanner Viewfinder
    if (showBarcodeScanner) {
        BarcodeScannerDialog(
            title = "ยิงบาร์โค้ด / QR Code เพื่อเช็คของ",
            promptText = "เล็งบาร์โค้ดสินค้า หรือ QR Code ให้อยู่ในเส้นนำสายตา หรือพิมพ์รหัสเพื่อเช็คสต็อก",
            sampleProducts = vm.products.take(6),
            onDismiss = { showBarcodeScanner = false },
            onCodeScanned = { scannedCode ->
                showBarcodeScanner = false
                val clean = scannedCode.trim()
                val found = vm.products.firstOrNull {
                    it.code.equals(clean, ignoreCase = true) ||
                    it.name.contains(clean, ignoreCase = true)
                }
                if (found != null) {
                    checkedProduct = found
                } else {
                    notFoundScannedCode = clean
                }
            }
        )
    }

    // 2. Product Check / Inventory Lookup Modal
    if (checkedProduct != null) {
        ProductCheckDialog(
            product = checkedProduct!!,
            onDismiss = { checkedProduct = null },
            onSell = {
                val current = checkedProduct!!
                val success = vm.sell(current, 1, activity)
                if (success) {
                    Toast.makeText(activity, "ขาย ${current.name} (1 ชิ้น) สำเร็จ", Toast.LENGTH_SHORT).show()
                    checkedProduct = vm.products.firstOrNull { it.code == current.code }
                } else {
                    Toast.makeText(activity, "สินค้าหมดสต็อก!", Toast.LENGTH_SHORT).show()
                }
            },
            onAddStock = { count ->
                val current = checkedProduct!!
                vm.adjustStock(current, count, activity)
                Toast.makeText(activity, "เติมสต็อก +$count ชิ้น เรียบร้อย", Toast.LENGTH_SHORT).show()
                checkedProduct = vm.products.firstOrNull { it.code == current.code }
            },
            onPrintLabel = {
                val current = checkedProduct!!
                val html = PrintHelper.generateProductBarcodeLabelHtml(vm.storeProfile, current)
                PrintHelper.printHtml(activity, html, "label_${current.code}")
            },
            onScanAnother = {
                checkedProduct = null
                showBarcodeScanner = true
            }
        )
    }

    // 3. Product QR Code & Barcode Inspector Modal
    if (viewingBarcodeProduct != null) {
        ProductBarcodeQrDialog(
            product = viewingBarcodeProduct!!,
            onDismiss = { viewingBarcodeProduct = null },
            onPrintLabel = {
                val current = viewingBarcodeProduct!!
                val html = PrintHelper.generateProductBarcodeLabelHtml(vm.storeProfile, current)
                PrintHelper.printHtml(activity, html, "label_${current.code}")
            },
            onQuickSell = {
                val current = viewingBarcodeProduct!!
                val success = vm.sell(current, 1, activity)
                if (success) {
                    Toast.makeText(activity, "ขาย ${current.name} (1 ชิ้น) แล้ว", Toast.LENGTH_SHORT).show()
                    viewingBarcodeProduct = vm.products.firstOrNull { it.code == current.code }
                } else {
                    Toast.makeText(activity, "สินค้าหมดสต็อก!", Toast.LENGTH_SHORT).show()
                }
            },
            onAddStock = { count ->
                val current = viewingBarcodeProduct!!
                vm.adjustStock(current, count, activity)
                Toast.makeText(activity, "เติมสต็อก +$count ชิ้น เรียบร้อย", Toast.LENGTH_SHORT).show()
                viewingBarcodeProduct = vm.products.firstOrNull { it.code == current.code }
            }
        )
    }

    // 4. Scanned Product Not Found in Inventory Modal
    if (notFoundScannedCode != null) {
        ProductNotFoundDialog(
            scannedCode = notFoundScannedCode!!,
            onDismiss = { notFoundScannedCode = null },
            onAddNewWithCode = { code ->
                initialAddProductCode = code
                notFoundScannedCode = null
                showAddProductDialog = true
            }
        )
    }

    // 5. Add New Product Dialog
    if (showAddProductDialog) {
        AddProductDialog(
            initialCode = initialAddProductCode,
            onDismiss = {
                showAddProductDialog = false
                initialAddProductCode = ""
            },
            onSave = { product ->
                vm.addProduct(product, activity)
                Toast.makeText(activity, "เพิ่มสินค้า ${product.name} แล้ว", Toast.LENGTH_SHORT).show()
                showAddProductDialog = false
                initialAddProductCode = ""
            }
        )
    }
}

@Composable
fun ProductPosCard(
    product: Product,
    onSell: () -> Unit,
    onQuickSell1: () -> Unit,
    onAddStock: () -> Unit,
    onDelete: () -> Unit,
    onViewBarcodeQr: () -> Unit,
    onPrintLabel: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    if (!product.image.isNullOrBlank()) {
                        AsyncImage(
                            model = product.image,
                            contentDescription = product.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Filled.PhoneAndroid,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = product.category,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        // Product Code and Clickable QR/Barcode preview chip
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f),
                            modifier = Modifier.clickable { onViewBarcodeQr() }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.QrCode,
                                    contentDescription = "ดู QR / บาร์โค้ด",
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.size(13.dp)
                                )
                                Text(
                                    text = product.code,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "฿ ${PrintHelper.formatPrice(product.price)}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (product.stock <= 5) MaterialTheme.colorScheme.error.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = "คงเหลือ: ${product.stock}",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (product.stock <= 5) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onSell,
                    enabled = product.stock > 0,
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(Icons.Filled.ShoppingCart, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (product.stock > 0) "ขายสินค้า" else "หมดสต็อก", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }

                FilledTonalButton(
                    onClick = onQuickSell1,
                    enabled = product.stock > 0,
                    modifier = Modifier.height(40.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("ขาย 1 ชิ้น", fontSize = 12.sp)
                }

                IconButton(
                    onClick = { expanded = !expanded },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.MoreVert,
                        contentDescription = "เพิ่มเติม"
                    )
                }
            }

            // Expanded Actions
            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = onAddStock,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("+5 สต็อก", fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = onPrintLabel,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Filled.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("พิมพ์ป้าย", fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = onViewBarcodeQr,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Filled.QrCode, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("QR / บาร์โค้ด", fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = onDelete,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Icon(Icons.Filled.DeleteOutline, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("ลบสินค้า", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

// ---------------- TAB 2: REPAIR ORDERS & BILLS SCREEN ----------------
@Composable
fun RepairOrdersScreen(
    vm: ShopVM,
    activity: Activity,
    onAddNewRepair: () -> Unit,
    onEditRepair: (RepairTicket) -> Unit,
    onPrintRepairTicket: (RepairTicket) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedStatusFilter by remember { mutableStateOf<RepairStatus?>(null) }

    val filteredRepairs = vm.repairs.filter { ticket ->
        val matchesSearch = searchQuery.isBlank() ||
                ticket.ticketNo.contains(searchQuery, ignoreCase = true) ||
                ticket.customerName.contains(searchQuery, ignoreCase = true) ||
                ticket.customerPhone.contains(searchQuery, ignoreCase = true) ||
                ticket.deviceModel.contains(searchQuery, ignoreCase = true) ||
                ticket.imeiOrSerial.contains(searchQuery, ignoreCase = true)

        val matchesStatus = selectedStatusFilter == null || ticket.status == selectedStatusFilter
        matchesSearch && matchesStatus
    }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddNewRepair,
                icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                text = { Text("เปิดบิลรับซ่อมใหม่") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            )
        }
    ) { pad ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(pad)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Search Input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("repair_search_input"),
                placeholder = { Text("ค้นหาชื่อลูกค้า, เบอร์โทร, เลขที่บิล, รุ่น...") },
                leadingIcon = {
                    Icon(Icons.Filled.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Filled.Clear, contentDescription = "ล้างการค้นหา")
                        }
                    }
                },
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Status Filter Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    FilterChip(
                        selected = selectedStatusFilter == null,
                        onClick = { selectedStatusFilter = null },
                        label = { Text("ทั้งหมด (${vm.repairs.size})", fontSize = 12.sp) },
                        shape = RoundedCornerShape(10.dp)
                    )
                }
                items(RepairStatus.values()) { status ->
                    val count = vm.repairs.count { it.status == status }
                    FilterChip(
                        selected = selectedStatusFilter == status,
                        onClick = { selectedStatusFilter = status },
                        label = { Text("${status.title} ($count)", fontSize = 12.sp) },
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "รายการงานซ่อม (${filteredRepairs.size} งาน)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            if (filteredRepairs.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Outlined.Build,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("ไม่มีรายการงานซ่อมในหมวดหมู่นี้", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .testTag("repair_list"),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(filteredRepairs, key = { it.ticketNo }) { ticket ->
                        RepairTicketCard(
                            ticket = ticket,
                            onPrint = { onPrintRepairTicket(ticket) },
                            onEdit = { onEditRepair(ticket) },
                            onStatusChange = { newStatus ->
                                vm.updateRepairStatus(ticket, newStatus, activity)
                                Toast.makeText(activity, "เปลี่ยนสถานะเป็น: ${newStatus.title}", Toast.LENGTH_SHORT).show()
                            },
                            onDelete = {
                                vm.deleteRepairTicket(ticket, activity)
                                Toast.makeText(activity, "ลบงานซ่อม ${ticket.ticketNo} แล้ว", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RepairTicketCard(
    ticket: RepairTicket,
    onPrint: () -> Unit,
    onEdit: () -> Unit,
    onStatusChange: (RepairStatus) -> Unit,
    onDelete: () -> Unit
) {
    var showStatusMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: Ticket No & Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = ticket.ticketNo,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "• ${PrintHelper.formatDateOnly(ticket.createdAt)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Clickable status badge to quick change
                Box {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = ticket.status.color.copy(alpha = 0.15f),
                        modifier = Modifier.clickable { showStatusMenu = true }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(ticket.status.color)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = ticket.status.title,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = ticket.status.color
                            )
                            Icon(
                                imageVector = Icons.Filled.ArrowDropDown,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = ticket.status.color
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = showStatusMenu,
                        onDismissRequest = { showStatusMenu = false }
                    ) {
                        RepairStatus.values().forEach { status ->
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .clip(CircleShape)
                                                .background(status.color)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(status.title, fontWeight = if (ticket.status == status) FontWeight.Bold else FontWeight.Normal)
                                    }
                                },
                                onClick = {
                                    onStatusChange(status)
                                    showStatusMenu = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Customer and Device info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "👤 ${ticket.customerName}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "📞 ${ticket.customerPhone}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "📱 ${ticket.deviceModel}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (ticket.imeiOrSerial.isNotBlank()) {
                        Text(
                            text = "IMEI: ${ticket.imeiOrSerial}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Symptoms Box
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Warning,
                            contentDescription = null,
                            tint = Color(0xFFD84315),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "อาการเสีย: ${ticket.symptoms}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFFD84315)
                        )
                    }
                    if (ticket.repairDetails.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "งานที่ทำ: ${ticket.repairDetails}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Cost Summary & Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "ค่าซ่อม: ฿ ${PrintHelper.formatPrice(ticket.totalCost)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    if (ticket.deposit > 0) {
                        Text(
                            text = "มัดจำ ฿ ${PrintHelper.formatPrice(ticket.deposit)} (เหลือ ฿ ${PrintHelper.formatPrice((ticket.totalCost - ticket.deposit).coerceAtLeast(0.0))})",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF2E7D32)
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    // Print Button
                    Button(
                        onClick = onPrint,
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(Icons.Filled.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("พิมพ์บิลซ่อม", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(Icons.Filled.Edit, contentDescription = "แก้ไข", modifier = Modifier.size(18.dp))
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(Icons.Filled.DeleteOutline, contentDescription = "ลบ", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

// ---------------- TAB 3: WARRANTY CERTIFICATE SCREEN ----------------
@Composable
fun WarrantyScreen(
    vm: ShopVM,
    activity: Activity,
    onAddNewWarranty: () -> Unit,
    onPrintWarranty: (WarrantyCard) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredWarranties = vm.warranties.filter { card ->
        searchQuery.isBlank() ||
                card.warrantyNo.contains(searchQuery, ignoreCase = true) ||
                card.customerName.contains(searchQuery, ignoreCase = true) ||
                card.customerPhone.contains(searchQuery, ignoreCase = true) ||
                card.itemTitle.contains(searchQuery, ignoreCase = true) ||
                card.imeiOrSerial.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddNewWarranty,
                icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                text = { Text("ออกใบรับประกันใหม่") },
                containerColor = Color(0xFF00796B),
                contentColor = Color.White
            )
        }
    ) { pad ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(pad)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Search Input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("warranty_search_input"),
                placeholder = { Text("ค้นหาชื่อลูกค้า, เบอร์โทร, IMEI, สินค้า...") },
                leadingIcon = {
                    Icon(Icons.Filled.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Filled.Clear, contentDescription = "ล้างการค้นหา")
                        }
                    }
                },
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "รายการใบรับประกัน (${filteredWarranties.size} ใบ)",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "แตะเพื่อพิมพ์ใบประกัน",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (filteredWarranties.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Outlined.VerifiedUser,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("ยังไม่มีข้อมูลใบรับประกัน", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .testTag("warranty_list"),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(filteredWarranties, key = { it.warrantyNo }) { warranty ->
                        WarrantyCardItem(
                            warranty = warranty,
                            onPrint = { onPrintWarranty(warranty) },
                            onDelete = {
                                vm.deleteWarrantyCard(warranty, activity)
                                Toast.makeText(activity, "ลบใบรับประกัน ${warranty.warrantyNo} แล้ว", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WarrantyCardItem(
    warranty: WarrantyCard,
    onPrint: () -> Unit,
    onDelete: () -> Unit
) {
    val isExpired = System.currentTimeMillis() > warranty.expireDate
    val daysRemaining = ((warranty.expireDate - System.currentTimeMillis()) / (24 * 60 * 60 * 1000)).coerceAtLeast(0)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Top: Warranty No & Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(
                        imageVector = Icons.Filled.Verified,
                        contentDescription = null,
                        tint = if (isExpired) Color.Gray else Color(0xFF2E7D32),
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = warranty.warrantyNo,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isExpired) Color(0xFFFFEBEE) else Color(0xFFE8F5E9)
                ) {
                    Text(
                        text = if (isExpired) "⛔ หมดประกันแล้ว" else "✅ คุ้มครองเหลือ $daysRemaining วัน",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isExpired) Color(0xFFC62828) else Color(0xFF2E7D32)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = warranty.itemTitle,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (warranty.imeiOrSerial.isNotBlank()) {
                Text(
                    text = "IMEI / Serial: ${warranty.imeiOrSerial}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "ลูกค้า: ${warranty.customerName}", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    Text(text = "โทร: ${warranty.customerPhone}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "ระยะเวลา: ${warranty.durationDays} วัน", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Text(
                        text = "หมดอายุ: ${PrintHelper.formatDateOnly(warranty.expireDate)}",
                        fontSize = 12.sp,
                        color = if (isExpired) Color(0xFFC62828) else MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = warranty.coverageType,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onPrint,
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00796B))
                    ) {
                        Icon(Icons.Filled.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("พิมพ์ใบประกัน", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(Icons.Filled.DeleteOutline, contentDescription = "ลบ", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

// ---------------- TAB 4: REPORT & PRINT SETTINGS SCREEN ----------------
@Composable
fun ReportAndPrintSettingsScreen(
    vm: ShopVM,
    activity: Activity,
    onPrintCustomBill: () -> Unit,
    onPrintRepairReport: (periodTitle: String, repairsList: List<RepairTicket>) -> Unit = { _, _ -> }
) {
    var shopName by remember { mutableStateOf(vm.storeProfile.shopName) }
    var address by remember { mutableStateOf(vm.storeProfile.address) }
    var phone by remember { mutableStateOf(vm.storeProfile.phone) }
    var lineId by remember { mutableStateOf(vm.storeProfile.lineId) }
    var taxId by remember { mutableStateOf(vm.storeProfile.taxId) }
    var promptPay by remember { mutableStateOf(vm.storeProfile.promptPay) }
    var footerMsg by remember { mutableStateOf(vm.storeProfile.footerMessage) }

    // State for QR Scanner and QR View Dialogs
    var activeScannerTarget by remember { mutableStateOf<String?>(null) } // "LINE" or "TAX"
    var viewingQrDialog by remember { mutableStateOf<Pair<String, String>?>(null) } // Pair(Type, Value)

    val totalSales = vm.sales.sumOf { it.total }
    val totalQty = vm.sales.sumOf { it.qty }

    // Filter repairs by today vs this month
    val now = System.currentTimeMillis()
    val todayStart = remember(now) {
        val cal = java.util.Calendar.getInstance()
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0)
        cal.set(java.util.Calendar.MINUTE, 0)
        cal.set(java.util.Calendar.SECOND, 0)
        cal.set(java.util.Calendar.MILLISECOND, 0)
        cal.timeInMillis
    }
    val monthStart = remember(now) {
        val cal = java.util.Calendar.getInstance()
        cal.set(java.util.Calendar.DAY_OF_MONTH, 1)
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0)
        cal.set(java.util.Calendar.MINUTE, 0)
        cal.set(java.util.Calendar.SECOND, 0)
        cal.set(java.util.Calendar.MILLISECOND, 0)
        cal.timeInMillis
    }

    val dailyRepairs = vm.repairs.filter { it.createdAt >= todayStart }
    val dailyRevenue = dailyRepairs.sumOf { it.totalCost }
    val dailyCount = dailyRepairs.size

    val monthlyRepairs = vm.repairs.filter { it.createdAt >= monthStart }
    val monthlyRevenue = monthlyRepairs.sumOf { it.totalCost }
    val monthlyCount = monthlyRepairs.size

    var selectedRepairPeriod by remember { mutableStateOf("daily") } // "daily" or "monthly"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Sales Summary Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "📊 รายงานสรุปยอดขาย",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = "${vm.sales.size} บิลการขาย",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("ยอดขายรวมสุทธิ", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("฿ ${PrintHelper.formatPrice(totalSales)}", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("จำนวนชิ้นที่ขาย", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("$totalQty ชิ้น", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Actions for reports
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onPrintCustomBill,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Filled.Print, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("พิมพ์รายงานยอดขาย", fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = {
                            val path = PrintHelper.createSalesBillFile(activity, vm.storeProfile, vm.sales.toList(), totalSales, "DAILY-SUMMARY")
                            Toast.makeText(activity, "📄 บันทึกไฟล์ที่: $path", Toast.LENGTH_LONG).show()
                        },
                        modifier = Modifier.height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Filled.FileDownload, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("ดาวน์โหลดไฟล์")
                    }
                }
            }
        }

        // Repair Summary Card (Daily & Monthly)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Build,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Text(
                            text = "สรุปยอดงานซ่อม (ต่อวัน & เดือน)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Tab selector (รายวัน / รายเดือน)
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(2.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (selectedRepairPeriod == "daily") MaterialTheme.colorScheme.primary else Color.Transparent,
                            modifier = Modifier.clickable { selectedRepairPeriod = "daily" }
                        ) {
                            Text(
                                text = "รายวัน",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selectedRepairPeriod == "daily") Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (selectedRepairPeriod == "monthly") MaterialTheme.colorScheme.primary else Color.Transparent,
                            modifier = Modifier.clickable { selectedRepairPeriod = "monthly" }
                        ) {
                            Text(
                                text = "รายเดือน",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selectedRepairPeriod == "monthly") Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                val currentRepairs = if (selectedRepairPeriod == "daily") dailyRepairs else monthlyRepairs
                val currentRevenue = if (selectedRepairPeriod == "daily") dailyRevenue else monthlyRevenue
                val currentCount = if (selectedRepairPeriod == "daily") dailyCount else monthlyCount
                val periodLabel = if (selectedRepairPeriod == "daily") "วันนี้" else "เดือนนี้"

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "ยอดเงินงานซ่อม ($periodLabel)",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "฿ ${PrintHelper.formatPrice(currentRevenue)}",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00796B)
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "จำนวนงานซ่อม ($periodLabel)",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "$currentCount เครื่อง",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Breakdown Chips
                val completed = currentRepairs.count { it.status == RepairStatus.DELIVERED }
                val ready = currentRepairs.count { it.status == RepairStatus.READY }
                val active = currentRepairs.count { it.status == RepairStatus.IN_PROGRESS || it.status == RepairStatus.PENDING || it.status == RepairStatus.WAITING_PARTS }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF2E7D32).copy(alpha = 0.12f),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("ส่งมอบแล้ว", fontSize = 11.sp, color = Color(0xFF2E7D32))
                            Text("$completed", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                        }
                    }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF00796B).copy(alpha = 0.12f),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("ซ่อมเสร็จรอรับ", fontSize = 11.sp, color = Color(0xFF00796B))
                            Text("$ready", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF00796B))
                        }
                    }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF1565C0).copy(alpha = 0.12f),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("กำลังดำเนินการ", fontSize = 11.sp, color = Color(0xFF1565C0))
                            Text("$active", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1565C0))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Actions for repair reports
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            val title = if (selectedRepairPeriod == "daily") "ยอดงานซ่อมรายวัน" else "ยอดงานซ่อมรายเดือน"
                            onPrintRepairReport(title, currentRepairs)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00796B))
                    ) {
                        Icon(Icons.Filled.Print, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (selectedRepairPeriod == "daily") "พิมพ์สรุปงานซ่อมประจำวัน" else "พิมพ์สรุปงานซ่อมประจำเดือน", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = {
                            val periodTitle = if (selectedRepairPeriod == "daily") "ยอดงานซ่อมรายวัน" else "ยอดงานซ่อมรายเดือน"
                            val safePeriod = if (selectedRepairPeriod == "daily") "daily" else "monthly"
                            val path = PrintHelper.createRepairSummaryFile(
                                context = activity,
                                store = vm.storeProfile,
                                periodTitle = periodTitle,
                                repairsList = currentRepairs,
                                totalCostSum = currentRevenue,
                                fileName = "repair_summary_${safePeriod}_${System.currentTimeMillis() % 100000}"
                            )
                            Toast.makeText(activity, "📄 บันทึกสรุปงานซ่อมที่: $path", Toast.LENGTH_LONG).show()
                        },
                        modifier = Modifier.height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Filled.FileDownload, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("ดาวน์โหลดไฟล์")
                    }
                }
            }
        }

        // Store & Printer Header Configuration Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "🖨️ ตั้งค่าหัวบิลและข้อมูลร้านค้า",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "ข้อมูลเหล่านี้จะปรากฏบนหัวใบเสร็จรับเงิน, บิลซ่อม, และใบรับประกันทุกใบ",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = shopName,
                    onValueChange = { shopName = it },
                    label = { Text("ชื่อร้านค้า") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("ที่อยู่ร้าน") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("เบอร์โทรติดต่อ") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = lineId,
                        onValueChange = { lineId = it },
                        label = { Text("LINE ID / QR Code") },
                        trailingIcon = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (lineId.isNotBlank()) {
                                    IconButton(
                                        onClick = {
                                            viewingQrDialog = Pair("LINE", lineId)
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.QrCode2,
                                            contentDescription = "แสดง QR Code",
                                            tint = Color(0xFF06C755)
                                        )
                                    }
                                }
                                IconButton(
                                    onClick = {
                                        activeScannerTarget = "LINE"
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.QrCodeScanner,
                                        contentDescription = "สแกน QR Code LINE",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = promptPay,
                        onValueChange = { promptPay = it },
                        label = { Text("เบอร์พร้อมเพย์รับเงิน") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = taxId,
                        onValueChange = { taxId = it },
                        label = { Text("เลขผู้เสียภาษี / QR") },
                        trailingIcon = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (taxId.isNotBlank()) {
                                    IconButton(
                                        onClick = {
                                            viewingQrDialog = Pair("TAX", taxId)
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.QrCode2,
                                            contentDescription = "แสดง QR Code",
                                            tint = Color(0xFF0052CC)
                                        )
                                    }
                                }
                                IconButton(
                                    onClick = {
                                        activeScannerTarget = "TAX"
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.QrCodeScanner,
                                        contentDescription = "สแกน QR Code เลขภาษี",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = footerMsg,
                    onValueChange = { footerMsg = it },
                    label = { Text("ข้อความท้ายใบเสร็จ") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        val updated = StoreProfile(
                            shopName = shopName,
                            address = address,
                            phone = phone,
                            lineId = lineId,
                            taxId = taxId,
                            promptPay = promptPay,
                            footerMessage = footerMsg
                        )
                        vm.updateStoreProfile(updated, activity)
                        Toast.makeText(activity, "บันทึกข้อมูลหัวบิลเรียบร้อยแล้ว", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Filled.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("บันทึกการตั้งค่าหัวบิล", fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    // Modal: QR Code Scanner for LINE or TAX ID
    activeScannerTarget?.let { target ->
        QrCodeScannerModal(
            title = if (target == "LINE") "สแกน QR Code LINE ID" else "สแกน QR Code เลขประจำตัวผู้เสียภาษี",
            promptText = if (target == "LINE") "สแกน QR Code เพิ่มเพื่อน / Official Account" else "สแกน QR Code หรือ บาร์โค้ด 13 หลัก",
            targetField = target,
            onDismiss = { activeScannerTarget = null },
            onCodeScanned = { scannedValue ->
                if (target == "LINE") {
                    lineId = scannedValue
                    Toast.makeText(activity, "📷 สแกน LINE ID สำเร็จ: $scannedValue", Toast.LENGTH_SHORT).show()
                } else {
                    taxId = scannedValue
                    Toast.makeText(activity, "📷 สแกนเลขประจำตัวผู้เสียภาษีสำเร็จ: $scannedValue", Toast.LENGTH_SHORT).show()
                }
                activeScannerTarget = null
            }
        )
    }

    // Modal: QR Code Display for LINE or TAX ID
    viewingQrDialog?.let { (type, value) ->
        QrCodeViewDialog(
            title = if (type == "LINE") "QR Code บัญชี LINE" else "QR Code เลขผู้เสียภาษี",
            subtitle = if (type == "LINE") "ลูกค้าสแกนเพื่อแอดไลน์ร้าน" else "สแกนสำหรับออกใบกำกับภาษี",
            valueToScan = value,
            qrType = type,
            onDismiss = { viewingQrDialog = null }
        )
    }
}

// ---------------- DIALOGS & FORMS ----------------

// 1. Universal Print & Thermal Slip Preview Dialog
@Composable
fun UniversalPrintPreviewDialog(
    jobTitle: String,
    htmlContent: String,
    rawText: String,
    filePath: String,
    onDismiss: () -> Unit,
    onPrintToPrinter: () -> Unit,
    onShare: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Title Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.Print, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                        }
                        Column {
                            Text(
                                text = "ตัวอย่างสลิป / สั่งพิมพ์",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = jobTitle,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, contentDescription = "ปิด")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Receipt Slip Visual Container
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                        .heightIn(max = 380.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFFAFAFA))
                        .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    Text(
                        text = rawText,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        color = Color(0xFF1E293B)
                    )
                }

                if (filePath.isNotBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "💾 บันทึกไฟล์แล้ว: ${File(filePath).name}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Print and Share Action Buttons
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onPrintToPrinter,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Filled.Print, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("🖨️ สั่งพิมพ์ออกเครื่องพิมพ์ (Print)", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = onShare,
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Filled.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("แชร์สลิป / ส่ง LINE", fontSize = 13.sp)
                        }

                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier.height(46.dp),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("เสร็จสิ้น")
                        }
                    }
                }
            }
        }
    }
}

// 2. Repair Ticket Form Dialog
@Composable
fun RepairFormDialog(
    initialTicket: RepairTicket? = null,
    onDismiss: () -> Unit,
    onSave: (RepairTicket) -> Unit,
    onSaveAndPrint: (RepairTicket) -> Unit
) {
    var ticketNo by remember { mutableStateOf(initialTicket?.ticketNo ?: "REP-${System.currentTimeMillis() % 100000}") }
    var customerName by remember { mutableStateOf(initialTicket?.customerName ?: "") }
    var customerPhone by remember { mutableStateOf(initialTicket?.customerPhone ?: "") }
    var deviceModel by remember { mutableStateOf(initialTicket?.deviceModel ?: "") }
    var imeiOrSerial by remember { mutableStateOf(initialTicket?.imeiOrSerial ?: "") }
    var devicePasscode by remember { mutableStateOf(initialTicket?.devicePasscode ?: "") }
    var symptoms by remember { mutableStateOf(initialTicket?.symptoms ?: "") }
    var repairDetails by remember { mutableStateOf(initialTicket?.repairDetails ?: "") }
    var totalCostStr by remember { mutableStateOf(if ((initialTicket?.totalCost ?: 0.0) > 0) initialTicket!!.totalCost.toString() else "") }
    var depositStr by remember { mutableStateOf(if ((initialTicket?.deposit ?: 0.0) > 0) initialTicket!!.deposit.toString() else "") }
    var warrantyDaysStr by remember { mutableStateOf((initialTicket?.warrantyDays ?: 90).toString()) }
    var status by remember { mutableStateOf(initialTicket?.status ?: RepairStatus.PENDING) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (initialTicket != null) "แก้ไขบิลงานซ่อม" else "📝 เปิดบิลรับซ่อมใหม่",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, contentDescription = "ปิด")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = ticketNo,
                    onValueChange = { ticketNo = it },
                    label = { Text("เลขที่บิลงานซ่อม") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = customerName,
                        onValueChange = { customerName = it },
                        label = { Text("ชื่อลูกค้า *") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = customerPhone,
                        onValueChange = { customerPhone = it },
                        label = { Text("เบอร์โทรศัพท์ *") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = deviceModel,
                        onValueChange = { deviceModel = it },
                        label = { Text("รุ่นอุปกรณ์ (เช่น iPhone 14) *") },
                        modifier = Modifier.weight(1.2f),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = devicePasscode,
                        onValueChange = { devicePasscode = it },
                        label = { Text("รหัสล็อคหน้าจอ") },
                        modifier = Modifier.weight(0.8f),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = imeiOrSerial,
                    onValueChange = { imeiOrSerial = it },
                    label = { Text("IMEI หรือ Serial Number") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = symptoms,
                    onValueChange = { symptoms = it },
                    label = { Text("อาการเสียที่ลูกค้าแจ้ง *") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = repairDetails,
                    onValueChange = { repairDetails = it },
                    label = { Text("รายการอะไหล่/งานซ่อมที่ต้องทำ") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = totalCostStr,
                        onValueChange = { totalCostStr = it },
                        label = { Text("ราคาค่าซ่อม (฿)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = depositStr,
                        onValueChange = { depositStr = it },
                        label = { Text("เงินมัดจำ (฿)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = warrantyDaysStr,
                    onValueChange = { warrantyDaysStr = it },
                    label = { Text("รับประกันงานซ่อม (วัน)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Submit Buttons
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            val cost = totalCostStr.toDoubleOrNull() ?: 0.0
                            val dep = depositStr.toDoubleOrNull() ?: 0.0
                            val days = warrantyDaysStr.toIntOrNull() ?: 90
                            val ticket = RepairTicket(
                                ticketNo = ticketNo.ifBlank { "REP-${System.currentTimeMillis() % 100000}" },
                                customerName = customerName.ifBlank { "ลูกค้าทั่วไป" },
                                customerPhone = customerPhone.ifBlank { "-" },
                                deviceModel = deviceModel.ifBlank { "อุปกรณ์ไม่ระบุรุ่น" },
                                imeiOrSerial = imeiOrSerial,
                                devicePasscode = devicePasscode,
                                symptoms = symptoms.ifBlank { "ตรวจเช็คทั่วไป" },
                                repairDetails = repairDetails,
                                estimatedCost = cost,
                                totalCost = cost,
                                deposit = dep,
                                status = status,
                                warrantyDays = days
                            )
                            onSaveAndPrint(ticket)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Filled.Print, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("บันทึก และ สั่งพิมพ์บิลซ่อม (Print)", fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = {
                            val cost = totalCostStr.toDoubleOrNull() ?: 0.0
                            val dep = depositStr.toDoubleOrNull() ?: 0.0
                            val days = warrantyDaysStr.toIntOrNull() ?: 90
                            val ticket = RepairTicket(
                                ticketNo = ticketNo.ifBlank { "REP-${System.currentTimeMillis() % 100000}" },
                                customerName = customerName.ifBlank { "ลูกค้าทั่วไป" },
                                customerPhone = customerPhone.ifBlank { "-" },
                                deviceModel = deviceModel.ifBlank { "อุปกรณ์ไม่ระบุรุ่น" },
                                imeiOrSerial = imeiOrSerial,
                                devicePasscode = devicePasscode,
                                symptoms = symptoms.ifBlank { "ตรวจเช็คทั่วไป" },
                                repairDetails = repairDetails,
                                estimatedCost = cost,
                                totalCost = cost,
                                deposit = dep,
                                status = status,
                                warrantyDays = days
                            )
                            onSave(ticket)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("บันทึกข้อมูลอย่างเดียว")
                    }
                }
            }
        }
    }
}

// 3. Warranty Form Dialog
@Composable
fun WarrantyFormDialog(
    onDismiss: () -> Unit,
    onSave: (WarrantyCard) -> Unit,
    onSaveAndPrint: (WarrantyCard) -> Unit
) {
    var warrantyNo by remember { mutableStateOf("WAR-${System.currentTimeMillis() % 100000}") }
    var customerName by remember { mutableStateOf("") }
    var customerPhone by remember { mutableStateOf("") }
    var itemTitle by remember { mutableStateOf("") }
    var imeiOrSerial by remember { mutableStateOf("") }
    var durationDaysStr by remember { mutableStateOf("90") }
    var coverageType by remember { mutableStateOf("รับประกันอะไหล่และงานซ่อม (Hardware & Labor)") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🛡️ ออกใบรับประกันสินค้า/บริการ",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, contentDescription = "ปิด")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = warrantyNo,
                    onValueChange = { warrantyNo = it },
                    label = { Text("เลขที่ใบรับประกัน") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = customerName,
                        onValueChange = { customerName = it },
                        label = { Text("ชื่อลูกค้า *") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = customerPhone,
                        onValueChange = { customerPhone = it },
                        label = { Text("เบอร์โทร *") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = itemTitle,
                    onValueChange = { itemTitle = it },
                    label = { Text("ชื่อสินค้า หรือ งานบริการที่รับประกัน *") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = imeiOrSerial,
                    onValueChange = { imeiOrSerial = it },
                    label = { Text("IMEI หรือ Serial Number") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Quick preset duration chips
                Text("เลือกระยะเวลารับประกัน:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("30", "90", "180", "365").forEach { days ->
                        FilterChip(
                            selected = durationDaysStr == days,
                            onClick = { durationDaysStr = days },
                            label = { Text(if (days == "365") "1 ปี" else "$days วัน", fontSize = 11.sp) },
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = coverageType,
                    onValueChange = { coverageType = it },
                    label = { Text("ขอบเขตความคุ้มครอง") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            val days = durationDaysStr.toIntOrNull() ?: 90
                            val startTime = System.currentTimeMillis()
                            val expTime = startTime + (days.toLong() * 24 * 60 * 60 * 1000)
                            val card = WarrantyCard(
                                warrantyNo = warrantyNo.ifBlank { "WAR-${System.currentTimeMillis() % 100000}" },
                                customerName = customerName.ifBlank { "ลูกค้าทั่วไป" },
                                customerPhone = customerPhone.ifBlank { "-" },
                                itemTitle = itemTitle.ifBlank { "สินค้าทั่วไป" },
                                imeiOrSerial = imeiOrSerial,
                                coverageType = coverageType,
                                durationDays = days,
                                startDate = startTime,
                                expireDate = expTime
                            )
                            onSaveAndPrint(card)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00796B))
                    ) {
                        Icon(Icons.Filled.Print, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("บันทึก และ สั่งพิมพ์ใบรับประกัน (Print)", fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = {
                            val days = durationDaysStr.toIntOrNull() ?: 90
                            val startTime = System.currentTimeMillis()
                            val expTime = startTime + (days.toLong() * 24 * 60 * 60 * 1000)
                            val card = WarrantyCard(
                                warrantyNo = warrantyNo.ifBlank { "WAR-${System.currentTimeMillis() % 100000}" },
                                customerName = customerName.ifBlank { "ลูกค้าทั่วไป" },
                                customerPhone = customerPhone.ifBlank { "-" },
                                itemTitle = itemTitle.ifBlank { "สินค้าทั่วไป" },
                                imeiOrSerial = imeiOrSerial,
                                coverageType = coverageType,
                                durationDays = days,
                                startDate = startTime,
                                expireDate = expTime
                            )
                            onSave(card)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("บันทึกอย่างเดียว")
                    }
                }
            }
        }
    }
}

// 4. Add Product Dialog
@Composable
fun AddProductDialog(
    initialCode: String = "",
    onDismiss: () -> Unit,
    onSave: (Product) -> Unit
) {
    var code by remember { mutableStateOf(initialCode) }
    var name by remember { mutableStateOf("") }
    var priceStr by remember { mutableStateOf("") }
    var stockStr by remember { mutableStateOf("10") }
    var category by remember { mutableStateOf("อุปกรณ์เสริม") }
    var imageUrl by remember { mutableStateOf("") }

    val defaultCategories = listOf("ซิมการ์ด", "อุปกรณ์ชาร์จ", "สายสัญญาณ", "พาวเวอร์แบงค์", "ฟิล์มและเคส", "อุปกรณ์เสียง", "สมาร์ทโฟน", "อุปกรณ์เสริม")

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("📦 เพิ่มสินค้าใหม่ในสต็อก", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, contentDescription = "ปิด")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = code,
                    onValueChange = { code = it },
                    label = { Text("รหัสสินค้า (เช่น TEL-007)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("ชื่อสินค้า") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = priceStr,
                        onValueChange = { priceStr = it },
                        label = { Text("ราคา (฿)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = stockStr,
                        onValueChange = { stockStr = it },
                        label = { Text("จำนวนสต็อก") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text("หมวดหมู่สินค้า:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(defaultCategories) { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat, fontSize = 11.sp) },
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = imageUrl,
                    onValueChange = { imageUrl = it },
                    label = { Text("URL รูปภาพสินค้า (ไม่บังคับ)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val autoCode = code.ifBlank { "TEL-${System.currentTimeMillis() % 10000}" }
                        val price = priceStr.toDoubleOrNull() ?: 0.0
                        val stock = stockStr.toIntOrNull() ?: 1
                        val img = imageUrl.ifBlank { null }
                        onSave(Product(autoCode, name.ifBlank { "สินค้าทั่วไป" }, price, stock, img, category))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Filled.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("บันทึกสินค้าลงระบบ", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// 5. Sell Quantity Dialog
@Composable
fun SellQuantityDialog(
    product: Product,
    onDismiss: () -> Unit,
    onConfirmSell: (Int) -> Unit
) {
    var qty by remember { mutableIntStateOf(1) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🛒 ขายสินค้าหน้าร้าน",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = product.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "ราคาชิ้นละ: ฿ ${PrintHelper.formatPrice(product.price)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "สต็อกคงเหลือ: ${product.stock} ชิ้น",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Quantity selector
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    FilledIconButton(
                        onClick = { if (qty > 1) qty-- },
                        enabled = qty > 1
                    ) {
                        Icon(Icons.Filled.Remove, contentDescription = "ลดจำนวน")
                    }

                    Text(
                        text = "$qty",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )

                    FilledIconButton(
                        onClick = { if (qty < product.stock) qty++ },
                        enabled = qty < product.stock
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = "เพิ่มจำนวน")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "ยอดรวม: ฿ ${PrintHelper.formatPrice(product.price * qty)}",
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("ยกเลิก")
                    }

                    Button(
                        onClick = { onConfirmSell(qty) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("ยืนยันขาย", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
