package rsteam.david.app.data.datasource

import androidx.paging.ItemKeyedDataSource
import com.f2prateek.rx.preferences2.Preference
import io.reactivex.BackpressureStrategy
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import rsteam.david.app.data.api.PluginService
import rsteam.david.app.data.arch.ApiModule
import rsteam.david.app.data.model.Plugin
import timber.log.Timber
import io.reactivex.functions.Action
import io.reactivex.subjects.ReplaySubject
import rsteam.david.app.data.arch.PreferenceModule

class PluginsDataSource(
    private val disposable: CompositeDisposable,
    private val pluginService: PluginService = ApiModule.pluginService,
    private val perPagePref: Preference<Int> = PreferenceModule.perPagePref
) : ItemKeyedDataSource<Long, Plugin>() {

    private var plugins = mutableListOf<Plugin>()

    private var count = 1

    private var errorLoading: ReplaySubject<Boolean> = ReplaySubject.create()

    private var retryCompletable: Completable? = null

    fun retry() {
        if (retryCompletable != null) {
            disposable.add(retryCompletable!!
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                }, {
                    Timber.e(it)
                })
            )
        }
    }

    fun errorLoadingFlowable(): Flowable<Boolean> = errorLoading.toFlowable(BackpressureStrategy.LATEST).share()

    override fun loadInitial(params: LoadInitialParams<Long>, callback: LoadInitialCallback<Plugin>) {
        disposable.add(pluginService.getPlugins(1, perPagePref.get()).subscribe({ pluginsResponse ->
            setRetry(null)
            count++
            plugins.addAll(pluginsResponse.plugins.plugins)
            plugins = plugins.distinct().toMutableList()
            errorLoading.onNext(false)
            callback.onResult(pluginsResponse.plugins.plugins)
        }, {
            setRetry(Action { loadInitial(params, callback) })
            errorLoading.onNext(true)
            Timber.e("&&&$it")
        }))
    }

    override fun loadAfter(params: LoadParams<Long>, callback: LoadCallback<Plugin>) {
        disposable.add(pluginService.getPlugins(count, perPagePref.get()).subscribe({ pluginsResponse ->
            setRetry(null)
            count++
            plugins.addAll(pluginsResponse.plugins.plugins)
            plugins = plugins.distinct().toMutableList()
            errorLoading.onNext(false)
            callback.onResult(pluginsResponse.plugins.plugins)
        }, {
            setRetry(Action { loadAfter(params, callback) })
            errorLoading.onNext(true)
            Timber.e("&&&$it")
        }))
    }

    override fun loadBefore(params: LoadParams<Long>, callback: LoadCallback<Plugin>) {
        // ignored, since we only ever append to our initial load
    }

    override fun getKey(item: Plugin): Long {
        return plugins.indexOf(item).toLong()
    }

    private fun setRetry(action: Action?) {
        if (action == null) {
            this.retryCompletable = null
        } else {
            this.retryCompletable = Completable.fromAction(action)
        }
    }

}