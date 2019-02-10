package rsteam.david.app.ui.plugin

import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import androidx.paging.RxPagedListBuilder
import com.f2prateek.rx.preferences2.Preference
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import rsteam.david.app.data.arch.PreferenceModule
import rsteam.david.app.data.datasource.PluginsDataSourceFactory
import rsteam.david.app.data.model.Plugin

class PluginViewModel(private val perPagePref: Preference<Int> = PreferenceModule.perPagePref) : ViewModel() {

    lateinit var pluginList: Flowable<PagedList<Plugin>>

    private val disposable = CompositeDisposable()

    private val pageSize = perPagePref.get()

    private val sourceFactory: PluginsDataSourceFactory

    var errorLoading : Flowable<Boolean>

    init {
        sourceFactory = PluginsDataSourceFactory(disposable)
        errorLoading = sourceFactory.errorLoading

    }

    fun isPerPagePrefSet() : Boolean = perPagePref.isSet

    fun setPerPagePref(perPage: Int) {
        perPagePref.set(perPage)
    }

    fun getData() {
        pluginList = RxPagedListBuilder(sourceFactory, pageSize)
            .buildFlowable(BackpressureStrategy.LATEST)
    }

    fun retry() {
        sourceFactory.pluginsDataSourceLiveData.value!!.retry()
    }

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }

}