package rsteam.david.app.data.arch

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import rsteam.david.app.BuildConfig
import rsteam.david.app.data.api.PluginService
import rsteam.david.app.data.model.utils.PluginDeserializer
import rsteam.david.app.data.model.utils.ScreenshotDeserializer
import rsteam.david.app.data.model.wrappers.PluginWrapper
import rsteam.david.app.data.model.wrappers.ScreenshotWrapper
import timber.log.Timber

object ApiModule {
    internal val pluginService by lazy {
        retrofit.create(PluginService::class.java)
    }

    private val gsonDeserializer = GsonBuilder()
        .registerTypeAdapter(PluginWrapper::class.java, PluginDeserializer())
        .create()

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_API_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gsonDeserializer))
            .client(okHttpClient)
            .build()
    }

    private val okHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor { message -> Timber.d(message) }
                .setLevel(HttpLoggingInterceptor.Level.HEADERS)
            )
            .build()
    }

    val gson by lazy {
        GsonBuilder()
            .registerTypeAdapter(ScreenshotWrapper::class.java, ScreenshotDeserializer())
            .create()
    }
}