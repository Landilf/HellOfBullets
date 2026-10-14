package ru.landilf.hellofbullets.data.storage.mappers.shop

import ru.landilf.hellofbullets.data.storage.entities.shop.ShopStateEntity
import ru.landilf.hellofbullets.domain.model.shop.ShopOffer
import ru.landilf.hellofbullets.domain.model.shop.ShopState
import java.time.LocalDate
import javax.inject.Inject

class ShopStateEntityToDomainMapper @Inject constructor() {
    operator fun invoke(
        entity: ShopStateEntity,
        offers: List<ShopOffer>
    ): ShopState {
        return ShopState(
            offers = offers,
            lastAutomaticRefreshDate = LocalDate.ofEpochDay(
                entity.lastAutomaticRefreshEpochDay
            ),
            manualRefreshCount = entity.manualRefreshCount
        )
    }
}