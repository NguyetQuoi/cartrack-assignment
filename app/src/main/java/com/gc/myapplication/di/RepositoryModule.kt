package com.gc.myapplication.di

import com.gc.myapplication.data.AppDataRepository
import com.gc.myapplication.data.DataSource
import com.gc.myapplication.data.local.RoomDatabaseStorage
import com.gc.myapplication.data.remote.AppRemoteStorage
import org.koin.core.qualifier.named
import org.koin.dsl.module

val repositoryModule = module {
    single<DataSource>(named(Scopes.REMOTE)) { AppRemoteStorage(get()) }
    single<DataSource>(named(Scopes.LOCAL)) { RoomDatabaseStorage(get()) }
    single { AppDataRepository(get(), get(named(Scopes.REMOTE)), get(named(Scopes.LOCAL)), get()) }
}
