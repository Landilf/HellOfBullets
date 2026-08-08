package ru.landilf.hellofbullets.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.landilf.hellofbullets.data.generator.KotlinEquipmentRandomGenerator
import ru.landilf.hellofbullets.domain.generator.EquipmentRandomGenerator

@Module
@InstallIn(SingletonComponent::class)
abstract class GeneratorModule {

    @Binds
    abstract fun bindEquipmentRandomGenerator(
        impl: KotlinEquipmentRandomGenerator
    ): EquipmentRandomGenerator
}