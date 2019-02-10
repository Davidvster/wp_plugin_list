package rsteam.david.app

import android.app.Application
import rsteam.david.app.data.arch.AppModule
import timber.log.Timber
import io.reactivex.plugins.RxJavaPlugins

open class App : Application() {
    override fun onCreate() {
        super.onCreate()
        AppModule.application = this

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        RxJavaPlugins.setErrorHandler { }
    }

}