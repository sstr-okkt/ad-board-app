package com.example.adboardapp.data.remote

import kotlinx.serialization.Serializable

// Response of GET /products
@Serializable
data class ProductsResponse(
    val products: List<ProductDto> = emptyList()
)

// One product from the dummyjson API
@Serializable
data class ProductDto(
    val id: Int,
    val title: String,
    val price: Double,
    val category: String,
    val stock: Int = 0,
    val brand: String? = null,
    val meta: MetaDto? = null,
    val reviews: List<ReviewDto> = emptyList()
)

// Creation date of a product
@Serializable
data class MetaDto(
    val createdAt: String? = null
)

// Review used as the ad author
@Serializable
data class ReviewDto(
    val reviewerName: String,
    val rating: Double = 0.0
)
