package ru.landilf.hellofbullets.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.landilf.hellofbullets.domain.model.shop.ShopState

interface ShopRepository {
    suspend fun getShopState(): ShopState?
    suspend fun saveShopState(shopState: ShopState)
    suspend fun applyManualRefresh(
        playerId: Long,
        updatedSilverAmount: Int,
        refreshedShopState: ShopState
    )

    fun observeShopState(): Flow<ShopState?>
}