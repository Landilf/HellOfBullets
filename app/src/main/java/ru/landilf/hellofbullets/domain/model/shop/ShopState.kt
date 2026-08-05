package ru.landilf.hellofbullets.domain.model.shop

import java.time.LocalDate

data class ShopState(
    val offers: List<ShopOffer>,
    val lastAutomaticRefreshDate: LocalDate,
    val manualRefreshCount: Int
) {
    companion object {
        const val MAX_MANUAL_REFRESH_COUNT = 3
    }
}
