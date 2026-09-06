package com.example

import android.content.Context
import android.content.Intent
import android.os.Build
import android.print.PrintAttributes
import android.print.PrintManager
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import java.io.File
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PrintHelper {

    fun formatPrice(amount: Double): String {
        return NumberFormat.getNumberInstance(Locale.US).apply {
            minimumFractionDigits = 0
            maximumFractionDigits = 2
        }.format(amount)
    }

    fun formatDate(timestamp: Long): String {
        return SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("th", "TH")).format(Date(timestamp))
    }

    fun formatDateOnly(timestamp: Long): String {
        return SimpleDateFormat("dd/MM/yyyy", Locale("th", "TH")).format(Date(timestamp))
    }

    // ---------------- HTML TEMPLATES FOR PRINTING ----------------

    fun generateSalesReceiptHtml(
        store: StoreProfile,
        sales: List<Sale>,
        totalAmount: Double,
        receivedAmount: Double = 0.0,
        changeAmount: Double = 0.0,
        receiptNo: String = "REC-${System.currentTimeMillis() % 1000000}"
    ): String {
        val dateStr = formatDate(System.currentTimeMillis())
        val itemsHtml = StringBuilder()

        sales.forEachIndexed { index, sale ->
            val cleanCode = sale.code.ifBlank { "000000" }
            itemsHtml.append("""
                <tr>
                    <td style="padding: 6px 0; text-align: left; vertical-align: top;">
                        <div style="font-weight: bold; color: #111;">${sale.productName.ifBlank { sale.code }}</div>
                        <div style="font-size: 11px; color: #555; margin-top: 2px;">
                            <span>รหัส: <b>${sale.code}</b></span>
                        </div>
                        <div style="margin-top: 3px; font-family: monospace; font-size: 12px; letter-spacing: 2px; color: #222; border-left: 2px solid #0052CC; padding-left: 4px;">
                            ||| | |||| || | | ${sale.code}
                        </div>
                    </td>
                    <td style="padding: 6px 0; text-align: center; vertical-align: top; font-weight: bold;">${sale.qty}</td>
                    <td style="padding: 6px 0; text-align: right; vertical-align: top; font-weight: bold; color: #0052CC;">${formatPrice(sale.total)}</td>
                </tr>
            """.trimIndent())
        }

        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="utf-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>ใบเสร็จรับเงิน - ${store.shopName}</title>
                <style>
                    body {
                        font-family: 'Sarabun', 'Helvetica Neue', Arial, sans-serif;
                        color: #111;
                        background: #fff;
                        margin: 0;
                        padding: 16px;
                        font-size: 13px;
                        line-height: 1.4;
                    }
                    .receipt-container {
                        max-width: 320px;
                        margin: 0 auto;
                        border: 1px dashed #ccc;
                        padding: 16px;
                    }
                    .header {
                        text-align: center;
                        margin-bottom: 12px;
                    }
                    .shop-name {
                        font-size: 18px;
                        font-weight: bold;
                        color: #0052CC;
                        margin-bottom: 4px;
                    }
                    .shop-info {
                        font-size: 11px;
                        color: #444;
                        margin-bottom: 2px;
                    }
                    .divider {
                        border-top: 1px dashed #444;
                        margin: 8px 0;
                    }
                    .double-divider {
                        border-top: 2px solid #111;
                        margin: 8px 0;
                    }
                    table {
                        width: 100%;
                        border-collapse: collapse;
                    }
                    th {
                        font-size: 12px;
                        font-weight: bold;
                        border-bottom: 1px dashed #666;
                        padding-bottom: 4px;
                    }
                    .total-row {
                        font-size: 14px;
                        font-weight: bold;
                    }
                    .footer {
                        text-align: center;
                        font-size: 11px;
                        color: #555;
                        margin-top: 14px;
                    }
                    .badge {
                        display: inline-block;
                        background: #eef4ff;
                        color: #0052cc;
                        padding: 2px 6px;
                        border-radius: 4px;
                        font-size: 10px;
                        font-weight: bold;
                    }
                    @media print {
                        body { padding: 0; }
                        .receipt-container { border: none; padding: 0; width: 100%; }
                    }
                </style>
            </head>
            <body>
                <div class="receipt-container">
                    <div class="header">
                        <div class="shop-name">${store.shopName}</div>
                        <div class="shop-info">${store.address}</div>
                        <div class="shop-info">โทร: ${store.phone} ${if (store.taxId.isNotBlank()) " | เลขประจำตัวผู้เสียภาษี: " + store.taxId else ""}</div>
                        <div style="margin-top: 6px;"><span class="badge">ใบเสร็จรับเงิน / SALES RECEIPT</span></div>
                    </div>

                    <div class="divider"></div>
                    <div style="font-size: 11px; display: flex; justify-content: space-between;">
                        <span>เลขที่: <b>$receiptNo</b></span>
                        <span>วันที่: $dateStr</span>
                    </div>
                    <div class="divider"></div>

                    <table>
                        <thead>
                            <tr>
                                <th style="text-align: left;">รายการ</th>
                                <th style="text-align: center; width: 40px;">จำนวน</th>
                                <th style="text-align: right; width: 70px;">รวม (฿)</th>
                            </tr>
                        </thead>
                        <tbody>
                            $itemsHtml
                        </tbody>
                    </table>

                    <div class="divider"></div>

                    <table>
                        <tr class="total-row">
                            <td style="text-align: left; padding: 4px 0;">ยอดสุทธิ:</td>
                            <td style="text-align: right; padding: 4px 0; color: #0052CC; font-size: 16px;">฿ ${formatPrice(totalAmount)}</td>
                        </tr>
                        ${if (receivedAmount > 0) """
                        <tr>
                            <td style="text-align: left; font-size: 11px; color: #555;">รับเงินมา:</td>
                            <td style="text-align: right; font-size: 11px;">฿ ${formatPrice(receivedAmount)}</td>
                        </tr>
                        <tr>
                            <td style="text-align: left; font-size: 11px; color: #555;">เงินทอน:</td>
                            <td style="text-align: right; font-size: 11px;">฿ ${formatPrice(changeAmount)}</td>
                        </tr>
                        """ else ""}
                    </table>

                    ${if (store.promptPay.isNotBlank()) """
                    <div style="text-align: center; margin-top: 10px; padding: 6px; background: #f8fafc; border-radius: 6px;">
                        <div style="font-size: 11px; font-weight: bold; color: #0052CC;">พร้อมเพย์ (PromptPay): ${store.promptPay}</div>
                    </div>
                    """ else ""}

                    <div style="text-align: center; margin-top: 14px; padding-top: 10px; border-top: 1px dashed #ccc;">
                        <div style="font-family: monospace; font-size: 16px; font-weight: bold; letter-spacing: 4px; color: #111;">
                            ||||| | |||| | ||| |||| | ||
                        </div>
                        <div style="font-size: 10px; color: #666; margin-top: 2px;">บาร์โค้ดตรวจสอบบิล: $receiptNo</div>
                    </div>

                    <div class="footer">
                        <div>${store.footerMessage}</div>
                        <div style="margin-top: 4px; font-size: 10px; color: #888;">*** ขอบคุณที่ไว้วางใจใช้บริการ ดีดี เทเลคอม ***</div>
                    </div>
                </div>
            </body>
            </html>
        """.trimIndent()
    }

    fun generateRepairTicketHtml(
        store: StoreProfile,
        ticket: RepairTicket
    ): String {
        val createdDate = formatDate(ticket.createdAt)
        val isCompleted = ticket.status == RepairStatus.DELIVERED || ticket.status == RepairStatus.READY

        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="utf-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>ใบรับซ่อม/ส่งมอบงานซ่อม - ${ticket.ticketNo}</title>
                <style>
                    body {
                        font-family: 'Sarabun', 'Helvetica Neue', Arial, sans-serif;
                        color: #111;
                        background: #fff;
                        margin: 0;
                        padding: 16px;
                        font-size: 13px;
                        line-height: 1.4;
                    }
                    .receipt-container {
                        max-width: 340px;
                        margin: 0 auto;
                        border: 1px solid #0052CC;
                        border-radius: 8px;
                        padding: 16px;
                    }
                    .header {
                        text-align: center;
                        margin-bottom: 10px;
                    }
                    .shop-name {
                        font-size: 18px;
                        font-weight: bold;
                        color: #0052CC;
                    }
                    .shop-info {
                        font-size: 11px;
                        color: #444;
                    }
                    .title-box {
                        background: #0052CC;
                        color: #fff;
                        text-align: center;
                        padding: 4px;
                        font-weight: bold;
                        font-size: 13px;
                        margin: 8px 0;
                        border-radius: 4px;
                    }
                    .info-grid {
                        width: 100%;
                        font-size: 12px;
                        margin-bottom: 8px;
                    }
                    .info-grid td {
                        padding: 3px 0;
                        vertical-align: top;
                    }
                    .label {
                        color: #555;
                        width: 100px;
                    }
                    .value {
                        font-weight: bold;
                        color: #111;
                    }
                    .divider {
                        border-top: 1px dashed #888;
                        margin: 8px 0;
                    }
                    .cost-table {
                        width: 100%;
                        margin: 8px 0;
                        font-size: 12px;
                    }
                    .cost-table td {
                        padding: 3px 0;
                    }
                    .total-box {
                        background: #f0f7ff;
                        padding: 8px;
                        border-radius: 6px;
                        margin: 8px 0;
                    }
                    .signature-box {
                        display: flex;
                        justify-content: space-between;
                        margin-top: 18px;
                        text-align: center;
                        font-size: 11px;
                    }
                    .signature-line {
                        border-top: 1px solid #555;
                        width: 120px;
                        margin-top: 28px;
                        padding-top: 4px;
                    }
                    .terms {
                        font-size: 10px;
                        color: #666;
                        margin-top: 12px;
                        line-height: 1.3;
                        background: #fafafa;
                        padding: 6px;
                        border-radius: 4px;
                    }
                    @media print {
                        body { padding: 0; }
                        .receipt-container { border: 1px solid #111; padding: 12px; width: 100%; }
                    }
                </style>
            </head>
            <body>
                <div class="receipt-container">
                    <div class="header">
                        <div class="shop-name">${store.shopName}</div>
                        <div class="shop-info">${store.address} | โทร: ${store.phone}</div>
                    </div>

                    <div class="title-box">
                        ${if (isCompleted) "ใบเสร็จและส่งมอบงานซ่อม" else "ใบรับเครื่องซ่อม / REPAIR TICKET"}
                    </div>

                    <table class="info-grid">
                        <tr>
                            <td class="label">เลขที่ใบรับซ่อม:</td>
                            <td class="value" style="color: #0052CC; font-size: 14px;">${ticket.ticketNo}</td>
                        </tr>
                        <tr>
                            <td class="label">วันที่รับเครื่อง:</td>
                            <td class="value">$createdDate</td>
                        </tr>
                        <tr>
                            <td class="label">ชื่อลูกค้า:</td>
                            <td class="value">${ticket.customerName}</td>
                        </tr>
                        <tr>
                            <td class="label">เบอร์โทรศัพท์:</td>
                            <td class="value">${ticket.customerPhone}</td>
                        </tr>
                        <tr>
                            <td class="label">รุ่นอุปกรณ์:</td>
                            <td class="value">${ticket.deviceModel}</td>
                        </tr>
                        <tr>
                            <td class="label">IMEI / S/N:</td>
                            <td class="value">${ticket.imeiOrSerial.ifBlank { "-" }}</td>
                        </tr>
                        <tr>
                            <td class="label">รหัสผ่านหน้าจอ:</td>
                            <td class="value">${ticket.devicePasscode.ifBlank { "ไม่มี / ปลดล็อคแล้ว" }}</td>
                        </tr>
                        <tr>
                            <td class="label">อาการเสีย:</td>
                            <td class="value" style="color: #d9381e;">${ticket.symptoms}</td>
                        </tr>
                        ${if (ticket.repairDetails.isNotBlank()) """
                        <tr>
                            <td class="label">รายละเอียดซ่อม:</td>
                            <td class="value">${ticket.repairDetails}</td>
                        </tr>
                        """ else ""}
                    </table>

                    <div class="divider"></div>

                    <div class="total-box">
                        <table class="cost-table">
                            <tr>
                                <td>ค่าบริการ/ค่าอะไหล่:</td>
                                <td style="text-align: right; font-weight: bold;">฿ ${formatPrice(ticket.totalCost)}</td>
                            </tr>
                            ${if (ticket.deposit > 0) """
                            <tr>
                                <td>เงินมัดจำ:</td>
                                <td style="text-align: right; color: #2e7d32;">- ฿ ${formatPrice(ticket.deposit)}</td>
                            </tr>
                            """ else ""}
                            <tr style="font-size: 14px; font-weight: bold; border-top: 1px solid #ccc;">
                                <td style="padding-top: 4px;">ยอดคงเหลือชำระ:</td>
                                <td style="text-align: right; padding-top: 4px; color: #0052CC;">฿ ${formatPrice((ticket.totalCost - ticket.deposit).coerceAtLeast(0.0))}</td>
                            </tr>
                        </table>
                    </div>

                    <div style="font-size: 11px; text-align: center;">
                        <span>การรับประกันงานซ่อม: <b>${ticket.warrantyDays} วัน</b></span>
                    </div>

                    <div class="terms">
                        <b>เงื่อนไขการรับบริการ:</b><br>
                        1. โปรดนำใบรับซ่อมฉบับนี้มารับเครื่องทุกครั้ง<br>
                        2. ประกันเฉพาะอาการและอะไหล่ที่เปลี่ยน ไม่รวมตกน้ำ จอแตก หรือโดนความชื้น<br>
                        3. หากไม่มารับเครื่องเกิน 60 วัน ทางร้านขอสงวนสิทธิ์ในการจัดการตามระเบียบ
                    </div>

                    <table style="width: 100%; margin-top: 24px; text-align: center; font-size: 11px;">
                        <tr>
                            <td style="width: 50%;">
                                <div style="border-top: 1px solid #666; width: 85%; margin: 20px auto 4px auto;"></div>
                                ลายมือชื่อลูกค้า
                            </td>
                            <td style="width: 50%;">
                                <div style="border-top: 1px solid #666; width: 85%; margin: 20px auto 4px auto;"></div>
                                ผู้รับเครื่อง / ช่างผู้ตรวจ
                            </td>
                        </tr>
                    </table>
                </div>
            </body>
            </html>
        """.trimIndent()
    }

    fun generateWarrantyCertificateHtml(
        store: StoreProfile,
        warranty: WarrantyCard
    ): String {
        val startDateStr = formatDateOnly(warranty.startDate)
        val expireDateStr = formatDateOnly(warranty.expireDate)
        val isExpired = System.currentTimeMillis() > warranty.expireDate

        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="utf-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>ใบรับประกันสินค้า - ${warranty.warrantyNo}</title>
                <style>
                    body {
                        font-family: 'Sarabun', 'Helvetica Neue', Arial, sans-serif;
                        color: #111;
                        background: #fff;
                        margin: 0;
                        padding: 16px;
                        font-size: 13px;
                        line-height: 1.4;
                    }
                    .cert-container {
                        max-width: 360px;
                        margin: 0 auto;
                        border: 2px solid #0052CC;
                        border-radius: 12px;
                        padding: 18px;
                        box-shadow: 0 4px 12px rgba(0,0,0,0.05);
                    }
                    .header {
                        text-align: center;
                    }
                    .shop-name {
                        font-size: 20px;
                        font-weight: 800;
                        color: #0052CC;
                        letter-spacing: 0.5px;
                    }
                    .cert-title {
                        display: inline-block;
                        background: linear-gradient(135deg, #0052CC, #00A3BF);
                        color: white;
                        padding: 4px 14px;
                        border-radius: 20px;
                        font-weight: bold;
                        font-size: 13px;
                        margin: 8px 0 12px 0;
                    }
                    .cert-no {
                        font-size: 12px;
                        color: #555;
                        text-align: center;
                        margin-bottom: 12px;
                    }
                    .info-table {
                        width: 100%;
                        border-collapse: collapse;
                        font-size: 12px;
                    }
                    .info-table td {
                        padding: 5px 0;
                        vertical-align: top;
                    }
                    .label {
                        color: #555;
                        width: 110px;
                    }
                    .value {
                        font-weight: bold;
                        color: #111;
                    }
                    .period-box {
                        background: ${if (isExpired) "#fff2f0" else "#eaf8f0"};
                        border: 1px solid ${if (isExpired) "#ffccc7" else "#b7eb8f"};
                        border-radius: 8px;
                        padding: 10px;
                        margin: 12px 0;
                        text-align: center;
                    }
                    .status-tag {
                        font-size: 14px;
                        font-weight: bold;
                        color: ${if (isExpired) "#cf1322" else "#389e0d"};
                    }
                    .terms {
                        font-size: 10px;
                        color: #666;
                        background: #f8fafc;
                        padding: 8px;
                        border-radius: 6px;
                        margin-top: 10px;
                        line-height: 1.4;
                    }
                    .sign-table {
                        width: 100%;
                        margin-top: 24px;
                        text-align: center;
                        font-size: 11px;
                    }
                    @media print {
                        body { padding: 0; }
                        .cert-container { border: 2px solid #0052CC; width: 100%; box-shadow: none; }
                    }
                </style>
            </head>
            <body>
                <div class="cert-container">
                    <div class="header">
                        <div class="shop-name">${store.shopName}</div>
                        <div style="font-size: 11px; color: #666;">${store.address} | โทร: ${store.phone}</div>
                        <div><span class="cert-title">ใบรับประกันสินค้าและงานบริการ</span></div>
                        <div class="cert-no">เลขที่ใบรับประกัน: <b>${warranty.warrantyNo}</b></div>
                    </div>

                    <table class="info-table">
                        <tr>
                            <td class="label">ชื่อลูกค้า:</td>
                            <td class="value">${warranty.customerName}</td>
                        </tr>
                        <tr>
                            <td class="label">เบอร์โทรศัพท์:</td>
                            <td class="value">${warranty.customerPhone}</td>
                        </tr>
                        <tr>
                            <td class="label">รายการสินค้า / ซ่อม:</td>
                            <td class="value" style="color: #0052CC;">${warranty.itemTitle}</td>
                        </tr>
                        <tr>
                            <td class="label">IMEI / S/N:</td>
                            <td class="value">${warranty.imeiOrSerial.ifBlank { "-" }}</td>
                        </tr>
                        <tr>
                            <td class="label">ประเภทการรับประกัน:</td>
                            <td class="value">${warranty.coverageType}</td>
                        </tr>
                    </table>

                    <div class="period-box">
                        <div class="status-tag">
                            ${if (isExpired) "⛔ หมดระยะเวลารับประกันแล้ว" else "✅ อยู่ในระยะเวลารับประกัน (${warranty.durationDays} วัน)"}
                        </div>
                        <div style="font-size: 12px; margin-top: 4px; color: #333;">
                            วันที่เริ่ม: <b>$startDateStr</b> &nbsp;➔&nbsp; วันที่สิ้นสุด: <b style="color: #0052CC;">$expireDateStr</b>
                        </div>
                    </div>

                    <div class="terms">
                        <b>เงื่อนไขและข้อกำหนดการรับประกัน:</b><br>
                        1. รับประกันเฉพาะความเสียหายจากการใช้งานปกติหรือข้อบกพร่องของอะไหล่ที่เปลี่ยน<br>
                        2. <u>ไม่ครอบคลุม</u>: เครื่องตกกระแทก จอแตก บอร์ดหัก ตกน้ำ โดนความชื้น หรือมีรอยแกะซ่อมจากที่อื่น<br>
                        3. กรุณาแสดงใบรับประกันนี้หรือแจ้งเลขที่/เบอร์โทรศัพท์เมื่อนำเครื่องเข้ารับบริการ
                    </div>

                    <table class="sign-table">
                        <tr>
                            <td style="width: 50%;">
                                <div style="border-top: 1px solid #888; width: 80%; margin: 24px auto 4px auto;"></div>
                                ลูกค้าผู้รับประกัน
                            </td>
                            <td style="width: 50%;">
                                <div style="border-top: 1px solid #888; width: 80%; margin: 24px auto 4px auto;"></div>
                                ประทับตรา / ช่างผู้ให้บริการ
                            </td>
                        </tr>
                    </table>
                </div>
            </body>
            </html>
        """.trimIndent()
    }

    // ---------------- NATIVE ANDROID PRINT DISPATCHER ----------------

    fun printHtml(context: Context, htmlContent: String, jobName: String) {
        try {
            val webView = WebView(context)
            webView.webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean = false

                override fun onPageFinished(view: WebView?, url: String?) {
                    val printManager = context.getSystemService(Context.PRINT_SERVICE) as? PrintManager
                    if (printManager != null) {
                        val printAdapter = webView.createPrintDocumentAdapter(jobName)
                        val printAttributes = PrintAttributes.Builder()
                            .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                            .setColorMode(PrintAttributes.COLOR_MODE_COLOR)
                            .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
                            .build()
                        printManager.print(jobName, printAdapter, printAttributes)
                    } else {
                        Toast.makeText(context, "ไม่พบบริการสั่งพิมพ์บนอุปกรณ์นี้", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
        } catch (e: Exception) {
            Toast.makeText(context, "เกิดข้อผิดพลาดในการพิมพ์: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    // ---------------- TEXT-BASED SLIP GENERATORS (FOR FILE DOWNLOAD & SHARING) ----------------

    fun createSalesBillFile(context: Context, store: StoreProfile, sales: List<Sale>, total: Double, receiptNo: String): String {
        val file = File(context.filesDir, "sales_bill_${receiptNo}.txt")
        val dateStr = formatDate(System.currentTimeMillis())

        val sb = StringBuilder()
        sb.append("========================================\n")
        sb.append("         ${store.shopName}\n")
        sb.append("   ${store.address}\n")
        sb.append("   โทร: ${store.phone}\n")
        sb.append("========================================\n")
        sb.append("เลขที่บิล: $receiptNo\n")
        sb.append("วันที่: $dateStr\n")
        sb.append("----------------------------------------\n")
        sb.append(String.format("%-20s %4s %10s\n", "รายการ", "จน.", "รวม(฿)"))
        sb.append("----------------------------------------\n")

        sales.forEach { s ->
            val name = if (s.productName.isNotBlank()) s.productName else s.code
            val shortName = if (name.length > 18) name.take(17) + "…" else name
            sb.append(String.format("%-20s %4d %10s\n", shortName, s.qty, formatPrice(s.total)))
            sb.append(" [${s.code}]\n")
        }

        sb.append("----------------------------------------\n")
        sb.append(String.format("รวมจำนวนชิ้น: %d ชิ้น\n", sales.sumOf { it.qty }))
        sb.append(String.format("ยอดสุทธิ: %s บาท\n", formatPrice(total)))
        if (store.promptPay.isNotBlank()) {
            sb.append("พร้อมเพย์: ${store.promptPay}\n")
        }
        sb.append("========================================\n")
        sb.append("   ${store.footerMessage}\n")
        sb.append("========================================\n")

        file.writeText(sb.toString())
        return file.absolutePath
    }

    fun createRepairBillFile(context: Context, store: StoreProfile, ticket: RepairTicket): String {
        val file = File(context.filesDir, "repair_bill_${ticket.ticketNo}.txt")
        val dateStr = formatDate(ticket.createdAt)

        val sb = StringBuilder()
        sb.append("========================================\n")
        sb.append("         ${store.shopName}\n")
        sb.append("   ใบรับเครื่องซ่อม / ใบเสร็จงานซ่อม\n")
        sb.append("   โทร: ${store.phone}\n")
        sb.append("========================================\n")
        sb.append("เลขที่งานซ่อม: ${ticket.ticketNo}\n")
        sb.append("วันที่: $dateStr\n")
        sb.append("ชื่อลูกค้า: ${ticket.customerName}\n")
        sb.append("เบอร์โทรศัพท์: ${ticket.customerPhone}\n")
        sb.append("----------------------------------------\n")
        sb.append("รุ่นอุปกรณ์: ${ticket.deviceModel}\n")
        sb.append("IMEI / S/N: ${ticket.imeiOrSerial.ifBlank { "-" }}\n")
        sb.append("รหัสผ่านเครื่อง: ${ticket.devicePasscode.ifBlank { "ไม่มี" }}\n")
        sb.append("อาการเสีย: ${ticket.symptoms}\n")
        if (ticket.repairDetails.isNotBlank()) {
            sb.append("งานที่ทำ: ${ticket.repairDetails}\n")
        }
        sb.append("----------------------------------------\n")
        sb.append("สถานะ: ${ticket.status.title}\n")
        sb.append("ค่าบริการ/อะไหล่: ${formatPrice(ticket.totalCost)} บาท\n")
        if (ticket.deposit > 0) {
            sb.append("มัดจำแล้ว: ${formatPrice(ticket.deposit)} บาท\n")
            sb.append("คงเหลือชำระ: ${formatPrice((ticket.totalCost - ticket.deposit).coerceAtLeast(0.0))} บาท\n")
        }
        sb.append("ประกันงานซ่อม: ${ticket.warrantyDays} วัน\n")
        sb.append("========================================\n")
        sb.append("หมายเหตุ: โปรดนำใบรับซ่อมนี้มารับเครื่อง\n")
        sb.append("========================================\n")

        file.writeText(sb.toString())
        return file.absolutePath
    }

    fun createWarrantySlipFile(context: Context, store: StoreProfile, warranty: WarrantyCard): String {
        val file = File(context.filesDir, "warranty_${warranty.warrantyNo}.txt")
        val startStr = formatDateOnly(warranty.startDate)
        val expStr = formatDateOnly(warranty.expireDate)

        val sb = StringBuilder()
        sb.append("========================================\n")
        sb.append("         ${store.shopName}\n")
        sb.append("        ใบรับประกันสินค้าและบริการ\n")
        sb.append("========================================\n")
        sb.append("เลขที่ใบรับประกัน: ${warranty.warrantyNo}\n")
        sb.append("ลูกค้า: ${warranty.customerName} (${warranty.customerPhone})\n")
        sb.append("สินค้า/งานบริการ: ${warranty.itemTitle}\n")
        sb.append("IMEI/Serial: ${warranty.imeiOrSerial.ifBlank { "-" }}\n")
        sb.append("ความคุ้มครอง: ${warranty.coverageType}\n")
        sb.append("ระยะเวลารับประกัน: ${warranty.durationDays} วัน\n")
        sb.append("เริ่มวันที่: $startStr  ถึงวันที่: $expStr\n")
        sb.append("----------------------------------------\n")
        sb.append("เงื่อนไข: ไม่รวมตกน้ำ จอแตก โดนความชื้น\n")
        sb.append("โทรติดต่อ: ${store.phone}\n")
        sb.append("========================================\n")

        file.writeText(sb.toString())
        return file.absolutePath
    }

    fun shareText(context: Context, text: String, title: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        context.startActivity(Intent.createChooser(intent, title))
    }

    // ---------------- REPAIR SUMMARY REPORTS (DAILY & MONTHLY) ----------------

    fun generateRepairSummaryHtml(
        store: StoreProfile,
        periodTitle: String,
        repairsList: List<RepairTicket>,
        totalCostSum: Double,
        completedCount: Int,
        inProgressCount: Int,
        pendingCount: Int
    ): String {
        val printTime = formatDate(System.currentTimeMillis())
        val rowsHtml = StringBuilder()

        repairsList.forEachIndexed { idx, t ->
            val statusColor = when (t.status) {
                RepairStatus.DELIVERED -> "#2E7D32"
                RepairStatus.READY -> "#00796B"
                RepairStatus.IN_PROGRESS -> "#1565C0"
                RepairStatus.CANCELLED -> "#C62828"
                else -> "#E65100"
            }
            rowsHtml.append("""
                <tr>
                    <td style="padding: 6px 4px; border-bottom: 1px solid #e2e8f0; font-size: 11px;">
                        <b>${t.ticketNo}</b><br>
                        <span style="color: #64748B;">${t.deviceModel}</span>
                    </td>
                    <td style="padding: 6px 4px; border-bottom: 1px solid #e2e8f0; font-size: 11px;">
                        ${t.customerName}<br>
                        <span style="color: #64748B;">${t.customerPhone}</span>
                    </td>
                    <td style="padding: 6px 4px; border-bottom: 1px solid #e2e8f0; font-size: 11px; text-align: center;">
                        <span style="color: ${statusColor}; font-weight: bold;">${t.status.title}</span>
                    </td>
                    <td style="padding: 6px 4px; border-bottom: 1px solid #e2e8f0; font-size: 11px; text-align: right; font-weight: bold; color: #0052CC;">
                        ฿ ${formatPrice(t.totalCost)}
                    </td>
                </tr>
            """.trimIndent())
        }

        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="utf-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>รายงานสรุปยอดงานซ่อม - $periodTitle</title>
                <style>
                    body {
                        font-family: 'Sarabun', -apple-system, BlinkMacSystemFont, Arial, sans-serif;
                        color: #0F172A;
                        background: #fff;
                        margin: 0;
                        padding: 16px;
                        font-size: 12px;
                    }
                    .report-card {
                        max-width: 500px;
                        margin: 0 auto;
                        border: 1px solid #CBD5E1;
                        border-radius: 12px;
                        padding: 20px;
                    }
                    .shop-header {
                        text-align: center;
                        margin-bottom: 14px;
                    }
                    .shop-name {
                        font-size: 18px;
                        font-weight: bold;
                        color: #0052CC;
                    }
                    .report-title {
                        font-size: 15px;
                        font-weight: bold;
                        background: #EFF6FF;
                        color: #1D4ED8;
                        padding: 6px 12px;
                        border-radius: 6px;
                        display: inline-block;
                        margin: 6px 0;
                    }
                    .stats-box {
                        display: flex;
                        justify-content: space-between;
                        background: #F8FAFC;
                        border: 1px solid #E2E8F0;
                        border-radius: 8px;
                        padding: 10px;
                        margin-bottom: 14px;
                        text-align: center;
                    }
                    .stat-item {
                        flex: 1;
                    }
                    .stat-val {
                        font-size: 16px;
                        font-weight: bold;
                        color: #0F172A;
                    }
                    .stat-lbl {
                        font-size: 10px;
                        color: #64748B;
                    }
                    table {
                        width: 100%;
                        border-collapse: collapse;
                        margin-bottom: 14px;
                    }
                    th {
                        background: #F1F5F9;
                        padding: 6px 4px;
                        font-size: 11px;
                        border-bottom: 2px solid #CBD5E1;
                        text-align: left;
                    }
                    .total-box {
                        background: #EFF6FF;
                        border: 1.5px solid #3B82F6;
                        border-radius: 8px;
                        padding: 12px;
                        display: flex;
                        justify-content: space-between;
                        align-items: center;
                        font-size: 14px;
                        font-weight: bold;
                    }
                </style>
            </head>
            <body>
                <div class="report-card">
                    <div class="shop-header">
                        <div class="shop-name">${store.shopName}</div>
                        <div style="font-size: 11px; color: #64748B;">${store.address} | โทร: ${store.phone}</div>
                        <div class="report-title">📊 สรุปยอดงานซ่อม: $periodTitle</div>
                        <div style="font-size: 10px; color: #94A3B8;">พิมพ์ข้อมูลเมื่อ: $printTime</div>
                    </div>

                    <div class="stats-box">
                        <div class="stat-item">
                            <div class="stat-val">${repairsList.size}</div>
                            <div class="stat-lbl">งานทั้งหมด</div>
                        </div>
                        <div class="stat-item">
                            <div class="stat-val" style="color: #2E7D32;">$completedCount</div>
                            <div class="stat-lbl">เสร็จ/ส่งมอบ</div>
                        </div>
                        <div class="stat-item">
                            <div class="stat-val" style="color: #1565C0;">$inProgressCount</div>
                            <div class="stat-lbl">กำลังซ่อม</div>
                        </div>
                        <div class="stat-item">
                            <div class="stat-val" style="color: #E65100;">$pendingCount</div>
                            <div class="stat-lbl">รอดำเนินการ</div>
                        </div>
                    </div>

                    <table>
                        <thead>
                            <tr>
                                <th>เลขที่ / รุ่น</th>
                                <th>ลูกค้า</th>
                                <th style="text-align: center;">สถานะ</th>
                                <th style="text-align: right;">ค่าซ่อม</th>
                            </tr>
                        </thead>
                        <tbody>
                            $rowsHtml
                        </tbody>
                    </table>

                    <div class="total-box">
                        <span>ยอดรวมค่าบริการงานซ่อมทั้งหมด:</span>
                        <span style="font-size: 18px; color: #1D4ED8;">฿ ${formatPrice(totalCostSum)}</span>
                    </div>

                    <div style="text-align: center; margin-top: 14px; font-size: 10px; color: #94A3B8;">
                        ระบบบริหารจัดการร้าน ดีดี เทเลคอม (DD Telecom POS & Repair)
                    </div>
                </div>
            </body>
            </html>
        """.trimIndent()
    }

    fun createRepairSummaryFile(
        context: Context,
        store: StoreProfile,
        periodTitle: String,
        repairsList: List<RepairTicket>,
        totalCostSum: Double,
        fileName: String? = null
    ): String {
        val safePeriod = periodTitle.replace(" ", "_").replace("/", "-")
        val effectiveName = fileName ?: "repair_summary_${safePeriod}_${System.currentTimeMillis() % 10000}"
        val file = File(context.filesDir, "${effectiveName}.txt")
        val printTime = formatDate(System.currentTimeMillis())

        val sb = StringBuilder()
        sb.append("========================================\n")
        sb.append("         ${store.shopName}\n")
        sb.append("   รายงานสรุปยอดงานซ่อม: $periodTitle\n")
        sb.append("========================================\n")
        sb.append("พิมพ์เมื่อ: $printTime\n")
        sb.append("จำนวนงานซ่อมทั้งหมด: ${repairsList.size} งาน\n")
        sb.append("----------------------------------------\n")
        sb.append(String.format("%-10s %-16s %10s\n", "เลขที่บิล", "รุ่น / สถานะ", "ค่าซ่อม(฿)"))
        sb.append("----------------------------------------\n")

        repairsList.forEach { t ->
            val shortModel = if (t.deviceModel.length > 15) t.deviceModel.take(14) + "…" else t.deviceModel
            sb.append(String.format("%-10s %-16s %10s\n", t.ticketNo, shortModel, formatPrice(t.totalCost)))
            sb.append(" ลูกค้า: ${t.customerName} [${t.status.title}]\n")
        }

        sb.append("----------------------------------------\n")
        sb.append(String.format("ยอดรวมค่าบริการงานซ่อม: %s บาท\n", formatPrice(totalCostSum)))
        sb.append("========================================\n")

        file.writeText(sb.toString())
        return file.absolutePath
    }

    // ---------------- PRODUCT BARCODE & PRICE TAG LABEL ----------------

    fun generateProductBarcodeLabelHtml(
        store: StoreProfile,
        product: Product
    ): String {
        val printDate = formatDateOnly(System.currentTimeMillis())
        val cleanCode = product.code.ifBlank { "PROD-000" }

        // Generate SVG barcode stripes
        val svgBars = StringBuilder()
        var currentX = 10
        val barPattern = cleanCode.map { (it.code % 5) + 1 }
        // Start guard
        svgBars.append("<rect x=\"$currentX\" y=\"0\" width=\"3\" height=\"44\" fill=\"#000\" />")
        currentX += 5
        svgBars.append("<rect x=\"$currentX\" y=\"0\" width=\"2\" height=\"44\" fill=\"#000\" />")
        currentX += 4

        for (w in barPattern) {
            svgBars.append("<rect x=\"$currentX\" y=\"0\" width=\"$w\" height=\"44\" fill=\"#000\" />")
            currentX += (w + 2)
        }

        // Stop guard
        svgBars.append("<rect x=\"$currentX\" y=\"0\" width=\"2\" height=\"44\" fill=\"#000\" />")
        currentX += 4
        svgBars.append("<rect x=\"$currentX\" y=\"0\" width=\"3\" height=\"44\" fill=\"#000\" />")
        currentX += 10

        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>ป้ายบาร์โค้ดสินค้า - ${product.code}</title>
                <style>
                    body {
                        font-family: 'Sarabun', -apple-system, sans-serif;
                        margin: 0;
                        padding: 16px;
                        background: #f1f5f9;
                        display: flex;
                        justify-content: center;
                    }
                    .label-card {
                        background: #fff;
                        width: 320px;
                        border: 2px solid #0f172a;
                        border-radius: 12px;
                        padding: 14px;
                        box-shadow: 0 4px 6px -1px rgba(0,0,0,0.1);
                        text-align: center;
                    }
                    .shop-header {
                        font-size: 11px;
                        font-weight: bold;
                        color: #64748b;
                        letter-spacing: 1px;
                        text-transform: uppercase;
                        margin-bottom: 4px;
                    }
                    .prod-name {
                        font-size: 15px;
                        font-weight: 800;
                        color: #0f172a;
                        margin-bottom: 6px;
                        line-height: 1.3;
                    }
                    .badge {
                        display: inline-block;
                        background: #e0f2fe;
                        color: #0369a1;
                        font-size: 10px;
                        font-weight: 600;
                        padding: 2px 8px;
                        border-radius: 6px;
                        margin-bottom: 8px;
                    }
                    .price-box {
                        font-size: 26px;
                        font-weight: 900;
                        color: #0284c7;
                        margin: 6px 0;
                    }
                    .barcode-svg-container {
                        margin: 10px auto 4px auto;
                        display: flex;
                        justify-content: center;
                    }
                    .code-text {
                        font-family: monospace;
                        font-size: 13px;
                        font-weight: bold;
                        letter-spacing: 2px;
                        color: #334155;
                    }
                    .footer-note {
                        font-size: 9px;
                        color: #94a3b8;
                        margin-top: 8px;
                        border-top: 1px dashed #cbd5e1;
                        padding-top: 6px;
                    }
                    @media print {
                        body { background: transparent; padding: 0; }
                        .label-card { border: 1.5px solid #000; box-shadow: none; width: 100%; }
                    }
                </style>
            </head>
            <body>
                <div class="label-card">
                    <div class="shop-header">${store.shopName}</div>
                    <div class="prod-name">${product.name}</div>
                    <span class="badge">${product.category}</span>
                    <div class="price-box">฿ ${formatPrice(product.price)}</div>

                    <div class="barcode-svg-container">
                        <svg width="$currentX" height="44" viewBox="0 0 $currentX 44">
                            $svgBars
                        </svg>
                    </div>
                    <div class="code-text">* $cleanCode *</div>

                    <div class="footer-note">
                        DD Telecom POS &bull; สต็อก: ${product.stock} ชิ้น &bull; พิมพ์เมื่อ $printDate
                    </div>
                </div>
            </body>
            </html>
        """.trimIndent()
    }

    fun createBarcodeLabelFile(
        context: Context,
        store: StoreProfile,
        product: Product
    ): String {
        val file = File(context.filesDir, "barcode_${product.code}.txt")
        val printDate = formatDate(System.currentTimeMillis())

        val sb = StringBuilder()
        sb.append("========================================\n")
        sb.append("        ${store.shopName}\n")
        sb.append("       ป้ายราคาสินค้า & บาร์โค้ด\n")
        sb.append("========================================\n")
        sb.append("ชื่อสินค้า: ${product.name}\n")
        sb.append("หมวดหมู่: ${product.category}\n")
        sb.append("รหัสสินค้า: ${product.code}\n")
        sb.append("----------------------------------------\n")
        sb.append("ราคาจำหน่าย: ฿ ${formatPrice(product.price)}\n")
        sb.append("สต็อกคงเหลือ: ${product.stock} ชิ้น\n")
        sb.append("----------------------------------------\n")
        sb.append("BARCODE: |||| | ||||| || |||| |||||\n")
        sb.append("           * ${product.code} *\n")
        sb.append("----------------------------------------\n")
        sb.append("พิมพ์เมื่อ: $printDate\n")
        sb.append("========================================\n")

        file.writeText(sb.toString())
        return file.absolutePath
    }
}

