package com.example.android_kotlin_quickstart

import android.app.Application
import com.example.android_kotlin_quickstart.viewmodel.AddEditHotelViewModel
import com.example.android_kotlin_quickstart.viewmodel.HomeViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.GlobalContext
import org.koin.dsl.module
import java.lang.ref.WeakReference

class AndroidKotlinQuickstart: Application() {
    override fun onCreate() {
        super.onCreate()

        // Enable Koin dependency injection framework
        GlobalContext.startKoin {
            //inject Android context
            androidContext(this@AndroidKotlinQuickstart)

            //dependency register modules
            modules(
                module {
                    viewModel { HomeViewModel(WeakReference(this@AndroidKotlinQuickstart)) }
                    viewModel { AddEditHotelViewModel(WeakReference(this@AndroidKotlinQuickstart))}
                })
        }
    }

}