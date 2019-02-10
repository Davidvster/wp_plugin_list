package rsteam.david.app.data.arch

import android.preference.PreferenceManager
import com.f2prateek.rx.preferences2.RxSharedPreferences
import rsteam.david.app.BuildConfig

object PreferenceModule {
    private val sharedPrefs by lazy {
        PreferenceManager.getDefaultSharedPreferences(AppModule.application)
    }

    private val rxSharedPrefs by lazy {
        RxSharedPreferences.create(sharedPrefs)
    }

    val perPagePref by lazy {
        rxSharedPrefs.getInteger("perpage", BuildConfig.DEFAULT_PER_PAGE)
    }

}