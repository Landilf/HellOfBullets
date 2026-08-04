package ru.landilf.hellofbullets.domain.model.shop

import java.time.LocalDate

data class ShopState(
    val offers: List<ShopOffer>,
    val lastAutomaticRefreshDate: LocalDate
)
