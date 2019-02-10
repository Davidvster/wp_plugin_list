package rsteam.david.app.ui.plugin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_plugin.*
import rsteam.david.app.BuildConfig
import rsteam.david.app.R
import rsteam.david.app.ui.dialog.AppDialog
import rsteam.david.app.ui.plugin.plugincontent.PluginContentActivity

class PluginActivity : AppCompatActivity() {

    private val mainViewModel by lazy {
        ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory(this.application)
        ).get(PluginViewModel::class.java)
    }

    private val mainAdapter by lazy { PluginAdapter(this) }

    private val disposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plugin)

        val linearLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        plugin_plugin_list.layoutManager = linearLayoutManager
        plugin_plugin_list.adapter = mainAdapter

        mainAdapter.onPluginClicked = {
            PluginContentActivity.start(this, it.name, it.downloadLink, it.homepage)
        }

        plugin_retry.setOnClickListener {
            plugin_retry_group.visibility = View.GONE
            plugin_progress.visibility = View.VISIBLE
            mainViewModel.retry()
        }

        if (mainViewModel.isPerPagePrefSet()) {
            getData()
        } else {
            showPerPagePrefDialog()
        }
    }

    private fun showPerPagePrefDialog() {
        AppDialog(this)
            .title(R.string.dialog_title)
            .positiveButton(R.string.dialog_positive) { it.dismiss() }
            .dismiss {
                mainViewModel.setPerPagePref(it.getInput()?:BuildConfig.DEFAULT_PER_PAGE)
                getData()
            }.show()
    }

    private fun getData() {
        plugin_progress.visibility = View.VISIBLE
        mainViewModel.getData()
        disposable.add(mainViewModel.pluginList
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { flowableList ->
                plugin_progress.visibility = View.GONE
                mainAdapter.submitList(flowableList)
            }
        )
        disposable.add(
            mainViewModel.errorLoading
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (it) {
                        plugin_progress.visibility = View.GONE
                        plugin_retry_group.visibility = View.VISIBLE
                    } else {
                        plugin_progress.visibility = View.GONE
                        plugin_retry_group.visibility = View.GONE
                    }
                }
        )
    }
}
