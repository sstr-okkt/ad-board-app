package com.example.adboardapp.ui

import org.junit.Assert.assertEquals
import org.junit.Test

class PhoneFormatTest {

    @Test
    fun formatsFullNumber() {
        assertEquals("+380 67 111 22 33", formatPhoneNumber("+380671112233"))
    }

    @Test
    fun formatsLocalNumber() {
        assertEquals("+380 67 111 22 33", formatPhoneNumber("0671112233"))
    }

    @Test
    fun keepsEmptyValue() {
        assertEquals("", formatPhoneNumber(""))
    }

    @Test
    fun formatsPartlyTypedNumbers() {
        assertEquals("+380 67 11", formatPhoneNumber("6711"))
        assertEquals("+380 67 111", formatPhoneNumber("67111"))
        assertEquals("+380 67 111 2", formatPhoneNumber("671112"))
        assertEquals("+380 67 111 22", formatPhoneNumber("6711122"))
        assertEquals("+380 67 111 22 3", formatPhoneNumber("67111223"))
    }
}
