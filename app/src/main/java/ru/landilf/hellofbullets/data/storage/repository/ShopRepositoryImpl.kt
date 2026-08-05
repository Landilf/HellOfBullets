package ru.landilf.hellofbullets.data.storage.repository

import androidx.room.withTransaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import ru.landilf.hellofbullets.data.storage.dao.PlayerDao
import ru.landilf.hellofbullets.data.storage.dao.ShopDao
import ru.landilf.hellofbullets.data.storage.database.AppDatabase
import ru.landilf.hellofbullets.data.storage.mappers.shop.ShopStorageMapper
import ru.landilf.hellofbullets.domain.model.shop.ShopState
import ru.landilf.hellofbullets.domain.repository.ShopRepository
import javax.inject.Inject

class ShopRepositoryImpl @Inject constructor(
    private val database: AppDatabase,
    private val shopDao: ShopDao,
    private val shopStorageMapper: ShopStorageMapper,
    private val playerDao: PlayerDao
) : ShopRepository {
    override suspend fun getShopState(): ShopState? {
        return database.withTransaction {
            val shopStateEntity = shopDao.getShopState()
                ?: return@withTransaction null

            shopStorageMapper.toDomain(
                shopStateEntity = shopStateEntity,
                weaponEntities = shopDao.getWeaponOffers(),
                armorEntities = shopDao.getArmorOffers(),
                artifactEntities = shopDao.getArtifactOffers()
            )
        }
    }

    override suspend fun saveShopState(shopState: ShopState) {
        database.withTransaction {
            val storageData = shopStorageMapper.toStorage(shopState)

            shopDao.replaceShop(
                shopState = storageData.shopState,
                weaponOffers = storageData.weaponOffers,
                armorOffers = storageData.armorOffers,
                artifactOffers = storageData.artifactOffers
            )
        }
    }

    override suspend fun applyManualRefresh(
        playerId: Long,
        updatedSilverAmount: Int,
        refreshedShopState: ShopState
    ) {
        database.withTransaction {
            val updatedRowCount = playerDao.updateSilverAmount(
                playerId = playerId,
                silverAmount = updatedSilverAmount,
            )
            check(updatedRowCount == 1) {
                "Профиль игрока с id $playerId не найден"
            }

            val storageData = shopStorageMapper.toStorage(refreshedShopState)

            shopDao.replaceShop(
                shopState = storageData.shopState,
                weaponOffers = storageData.weaponOffers,
                armorOffers = storageData.armorOffers,
                artifactOffers = storageData.artifactOffers
            )
        }
    }

    override fun observeShopState(): Flow<ShopState?> {
        return combine(
            shopDao.observeShopState(),
            shopDao.observeWeaponOffers(),
            shopDao.observeArmorOffers(),
            shopDao.observeArtifactOffers()
        ) { _, _, _, _ -> }.map { getShopState() }
    }
}