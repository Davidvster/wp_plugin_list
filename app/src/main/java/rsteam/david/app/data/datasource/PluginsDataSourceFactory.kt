package rsteam.david.app.data.datasource

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import rsteam.david.app.data.model.Plugin

class PluginsDataSourceFactory(private val disposable: CompositeDisposable) : DataSource.Factory<Long, Plugin>() {

    private val pluginDataSource: PluginsDataSource by lazy {
        PluginsDataSource(disposable)
    }
    val errorLoading: Flowable<Boolean> by lazy {
        pluginDataSource.errorLoadingFlowable()
    }

    val pluginsDataSourceLiveData = MutableLiveData<PluginsDataSource>()

    override fun create(): DataSource<Long, Plugin> {
        pluginsDataSourceLiveData.postValue(pluginDataSource)
        return pluginDataSource
    }

}