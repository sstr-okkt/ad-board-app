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
}
