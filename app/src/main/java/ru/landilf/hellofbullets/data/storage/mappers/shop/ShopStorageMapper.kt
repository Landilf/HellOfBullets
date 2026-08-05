package ru.landilf.hellofbullets.data.storage.mappers.shop

import ru.landilf.hellofbullets.data.storage.entities.shop.ShopArmorOfferEntity
import ru.landilf.hellofbullets.data.storage.entities.shop.ShopArtifactOfferEntity
import ru.landilf.hellofbullets.data.storage.entities.shop.ShopStateEntity
import ru.landilf.hellofbullets.data.storage.entities.shop.ShopWeaponOfferEntity
import ru.landilf.hellofbullets.domain.model.equipment.ArmorItem
import ru.landilf.hellofbullets.domain.model.equipment.ArtifactItem
import ru.landilf.hellofbullets.domain.model.equipment.WeaponItem
import ru.landilf.hellofbullets.domain.model.shop.ShopState
import javax.inject.Inject

data class ShopStorageData(
    val shopState: ShopStateEntity,
    val weaponOffers: List<ShopWeaponOfferEntity>,
    val armorOffers: List<ShopArmorOfferEntity>,
    val artifactOffers: List<ShopArtifactOfferEntity>
)

class ShopStorageMapper @Inject constructor(
    private val shopStateEntityToDomainMapper: ShopStateEntityToDomainMapper,
    private val shopStateDomainToEntityMapper: ShopStateDomainToEntityMapper,
    private val shopWeaponOfferEntityToDomainMapper: ShopWeaponOfferEntityToDomainMapper,
    private val shopWeaponOfferDomainToEntityMapper: ShopWeaponOfferDomainToEntityMapper,
    private val shopArmorOfferEntityToDomainMapper: ShopArmorOfferEntityToDomainMapper,
    private val shopArmorOfferDomainToEntityMapper: ShopArmorOfferDomainToEntityMapper,
    private val shopArtifactOfferEntityToDomainMapper: ShopArtifactOfferEntityToDomainMapper,
    private val shopArtifactOfferDomainToEntityMapper: ShopArtifactOfferDomainToEntityMapper
) {
    fun toDomain(
        shopStateEntity: ShopStateEntity,
        weaponEntities: List<ShopWeaponOfferEntity>,
        armorEntities: List<ShopArmorOfferEntity>,
        artifactEntities: List<ShopArtifactOfferEntity>
    ): ShopState {
        val offersWithPositions = buildList {
            addAll(weaponEntities.map {
                it.position to shopWeaponOfferEntityToDomainMapper(it)
            })
            addAll(armorEntities.map {
                it.position to shopArmorOfferEntityToDomainMapper(it)
            })
            addAll(artifactEntities.map {
                it.position to shopArtifactOfferEntityToDomainMapper(it)
            })
        }

        return shopStateEntityToDomainMapper(
            entity = shopStateEntity,
            offers = offersWithPositions
                .sortedBy { it.first }
                .map { it.second }
        )
    }

    fun toStorage(shopState: ShopState): ShopStorageData {
        return ShopStorageData(
            shopState = shopStateDomainToEntityMapper(shopState),
            weaponOffers = shopState.offers.mapIndexedNotNull { position, offer ->
                if (offer.item is WeaponItem) {
                    shopWeaponOfferDomainToEntityMapper(
                        offer = offer,
                        position = position
                    )
                } else {
                    null
                }
            },
            armorOffers = shopState.offers.mapIndexedNotNull { position, offer ->
                if (offer.item is ArmorItem) {
                    shopArmorOfferDomainToEntityMapper(
                        offer = offer,
                        position = position
                    )
                } else {
                    null
                }
            },
            artifactOffers = shopState.offers.mapIndexedNotNull { position, offer ->
                if (offer.item is ArtifactItem) {
                    shopArtifactOfferDomainToEntityMapper(
                        offer = offer,
                        position = position
                    )
                } else {
                    null
                }
            }
        )
    }
}