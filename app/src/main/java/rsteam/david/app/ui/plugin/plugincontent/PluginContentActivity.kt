package rsteam.david.app.ui.plugin.plugincontent

import android.Manifest
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_plugin_content.*
import rsteam.david.app.R
import android.net.Uri
import android.os.Build
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import com.fondesa.kpermissions.extension.listeners
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.fondesa.kpermissions.request.PermissionRequest
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import rsteam.david.app.data.model.utils.StringUtils.humanReadableByteCount
import rsteam.david.app.data.repository.DownloadComplete
import rsteam.david.app.data.repository.DownloadError
import rsteam.david.app.data.repository.DownloadProgress

class PluginContentActivity : AppCompatActivity() {

    private lateinit var perm: PermissionRequest
    private var storagePermanentlyDeniedOrAccepted = false

    private val detailViewModel by lazy {
        ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory(this.application)
        ).get(PluginContentViewModel::class.java)
    }

    private val disposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plugin_content)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        title = intent.getStringExtra(ARG_PLUGIN_NAME)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            perm = permissionsBuilder(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE).build()
            perm.listeners {
                onAccepted {
                    perm.detachAllListeners()
                    storagePermanentlyDeniedOrAccepted = true
                    plugin_content_info_text.visibility = View.GONE
                    setPluginContent()
                }
                onDenied { }
                onPermanentlyDenied {
                    perm.detachAllListeners()
                    storagePermanentlyDeniedOrAccepted = true
                    plugin_content_info_text.text = getString(R.string.plugin_content_storage_denied)
                    plugin_content_info_text.visibility = View.VISIBLE
                }
            }
        } else {
            storagePermanentlyDeniedOrAccepted = true
            plugin_content_info_text.visibility = View.GONE
            setPluginContent()
        }
    }


    override fun onResume() {
        super.onResume()
        if (storagePermanentlyDeniedOrAccepted.not()) {
            perm.send()
        }
    }
    private fun setPluginContent() {
        val homepage = intent.getStringExtra(ARG_HOMEPAGE)
        if (homepage.isNullOrBlank().not()){
            plugin_content_homepage.visibility = View.VISIBLE
            plugin_content_homepage.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(homepage)
                startActivity(intent)
            }
        }

        val fileUrl = intent.getStringExtra(ARG_DOWNLOAD_LINK)
        if (fileUrl.isNullOrBlank().not()){
            downloadFile(fileUrl)
        }

        plugin_content_download_retry.setOnClickListener {
            downloadFile(fileUrl)
        }
    }

    private fun downloadFile(url: String) {
        plugin_content_download_progress.progress = 0
        plugin_content_info_text.text = getString(R.string.plugin_content_downloading, 0)
        plugin_content_download_progress.visibility = View.VISIBLE
        plugin_content_info_text.visibility = View.VISIBLE
        disposable.add(detailViewModel.downloadFile(url)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ downloadResult ->
                when (downloadResult) {
                    is DownloadProgress -> {
                        plugin_content_info_text.text = getString(R.string.plugin_content_downloading, downloadResult.currentProgress)
                        plugin_content_download_progress.progress = downloadResult.currentProgress
                    }
                    is DownloadComplete -> {
                        if (downloadResult.filePath != null) {
                            plugin_content_download_progress.visibility = View.GONE
                            plugin_content_info_text.visibility = View.GONE
                            plugin_content_download_retry.visibility = View.GONE
                            analiseFile(downloadResult.filePath, downloadResult.fileName)
                        } else {
                            showDownloadError()
                        }
                    }
                    is DownloadError -> {
                        showDownloadError()
                    }
                }
            }, {
                showDownloadError()
            })
        )
    }

    private fun showDownloadError() {
        plugin_content_info_text.text = getString(R.string.plugin_content_download_error)
        plugin_content_download_progress.visibility = View.GONE
        plugin_content_download_retry.visibility = View.VISIBLE
    }

    private fun analiseFile(filePath: String?, fileName: String) {
        plugin_content_list_progress.visibility = View.VISIBLE

        disposable.add(detailViewModel.analyseFile(filePath, fileName)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ fileList ->
                plugin_content_list_progress.visibility = View.GONE
                val list = fileList.map {
                    String.format(getString(R.string.plugin_content_element_text), it.fileType, humanReadableByteCount(it.size))
                }
                val pluginContentAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, list)
                plugin_content_list.adapter = pluginContentAdapter
            }, {
                plugin_content_list_progress.visibility = View.GONE
                plugin_content_info_text.text = getString(R.string.plugin_content_analyse_error)
                plugin_content_info_text.visibility = View.VISIBLE
            }))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        private const val ARG_PLUGIN_NAME = "pluginName.string"
        private const val ARG_DOWNLOAD_LINK = "downloadLink.string"
        private const val ARG_HOMEPAGE = "homepage.string"

        @JvmStatic
        fun start(context: Activity, pluginName: String, downloadLink: String, homepage: String) {
            val intent = Intent(context, PluginContentActivity::class.java)
            intent.putExtra(ARG_PLUGIN_NAME, pluginName)
            intent.putExtra(ARG_DOWNLOAD_LINK, downloadLink)
            intent.putExtra(ARG_HOMEPAGE, homepage)
            context.startActivity(intent)
        }
    }
}
