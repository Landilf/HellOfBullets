package ru.landilf.hellofbullets.domain.usecase.shop

import kotlinx.coroutines.flow.Flow
import ru.landilf.hellofbullets.domain.model.shop.ShopState
import ru.landilf.hellofbullets.domain.repository.ShopRepository
import javax.inject.Inject

class ObserveShopStateUseCase @Inject constructor(
    private val shopRepository: ShopRepository
) {
    operator fun invoke(): Flow<ShopState?> {
        return shopRepository.observeShopState()
    }
}