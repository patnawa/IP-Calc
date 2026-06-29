package com.example.ipcalculator

import java.math.BigInteger
import java.util.Locale

object IPCalculator {

    // --- HELPER DATA STRUCTURES ---

    data class IPv4Result(
        val ip: String,
        val prefix: Int,
        val subnetMask: String,
        val wildcardMask: String,
        val networkAddress: String,
        val broadcastAddress: String,
        val usableRangeStart: String,
        val usableRangeEnd: String,
        val totalHosts: Long,
        val usableHosts: Long,
        val ipClass: String,
        val ipType: String,
        val ipBinary: String,
        val maskBinary: String,
        val networkBinary: String,
        val broadcastBinary: String
    )

    data class IPv6Result(
        val ip: String,
        val prefix: Int,
        val compressed: String,
        val expanded: String,
        val networkAddress: String,
        val rangeStart: String,
        val rangeEnd: String,
        val totalHosts: String,
        val type: String
    )

    data class VlsmSubnet(
        val id: Int,
        val name: String,
        val requestedHosts: Int,
        val allocatedHosts: Long,
        val subnetAddress: String,
        val prefix: Int,
        val mask: String,
        val rangeStart: String,
        val rangeEnd: String,
        val broadcast: String
    )

    data class FlsmSubnet(
        val id: Int,
        val subnetAddress: String,
        val prefix: Int,
        val mask: String,
        val rangeStart: String,
        val rangeEnd: String,
        val broadcast: String
    )

    data class CidrEntry(
        val prefix: Int,
        val mask: String,
        val wildcard: String,
        val totalHosts: Long,
        val usableHosts: Long,
        val example: String
    )

    data class SubnetComparison(
        val subnetA: IPv4Result,
        val subnetB: IPv4Result,
        val overlaps: Boolean,
        val aContainsB: Boolean,
        val bContainsA: Boolean
    )

    data class PortEntry(
        val port: Int,
        val protocol: String,
        val service: String,
        val description: String,
        val category: String
    )

    data class ClassEntry(
        val className: String,
        val range: String,
        val defaultMask: String,
        val defaultPrefix: Int,
        val totalNetworks: String,
        val hostsPerNetwork: String
    )

    data class VlsmUtilization(
        val totalSpace: Long,
        val allocatedSpace: Long,
        val wastedSpace: Long,
        val utilizationPercent: Double
    )

    // --- IPv4 VALIDATION & PARSING ---

    fun isValidIPv4(ip: String): Boolean {
        val parts = ip.split(".")
        if (parts.size != 4) return false
        return parts.all {
            val num = it.toIntOrNull()
            num != null && num in 0..255 && (it == "0" || !it.startsWith("0"))
        }
    }

    fun ipv4ToLong(ip: String): Long {
        val parts = ip.split(".")
        var result = 0L
        for (i in 0..3) {
            result = (result shl 8) or parts[i].toLong()
        }
        return result
    }

    fun longToIPv4(value: Long): String {
        return "${(value shr 24) and 0xFF}.${(value shr 16) and 0xFF}.${(value shr 8) and 0xFF}.${value and 0xFF}"
    }

    fun longToBinaryString(value: Long): String {
        val raw = value.toString(2).padStart(32, '0')
        return "${raw.substring(0, 8)}.${raw.substring(8, 16)}.${raw.substring(16, 24)}.${raw.substring(24, 32)}"
    }

    fun cidrToMask(prefix: Int): Long {
        if (prefix == 0) return 0L
        return (0xFFFFFFFFL shl (32 - prefix)) and 0xFFFFFFFFL
    }

    fun maskToCidr(maskStr: String): Int? {
        if (!isValidIPv4(maskStr)) return null
        val maskVal = ipv4ToLong(maskStr)
        val inverted = maskVal xor 0xFFFFFFFFL
        if (inverted != 0L && (inverted and (inverted + 1)) != 0L) return null
        var count = 0
        var temp = maskVal
        while (temp != 0L) {
            count += (temp and 1L).toInt()
            temp = temp ushr 1
        }
        return count
    }

    // --- Wildcard <-> Mask ---

    fun wildcardToMask(wildcard: String): String? {
        if (!isValidIPv4(wildcard)) return null
        val wildcardLong = ipv4ToLong(wildcard)
        val maskLong = wildcardLong xor 0xFFFFFFFFL
        val cidr = maskToCidr(longToIPv4(maskLong))
        return if (cidr != null) longToIPv4(maskLong) else null
    }

    fun maskToWildcard(mask: String): String? {
        if (!isValidIPv4(mask)) return null
        if (maskToCidr(mask) == null) return null
        return longToIPv4(ipv4ToLong(mask) xor 0xFFFFFFFFL)
    }

    // --- IPv4 CALCULATION ---

    fun calculateIPv4(ipStr: String, prefix: Int): IPv4Result? {
        if (!isValidIPv4(ipStr) || prefix !in 0..32) return null
        val ipLong = ipv4ToLong(ipStr)
        val maskLong = cidrToMask(prefix)
        val wildcardLong = maskLong xor 0xFFFFFFFFL
        val networkLong = ipLong and maskLong
        val broadcastLong = ipLong or wildcardLong

        val totalHosts = 1L shl (32 - prefix)
        val (usableHosts, startLong, endLong) = when (prefix) {
            32 -> Triple(1L, networkLong, networkLong)
            31 -> Triple(2L, networkLong, networkLong + 1)
            else -> Triple(totalHosts - 2, networkLong + 1, broadcastLong - 1)
        }

        val firstOctet = (ipLong shr 24) and 0xFF
        val ipClass = when (firstOctet) {
            in 0..127 -> "Class A"
            in 128..191 -> "Class B"
            in 192..223 -> "Class C"
            in 224..239 -> "Class D (Multicast)"
            else -> "Class E (Experimental)"
        }

        val ipType = when {
            firstOctet == 10L -> "Private (RFC 1918)"
            firstOctet == 172L && ((ipLong shr 16) and 0xFF) in 16..31 -> "Private (RFC 1918)"
            firstOctet == 192L && ((ipLong shr 16) and 0xFF) == 168L -> "Private (RFC 1918)"
            firstOctet == 127L -> "Loopback"
            firstOctet == 169L && ((ipLong shr 16) and 0xFF) == 254L -> "Link-local"
            firstOctet in 224..239 -> "Multicast"
            firstOctet == 0L -> "Current Network"
            firstOctet in 100..100 && ((ipLong shr 16) and 0xFF) in 64..127 -> "CGNAT (RFC 6598)"
            firstOctet == 198L && ((ipLong shr 16) and 0xFF) in 18..19 -> "Benchmark (RFC 2544)"
            else -> "Public"
        }

        return IPv4Result(
            ip = ipStr, prefix = prefix,
            subnetMask = longToIPv4(maskLong), wildcardMask = longToIPv4(wildcardLong),
            networkAddress = longToIPv4(networkLong), broadcastAddress = longToIPv4(broadcastLong),
            usableRangeStart = longToIPv4(startLong), usableRangeEnd = longToIPv4(endLong),
            totalHosts = totalHosts, usableHosts = usableHosts,
            ipClass = ipClass, ipType = ipType,
            ipBinary = longToBinaryString(ipLong), maskBinary = longToBinaryString(maskLong),
            networkBinary = longToBinaryString(networkLong), broadcastBinary = longToBinaryString(broadcastLong)
        )
    }

    // --- Is IP in Subnet? ---

    fun isIpInSubnet(ip: String, network: String, prefix: Int): Boolean {
        if (!isValidIPv4(ip) || !isValidIPv4(network) || prefix !in 0..32) return false
        val ipLong = ipv4ToLong(ip)
        val networkLong = ipv4ToLong(network)
        val maskLong = cidrToMask(prefix)
        return (ipLong and maskLong) == (networkLong and maskLong)
    }

    // --- Compare two subnets ---

    fun compareSubnets(ipA: String, prefixA: Int, ipB: String, prefixB: Int): SubnetComparison? {
        val resultA = calculateIPv4(ipA, prefixA) ?: return null
        val resultB = calculateIPv4(ipB, prefixB) ?: return null
        val netAStart = ipv4ToLong(resultA.networkAddress)
        val netAEnd = ipv4ToLong(resultA.broadcastAddress)
        val netBStart = ipv4ToLong(resultB.networkAddress)
        val netBEnd = ipv4ToLong(resultB.broadcastAddress)
        val overlaps = netAStart <= netBEnd && netBStart <= netAEnd
        val aContainsB = netAStart <= netBStart && netAEnd >= netBEnd
        val bContainsA = netBStart <= netAStart && netBEnd >= netAEnd
        return SubnetComparison(resultA, resultB, overlaps, aContainsB, bContainsA)
    }

    // --- CIDR Reference Table ---

    fun getCidrReferenceTable(): List<CidrEntry> {
        return (0..32).map { prefix ->
            val maskLong = cidrToMask(prefix)
            val wildcardLong = maskLong xor 0xFFFFFFFFL
            val totalHosts = 1L shl (32 - prefix)
            val usableHosts = when (prefix) { 32 -> 1L; 31 -> 2L; else -> totalHosts - 2 }
            val example = when (prefix) {
                0 -> "Default route"; in 1..8 -> "Class A"; in 9..16 -> "Class B"
                in 17..24 -> "Class C"; in 25..28 -> "Small subnet"; 29 -> "6 usable hosts"
                30 -> "Point-to-point"; 31 -> "P2P (RFC 3021)"; 32 -> "Host route"; else -> ""
            }
            CidrEntry(prefix, longToIPv4(maskLong), longToIPv4(wildcardLong), totalHosts, usableHosts, example)
        }
    }

    // --- Common Ports Reference ---

    fun getCommonPorts(): List<PortEntry> {
        return listOf(
            PortEntry(20, "TCP", "FTP-Data", "FTP Data Transfer", "File Transfer"),
            PortEntry(21, "TCP", "FTP", "FTP Control", "File Transfer"),
            PortEntry(22, "TCP", "SSH", "Secure Shell", "Remote Access"),
            PortEntry(23, "TCP", "Telnet", "Telnet (Unencrypted)", "Remote Access"),
            PortEntry(25, "TCP", "SMTP", "Simple Mail Transfer", "Email"),
            PortEntry(53, "TCP/UDP", "DNS", "Domain Name System", "DNS/DHCP"),
            PortEntry(67, "UDP", "DHCP-S", "DHCP Server", "DNS/DHCP"),
            PortEntry(68, "UDP", "DHCP-C", "DHCP Client", "DNS/DHCP"),
            PortEntry(69, "UDP", "TFTP", "Trivial File Transfer", "File Transfer"),
            PortEntry(80, "TCP", "HTTP", "Hypertext Transfer Protocol", "Web"),
            PortEntry(110, "TCP", "POP3", "Post Office Protocol v3", "Email"),
            PortEntry(123, "UDP", "NTP", "Network Time Protocol", "Other"),
            PortEntry(143, "TCP", "IMAP", "Internet Message Access", "Email"),
            PortEntry(161, "UDP", "SNMP", "Simple Network Management", "Monitoring"),
            PortEntry(179, "TCP", "BGP", "Border Gateway Protocol", "Other"),
            PortEntry(389, "TCP", "LDAP", "Lightweight Directory Access", "Other"),
            PortEntry(443, "TCP", "HTTPS", "HTTP over TLS/SSL", "Web"),
            PortEntry(445, "TCP", "SMB", "Server Message Block", "File Transfer"),
            PortEntry(465, "TCP", "SMTPS", "SMTP over SSL", "Email"),
            PortEntry(500, "UDP", "IKE", "Internet Key Exchange", "Security"),
            PortEntry(514, "UDP", "Syslog", "System Logging", "Monitoring"),
            PortEntry(587, "TCP", "Submission", "Email Submission", "Email"),
            PortEntry(636, "TCP", "LDAPS", "LDAP over SSL", "Other"),
            PortEntry(853, "TCP", "DoT", "DNS over TLS", "DNS/DHCP"),
            PortEntry(993, "TCP", "IMAPS", "IMAP over SSL", "Email"),
            PortEntry(995, "TCP", "POP3S", "POP3 over SSL", "Email"),
            PortEntry(1080, "TCP", "SOCKS", "SOCKS Proxy", "Other"),
            PortEntry(1194, "UDP", "OpenVPN", "OpenVPN", "Security"),
            PortEntry(1433, "TCP", "MSSQL", "Microsoft SQL Server", "Database"),
            PortEntry(1521, "TCP", "Oracle", "Oracle Database", "Database"),
            PortEntry(1723, "TCP", "PPTP", "Point-to-Point Tunneling", "Security"),
            PortEntry(2049, "TCP/UDP", "NFS", "Network File System", "Other"),
            PortEntry(3306, "TCP", "MySQL", "MySQL Database", "Database"),
            PortEntry(3389, "TCP", "RDP", "Remote Desktop Protocol", "Remote Access"),
            PortEntry(4500, "UDP", "IPsec-NAT", "IPsec NAT Traversal", "Security"),
            PortEntry(5060, "TCP/UDP", "SIP", "Session Initiation Protocol", "Other"),
            PortEntry(5432, "TCP", "PostgreSQL", "PostgreSQL Database", "Database"),
            PortEntry(5900, "TCP", "VNC", "Virtual Network Computing", "Remote Access"),
            PortEntry(6379, "TCP", "Redis", "Redis In-Memory Store", "Database"),
            PortEntry(8080, "TCP", "HTTP-Alt", "HTTP Alternate (Proxy)", "Web"),
            PortEntry(8443, "TCP", "HTTPS-Alt", "HTTPS Alternate", "Web"),
            PortEntry(8883, "TCP", "MQTT-S", "MQTT over SSL", "Other"),
            PortEntry(9090, "TCP", "Prometheus", "Prometheus Monitoring", "Monitoring"),
            PortEntry(27017, "TCP", "MongoDB", "MongoDB NoSQL Database", "Database"),
            PortEntry(51820, "UDP", "WireGuard", "WireGuard VPN", "Security")
        )
    }

    // --- Network Class Reference ---

    fun getNetworkClassTable(): List<ClassEntry> {
        return listOf(
            ClassEntry("Class A", "1.0.0.0 – 126.255.255.255", "255.0.0.0", 8, "126", "16,777,214"),
            ClassEntry("Class B", "128.0.0.0 – 191.255.255.255", "255.255.0.0", 16, "16,384", "65,534"),
            ClassEntry("Class C", "192.0.0.0 – 223.255.255.255", "255.255.255.0", 24, "2,097,152", "254"),
            ClassEntry("Class D", "224.0.0.0 – 239.255.255.255", "N/A (Multicast)", 0, "N/A", "N/A"),
            ClassEntry("Class E", "240.0.0.0 – 255.255.255.255", "N/A (Reserved)", 0, "N/A", "N/A")
        )
    }

    // --- VLSM Utilization ---

    fun calculateSubnetUtilization(basePrefix: Int, subnets: List<VlsmSubnet>): VlsmUtilization {
        val totalSpace = 1L shl (32 - basePrefix)
        var allocatedSpace = 0L
        for (subnet in subnets) {
            allocatedSpace += 1L shl (32 - subnet.prefix)
        }
        val wastedSpace = totalSpace - allocatedSpace
        val pct = if (totalSpace > 0) (allocatedSpace.toDouble() / totalSpace.toDouble()) * 100.0 else 0.0
        return VlsmUtilization(totalSpace, allocatedSpace, wastedSpace, pct)
    }

    // --- IPv6 VALIDATION & PARSING ---

    fun isValidIPv6(ip: String): Boolean {
        val cleaned = ip.trim()
        if (cleaned.isEmpty()) return false
        // Only one :: allowed
        val doubleColonOccurrences = Regex("::").findAll(cleaned).count()
        if (doubleColonOccurrences > 1) return false

        val parts = cleaned.split(":")
        if (parts.size > 8) return false

        val hasDoubleColon = cleaned.contains("::")
        val regex = Regex("^[0-9a-fA-F]{1,4}$")
        var nonEmptyParts = 0
        for (part in parts) {
            if (part.isEmpty()) continue
            if (!regex.matches(part)) return false
            nonEmptyParts++
        }
        if (!hasDoubleColon && nonEmptyParts != 8) return false
        if (hasDoubleColon && nonEmptyParts > 7) return false
        return true
    }

    fun ipv6ToBigInteger(ipStr: String): BigInteger {
        val cleaned = ipStr.trim()
        val parts = cleaned.split(":")
        val expanded = ArrayList<String>()
        var doubleColonIndex = -1
        for (i in parts.indices) {
            if (parts[i].isEmpty()) {
                if (doubleColonIndex == -1) doubleColonIndex = i
            } else {
                expanded.add(parts[i].padStart(4, '0'))
            }
        }
        if (doubleColonIndex != -1) {
            val missingParts = 8 - expanded.size
            val fill = List(missingParts) { "0000" }
            val insertIdx = if (doubleColonIndex > expanded.size) expanded.size else doubleColonIndex
            expanded.addAll(insertIdx, fill)
        }
        return BigInteger(expanded.joinToString(""), 16)
    }

    fun bigIntegerToIPv6(value: BigInteger): String {
        val mask16 = BigInteger("FFFF", 16)
        var temp = value
        val parts = Array(8) { "" }
        for (i in 7 downTo 0) {
            parts[i] = temp.and(mask16).toInt().toString(16)
            temp = temp.shiftRight(16)
        }
        return compressIPv6(parts.joinToString(":"))
    }

    private fun compressIPv6(ipStr: String): String {
        val parts = ipStr.split(":")
        var bestStart = -1; var bestLen = -1; var currentStart = -1; var currentLen = 0
        for (i in parts.indices) {
            if (parts[i].toIntOrNull(16) == 0) {
                if (currentStart == -1) { currentStart = i; currentLen = 1 } else currentLen++
            } else {
                if (currentLen > bestLen) { bestLen = currentLen; bestStart = currentStart }
                currentStart = -1; currentLen = 0
            }
        }
        if (currentLen > bestLen) { bestLen = currentLen; bestStart = currentStart }
        if (bestLen < 2) return parts.joinToString(":") { it.toInt(16).toString(16) }

        val before = parts.take(bestStart).joinToString(":") { it.toInt(16).toString(16) }
        val after = parts.drop(bestStart + bestLen).joinToString(":") { it.toInt(16).toString(16) }
        return when {
            before.isEmpty() && after.isEmpty() -> "::"
            before.isEmpty() -> "::$after"
            after.isEmpty() -> "$before::"
            else -> "$before::$after"
        }
    }

    fun calculateIPv6(ipStr: String, prefix: Int): IPv6Result? {
        if (!isValidIPv6(ipStr) || prefix !in 0..128) return null
        val ipVal = ipv6ToBigInteger(ipStr)
        val fullMask = BigInteger.ONE.shiftLeft(128).subtract(BigInteger.ONE)
        val netmask = if (prefix == 0) BigInteger.ZERO else fullMask.shiftLeft(128 - prefix).and(fullMask)
        val wildcard = netmask.xor(fullMask)
        val networkVal = ipVal.and(netmask)
        val broadcastVal = networkVal.or(wildcard)

        val expandedHex = ipVal.toString(16).padStart(32, '0')
        val firstWord = expandedHex.substring(0, 4).uppercase(Locale.ROOT)
        val type = when {
            ipVal == BigInteger.ZERO -> "Unspecified (::)"
            ipVal == BigInteger.ONE -> "Loopback (::1)"
            firstWord.matches(Regex("FE[89AB].")) -> "Link-Local Unicast"
            firstWord.startsWith("FC") || firstWord.startsWith("FD") -> "Unique Local"
            firstWord.startsWith("FF") -> "Multicast"
            else -> "Global Unicast"
        }

        val expandedBuilder = StringBuilder()
        for (i in 0 until 8) {
            expandedBuilder.append(expandedHex.substring(i * 4, i * 4 + 4))
            if (i < 7) expandedBuilder.append(":")
        }
        val totalHosts = if (128 - prefix >= 63) "2^${128 - prefix}" else BigInteger.ONE.shiftLeft(128 - prefix).toString()

        return IPv6Result(
            ip = ipStr, prefix = prefix,
            compressed = compressIPv6(expandedBuilder.toString()),
            expanded = expandedBuilder.toString(),
            networkAddress = bigIntegerToIPv6(networkVal),
            rangeStart = bigIntegerToIPv6(networkVal),
            rangeEnd = bigIntegerToIPv6(broadcastVal),
            totalHosts = totalHosts, type = type
        )
    }

    // --- VLSM SUBNETTER ---

    fun calculateVLSM(baseIpStr: String, basePrefix: Int, requirements: List<Pair<String, Int>>): List<VlsmSubnet>? {
        if (!isValidIPv4(baseIpStr) || basePrefix !in 0..32) return null
        val baseIp = ipv4ToLong(baseIpStr)
        val baseMask = cidrToMask(basePrefix)
        val baseNetwork = baseIp and baseMask
        val baseSize = 1L shl (32 - basePrefix)
        val baseBroadcast = baseNetwork + baseSize - 1

        val sortedReqs = requirements.sortedByDescending { it.second }
        val resultList = ArrayList<VlsmSubnet>()
        var currentIp = baseNetwork

        for ((idx, req) in sortedReqs.withIndex()) {
            val (name, neededHosts) = req
            if (neededHosts <= 0) continue

            val neededTotal = neededHosts.toLong() + 2L
            var powerOfTwo = 2L
            while (powerOfTwo < neededTotal) { powerOfTwo = powerOfTwo shl 1 }

            val prefix = 32 - java.lang.Long.numberOfTrailingZeros(powerOfTwo).toInt()
            if (currentIp + powerOfTwo - 1 > baseBroadcast) return null

            val subnetNetwork = currentIp
            val subnetBroadcast = currentIp + powerOfTwo - 1

            resultList.add(VlsmSubnet(
                id = idx + 1, name = name, requestedHosts = neededHosts,
                allocatedHosts = powerOfTwo - 2,
                subnetAddress = longToIPv4(subnetNetwork), prefix = prefix,
                mask = longToIPv4(cidrToMask(prefix)),
                rangeStart = longToIPv4(subnetNetwork + 1),
                rangeEnd = longToIPv4(subnetBroadcast - 1),
                broadcast = longToIPv4(subnetBroadcast)
            ))
            currentIp += powerOfTwo
        }
        return resultList
    }

    // --- FLSM SUBNETTER ---

    fun calculateFLSM(baseIpStr: String, basePrefix: Int, subnetsCount: Int): List<FlsmSubnet>? {
        if (!isValidIPv4(baseIpStr) || basePrefix !in 0..32 || subnetsCount <= 0) return null
        val bitsNeeded = when {
            subnetsCount <= 1 -> 0
            else -> { var power = 1; var bits = 0; while (power < subnetsCount) { power = power shl 1; bits++ }; bits }
        }
        val newPrefix = basePrefix + bitsNeeded
        if (newPrefix > 32) return null

        val baseIp = ipv4ToLong(baseIpStr)
        val baseNetwork = baseIp and cidrToMask(basePrefix)
        val newSubnetSize = 1L shl (32 - newPrefix)

        return (0 until subnetsCount).map { i ->
            val subnetNetwork = baseNetwork + (i * newSubnetSize)
            val subnetBroadcast = subnetNetwork + newSubnetSize - 1
            val (rangeStart, rangeEnd) = when (newPrefix) {
                32 -> Pair(subnetNetwork, subnetNetwork)
                31 -> Pair(subnetNetwork, subnetNetwork + 1)
                else -> Pair(subnetNetwork + 1, subnetBroadcast - 1)
            }
            FlsmSubnet(i + 1, longToIPv4(subnetNetwork), newPrefix, longToIPv4(cidrToMask(newPrefix)),
                longToIPv4(rangeStart), longToIPv4(rangeEnd), longToIPv4(subnetBroadcast))
        }
    }

    // --- IP CONVERTER ---

    fun ipv4ToHex(ip: String): String? {
        if (!isValidIPv4(ip)) return null
        return ipv4ToLong(ip).toString(16).uppercase(Locale.ROOT).padStart(8, '0')
    }

    fun hexToIPv4(hex: String): String? {
        val cleaned = hex.trim().replace("0x", "").replace("0X", "")
        if (cleaned.length != 8) return null
        return try { val v = cleaned.toLong(16); if (v in 0..0xFFFFFFFFL) longToIPv4(v) else null } catch (_: Exception) { null }
    }

    fun ipv4ToDecimal(ip: String): String? {
        if (!isValidIPv4(ip)) return null
        return ipv4ToLong(ip).toString()
    }

    fun decimalToIPv4(decimalStr: String): String? {
        return try { val v = decimalStr.trim().toLong(); if (v in 0..0xFFFFFFFFL) longToIPv4(v) else null } catch (_: Exception) { null }
    }

    fun binaryToIPv4(binStr: String): String? {
        val cleaned = binStr.replace(".", "").trim()
        if (cleaned.length != 32 || !cleaned.all { it == '0' || it == '1' }) return null
        return try { longToIPv4(cleaned.toLong(2)) } catch (_: Exception) { null }
    }

    // --- SUPERNETTING / ROUTE SUMMARIZATION ---

    fun summarizeRoutes(subnets: List<String>): Pair<String, Int>? {
        if (subnets.isEmpty()) return null
        val networkAddresses = ArrayList<Long>()
        var minPrefix = 32

        for (sub in subnets) {
            val parts = sub.split("/")
            if (parts.isEmpty()) return null
            val ipStr = parts[0]
            if (!isValidIPv4(ipStr)) return null
            val prefix = if (parts.size > 1) parts[1].toIntOrNull() ?: 32 else 32
            // Normalize to network address
            val ipLong = ipv4ToLong(ipStr)
            networkAddresses.add(ipLong and cidrToMask(prefix))
            if (prefix < minPrefix) minPrefix = prefix
        }
        if (networkAddresses.isEmpty()) return null

        var commonPrefixLength = 0
        for (bit in 31 downTo (32 - minPrefix)) {
            val bitMask = 1L shl bit
            val firstBit = networkAddresses[0] and bitMask
            if (networkAddresses.all { (it and bitMask) == firstBit }) commonPrefixLength++ else break
        }
        val mask = cidrToMask(commonPrefixLength)
        return Pair(longToIPv4(networkAddresses[0] and mask), commonPrefixLength)
    }
}
