package com.example.ipcalculator

import org.junit.Assert.assertTrue
import org.junit.Test

class IPCalculatorTest {
    @Test
    fun smokeTest() {
        assertTrue(IPCalculator.isValidIPv4("192.168.1.1"))
    }
}