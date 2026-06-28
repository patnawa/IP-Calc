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
        val ipType: String, // Private, Public, Loopback, Link-local, Multicast
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
        val totalHosts: String, // String since it can be up to 2^128
        val type: String
    )

    data class VlsmSubnet(
        val id: Int,
        val name: String,
        val requestedHosts: Int,
        val allocatedHosts: Int,
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
        var count = 0
        var temp = maskVal
        // Must be continuous 1s followed by continuous 0s
        // Let's count trailing zeros
        while (temp and 1L == 0L && count < 32) {
            temp = temp shr 1
            count++
        }
        // The remaining bits must be all 1s
        val ones = 32 - count
        val expected = ((1L shl ones) - 1L)
        if (temp == expected) {
            return ones
        }
        return null
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

        // IP Class
        val firstOctet = (ipLong shr 24) and 0xFF
        val ipClass = when (firstOctet) {
            in 0..127 -> "Class A"
            in 128..191 -> "Class B"
            in 192..223 -> "Class C"
            in 224..239 -> "Class D (Multicast)"
            else -> "Class E (Experimental)"
        }

        // IP Type
        val ipType = when {
            firstOctet == 10L -> "Private (RFC 1918)"
            firstOctet == 172L && ((ipLong shr 16) and 0xFF) in 16..31 -> "Private (RFC 1918)"
            firstOctet == 192L && ((ipLong shr 16) and 0xFF) == 168L -> "Private (RFC 1918)"
            firstOctet == 127L -> "Loopback"
            firstOctet == 169L && ((ipLong shr 16) and 0xFF) == 254L -> "Link-local"
            firstOctet in 224..239 -> "Multicast"
            else -> "Public"
        }

        return IPv4Result(
            ip = ipStr,
            prefix = prefix,
            subnetMask = longToIPv4(maskLong),
            wildcardMask = longToIPv4(wildcardLong),
            networkAddress = longToIPv4(networkLong),
            broadcastAddress = longToIPv4(broadcastLong),
            usableRangeStart = longToIPv4(startLong),
            usableRangeEnd = longToIPv4(endLong),
            totalHosts = totalHosts,
            usableHosts = usableHosts,
            ipClass = ipClass,
            ipType = ipType,
            ipBinary = longToBinaryString(ipLong),
            maskBinary = longToBinaryString(maskLong),
            networkBinary = longToBinaryString(networkLong),
            broadcastBinary = longToBinaryString(broadcastLong)
        )
    }

    // --- IPv6 VALIDATION & PARSING ---

    fun isValidIPv6(ip: String): Boolean {
        // Simple regex or token-based validation
        val cleaned = ip.trim()
        if (cleaned.isEmpty()) return false
        val parts = cleaned.split(":")
        if (parts.size > 8) return false

        var doubleColonCount = 0
        val regex = Regex("^[0-9a-fA-F]{1,4}$")

        for ((idx, part) in parts.withIndex()) {
            if (part.isEmpty()) {
                if (idx == 0 || idx == parts.lastIndex) {
                    // Allowed if it is part of a leading or trailing double colon
                    if (idx == 0 && parts.size > 1 && parts[1].isEmpty()) {
                        // case ::1
                        continue
                    }
                    if (idx == parts.lastIndex && parts.size > 1 && parts[parts.lastIndex - 1].isEmpty()) {
                        // case 1::
                        continue
                    }
                }
                doubleColonCount++
                continue
            }
            if (!regex.matches(part)) {
                return false
            }
        }

        if (doubleColonCount > 2) return false // e.g. :: inside string
        if (doubleColonCount == 0 && parts.size != 8) return false
        return true
    }

    fun ipv6ToBigInteger(ipStr: String): BigInteger {
        val cleaned = ipStr.trim()
        val parts = cleaned.split(":")
        val expanded = ArrayList<String>()

        var doubleColonIndex = -1
        for (i in parts.indices) {
            if (parts[i].isEmpty()) {
                if (doubleColonIndex == -1) {
                    doubleColonIndex = i
                }
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

        val hexStr = expanded.joinToString("")
        return BigInteger(hexStr, 16)
    }

    fun bigIntegerToIPv6(value: BigInteger): String {
        val mask16 = BigInteger("FFFF", 16)
        var temp = value
        val parts = Array(8) { "" }
        for (i in 7 downTo 0) {
            val partVal = temp.and(mask16).toInt()
            parts[i] = partVal.toString(16)
            temp = temp.shiftRight(16)
        }
        return compressIPv6(parts.joinToString(":"))
    }

    private fun compressIPv6(ipStr: String): String {
        val parts = ipStr.split(":")
        var bestStart = -1
        var bestLen = -1
        var currentStart = -1
        var currentLen = 0

        for (i in parts.indices) {
            if (parts[i].toIntOrNull(16) == 0) {
                if (currentStart == -1) {
                    currentStart = i
                    currentLen = 1
                } else {
                    currentLen++
                }
            } else {
                if (currentLen > bestLen) {
                    bestLen = currentLen
                    bestStart = currentStart
                }
                currentStart = -1
                currentLen = 0
            }
        }
        if (currentLen > bestLen) {
            bestLen = currentLen
            bestStart = currentStart
        }

        if (bestLen < 2) return parts.joinToString(":") { it.toInt(16).toString(16) }

        val result = ArrayList<String>()
        for (i in parts.indices) {
            if (i == bestStart) {
                result.add("")
            } else if (i in bestStart until bestStart + bestLen) {
                if (i == parts.lastIndex) {
                    result.add("")
                }
                continue
            } else {
                result.add(parts[i].toInt(16).toString(16))
            }
        }
        var resStr = result.joinToString(":")
        if (resStr.startsWith(":")) resStr = "$resStr"
        if (resStr.endsWith(":")) resStr = "$resStr"
        return resStr.replace(":::", "::")
    }

    fun calculateIPv6(ipStr: String, prefix: Int): IPv6Result? {
        if (!isValidIPv6(ipStr) || prefix !in 0..128) return null
        val ipVal = ipv6ToBigInteger(ipStr)

        val fullMask = BigInteger.ONE.shiftLeft(128).subtract(BigInteger.ONE)
        val netmask = if (prefix == 0) BigInteger.ZERO else fullMask.shiftLeft(128 - prefix).and(fullMask)
        val wildcard = netmask.xor(fullMask)

        val networkVal = ipVal.and(netmask)
        val broadcastVal = networkVal.or(wildcard)

        // Type
        val type = when {
            ipVal == BigInteger.ZERO -> "Unspecified"
            ipVal == BigInteger.ONE -> "Loopback (::1)"
            ipStr.lowercase(Locale.ROOT).startsWith("fe80") -> "Link-Local Unicast"
            ipStr.lowercase(Locale.ROOT).startsWith("fc") || ipStr.lowercase(Locale.ROOT).startsWith("fd") -> "Unique Local"
            ipStr.lowercase(Locale.ROOT).startsWith("ff") -> "Multicast"
            else -> "Global Unicast"
        }

        // Expanded full hex string
        val expandedHex = ipVal.toString(16).padStart(32, '0')
        val expandedBuilder = StringBuilder()
        for (i in 0 until 8) {
            expandedBuilder.append(expandedHex.substring(i * 4, i * 4 + 4))
            if (i < 7) expandedBuilder.append(":")
        }

        val totalHosts = if (128 - prefix >= 63) "2^${128 - prefix}" else BigInteger.ONE.shiftLeft(128 - prefix).toString()

        return IPv6Result(
            ip = ipStr,
            prefix = prefix,
            compressed = compressIPv6(expandedBuilder.toString()),
            expanded = expandedBuilder.toString(),
            networkAddress = bigIntegerToIPv6(networkVal),
            rangeStart = bigIntegerToIPv6(networkVal),
            rangeEnd = bigIntegerToIPv6(broadcastVal),
            totalHosts = totalHosts,
            type = type
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

        // Sort requirements descending by host count
        val sortedReqs = requirements.sortedByDescending { it.second }
        val resultList = ArrayList<VlsmSubnet>()

        var currentIp = baseNetwork

        for ((idx, req) in sortedReqs.withIndex()) {
            val (name, neededHosts) = req
            if (neededHosts <= 0) continue

            // Usable hosts formula: totalHosts - 2
            // So we need at least neededHosts + 2 total hosts.
            val neededTotal = neededHosts + 2
            var powerOfTwo = 2
            while (powerOfTwo < neededTotal) {
                powerOfTwo = powerOfTwo shl 1
            }

            val prefix = 32 - java.lang.Long.numberOfTrailingZeros(powerOfTwo.toLong())
            val subnetMaskStr = longToIPv4(cidrToMask(prefix))

            // Check if remaining IP space is enough
            if (currentIp + powerOfTwo - 1 > baseBroadcast) {
                // Out of space!
                return null
            }

            val subnetNetwork = currentIp
            val subnetBroadcast = currentIp + powerOfTwo - 1
            val rangeStart = subnetNetwork + 1
            val rangeEnd = subnetBroadcast - 1

            resultList.add(
                VlsmSubnet(
                    id = idx + 1,
                    name = name,
                    requestedHosts = neededHosts,
                    allocatedHosts = powerOfTwo - 2,
                    subnetAddress = longToIPv4(subnetNetwork),
                    prefix = prefix,
                    mask = subnetMaskStr,
                    rangeStart = longToIPv4(rangeStart),
                    rangeEnd = longToIPv4(rangeEnd),
                    broadcast = longToIPv4(subnetBroadcast)
                )
            )

            currentIp += powerOfTwo
        }

        return resultList
    }

    // --- FLSM SUBNETTER ---

    fun calculateFLSM(baseIpStr: String, basePrefix: Int, subnetsCount: Int): List<FlsmSubnet>? {
        if (!isValidIPv4(baseIpStr) || basePrefix !in 0..32 || subnetsCount <= 0) return null

        val bitsNeeded = when {
            subnetsCount <= 1 -> 0
            else -> {
                var power = 1
                var bits = 0
                while (power < subnetsCount) {
                    power = power shl 1
                    bits++
                }
                bits
            }
        }

        val newPrefix = basePrefix + bitsNeeded
        if (newPrefix > 32) return null // Cannot divide further

        val baseIp = ipv4ToLong(baseIpStr)
        val baseMask = cidrToMask(basePrefix)
        val baseNetwork = baseIp and baseMask
        val newSubnetSize = 1L shl (32 - newPrefix)

        val result = ArrayList<FlsmSubnet>()
        for (i in 0 until subnetsCount) {
            val subnetNetwork = baseNetwork + (i * newSubnetSize)
            val subnetBroadcast = subnetNetwork + newSubnetSize - 1
            val rangeStart = subnetNetwork + 1
            val rangeEnd = subnetBroadcast - 1

            result.add(
                FlsmSubnet(
                    id = i + 1,
                    subnetAddress = longToIPv4(subnetNetwork),
                    prefix = newPrefix,
                    mask = longToIPv4(cidrToMask(newPrefix)),
                    rangeStart = longToIPv4(rangeStart),
                    rangeEnd = longToIPv4(rangeEnd),
                    broadcast = longToIPv4(subnetBroadcast)
                )
            )
        }

        return result
    }

    // --- IP CONVERTER ---

    fun ipv4ToHex(ip: String): String? {
        if (!isValidIPv4(ip)) return null
        val longVal = ipv4ToLong(ip)
        return longVal.toString(16).uppercase(Locale.ROOT).padStart(8, '0')
    }

    fun hexToIPv4(hex: String): String? {
        val cleaned = hex.trim().replace("0x", "")
        if (cleaned.length != 8) return null
        return try {
            val longVal = cleaned.toLong(16)
            if (longVal in 0..0xFFFFFFFFL) longToIPv4(longVal) else null
        } catch (e: Exception) {
            null
        }
    }

    fun ipv4ToDecimal(ip: String): String? {
        if (!isValidIPv4(ip)) return null
        return ipv4ToLong(ip).toString()
    }

    fun decimalToIPv4(decimalStr: String): String? {
        return try {
            val value = decimalStr.trim().toLong()
            if (value in 0..0xFFFFFFFFL) longToIPv4(value) else null
        } catch (e: Exception) {
            null
        }
    }

    fun binaryToIPv4(binStr: String): String? {
        val cleaned = binStr.replace(".", "").trim()
        if (cleaned.length != 32 || !cleaned.all { it == '0' || it == '1' }) return null
        return try {
            val value = cleaned.toLong(2)
            longToIPv4(value)
        } catch (e: Exception) {
            null
        }
    }

    // --- SUPERNETTING / ROUTE SUMMARIZATION ---

    fun summarizeRoutes(subnets: List<String>): Pair<String, Int>? {
        if (subnets.isEmpty()) return null
        val parsed = ArrayList<Long>()
        var minPrefix = 32

        for (sub in subnets) {
            val parts = sub.split("/")
            if (parts.isEmpty()) return null
            val ipStr = parts[0]
            if (!isValidIPv4(ipStr)) return null
            val prefix = if (parts.size > 1) parts[1].toIntOrNull() ?: 32 else 32
            parsed.add(ipv4ToLong(ipStr))
            if (prefix < minPrefix) minPrefix = prefix
        }

        if (parsed.isEmpty()) return null

        // Find common prefix bits among all IP addresses
        var commonPrefixLength = 0
        for (bit in 31 downTo (32 - minPrefix)) {
            val bitMask = 1L shl bit
            val firstBit = parsed[0] and bitMask
            val allSame = parsed.all { (it and bitMask) == firstBit }
            if (allSame) {
                commonPrefixLength++
            } else {
                break
            }
        }

        val mask = cidrToMask(commonPrefixLength)
        val summarizedNetwork = parsed[0] and mask

        return Pair(longToIPv4(summarizedNetwork), commonPrefixLength)
    }
}
