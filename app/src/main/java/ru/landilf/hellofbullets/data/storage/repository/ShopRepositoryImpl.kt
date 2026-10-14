package ru.landilf.hellofbullets.data.storage.repository

import androidx.room.withTransaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import ru.landilf.hellofbullets.data.storage.dao.EquipmentDao
import ru.landilf.hellofbullets.data.storage.dao.PlayerDao
import ru.landilf.hellofbullets.data.storage.dao.ShopDao
import ru.landilf.hellofbullets.data.storage.database.AppDatabase
import ru.landilf.hellofbullets.data.storage.mappers.equipment.EquipmentStorageMapper
import ru.landilf.hellofbullets.data.storage.mappers.shop.ShopStorageMapper
import ru.landilf.hellofbullets.domain.model.equipment.ArmorItem
import ru.landilf.hellofbullets.domain.model.equipment.ArtifactItem
import ru.landilf.hellofbullets.domain.model.equipment.Item
import ru.landilf.hellofbullets.domain.model.equipment.WeaponItem
import ru.landilf.hellofbullets.domain.model.shop.PurchaseShopOfferResult
import ru.landilf.hellofbullets.domain.model.shop.ShopState
import ru.landilf.hellofbullets.domain.repository.ShopRepository
import javax.inject.Inject

class ShopRepositoryImpl @Inject constructor(
    private val database: AppDatabase,
    private val shopDao: ShopDao,
    private val shopStorageMapper: ShopStorageMapper,
    private val playerDao: PlayerDao,
    private val equipmentDao: EquipmentDao,
    private val equipmentStorageMapper: EquipmentStorageMapper
) : ShopRepository {
    override suspend fun getShopState(): ShopState? {
        return database.withTransaction {
            getShopStateSnapshot()
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

    override suspend fun purchaseOffer(
        playerId: Long,
        itemId: Long
    ): PurchaseShopOfferResult {
        return database.withTransaction {
            val playerProfile = playerDao.getPlayerProfile()
                ?: error("Профиль игрока с id $playerId не найден")

            val shopState = getShopStateSnapshot()
                ?: return@withTransaction PurchaseShopOfferResult.OfferNotFound

            val offer = shopState.offers.firstOrNull { it.item.id == itemId }
                ?: return@withTransaction PurchaseShopOfferResult.OfferNotFound

            if (offer.isSold) {
                return@withTransaction PurchaseShopOfferResult.OfferAlreadySold
            }

            if (playerProfile.silverAmount < offer.purchasePrice) {
                return@withTransaction PurchaseShopOfferResult.InsufficientSilver(
                    requiredSilverAmount = offer.purchasePrice,
                    currentSilverAmount = playerProfile.silverAmount
                )
            }

            check(markOfferAsSold(offer.item) == 1) {
                "Не удалось пометить предложение с id $itemId как проданное"
            }

            addItemToInventory(
                item = offer.item,
                ownerId = playerId
            )

            val remainingSilverAmount = playerProfile.silverAmount - offer.purchasePrice
            check(
                playerDao.updateSilverAmount(
                    playerId = playerId,
                    silverAmount = remainingSilverAmount
                ) == 1
            ) {
                "Не удалось обновить количество серебра игрока с id $playerId"
            }

            PurchaseShopOfferResult.Success(
                purchasedItem = offer.item,
                spentSilverAmount = offer.purchasePrice,
                remainingSilverAmount = remainingSilverAmount
            )
        }
    }

    private suspend fun getShopStateSnapshot(): ShopState? {
        val shopStateEntity = shopDao.getShopState() ?: return null

        return shopStorageMapper.toDomain(
            shopStateEntity = shopStateEntity,
            weaponEntities = shopDao.getWeaponOffers(),
            armorEntities = shopDao.getArmorOffers(),
            artifactEntities = shopDao.getArtifactOffers()
        )
    }

    private suspend fun markOfferAsSold(item: Item): Int {
        return when (item) {
            is WeaponItem -> shopDao.markWeaponOfferSold(item.id)
            is ArmorItem -> shopDao.markArmorOfferSold(item.id)
            is ArtifactItem -> shopDao.markArtifactOfferSold(item.id)
        }
    }

    private suspend fun addItemToInventory(
        item: Item,
        ownerId: Long
    ) {
        val equipmentData = equipmentStorageMapper.toStorage(
            items = listOf(item),
            ownerId = ownerId
        )

        when (item) {
            is WeaponItem -> equipmentDao.upsertWeaponItems(equipmentData.weaponItems)
            is ArmorItem -> equipmentDao.upsertArmorItems(equipmentData.armorItems)
            is ArtifactItem -> equipmentDao.upsertArtifactItems(equipmentData.artifactItems)
        }
    }
}