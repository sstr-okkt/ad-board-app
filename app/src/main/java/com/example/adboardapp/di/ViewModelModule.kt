package com.example.adboardapp.di

import com.example.adboardapp.ui.AdEditViewModel
import com.example.adboardapp.ui.DetailViewModel
import com.example.adboardapp.ui.ListViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

// Provides the ViewModels
val viewModelModule = module {
    viewModel { ListViewModel(get(), get(), get()) }
    viewModel { params -> DetailViewModel(params.get(), get(), get(), get(), get()) }
    viewModel { params -> AdEditViewModel(params.get(), get(), get()) }
}
