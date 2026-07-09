package com.example.ipcalculator

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class IPCalculatorTest {

    @Test
    fun isValidIPv4_basic() {
        assertTrue(IPCalculator.isValidIPv4("192.168.1.1"))
        assertTrue(IPCalculator.isValidIPv4("0.0.0.0"))
        assertTrue(IPCalculator.isValidIPv4("255.255.255.255"))
        assertFalse(IPCalculator.isValidIPv4("256.0.0.1"))
        assertFalse(IPCalculator.isValidIPv4("192.168.1"))
        assertFalse(IPCalculator.isValidIPv4("192.168.001.1"))
    }

    @Test
    fun ipv4ToLong_roundTrip() {
        val ip = "192.168.1.1"
        assertEquals(ip, IPCalculator.longToIPv4(IPCalculator.ipv4ToLong(ip)))
    }

    @Test
    fun cidrToMask_standard() {
        assertEquals(0xFFFFFF00L, IPCalculator.cidrToMask(24))
        assertEquals(0xFF000000L, IPCalculator.cidrToMask(8))
        assertEquals(0L, IPCalculator.cidrToMask(0))
    }

    @Test
    fun maskToCidr_valid() {
        assertEquals(24, IPCalculator.maskToCidr("255.255.255.0"))
        assertEquals(8, IPCalculator.maskToCidr("255.0.0.0"))
        assertNull(IPCalculator.maskToCidr("255.255.255.1"))
    }

    @Test
    fun calculateIPv4_classC() {
        val r = IPCalculator.calculateIPv4("192.168.1.1", 24)
        assertNotNull(r)
        assertEquals("255.255.255.0", r!!.subnetMask)
        assertEquals("192.168.1.0", r.networkAddress)
        assertEquals("192.168.1.255", r.broadcastAddress)
        assertEquals("192.168.1.1", r.usableRangeStart)
        assertEquals("192.168.1.254", r.usableRangeEnd)
        assertEquals(254L, r.usableHosts)
    }

    @Test
    fun calculateIPv4_prefix32() {
        val r = IPCalculator.calculateIPv4("10.0.0.1", 32)
        assertNotNull(r)
        assertEquals(1L, r!!.usableHosts)
    }

    @Test
    fun calculateIPv4_prefix31() {
        val r = IPCalculator.calculateIPv4("192.168.1.0", 31)
        assertNotNull(r)
        assertEquals(2L, r!!.usableHosts)
    }

    @Test
    fun calculateIPv4_invalid() {
        assertNull(IPCalculator.calculateIPv4("invalid", 24))
        assertNull(IPCalculator.calculateIPv4("192.168.1.1", 33))
    }

    @Test
    fun isIpInSubnet() {
        assertTrue(IPCalculator.isIpInSubnet("192.168.1.50", "192.168.1.0", 24))
        assertFalse(IPCalculator.isIpInSubnet("192.168.2.1", "192.168.1.0", 24))
    }

    @Test
    fun binaryToIPv4_valid() {
        assertEquals("192.168.1.1", IPCalculator.binaryToIPv4("11000000.10101000.00000001.00000001"))
    }

    @Test
    fun binaryToIPv4_rejectsMalformed() {
        assertNull(IPCalculator.binaryToIPv4("11000000101010000000000100000001"))
        assertNull(IPCalculator.binaryToIPv4("11000000.10101000.00000001"))
    }

    @Test
    fun longToBinaryString_format() {
        val bin = IPCalculator.longToBinaryString(IPCalculator.ipv4ToLong("192.168.1.1"))
        assertEquals("11000000.10101000.00000001.00000001", bin)
    }

    @Test
    fun cidrReferenceTable() {
        val table = IPCalculator.getCidrReferenceTable()
        assertEquals(33, table.size)
    }

    @Test
    fun commonPorts() {
        val ports = IPCalculator.getCommonPorts()
        assertTrue(ports.any { it.port == 443 })
    }

    @Test
    fun networkClassTable() {
        val classes = IPCalculator.getNetworkClassTable()
        assertEquals(5, classes.size)
    }

    @Test
    fun calculateVLSM_basic() {
        val r = IPCalculator.calculateVLSM("192.168.1.0", 24, listOf(Pair("A", 50), Pair("B", 20)))
        assertNotNull(r)
        assertEquals(2, r!!.size)
    }

    @Test
    fun calculateFLSM_basic() {
        val r = IPCalculator.calculateFLSM("192.168.1.0", 24, 4)
        assertNotNull(r)
        assertEquals(4, r!!.size)
    }

    @Test
    fun summarizeRoutes() {
        val r = IPCalculator.summarizeRoutes(listOf("192.168.0.0/24", "192.168.1.0/24"))
        assertNotNull(r)
    }

    @Test
    fun isValidIPv6() {
        assertTrue(IPCalculator.isValidIPv6("2001:db8::1"))
        assertTrue(IPCalculator.isValidIPv6("::1"))
        assertFalse(IPCalculator.isValidIPv6("2001:db8:::1"))
    }

    @Test
    fun calculateIPv6() {
        val r = IPCalculator.calculateIPv6("2001:db8::1", 64)
        assertNotNull(r)
        assertEquals(64, r!!.prefix)
    }

    @Test
    fun ipv4ToHex() {
        assertEquals("C0A80101", IPCalculator.ipv4ToHex("192.168.1.1"))
    }

    @Test
    fun hexToIPv4() {
        assertEquals("192.168.1.1", IPCalculator.hexToIPv4("C0A80101"))
    }

    @Test
    fun ipv4ToDecimal() {
        assertEquals("3232235777", IPCalculator.ipv4ToDecimal("192.168.1.1"))
    }

    @Test
    fun decimalToIPv4() {
        assertEquals("192.168.1.1", IPCalculator.decimalToIPv4("3232235777"))
    }

    @Test
    fun formatMacAddress() {
        val r = IPCalculator.formatMacAddress("00:11:22:aa:bb:cc")
        assertTrue(r.isNotEmpty())
    }

    @Test
    fun generateQuizQuestion() {
        for (i in 1..5) {
            val q = IPCalculator.generateQuizQuestion()
            assertTrue(q.prefix in 8..29)
            assertEquals(4, q.options.size)
        }
    }

    @Test
    fun calculateWildcard() {
        assertEquals("0.0.0.255", IPCalculator.calculateWildcard(24))
    }

    @Test
    fun generateCiscoAcl() {
        val acl = IPCalculator.generateCiscoAcl("192.168.1.0", 24, "permit", "tcp", "any", "")
        assertTrue(acl.contains("access-list"))
    }

    @Test
    fun compareSubnets() {
        val r = IPCalculator.compareSubnets("192.168.1.0", 24, "192.168.0.0", 16)
        assertNotNull(r)
        assertTrue(r!!.overlaps)
    }

    @Test
    fun vlsmUtilization() {
        val subs = IPCalculator.calculateVLSM("192.168.1.0", 24, listOf(Pair("A", 50)))!!
        val u = IPCalculator.calculateSubnetUtilization(24, subs)
        assertTrue(u.utilizationPercent > 0)
    }
}