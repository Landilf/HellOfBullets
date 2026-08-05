package ru.landilf.hellofbullets.data.storage.mappers.shop

import ru.landilf.hellofbullets.data.storage.entities.shop.ShopStateEntity
import ru.landilf.hellofbullets.domain.model.shop.ShopState
import javax.inject.Inject

class ShopStateDomainToEntityMapper @Inject constructor() {
    operator fun invoke(
        shopState: ShopState
    ): ShopStateEntity {
        return ShopStateEntity(
            lastAutomaticRefreshEpochDay = shopState.lastAutomaticRefreshDate.toEpochDay(),
            manualRefreshCount = shopState.manualRefreshCount
        )
    }
}