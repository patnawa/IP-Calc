# IP Calculator Suite

IP Calculator Suite is a feature-rich, modern Android application designed for network engineers, administrators, and students. Built with Jetpack Compose and Material 3, it offers an elegant dark-theme design with a complete set of subnetting, conversion, and summarization tools.

## Features

1. **IPv4 & IPv6 Subnet Calculator**
   - Calculate network addresses, broadcast addresses, usable host ranges, and total host capacities.
   - Dynamic CIDR prefix adjustments using sliders and text fields.
   - Subnet mask input fields that automatically synchronize with CIDR prefixes.
   - Dotted-binary viewer highlighting network and host bits (Network in Cyan, Host in Pink).

2. **VLSM & FLSM Subnetter**
   - **VLSM**: Allocate variable host requirements in descending order.
   - **FLSM**: Divide a base network into a fixed number of equal subnets.
   - **Visual IP Map (New!)**: Interactive horizontal bar representing allocated subnets and remaining free address space.

3. **Subnetting Quiz (New!)**
   - Interactive training mode for CCNA and Network+ exams.
   - Multi-choice questions covering networks, broadcasts, host counts, and wildcards.
   - Scoreboards, streak trackers, and detailed explanations of calculations.

4. **IPv6 EUI-64 Generator (New!)**
   - Derive an IPv6 address from a MAC Address using the EUI-64 standard.
   - Step-by-step educational breakdown of the bitwise calculation.

5. **MAC Vendor Lookup (New!)**
   - Find the manufacturer (vendor) of a network card from its OUI registry.
   - Convert MAC addresses between colon, hyphen, dot, and raw hex formats.

6. **Cheat Sheets (New!)**
   - **CIDR Prefix Chart**: Cheat sheet listing all prefix ranges (/0 to /32) with hosts and wildcard details.
   - **TCP/UDP Common Ports**: Searchable and filterable reference of well-known network ports.
   - **IP Subnet Containment Checker**: Verify if a target IP falls inside a subnet range.
   - **Subnet Comparison**: Side-by-side comparative table of two subnets detecting conflicts and containment.

7. **IP Converter**
   - Convert IP addresses between Dot-Decimal, Binary (32-bit), Hexadecimal, and Decimal/Long Integer formats.
   - Copy results directly to the clipboard with one tap.

---

## Tech Stack
- **UI Toolkit**: Jetpack Compose
- **Design System**: Material 3 (Futuristic Deep Space neon theme)
- **Language**: Kotlin
- **Gradle Version**: 9.1.0
- **Android SDK Target**: API 36 (Android 16)
- **State Preservation**: Fully rotation-safe via `rememberSaveable`
