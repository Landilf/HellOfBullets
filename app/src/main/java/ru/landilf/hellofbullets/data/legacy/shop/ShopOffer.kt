package ru.landilf.hellofbullets.data.legacy.shop

import ru.landilf.hellofbullets.data.legacy.equipment.Item

data class ShopOffer(
    val item: Item,
    val price: Int
)