package com.example.ipcalculator

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import org.junit.Test

class IPCalculatorTest {

    // --- IPv4 Validation ---

    @Test
    fun isValidIPv4_acceptsValidAddresses() {
        assertTrue(IPCalculator.isValidIPv4("192.168.1.1"))
        assertTrue(IPCalculator.isValidIPv4("10.0.0.1"))
        assertTrue(IPCalculator.isValidIPv4("255.255.255.255"))
        assertTrue(IPCalculator.isValidIPv4("0.0.0.0"))
        assertTrue(IPCalculator.isValidIPv4("172.16.254.1"))
    }

    @Test
    fun isValidIPv4_rejectsInvalidOctets() {
        assertTrue(!IPCalculator.isValidIPv4("256.0.0.1"))
        assertTrue(!IPCalculator.isValidIPv4("192.168.1"))
        assertTrue(!IPCalculator.isValidIPv4("192.168.1.1.1"))
        assertTrue(!IPCalculator.isValidIPv4("abc.def.ghi.jkl"))
    }

    @Test
    fun isValidIPv4_rejectsLeadingZeros() {
        assertTrue(!IPCalculator.isValidIPv4("192.168.001.1"))
    }

    // --- IPv4 Calculation ---

    @Test
    fun calculateIPv4_classC() {
        val result = IPCalculator.calculateIPv4("192.168.1.1", 24)
        assertNotNull(result)
        result!!
        assertEquals("255.255.255.0", result.subnetMask)
        assertEquals("0.0.0.255", result.wildcardMask)
        assertEquals("192.168.1.0", result.networkAddress)
        assertEquals("192.168.1.255", result.broadcastAddress)
        assertEquals("192.168.1.1", result.usableRangeStart)
        assertEquals("192.168.1.254", result.usableRangeEnd)
        assertEquals(254L, result.usableHosts)
        assertEquals(256L, result.totalHosts)
        assertEquals("Class C", result.ipClass)
    }

    @Test
    fun calculateIPv4_prefix32() {
        val result = IPCalculator.calculateIPv4("10.0.0.1", 32)
        assertNotNull(result)
        result!!
        assertEquals("255.255.255.255", result.subnetMask)
        assertEquals(1L, result.usableHosts)
        assertEquals("10.0.0.1", result.usableRangeStart)
        assertEquals("10.0.0.1", result.usableRangeEnd)
    }

    @Test
    fun calculateIPv4_prefix31() {
        val result = IPCalculator.calculateIPv4("192.168.1.0", 31)
        assertNotNull(result)
        result!!
        assertEquals(2L, result.usableHosts)
        assertEquals("192.168.1.0", result.usableRangeStart)
        assertEquals("192.168.1.1", result.usableRangeEnd)
    }

    @Test
    fun calculateIPv4_invalidPrefix() {
        assertNull(IPCalculator.calculateIPv4("192.168.1.1", 33))
        assertNull(IPCalculator.calculateIPv4("192.168.1.1", -1))
    }

    // --- Private IP Detection ---

    @Test
    fun calculateIPv4_detectsPrivateRanges() {
        val rfc1918_10 = IPCalculator.calculateIPv4("10.0.0.1", 8)
        assertNotNull(rfc1918_10)
        assertTrue(rfc1918_10!!.ipType.contains("Private"))

        val rfc1918_172 = IPCalculator.calculateIPv4("172.16.0.1", 16)
        assertNotNull(rfc1918_172)
        assertTrue(rfc1918_172!!.ipType.contains("Private"))

        val rfc1918_192 = IPCalculator.calculateIPv4("192.168.0.1", 24)
        assertNotNull(rfc1918_192)
        assertTrue(rfc1918_192!!.ipType.contains("Private"))

        val loopback = IPCalculator.calculateIPv4("127.0.0.1", 8)
        assertNotNull(loopback)
        assertEquals("Loopback", loopback!!.ipType)
    }

    // --- CIDR / Mask Conversion ---

    @Test
    fun cidrToMask_standardPrefixes() {
        assertEquals(0xFFFFFF00L, IPCalculator.cidrToMask(24))
        assertEquals(0xFF000000L, IPCalculator.cidrToMask(8))
        assertEquals(0xFFFFFFFFL, IPCalculator.cidrToMask(32) and 0xFFFFFFFFL)
        assertEquals(0L, IPCalculator.cidrToMask(0))
    }

    // --- IP <-> Long Conversion ---

    @Test
    fun ipv4ToLong_roundTrip() {
        val ip = "192.168.1.1"
        val long = IPCalculator.ipv4ToLong(ip)
        assertEquals(ip, IPCalculator.longToIPv4(long))
    }

    // --- IP In Subnet ---

    @Test
    fun isIpInSubnet_positive() {
        assertTrue(IPCalculator.isIpInSubnet("192.168.1.50", "192.168.1.0", 24))
        assertTrue(IPCalculator.isIpInSubnet("10.0.0.1", "10.0.0.0", 8))
    }

    @Test
    fun isIpInSubnet_negative() {
        assertTrue(!IPCalculator.isIpInSubnet("192.168.2.1", "192.168.1.0", 24))
        assertTrue(!IPCalculator.isIpInSubnet("11.0.0.1", "10.0.0.0", 8))
    }

    // --- Subnet Comparison ---

    @Test
    fun compareSubnets_overlapping() {
        val result = IPCalculator.compareSubnets("192.168.1.0", 24, "192.168.0.0", 16)
        assertNotNull(result)
        result!!
        assertTrue(result.overlaps)
        assertTrue(!result.aContainsB)
        assertTrue(result.bContainsA)
    }

    @Test
    fun compareSubnets_noOverlap() {
        val result = IPCalculator.compareSubnets("192.168.1.0", 24, "10.0.0.0", 8)
        assertNotNull(result)
        result!!
        assertTrue(!result.overlaps)
    }

    // --- CIDR Reference Table ---

    @Test
    fun getCidrReferenceTable_hasAllPrefixes() {
        val table = IPCalculator.getCidrReferenceTable()
        assertEquals(33, table.size) // /0 through /32
        assertEquals(0, table[0].prefix)
        assertEquals(32, table[32].prefix)
    }

    // --- Binary Converter ---

    @Test
    fun binaryToIPv4_validInput() {
        val result = IPCalculator.binaryToIPv4("11000000.10101000.00000001.00000001")
        assertEquals("192.168.1.1", result)
    }

    @Test
    fun binaryToIPv4_rejectsMalformed() {
        assertNull(IPCalculator.binaryToIPv4("11000000101010000000000100000001")) // no dots
        assertNull(IPCalculator.binaryToIPv4("11000000.10101000.00000001"))       // 3 octets
        assertNull(IPCalculator.binaryToIPv4("11000000.10101000.00000001.00000001.00000000")) // 5 octets
    }

    // --- VLSM ---

    @Test
    fun calculateVLSM_classCsplit() {
        val result = IPCalculator.calculateVLSM(
            "192.168.1.0", 24,
            listOf(Pair("Sales", 50), Pair("Engineering", 20), Pair("IT", 10))
        )
        assertNotNull(result)
        result!!
        assertEquals(3, result.size)
        // Largest subnet first (sorted descending)
        assertEquals("Sales", result[0].name)
        assertTrue(result[0].allocatedHosts >= 50)
        // All subnets should be within the base network
        for (subnet in result) {
            assertTrue(IPCalculator.isIpInSubnet(subnet.subnetAddress, "192.168.1.0", 24))
        }
    }

    @Test
    fun calculateVLSM_invalidBase() {
        val result = IPCalculator.calculateVLSM("invalid", 24, listOf(Pair("A", 10)))
        assertNull(result)
    }

    // --- VLSM Utilization ---

    @Test
    fun calculateSubnetUtilization() {
        val result = IPCalculator.calculateVLSM("192.168.1.0", 24, listOf(Pair("A", 50)))!!
        val utilization = IPCalculator.calculateSubnetUtilization(24, result)
        assertTrue(utilization.utilizationPercent > 0)
        assertTrue(utilization.utilizationPercent <= 100)
        assertEquals(256L, utilization.totalSpace)
    }

    // --- FLSM ---

    @Test
    fun calculateFLSM_4subnets() {
        val result = IPCalculator.calculateFLSM("192.168.1.0", 24, 4)
        assertNotNull(result)
        result!!
        assertEquals(4, result.size)
        assertEquals(26, result[0].prefix) // /24 + 2 bits = /26
    }

    // --- Supernetting ---

    @Test
    fun summarizeRoutes_consecutive() {
        val result = IPCalculator.summarizeRoutes(
            listOf("192.168.0.0/24", "192.168.1.0/24", "192.168.2.0/24", "192.168.3.0/24")
        )
        assertNotNull(result)
        result!!
        assertEquals("192.168.0.0", result.first)
        assertEquals(22, result.second)
    }

    // --- IPv6 Validation ---

    @Test
    fun isValidIPv6_acceptsValidAddresses() {
        assertTrue(IPCalculator.isValidIPv6("2001:db8::1"))
        assertTrue(IPCalculator.isValidIPv6("::1"))
        assertTrue(IPCalculator.isValidIPv6("fe80::1"))
    }

    @Test
    fun isValidIPv6_rejectsInvalid() {
        assertTrue(!IPCalculator.isValidIPv6("2001:db8:::1"))  // triple colon
        assertTrue(!IPCalculator.isValidIPv6("gggg::1"))       // invalid hex
    }

    // --- IPv6 Calculation ---

    @Test
    fun calculateIPv6_standard() {
        val result = IPCalculator.calculateIPv6("2001:db8::1", 64)
        assertNotNull(result)
        result!!
        assertEquals(64, result.prefix)
        assertTrue(result.type.isNotEmpty())
    }

    // --- EUI-64 ---

    @Test
    fun calculateEui64_valid() {
        val result = IPCalculator.calculateEui64("00:11:22:33:44:55", "2001:db8::/64")
        assertNotNull(result)
        result!!
        assertTrue(result.contains("ff:fe"))
    }

    // --- Quiz ---

    @Test
    fun generateQuizQuestion_producesValidQuestion() {
        for (i in 1..20) {
            val q = IPCalculator.generateQuizQuestion()
            assertTrue(q.prefix in 8..29)
            assertEquals(4, q.options.size)
            assertTrue(q.options.contains(q.correctAnswer))
            assertTrue(q.prompt.isNotEmpty())
        }
    }

    // --- MAC Formatting ---

    @Test
    fun formatMacAddress_validInput() {
        val result = IPCalculator.formatMacAddress("00:11:22:aa:bb:cc")
        assertEquals(4, result.size)
        assertEquals("00:11:22:AA:BB:CC", result["colon"])
    }

    @Test
    fun formatMacAddress_invalidLength() {
        val result = IPCalculator.formatMacAddress("00:11:22:aa")
        assertTrue(result.isEmpty())
    }

    // --- Wildcard ---

    @Test
    fun wildcardToMask_valid() {
        assertEquals("255.255.255.0", IPCalculator.wildcardToMask("0.0.0.255"))
    }

    // --- Cisco ACL ---

    @Test
    fun generateCiscoAcl_standard() {
        val acl = IPCalculator.generateCiscoAcl(
            "192.168.1.0", 24, "permit", "tcp", "any", ""
        )
        assertTrue(acl.contains("access-list 101"))
        assertTrue(acl.contains("permit"))
        assertTrue(acl.contains("tcp"))
        assertTrue(acl.contains("192.168.1.0"))
    }
}