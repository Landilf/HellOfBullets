package com.landilf.hellofbullets.data.shop

import com.landilf.hellofbullets.data.equipment.Item

data class ShopOffer(
    val item: Item,
    val price: Int
)