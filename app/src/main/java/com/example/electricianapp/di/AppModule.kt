package com.example.electricianapp.di

import com.example.electricianapp.domain.usecase.materials.GetTransactionHistoryUseCase
import com.example.electricianapp.domain.usecase.materials.GetTransactionHistoryUseCaseImpl
import com.example.electricianapp.domain.usecase.materials.SaveTransactionUseCase
import com.example.electricianapp.domain.usecase.materials.SaveTransactionUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for providing application-level dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    /**
     * Provides the implementation of [GetTransactionHistoryUseCase]
     */
    @Binds
    @Singleton
    abstract fun provideGetTransactionHistoryUseCase(
        impl: GetTransactionHistoryUseCaseImpl
    ): GetTransactionHistoryUseCase

    /**
     * Provides the implementation of [SaveTransactionUseCase]
     */
    @Binds
    @Singleton
    abstract fun provideSaveTransactionUseCase(
        impl: SaveTransactionUseCaseImpl
    ): SaveTransactionUseCase
}
