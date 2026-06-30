package com.example.ipcalculator

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.util.Locale

enum class AppLanguage(val code: String, val displayName: String) {
    EN("en", "English"),
    TH("th", "ไทย"),
    ES("es", "Español"),
    ZH("zh", "中文"),
    JA("ja", "日本語"),
    DE("de", "Deutsch"),
    FR("fr", "Français")
}

object Translator {
    var currentLanguage by mutableStateOf(LanguageHelper.getSystemLanguage())

    private val dictionary = mapOf(
        "app_title" to mapOf(
            AppLanguage.EN to "IP Calculator Suite",
            AppLanguage.TH to "ชุดเครื่องมือเครื่องคิดเลข IP",
            AppLanguage.ES to "Calculadora de IP",
            AppLanguage.ZH to "IP 计算器套件",
            AppLanguage.JA to "IP電卓スイート",
            AppLanguage.DE to "IP-Rechner-Suite",
            AppLanguage.FR to "Suite de Calculateur IP"
        ),
        "subnet" to mapOf(
            AppLanguage.EN to "Subnet", AppLanguage.TH to "ซับเน็ต", AppLanguage.ES to "Subred",
            AppLanguage.ZH to "子网", AppLanguage.JA to "サブネット", AppLanguage.DE to "Subnetz", AppLanguage.FR to "Sous-réseau"
        ),
        "vlsm" to mapOf(
            AppLanguage.EN to "VLSM/FLSM", AppLanguage.TH to "VLSM/FLSM", AppLanguage.ES to "VLSM/FLSM",
            AppLanguage.ZH to "VLSM/FLSM", AppLanguage.JA to "VLSM/FLSM", AppLanguage.DE to "VLSM/FLSM", AppLanguage.FR to "VLSM/FLSM"
        ),
        "converter" to mapOf(
            AppLanguage.EN to "Converter", AppLanguage.TH to "แปลงเลข IP", AppLanguage.ES to "Conversor",
            AppLanguage.ZH to "转换器", AppLanguage.JA to "変換器", AppLanguage.DE to "Konverter", AppLanguage.FR to "Convertisseur"
        ),
        "supernet" to mapOf(
            AppLanguage.EN to "Supernet", AppLanguage.TH to "ซูเปอร์เน็ต", AppLanguage.ES to "Superred",
            AppLanguage.ZH to "超网", AppLanguage.JA to "スーパーネット", AppLanguage.DE to "Supernetz", AppLanguage.FR to "Super-réseau"
        ),
        "cidr_chart" to mapOf(
            AppLanguage.EN to "CIDR Chart", AppLanguage.TH to "ตาราง CIDR", AppLanguage.ES to "Tabla CIDR",
            AppLanguage.ZH to "CIDR 对照表", AppLanguage.JA to "CIDRチャート", AppLanguage.DE to "CIDR-Tabelle", AppLanguage.FR to "Tableau CIDR"
        ),
        "ip_checker" to mapOf(
            AppLanguage.EN to "IP Checker", AppLanguage.TH to "ตรวจ IP", AppLanguage.ES to "Verificador IP",
            AppLanguage.ZH to "IP 检查器", AppLanguage.JA to "IPチェッカー", AppLanguage.DE to "IP-Prüfer", AppLanguage.FR to "Vérificateur IP"
        ),
        "compare" to mapOf(
            AppLanguage.EN to "Compare", AppLanguage.TH to "เปรียบเทียบ", AppLanguage.ES to "Comparar",
            AppLanguage.ZH to "比较", AppLanguage.JA to "比較", AppLanguage.DE to "Vergleichen", AppLanguage.FR to "Comparer"
        ),
        "ports" to mapOf(
            AppLanguage.EN to "Ports", AppLanguage.TH to "พอร์ตทั่วไป", AppLanguage.ES to "Puertos",
            AppLanguage.ZH to "端口", AppLanguage.JA to "ポート", AppLanguage.DE to "Ports", AppLanguage.FR to "Ports"
        ),
        "eui64" to mapOf(
            AppLanguage.EN to "EUI-64", AppLanguage.TH to "EUI-64", AppLanguage.ES to "EUI-64",
            AppLanguage.ZH to "EUI-64", AppLanguage.JA to "EUI-64", AppLanguage.DE to "EUI-64", AppLanguage.FR to "EUI-64"
        ),
        "mac_oui" to mapOf(
            AppLanguage.EN to "MAC OUI", AppLanguage.TH to "ค้นหา MAC", AppLanguage.ES to "OUI MAC",
            AppLanguage.ZH to "MAC OUI", AppLanguage.JA to "MAC OUI", AppLanguage.DE to "MAC-OUI", AppLanguage.FR to "OUI MAC"
        ),
        "quiz" to mapOf(
            AppLanguage.EN to "Quiz", AppLanguage.TH to "ทดสอบความรู้", AppLanguage.ES to "Examen",
            AppLanguage.ZH to "测验", AppLanguage.JA to "クイズ", AppLanguage.DE to "Quiz", AppLanguage.FR to "Quiz"
        ),
        "about" to mapOf(
            AppLanguage.EN to "About", AppLanguage.TH to "เกี่ยวกับ", AppLanguage.ES to "Acerca de",
            AppLanguage.ZH to "关于", AppLanguage.JA to "情報", AppLanguage.DE to "Über", AppLanguage.FR to "À propos"
        ),
        "ip_address" to mapOf(
            AppLanguage.EN to "IP Address", AppLanguage.TH to "ที่อยู่ IP", AppLanguage.ES to "Dirección IP",
            AppLanguage.ZH to "IP 地址", AppLanguage.JA to "IPアドレス", AppLanguage.DE to "IP-Adresse", AppLanguage.FR to "Adresse IP"
        ),
        "subnet_mask" to mapOf(
            AppLanguage.EN to "Subnet Mask", AppLanguage.TH to "ซับเน็ตมาสก์", AppLanguage.ES to "Máscara de subred",
            AppLanguage.ZH to "子网掩码", AppLanguage.JA to "サブネットマスク", AppLanguage.DE to "Subnetzmaske", AppLanguage.FR to "Masque de sous-réseau"
        ),
        "wildcard_mask" to mapOf(
            AppLanguage.EN to "Wildcard Mask", AppLanguage.TH to "ไวลด์การ์ดมาสก์", AppLanguage.ES to "Máscara wildcard",
            AppLanguage.ZH to "反掩码", AppLanguage.JA to "ワイルドカードマスク", AppLanguage.DE to "Wildcard-Maske", AppLanguage.FR to "Masque wildcard"
        ),
        "network_address" to mapOf(
            AppLanguage.EN to "Network Address", AppLanguage.TH to "ที่อยู่เครือข่าย", AppLanguage.ES to "Dirección de red",
            AppLanguage.ZH to "网络地址", AppLanguage.JA to "ネットワークアドレス", AppLanguage.DE to "Netzwerkadresse", AppLanguage.FR to "Adresse réseau"
        ),
        "broadcast_address" to mapOf(
            AppLanguage.EN to "Broadcast Address", AppLanguage.TH to "ที่อยู่บรอดแคสต์", AppLanguage.ES to "Dirección de broadcast",
            AppLanguage.ZH to "广播地址", AppLanguage.JA to "ブロードキャストアドレス", AppLanguage.DE to "Broadcast-Adresse", AppLanguage.FR to "Adresse de diffusion"
        ),
        "usable_range" to mapOf(
            AppLanguage.EN to "Usable Host Range", AppLanguage.TH to "ช่วง IP ที่ใช้งานได้", AppLanguage.ES to "Rango de IP útil",
            AppLanguage.ZH to "可用主机范围", AppLanguage.JA to "使用可能なホスト範囲", AppLanguage.DE to "Nutzbarer Host-Bereich", AppLanguage.FR to "Plage d'adresses utiles"
        ),
        "usable_hosts" to mapOf(
            AppLanguage.EN to "Usable Hosts", AppLanguage.TH to "จำนวนโฮสต์ที่ใช้งานได้", AppLanguage.ES to "Hosts útiles",
            AppLanguage.ZH to "可用主机数", AppLanguage.JA to "使用可能なホスト数", AppLanguage.DE to "Nutzbare Hosts", AppLanguage.FR to "Hôtes utiles"
        ),
        "total_hosts" to mapOf(
            AppLanguage.EN to "Total Hosts", AppLanguage.TH to "โฮสต์ทั้งหมด", AppLanguage.ES to "Hosts totales",
            AppLanguage.ZH to "总主机数", AppLanguage.JA to "総ホスト数", AppLanguage.DE to "Hosts gesamt", AppLanguage.FR to "Nombre total d'hôtes"
        ),
        "ip_class" to mapOf(
            AppLanguage.EN to "IP Class", AppLanguage.TH to "คลาสของ IP", AppLanguage.ES to "Clase de IP",
            AppLanguage.ZH to "IP 类别", AppLanguage.JA to "IPクラス", AppLanguage.DE to "IP-Klasse", AppLanguage.FR to "Classe d'IP"
        ),
        "ip_type" to mapOf(
            AppLanguage.EN to "IP Type", AppLanguage.TH to "ประเภท IP", AppLanguage.ES to "Tipo de IP",
            AppLanguage.ZH to "IP 类型", AppLanguage.JA to "IPタイプ", AppLanguage.DE to "IP-Typ", AppLanguage.FR to "Type d'IP"
        ),
        "calculate" to mapOf(
            AppLanguage.EN to "Calculate", AppLanguage.TH to "คำนวณ", AppLanguage.ES to "Calcular",
            AppLanguage.ZH to "计算", AppLanguage.JA to "計算", AppLanguage.DE to "Berechnen", AppLanguage.FR to "Calculer"
        ),
        "copy_all" to mapOf(
            AppLanguage.EN to "Copy All", AppLanguage.TH to "คัดลอกทั้งหมด", AppLanguage.ES to "Copiar todo",
            AppLanguage.ZH to "复制全部", AppLanguage.JA to "すべてコピー", AppLanguage.DE to "Alles kopieren", AppLanguage.FR to "Tout copier"
        ),
        "share" to mapOf(
            AppLanguage.EN to "Share", AppLanguage.TH to "แชร์", AppLanguage.ES to "Compartir",
            AppLanguage.ZH to "分享", AppLanguage.JA to "共有", AppLanguage.DE to "Teilen", AppLanguage.FR to "Partager"
        ),
        "error_invalid_ipv4" to mapOf(
            AppLanguage.EN to "Invalid IPv4 format.", AppLanguage.TH to "รูปแบบ IPv4 ไม่ถูกต้อง", AppLanguage.ES to "Formato IPv4 no válido.",
            AppLanguage.ZH to "无效的 IPv4 格式。", AppLanguage.JA to "無効なIPv4形式です。", AppLanguage.DE to "Ungültiges IPv4-Format.", AppLanguage.FR to "Format IPv4 invalide."
        ),
        "error_invalid_ipv6" to mapOf(
            AppLanguage.EN to "Invalid IPv6 format.", AppLanguage.TH to "รูปแบบ IPv6 ไม่ถูกต้อง", AppLanguage.ES to "Formato IPv6 no válido.",
            AppLanguage.ZH to "无效的 IPv6 格式。", AppLanguage.JA to "無効なIPv6形式です。", AppLanguage.DE to "Ungültiges IPv6-Format.", AppLanguage.FR to "Format IPv6 invalide."
        ),
        "base_network_config" to mapOf(
            AppLanguage.EN to "Base Network Configuration", AppLanguage.TH to "การกำหนดค่าเน็ตเวิร์กเริ่มต้น", AppLanguage.ES to "Configuración de red base",
            AppLanguage.ZH to "基本网络配置", AppLanguage.JA to "基本ネットワーク設定", AppLanguage.DE to "Basis-Netzwerkkonfiguration", AppLanguage.FR to "Configuration réseau de base"
        ),
        "add_subnet" to mapOf(
            AppLanguage.EN to "Add Subnet", AppLanguage.TH to "เพิ่มซับเน็ต", AppLanguage.ES to "Agregar subred",
            AppLanguage.ZH to "添加子网", AppLanguage.JA to "サブネット追加", AppLanguage.DE to "Subnetz hinzufügen", AppLanguage.FR to "Ajouter un sous-réseau"
        ),
        "subnet_name" to mapOf(
            AppLanguage.EN to "Subnet Name", AppLanguage.TH to "ชื่อซับเน็ต", AppLanguage.ES to "Nombre de subred",
            AppLanguage.ZH to "子网名称", AppLanguage.JA to "サブネット名", AppLanguage.DE to "Subnetzname", AppLanguage.FR to "Nom du sous-réseau"
        ),
        "hosts_needed" to mapOf(
            AppLanguage.EN to "Hosts Needed", AppLanguage.TH to "จำนวนโฮสต์ที่ต้องการ", AppLanguage.ES to "Hosts necesarios",
            AppLanguage.ZH to "所需主机数", AppLanguage.JA to "必要なホスト数", AppLanguage.DE to "Benötigte Hosts", AppLanguage.FR to "Hôtes requis"
        ),
        "wasted_space" to mapOf(
            AppLanguage.EN to "Wasted Space", AppLanguage.TH to "IP ที่เสียไปโดยเปล่าประโยชน์", AppLanguage.ES to "Espacio desperdiciado",
            AppLanguage.ZH to "浪费的空间", AppLanguage.JA to "浪費されたスペース", AppLanguage.DE to "Verschwendeter Platz", AppLanguage.FR to "Espace gaspillé"
        ),
        "utilization" to mapOf(
            AppLanguage.EN to "Utilization", AppLanguage.TH to "ประสิทธิภาพการใช้งาน", AppLanguage.ES to "Utilización",
            AppLanguage.ZH to "利用率", AppLanguage.JA to "利用率", AppLanguage.DE to "Auslastung", AppLanguage.FR to "Utilisation"
        ),
        "check_containment" to mapOf(
            AppLanguage.EN to "Check Containment", AppLanguage.TH to "ตรวจสอบการครอบคลุม IP", AppLanguage.ES to "Verificar contención",
            AppLanguage.ZH to "检查包含关系", AppLanguage.JA to "包含関係の確認", AppLanguage.DE to "Enthält-Prüfung", AppLanguage.FR to "Vérifier l'appartenance"
        ),
        "compare_subnets" to mapOf(
            AppLanguage.EN to "Compare Subnets", AppLanguage.TH to "เปรียบเทียบซับเน็ต", AppLanguage.ES to "Comparar subredes",
            AppLanguage.ZH to "比较子网", AppLanguage.JA to "サブネット比較", AppLanguage.DE to "Subnetze vergleichen", AppLanguage.FR to "Comparer les sous-réseaux"
        ),
        "mac_address" to mapOf(
            AppLanguage.EN to "MAC Address", AppLanguage.TH to "ที่อยู่ MAC", AppLanguage.ES to "Dirección MAC",
            AppLanguage.ZH to "MAC 地址", AppLanguage.JA to "MACアドレス", AppLanguage.DE to "MAC-Adresse", AppLanguage.FR to "Adresse MAC"
        ),
        "quiz_score" to mapOf(
            AppLanguage.EN to "Score", AppLanguage.TH to "คะแนน", AppLanguage.ES to "Puntuación",
            AppLanguage.ZH to "分数", AppLanguage.JA to "スコア", AppLanguage.DE to "Punktestand", AppLanguage.FR to "Score"
        ),
        "quiz_streak" to mapOf(
            AppLanguage.EN to "Streak", AppLanguage.TH to "ทำถูกต่อเนื่อง", AppLanguage.ES to "Racha",
            AppLanguage.ZH to "连胜", AppLanguage.JA to "連続正解", AppLanguage.DE to "Serie", AppLanguage.FR to "Série"
        ),
        "next_question" to mapOf(
            AppLanguage.EN to "Next Question", AppLanguage.TH to "ข้อถัดไป", AppLanguage.ES to "Siguiente pregunta",
            AppLanguage.ZH to "下一题", AppLanguage.JA to "次の問題", AppLanguage.DE to "Nächste Frage", AppLanguage.FR to "Question suivante"
        )
    )

    fun t(key: String): String {
        val entry = dictionary[key] ?: return key
        return entry[currentLanguage] ?: entry[AppLanguage.EN] ?: key
    }
}

object LanguageHelper {
    fun getSystemLanguage(): AppLanguage {
        val sysLang = Locale.getDefault().language.lowercase(Locale.ROOT)
        return AppLanguage.values().firstOrNull { it.code == sysLang } ?: AppLanguage.EN
    }
}
