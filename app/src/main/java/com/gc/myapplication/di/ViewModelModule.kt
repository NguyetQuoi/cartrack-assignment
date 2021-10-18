package com.gc.myapplication.di

import com.gc.myapplication.ui.detail.UserDetailViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { UserDetailViewModel(get()) }
}