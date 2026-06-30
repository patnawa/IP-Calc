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
    KO("ko", "한국어"),
    DE("de", "Deutsch"),
    FR("fr", "Français"),
    RU("ru", "Русский"),
    PT("pt", "Português"),
    HI("hi", "हिन्दी"),
    VI("vi", "Tiếng Việt"),
    AR("ar", "العربية"),
    IT("it", "Italiano")
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
            AppLanguage.KO to "IP 계산기 패키지",
            AppLanguage.DE to "IP-Rechner-Suite",
            AppLanguage.FR to "Suite de Calculateur IP",
            AppLanguage.RU to "Набор IP-калькуляторов",
            AppLanguage.PT to "Calculadora de IP",
            AppLanguage.HI to "आईपी कैलकुलेटर सूट",
            AppLanguage.VI to "Bộ công cụ IP Calculator",
            AppLanguage.AR to "حزمة حاسبة IP",
            AppLanguage.IT to "Calcolatore IP"
        ),
        "subnet" to mapOf(
            AppLanguage.EN to "Subnet", AppLanguage.TH to "ซับเน็ต", AppLanguage.ES to "Subred",
            AppLanguage.ZH to "子网", AppLanguage.JA to "サブネット", AppLanguage.KO to "서브넷",
            AppLanguage.DE to "Subnetz", AppLanguage.FR to "Sous-réseau", AppLanguage.RU to "Подсеть",
            AppLanguage.PT to "Sub-rede", AppLanguage.HI to "सबनेट", AppLanguage.VI to "Mạng con",
            AppLanguage.AR to "شبكة فرعية", AppLanguage.IT to "Sottorete"
        ),
        "vlsm" to mapOf(
            AppLanguage.EN to "VLSM/FLSM", AppLanguage.TH to "VLSM/FLSM", AppLanguage.ES to "VLSM/FLSM",
            AppLanguage.ZH to "VLSM/FLSM", AppLanguage.JA to "VLSM/FLSM", AppLanguage.KO to "VLSM/FLSM",
            AppLanguage.DE to "VLSM/FLSM", AppLanguage.FR to "VLSM/FLSM", AppLanguage.RU to "VLSM/FLSM",
            AppLanguage.PT to "VLSM/FLSM", AppLanguage.HI to "VLSM/FLSM", AppLanguage.VI to "VLSM/FLSM",
            AppLanguage.AR to "VLSM/FLSM", AppLanguage.IT to "VLSM/FLSM"
        ),
        "converter" to mapOf(
            AppLanguage.EN to "Converter", AppLanguage.TH to "แปลงเลข IP", AppLanguage.ES to "Conversor",
            AppLanguage.ZH to "转换器", AppLanguage.JA to "変換器", AppLanguage.KO to "변환기",
            AppLanguage.DE to "Konverter", AppLanguage.FR to "Convertisseur", AppLanguage.RU to "Конвертер",
            AppLanguage.PT to "Conversor", AppLanguage.HI to "कनवर्टर", AppLanguage.VI to "Bộ chuyển đổi",
            AppLanguage.AR to "المحول", AppLanguage.IT to "Convertitore"
        ),
        "supernet" to mapOf(
            AppLanguage.EN to "Supernet", AppLanguage.TH to "ซูเปอร์เน็ต", AppLanguage.ES to "Superred",
            AppLanguage.ZH to "超网", AppLanguage.JA to "スーパーネット", AppLanguage.KO to "슈퍼넷",
            AppLanguage.DE to "Supernetz", AppLanguage.FR to "Super-réseau", AppLanguage.RU to "Суперсеть",
            AppLanguage.PT to "Super-rede", AppLanguage.HI to "सुपरनेट", AppLanguage.VI to "Mạng siêu cấp",
            AppLanguage.AR to "شبكة فائقة", AppLanguage.IT to "Superrete"
        ),
        "cidr_chart" to mapOf(
            AppLanguage.EN to "CIDR Chart", AppLanguage.TH to "ตาราง CIDR", AppLanguage.ES to "Tabla CIDR",
            AppLanguage.ZH to "CIDR 对照表", AppLanguage.JA to "CIDRチャート", AppLanguage.KO to "CIDR 표",
            AppLanguage.DE to "CIDR-Tabelle", AppLanguage.FR to "Tableau CIDR", AppLanguage.RU to "Таблица CIDR",
            AppLanguage.PT to "Tabela CIDR", AppLanguage.HI to "सीआईडीआर चार्ट", AppLanguage.VI to "Bảng CIDR",
            AppLanguage.AR to "جدول CIDR", AppLanguage.IT to "Tabella CIDR"
        ),
        "ip_checker" to mapOf(
            AppLanguage.EN to "IP Checker", AppLanguage.TH to "ตรวจ IP", AppLanguage.ES to "Verificador IP",
            AppLanguage.ZH to "IP 检查器", AppLanguage.JA to "IPチェッカー", AppLanguage.KO to "IP 검사기",
            AppLanguage.DE to "IP-Prüfer", AppLanguage.FR to "Vérificateur IP", AppLanguage.RU to "Проверка IP",
            AppLanguage.PT to "Verificador IP", AppLanguage.HI to "आईपी चेकर", AppLanguage.VI to "Kiểm tra IP",
            AppLanguage.AR to "فاحص IP", AppLanguage.IT to "Verificatore IP"
        ),
        "compare" to mapOf(
            AppLanguage.EN to "Compare", AppLanguage.TH to "เปรียบเทียบ", AppLanguage.ES to "Comparar",
            AppLanguage.ZH to "比较", AppLanguage.JA to "比較", AppLanguage.KO to "비교",
            AppLanguage.DE to "Vergleichen", AppLanguage.FR to "Comparer", AppLanguage.RU to "Сравнение",
            AppLanguage.PT to "Comparar", AppLanguage.HI to "तुलना करें", AppLanguage.VI to "So sánh",
            AppLanguage.AR to "مقارنة", AppLanguage.IT to "Confronta"
        ),
        "ports" to mapOf(
            AppLanguage.EN to "Ports", AppLanguage.TH to "พอร์ตทั่วไป", AppLanguage.ES to "Puertos",
            AppLanguage.ZH to "端口", AppLanguage.JA to "ポート", AppLanguage.KO to "포트",
            AppLanguage.DE to "Ports", AppLanguage.FR to "Ports", AppLanguage.RU to "Порты",
            AppLanguage.PT to "Portas", AppLanguage.HI to "पोर्ट्स", AppLanguage.VI to "Cổng mạng",
            AppLanguage.AR to "المنافذ", AppLanguage.IT to "Porte"
        ),
        "eui64" to mapOf(
            AppLanguage.EN to "EUI-64", AppLanguage.TH to "EUI-64", AppLanguage.ES to "EUI-64",
            AppLanguage.ZH to "EUI-64", AppLanguage.JA to "EUI-64", AppLanguage.KO to "EUI-64",
            AppLanguage.DE to "EUI-64", AppLanguage.FR to "EUI-64", AppLanguage.RU to "EUI-64",
            AppLanguage.PT to "EUI-64", AppLanguage.HI to "EUI-64", AppLanguage.VI to "EUI-64",
            AppLanguage.AR to "EUI-64", AppLanguage.IT to "EUI-64"
        ),
        "mac_oui" to mapOf(
            AppLanguage.EN to "MAC OUI", AppLanguage.TH to "ค้นหา MAC", AppLanguage.ES to "OUI MAC",
            AppLanguage.ZH to "MAC OUI", AppLanguage.JA to "MAC OUI", AppLanguage.KO to "MAC OUI",
            AppLanguage.DE to "MAC-OUI", AppLanguage.FR to "OUI MAC", AppLanguage.RU to "MAC OUI",
            AppLanguage.PT to "MAC OUI", AppLanguage.HI to "मैक OUI", AppLanguage.VI to "MAC OUI",
            AppLanguage.AR to "OUI لـ MAC", AppLanguage.IT to "MAC OUI"
        ),
        "quiz" to mapOf(
            AppLanguage.EN to "Quiz", AppLanguage.TH to "ทดสอบความรู้", AppLanguage.ES to "Examen",
            AppLanguage.ZH to "测验", AppLanguage.JA to "クイズ", AppLanguage.KO to "퀴즈",
            AppLanguage.DE to "Quiz", AppLanguage.FR to "Quiz", AppLanguage.RU to "Викторина",
            AppLanguage.PT to "Quiz", AppLanguage.HI to "प्रश्नोत्तरी", AppLanguage.VI to "Trắc nghiệm",
            AppLanguage.AR to "اختبار", AppLanguage.IT to "Quiz"
        ),
        "about" to mapOf(
            AppLanguage.EN to "About", AppLanguage.TH to "เกี่ยวกับ", AppLanguage.ES to "Acerca de",
            AppLanguage.ZH to "关于", AppLanguage.JA to "情報", AppLanguage.KO to "정보",
            AppLanguage.DE to "Über", AppLanguage.FR to "À propos", AppLanguage.RU to "О программе",
            AppLanguage.PT to "Sobre", AppLanguage.HI to "के बारे में", AppLanguage.VI to "Thông tin",
            AppLanguage.AR to "حول", AppLanguage.IT to "Info"
        ),
        "ip_address" to mapOf(
            AppLanguage.EN to "IP Address", AppLanguage.TH to "ที่อยู่ IP", AppLanguage.ES to "Dirección IP",
            AppLanguage.ZH to "IP 地址", AppLanguage.JA to "IPアドレス", AppLanguage.KO to "IP 주소",
            AppLanguage.DE to "IP-Adresse", AppLanguage.FR to "Adresse IP", AppLanguage.RU to "IP-адрес",
            AppLanguage.PT to "Endereço IP", AppLanguage.HI to "आईपी पता", AppLanguage.VI to "Địa chỉ IP",
            AppLanguage.AR to "عنوان IP", AppLanguage.IT to "Indirizzo IP"
        ),
        "subnet_mask" to mapOf(
            AppLanguage.EN to "Subnet Mask", AppLanguage.TH to "ซับเน็ตมาสก์", AppLanguage.ES to "Máscara de subred",
            AppLanguage.ZH to "子网掩码", AppLanguage.JA to "サブネットマスク", AppLanguage.KO to "서브넷 마스크",
            AppLanguage.DE to "Subnetzmaske", AppLanguage.FR to "Masque de sous-réseau", AppLanguage.RU to "Маска подсети",
            AppLanguage.PT to "Máscara de Sub-rede", AppLanguage.HI to "सबनेट मास्क", AppLanguage.VI to "Mặt nạ mạng con",
            AppLanguage.AR to "قناع الشبكة الفرعية", AppLanguage.IT to "Maschera di sottorete"
        ),
        "wildcard_mask" to mapOf(
            AppLanguage.EN to "Wildcard Mask", AppLanguage.TH to "ไวลด์การ์ดมาสก์", AppLanguage.ES to "Máscara wildcard",
            AppLanguage.ZH to "反掩码", AppLanguage.JA to "ワイルドカードマスク", AppLanguage.KO to "와일드카드 마스크",
            AppLanguage.DE to "Wildcard-Maske", AppLanguage.FR to "Masque wildcard", AppLanguage.RU to "Обратная маска",
            AppLanguage.PT to "Máscara Wildcard", AppLanguage.HI to "वाइल्डकार्ड मास्क", AppLanguage.VI to "Mặt nạ Wildcard",
            AppLanguage.AR to "قناع متقلب", AppLanguage.IT to "Maschera wildcard"
        ),
        "network_address" to mapOf(
            AppLanguage.EN to "Network Address", AppLanguage.TH to "ที่อยู่เครือข่าย", AppLanguage.ES to "Dirección de red",
            AppLanguage.ZH to "网络地址", AppLanguage.JA to "ネットワークアドレス", AppLanguage.KO to "네트워크 주소",
            AppLanguage.DE to "Netzwerkadresse", AppLanguage.FR to "Adresse réseau", AppLanguage.RU to "Адрес сети",
            AppLanguage.PT to "Endereço de Rede", AppLanguage.HI to "नेटवर्क पता", AppLanguage.VI to "Địa chỉ mạng",
            AppLanguage.AR to "عنوان الشبكة", AppLanguage.IT to "Indirizzo di rete"
        ),
        "broadcast_address" to mapOf(
            AppLanguage.EN to "Broadcast Address", AppLanguage.TH to "ที่อยู่บรอดแคสต์", AppLanguage.ES to "Dirección de broadcast",
            AppLanguage.ZH to "广播地址", AppLanguage.JA to "ブロードキャストアドレス", AppLanguage.KO to "브로드캐스트 주소",
            AppLanguage.DE to "Broadcast-Adresse", AppLanguage.FR to "Adresse de diffusion", AppLanguage.RU to "Широковещательный адрес",
            AppLanguage.PT to "Endereço de Broadcast", AppLanguage.HI to "प्रसारण पता", AppLanguage.VI to "Địa chỉ quảng bá",
            AppLanguage.AR to "عنوان البث", AppLanguage.IT to "Indirizzo broadcast"
        ),
        "usable_range" to mapOf(
            AppLanguage.EN to "Usable Host Range", AppLanguage.TH to "ช่วง IP ที่ใช้งานได้", AppLanguage.ES to "Rango de IP útil",
            AppLanguage.ZH to "可用主机范围", AppLanguage.JA to "使用可能なホスト範囲", AppLanguage.KO to "사용 가능한 호스트 범위",
            AppLanguage.DE to "Nutzbarer Host-Bereich", AppLanguage.FR to "Plage d'adresses utiles", AppLanguage.RU to "Диапазон хостов",
            AppLanguage.PT to "Faixa Útil de Host", AppLanguage.HI to "उपयोगकर्ता होस्ट रेंज", AppLanguage.VI to "Dải IP sử dụng được",
            AppLanguage.AR to "نطاق المضيفين المتاح", AppLanguage.IT to "Intervallo host utilizzabile"
        ),
        "usable_hosts" to mapOf(
            AppLanguage.EN to "Usable Hosts", AppLanguage.TH to "จำนวนโฮสต์ที่ใช้งานได้", AppLanguage.ES to "Hosts útiles",
            AppLanguage.ZH to "可用主机数", AppLanguage.JA to "使用可能なホスト数", AppLanguage.KO to "사용 가능한 호스트 수",
            AppLanguage.DE to "Nutzbare Hosts", AppLanguage.FR to "Hôtes utiles", AppLanguage.RU to "Доступные хосты",
            AppLanguage.PT to "Hosts Úteis", AppLanguage.HI to "उपयोगकर्ता होस्ट", AppLanguage.VI to "Số host sử dụng được",
            AppLanguage.AR to "المضيفين المتاحين", AppLanguage.IT to "Host utilizzabili"
        ),
        "total_hosts" to mapOf(
            AppLanguage.EN to "Total Hosts", AppLanguage.TH to "โฮสต์ทั้งหมด", AppLanguage.ES to "Hosts totales",
            AppLanguage.ZH to "总主机数", AppLanguage.JA to "総ホスト数", AppLanguage.KO to "총 호스트 수",
            AppLanguage.DE to "Hosts gesamt", AppLanguage.FR to "Nombre total d'hôtes", AppLanguage.RU to "Всего хостов",
            AppLanguage.PT to "Hosts Totais", AppLanguage.HI to "कुल होस्ट", AppLanguage.VI to "Tổng số host",
            AppLanguage.AR to "إجمالي المضيفين", AppLanguage.IT to "Host totali"
        ),
        "ip_class" to mapOf(
            AppLanguage.EN to "IP Class", AppLanguage.TH to "คลาสของ IP", AppLanguage.ES to "Clase de IP",
            AppLanguage.ZH to "IP 类别", AppLanguage.JA to "IPクラス", AppLanguage.KO to "IP 클래스",
            AppLanguage.DE to "IP-Klasse", AppLanguage.FR to "Classe d'IP", AppLanguage.RU to "Класс IP",
            AppLanguage.PT to "Classe de IP", AppLanguage.HI to "आईपी वर्ग", AppLanguage.VI to "Lớp IP",
            AppLanguage.AR to "فئة IP", AppLanguage.IT to "Classe IP"
        ),
        "ip_type" to mapOf(
            AppLanguage.EN to "IP Type", AppLanguage.TH to "ประเภท IP", AppLanguage.ES to "Tipo de IP",
            AppLanguage.ZH to "IP 类型", AppLanguage.JA to "IPタイプ", AppLanguage.KO to "IP 유형",
            AppLanguage.DE to "IP-Typ", AppLanguage.FR to "Type d'IP", AppLanguage.RU to "Тип IP",
            AppLanguage.PT to "Tipo de IP", AppLanguage.HI to "आईपी प्रकार", AppLanguage.VI to "Loại IP",
            AppLanguage.AR to "نوع IP", AppLanguage.IT to "Tipo IP"
        ),
        "calculate" to mapOf(
            AppLanguage.EN to "Calculate", AppLanguage.TH to "คำนวณ", AppLanguage.ES to "Calcular",
            AppLanguage.ZH to "计算", AppLanguage.JA to "計算", AppLanguage.KO to "계산",
            AppLanguage.DE to "Berechnen", AppLanguage.FR to "Calculer", AppLanguage.RU to "Вычислить",
            AppLanguage.PT to "Calcular", AppLanguage.HI to "गणना करें", AppLanguage.VI to "Tính toán",
            AppLanguage.AR to "حساب", AppLanguage.IT to "Calcola"
        ),
        "copy_all" to mapOf(
            AppLanguage.EN to "Copy All", AppLanguage.TH to "คัดลอกทั้งหมด", AppLanguage.ES to "Copiar todo",
            AppLanguage.ZH to "复制全部", AppLanguage.JA to "すべてコピー", AppLanguage.KO to "전체 복사",
            AppLanguage.DE to "Alles kopieren", AppLanguage.FR to "Tout copier", AppLanguage.RU to "Копировать все",
            AppLanguage.PT to "Copiar Tudo", AppLanguage.HI to "सभी कॉपी करें", AppLanguage.VI to "Sao chép hết",
            AppLanguage.AR to "نسخ الكل", AppLanguage.IT to "Copia tutto"
        ),
        "share" to mapOf(
            AppLanguage.EN to "Share", AppLanguage.TH to "แชร์", AppLanguage.ES to "Compartir",
            AppLanguage.ZH to "分享", AppLanguage.JA to "共有", AppLanguage.KO to "공유",
            AppLanguage.DE to "Teilen", AppLanguage.FR to "Partager", AppLanguage.RU to "Поделиться",
            AppLanguage.PT to "Compartilhar", AppLanguage.HI to "शेयर करें", AppLanguage.VI to "Chia sẻ",
            AppLanguage.AR to "مشاركة", AppLanguage.IT to "Condividi"
        ),
        "error_invalid_ipv4" to mapOf(
            AppLanguage.EN to "Invalid IPv4 format.", AppLanguage.TH to "รูปแบบ IPv4 ไม่ถูกต้อง", AppLanguage.ES to "Formato IPv4 no válido.",
            AppLanguage.ZH to "无效的 IPv4 格式。", AppLanguage.JA to "無効なIPv4形式です。", AppLanguage.KO to "올바르지 않은 IPv4 형식.",
            AppLanguage.DE to "Ungültiges IPv4-Format.", AppLanguage.FR to "Format IPv4 invalide.", AppLanguage.RU to "Неверный формат IPv4.",
            AppLanguage.PT to "Formato IPv4 inválido.", AppLanguage.HI to "अमान्य IPv4 प्रारूप।", AppLanguage.VI to "Định dạng IPv4 sai.",
            AppLanguage.AR to "تنسيق IPv4 غير صالح.", AppLanguage.IT to "Formato IPv4 non valido."
        ),
        "error_invalid_ipv6" to mapOf(
            AppLanguage.EN to "Invalid IPv6 format.", AppLanguage.TH to "รูปแบบ IPv6 ไม่ถูกต้อง", AppLanguage.ES to "Formato IPv6 no válido.",
            AppLanguage.ZH to "无效的 IPv6 格式。", AppLanguage.JA to "無効なIPv6形式です。", AppLanguage.KO to "올바르지 않은 IPv6 형식.",
            AppLanguage.DE to "Ungültiges IPv6-Format.", AppLanguage.FR to "Format IPv6 invalide.", AppLanguage.RU to "Неверный формат IPv6.",
            AppLanguage.PT to "Formato IPv6 inválido.", AppLanguage.HI to "अमान्य IPv6 प्रारूप।", AppLanguage.VI to "Định dạng IPv6 sai.",
            AppLanguage.AR to "تنسيق IPv6 غير صالح.", AppLanguage.IT to "Formato IPv6 non valido."
        ),
        "base_network_config" to mapOf(
            AppLanguage.EN to "Base Network Configuration", AppLanguage.TH to "การกำหนดค่าเน็ตเวิร์กเริ่มต้น", AppLanguage.ES to "Configuración de red base",
            AppLanguage.ZH to "基本网络配置", AppLanguage.JA to "基本ネットワーク設定", AppLanguage.KO to "기본 네트워크 설정",
            AppLanguage.DE to "Basis-Netzwerkkonfiguration", AppLanguage.FR to "Configuration réseau de base", AppLanguage.RU to "Конфигурация сети",
            AppLanguage.PT to "Configuração de Rede Base", AppLanguage.HI to "मूल नेटवर्क कॉन्फ़िगरेशन", AppLanguage.VI to "Cấu hình mạng cơ sở",
            AppLanguage.AR to "إعدادات الشبكة الأساسية", AppLanguage.IT to "Configurazione di rete base"
        ),
        "add_subnet" to mapOf(
            AppLanguage.EN to "Add Subnet", AppLanguage.TH to "เพิ่มซับเน็ต", AppLanguage.ES to "Agregar subred",
            AppLanguage.ZH to "添加子网", AppLanguage.JA to "サブネット追加", AppLanguage.KO to "서브넷 추가",
            AppLanguage.DE to "Subnetz hinzufügen", AppLanguage.FR to "Ajouter un sous-réseau", AppLanguage.RU to "Добавить подсеть",
            AppLanguage.PT to "Adicionar Sub-rede", AppLanguage.HI to "सबनेट जोड़ें", AppLanguage.VI to "Thêm mạng con",
            AppLanguage.AR to "إضافة شبكة فرعية", AppLanguage.IT to "Aggiungi sottorete"
        ),
        "subnet_name" to mapOf(
            AppLanguage.EN to "Subnet Name", AppLanguage.TH to "ชื่อซับเน็ต", AppLanguage.ES to "Nombre de subred",
            AppLanguage.ZH to "子网名称", AppLanguage.JA to "サブネット名", AppLanguage.KO to "서브넷 이름",
            AppLanguage.DE to "Subnetzname", AppLanguage.FR to "Nom du sous-réseau", AppLanguage.RU to "Имя подсети",
            AppLanguage.PT to "Nome da Sub-rede", AppLanguage.HI to "सबनेट का नाम", AppLanguage.VI to "Tên mạng con",
            AppLanguage.AR to "اسم الشبكة الفرعية", AppLanguage.IT to "Nome sottorete"
        ),
        "hosts_needed" to mapOf(
            AppLanguage.EN to "Hosts Needed", AppLanguage.TH to "จำนวนโฮสต์ที่ต้องการ", AppLanguage.ES to "Hosts necesarios",
            AppLanguage.ZH to "所需主机数", AppLanguage.JA to "必要なホスト数", AppLanguage.KO to "필요한 호스트 수",
            AppLanguage.DE to "Benötigte Hosts", AppLanguage.FR to "Hôtes requis", AppLanguage.RU to "Необходимые хосты",
            AppLanguage.PT to "Hosts Necessários", AppLanguage.HI to "आवश्यक होस्ट", AppLanguage.VI to "Số host cần dùng",
            AppLanguage.AR to "المضيفين المطلوبين", AppLanguage.IT to "Host necessari"
        ),
        "wasted_space" to mapOf(
            AppLanguage.EN to "Wasted Space", AppLanguage.TH to "IP ที่เสียไปโดยเปล่าประโยชน์", AppLanguage.ES to "Espacio desperdiciado",
            AppLanguage.ZH to "浪费的空间", AppLanguage.JA to "浪費されたスペース", AppLanguage.KO to "낭비된 주소",
            AppLanguage.DE to "Verschwendeter Platz", AppLanguage.FR to "Espace gaspillé", AppLanguage.RU to "Потерянные адреса",
            AppLanguage.PT to "Espaço Desperdiçado", AppLanguage.HI to "व्यर्थ स्थान", AppLanguage.VI to "Địa chỉ lãng phí",
            AppLanguage.AR to "العناوين الضائعة", AppLanguage.IT to "Spazio sprecato"
        ),
        "utilization" to mapOf(
            AppLanguage.EN to "Utilization", AppLanguage.TH to "ประสิทธิภาพการใช้งาน", AppLanguage.ES to "Utilización",
            AppLanguage.ZH to "利用率", AppLanguage.JA to "利用率", AppLanguage.KO to "사용률",
            AppLanguage.DE to "Auslastung", AppLanguage.FR to "Utilisation", AppLanguage.RU to "Использование",
            AppLanguage.PT to "Utilização", AppLanguage.HI to "उपयोग दर", AppLanguage.VI to "Tỷ lệ sử dụng",
            AppLanguage.AR to "نسبة الاستخدام", AppLanguage.IT to "Utilizzo"
        ),
        "check_containment" to mapOf(
            AppLanguage.EN to "Check Containment", AppLanguage.TH to "ตรวจสอบการครอบคลุม IP", AppLanguage.ES to "Verificar contención",
            AppLanguage.ZH to "检查包含关系", AppLanguage.JA to "包含関係の確認", AppLanguage.KO to "포함 여부 확인",
            AppLanguage.DE to "Enthält-Prüfung", AppLanguage.FR to "Vérifier l'appartenance", AppLanguage.RU to "Проверить вхождение",
            AppLanguage.PT to "Verificar Contenção", AppLanguage.HI to "सीमा जांचें", AppLanguage.VI to "Kiểm tra thuộc dải",
            AppLanguage.AR to "فحص الاحتواء", AppLanguage.IT to "Verifica contenzione"
        ),
        "compare_subnets" to mapOf(
            AppLanguage.EN to "Compare Subnets", AppLanguage.TH to "เปรียบเทียบซับเน็ต", AppLanguage.ES to "Comparar subredes",
            AppLanguage.ZH to "比较子网", AppLanguage.JA to "サブネット比較", AppLanguage.KO to "서브넷 비교",
            AppLanguage.DE to "Subnetze vergleichen", AppLanguage.FR to "Comparer les sous-réseaux", AppLanguage.RU to "Сравнить подсети",
            AppLanguage.PT to "Comparar Sub-redes", AppLanguage.HI to "सबनेट तुलना", AppLanguage.VI to "So sánh mạng con",
            AppLanguage.AR to "مقارنة الشبكات الفرعية", AppLanguage.IT to "Confronta sottoreti"
        ),
        "mac_address" to mapOf(
            AppLanguage.EN to "MAC Address", AppLanguage.TH to "ที่อยู่ MAC", AppLanguage.ES to "Dirección MAC",
            AppLanguage.ZH to "MAC 地址", AppLanguage.JA to "MACアドレス", AppLanguage.KO to "MAC 주소",
            AppLanguage.DE to "MAC-Adresse", AppLanguage.FR to "Adresse MAC", AppLanguage.RU to "MAC-адрес",
            AppLanguage.PT to "Endereço MAC", AppLanguage.HI to "मैक पता", AppLanguage.VI to "Địa chỉ MAC",
            AppLanguage.AR to "عنوان MAC", AppLanguage.IT to "Indirizzo MAC"
        ),
        "quiz_score" to mapOf(
            AppLanguage.EN to "Score", AppLanguage.TH to "คะแนน", AppLanguage.ES to "Puntuación",
            AppLanguage.ZH to "分数", AppLanguage.JA to "スコア", AppLanguage.KO to "점수",
            AppLanguage.DE to "Punktestand", AppLanguage.FR to "Score", AppLanguage.RU to "Счет",
            AppLanguage.PT to "Pontuação", AppLanguage.HI to "अंक", AppLanguage.VI to "Điểm số",
            AppLanguage.AR to "النتيجة", AppLanguage.IT to "Punteggio"
        ),
        "quiz_streak" to mapOf(
            AppLanguage.EN to "Streak", AppLanguage.TH to "ทำถูกต่อเนื่อง", AppLanguage.ES to "Racha",
            AppLanguage.ZH to "连胜", AppLanguage.JA to "連続正解", AppLanguage.KO to "연속 정답",
            AppLanguage.DE to "Serie", AppLanguage.FR to "Série", AppLanguage.RU to "Серия",
            AppLanguage.PT to "Sequência", AppLanguage.HI to "लगातार सही", AppLanguage.VI to "Chuỗi đúng",
            AppLanguage.AR to "المتتالي", AppLanguage.IT to "Striscia"
        ),
        "next_question" to mapOf(
            AppLanguage.EN to "Next Question", AppLanguage.TH to "ข้อถัดไป", AppLanguage.ES to "Siguiente pregunta",
            AppLanguage.ZH to "下一题", AppLanguage.JA to "次の問題", AppLanguage.KO to "다음 문제",
            AppLanguage.DE to "Nächste Frage", AppLanguage.FR to "Question suivante", AppLanguage.RU to "Следующий вопрос",
            AppLanguage.PT to "Próxima Pergunta", AppLanguage.HI to "अगला प्रश्न", AppLanguage.VI to "Câu hỏi tiếp",
            AppLanguage.AR to "السؤال التالي", AppLanguage.IT to "Prossima domanda"
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
