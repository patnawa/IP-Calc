# IP Calculator Suite (Swiss Army Knife Network Utility)

IP Calculator Suite is a feature-rich, high-performance Android application designed for network engineers, administrators, and students. Built with Jetpack Compose and Material 3, it offers an elegant dark-theme design with a complete set of subnetting, diagnostics, generation, and learning tools.

---

## Navigation & Architecture
To accommodate 17 distinct functional modules without cluttering the interface, the app features a **Searchable Dashboard Grid** home launcher. Users can browse tools by logical categories or search for any tool dynamically (e.g., typing "ping" or "acl" instantly highlights the matching tool). 

Each tool page is isolated and features system back-press handling to return to the dashboard smoothly. The top app bar hosts a **Global Multi-Language Selector** visible on every page.

---

## Featured Tools (17 Modules)

### 1. Subnetting & Design
- **IPv4 & IPv6 Subnet Calculator**: Calculate addresses, usable host ranges, binary representations, classes, and types.
- **VLSM/FLSM Planner**: Plan subnets dynamically based on host requirements or a fixed number of segments.
- **Visual IP Map**: A visual horizontal block bar representing VLSM allocated space, utilization, and wasted space.
- **Subnet Design Template Wizard (New!)**: Choose templates like "Corporate Office", "Startup Tech Office", or "Smart Home" to auto-generate a complete subnet configuration instantly.
- **Route Summarizer (Supernetting)**: Combine multiple IP subnets into a single optimized route block.

### 2. Diagnostics & Verification
- **Ping & Subnet Sweep Scanner (New!)**: A multi-tab diagnostics tool. Run single host ICMP/TCP pings with latency logs, or scan/sweep an entire subnet range concurrently using coroutines to detect online devices.
- **DNS Resolver & WHOIS Query (New!)**: Resolve DNS records (A, AAAA, MX, NS, CNAME, TXT) using Google DNS API, or lookup domain registration dates and registrar ownership details using WHOIS RDAP endpoints.
- **IP Containment Checker**: Verify if an IP address resides within a given network prefix.
- **Subnet Comparator**: Compare two subnets side-by-side to detect overlaps, containment, and configurations.

### 3. Coding & Converters
- **IP Format Converter**: Dynamic translation between Dot-Decimal, Binary (32-bit), Hexadecimal, and Long Integer decimal representations.
- **Cisco ACL & Wildcard Generator (New!)**: Generate wildcard masks and Cisco IOS ACL command syntax snippets (Permit/Deny rules) for routers and firewalls.
- **IPv6 EUI-64 Generator**: Calculate EUI-64 addresses from MAC addresses with detailed step-by-step breakdown.
- **MAC Vendor Lookup**: Search manufacturer OUIs (Apple, Cisco, Intel, etc.) and convert MAC formats.

### 4. Reference & Learning (Educational)
- **Interactive Subnetting Quiz**: Interactive exam practice for CCNA and Network+ certifications with streaks, scoreboards, and step-by-step calculations explanation.
- **Network Cheat Sheets (New!)**: Study cards for the 7-Layer OSI Model, RJ-45 Ethernet Cable wiring standards (T568A vs T568B), IPv4/IPv6 packet header fields, and TIA-598 12-fiber color codes.
- **CIDR Prefix Cheat Sheet**: Scrollable cheat sheet for /0 to /32 prefixes.
- **TCP/UDP Common Ports Chart**: Searchable and category-filterable well-known ports directory.

---

## Tech Stack
- **UI Toolkit**: Jetpack Compose
- **Design System**: Material 3 (Neon Space Cyberpunk Theme)
- **Language**: Kotlin
- **Gradle Version**: 9.1.0
- **Android SDK Target**: API 36 (Android 16)
- **State Preservation**: Rotation-safe via `rememberSaveable`
- **Localization**: Localized in 14 major languages (EN, TH, ES, ZH, JA, KO, DE, FR, RU, PT, HI, VI, AR, IT) via a dynamic translation bar.
