package com.example.adboardapp.model

import org.junit.Assert.assertEquals
import org.junit.Test

class RatingStarsTest {

    @Test
    fun roundsToTheNearestWholeNumber() {
        assertEquals(0, ratingStars(0.0))
        assertEquals(3, ratingStars(3.2))
        assertEquals(4, ratingStars(4.0))
        assertEquals(4, ratingStars(4.49))
        assertEquals(5, ratingStars(4.5))
        assertEquals(5, ratingStars(4.6))
        assertEquals(5, ratingStars(5.0))
    }

    @Test
    fun staysBetweenZeroAndFive() {
        assertEquals(0, ratingStars(-1.0))
        assertEquals(5, ratingStars(6.0))
    }
}
