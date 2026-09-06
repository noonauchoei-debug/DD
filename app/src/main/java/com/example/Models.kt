package com.example

import androidx.compose.ui.graphics.Color

// ---------------- STORE PROFILE ----------------
data class StoreProfile(
    val shopName: String = "ดีดี เทเลคอม (DD Telecom)",
    val address: String = "123/45 ศูนย์การค้าไอที ถ.สุขุมวิท กรุงเทพฯ 10110",
    val phone: String = "081-234-5678, 089-987-6543",
    val lineId: String = "@ddtelecom",
    val taxId: String = "0105566012345",
    val promptPay: String = "0812345678",
    val footerMessage: String = "สินค้ารับประกันคุณภาพ ยินดีให้บริการ ขอบคุณครับ"
)

// ---------------- PRODUCTS & SALES ----------------
data class Product(
    val code: String = "",
    val name: String = "",
    val price: Double = 0.0,
    var stock: Int = 0,
    var image: String? = null,
    val category: String = "ทั่วไป"
)

data class Sale(
    val code: String = "",
    val qty: Int = 0,
    val total: Double = 0.0,
    val productName: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

// ---------------- REPAIR SERVICE ----------------
enum class RepairStatus(val title: String, val colorHex: Long) {
    PENDING("รับเครื่องแล้ว / รอตรวจ", 0xFFE65100),
    IN_PROGRESS("กำลังดำเนินการซ่อม", 0xFF1565C0),
    WAITING_PARTS("รออะไหล่", 0xFF6A1B9A),
    READY("ซ่อมเสร็จแล้ว / รอรับ", 0xFF2E7D32),
    DELIVERED("ส่งมอบเครื่องแล้ว", 0xFF455A64),
    CANCELLED("ยกเลิกการซ่อม", 0xFFC62828);

    val color: Color get() = Color(colorHex)
}

data class RepairTicket(
    val ticketNo: String = "REP-${System.currentTimeMillis() % 100000}",
    val customerName: String = "",
    val customerPhone: String = "",
    val deviceModel: String = "",
    val imeiOrSerial: String = "",
    val devicePasscode: String = "",
    val symptoms: String = "",
    val repairDetails: String = "",
    val estimatedCost: Double = 0.0,
    val totalCost: Double = 0.0,
    val deposit: Double = 0.0,
    val status: RepairStatus = RepairStatus.PENDING,
    val warrantyDays: Int = 90,
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null,
    val technicianNote: String = ""
)

// ---------------- WARRANTY CARDS ----------------
data class WarrantyCard(
    val warrantyNo: String = "WAR-${System.currentTimeMillis() % 100000}",
    val customerName: String = "",
    val customerPhone: String = "",
    val itemTitle: String = "",
    val imeiOrSerial: String = "",
    val coverageType: String = "ประกันอะไหล่และค่าแรง (Hardware & Labor)",
    val durationDays: Int = 90,
    val startDate: Long = System.currentTimeMillis(),
    val expireDate: Long = System.currentTimeMillis() + (90L * 24 * 60 * 60 * 1000),
    val note: String = ""
)
