# IP Calculator Suite

IP Calculator Suite is a feature-rich, modern Android application designed for network engineers, administrators, and students. Built with Jetpack Compose and Material 3, it offers an elegant dark-theme design with a complete set of subnetting, conversion, and summarization tools.

## Features

1. **IPv4 & IPv6 Subnet Calculator**
   - Calculate network addresses, broadcast addresses, usable host ranges, and total host capacities.
   - Dynamic CIDR prefix adjustments using sliders.
   - Visual vertical alignment of binary structures for IP addresses and subnet masks.

2. **VLSM & FLSM Subnetter**
   - **VLSM**: Dynamically allocate subnets based on variable host size requirements (allocated in descending order of size).
   - **FLSM**: Divide a base network into a fixed number of equal subnets.

3. **IP Converter**
   - Convert IP addresses between Dot-Decimal, Binary (32-bit), Hexadecimal, and Decimal/Long Integer formats.
   - Copy results directly to the clipboard with one tap.

4. **Supernetting (Route Summarizer)**
   - Group multiple IP subnets into a single optimized summary route.
   - Computes prefix alignment and displays subnet boundaries.

---

## Tech Stack
- **UI Toolkit**: Jetpack Compose
- **Design System**: Material 3
- **Language**: Kotlin
- **Gradle Version**: 9.1.0
- **Android SDK Target**: API 36 (Android 16)
