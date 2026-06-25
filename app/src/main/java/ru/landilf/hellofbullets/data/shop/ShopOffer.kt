package ru.landilf.hellofbullets.data.shop

import ru.landilf.hellofbullets.data.equipment.Item

data class ShopOffer(
    val item: Item,
    val price: Int
)