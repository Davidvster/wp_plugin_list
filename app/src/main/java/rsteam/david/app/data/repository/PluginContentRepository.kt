package rsteam.david.app.data.repository

import android.os.Environment
import com.downloader.*
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.subjects.ReplaySubject
import rsteam.david.app.data.arch.AppModule

class PluginContentRepository {

    init {
        val config = PRDownloaderConfig.newBuilder()
            .build()
        PRDownloader.initialize(AppModule.application, config)
    }

    fun downloadFile(url: String) : Flowable<DownloadResult> {

        val subject = ReplaySubject.create<DownloadResult>()
        val filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path
        val fileName = url.substring(url.lastIndexOf("/") + 1, url.length)

        PRDownloader.download(url, filePath, fileName)
            .setPriority(Priority.HIGH)
            .build()
            .setOnProgressListener { progress ->
                subject.onNext(DownloadProgress(fileName, ((progress.currentBytes / progress.totalBytes.toDouble()) * 100).toInt()))
            }
            .start(object : OnDownloadListener {
                override fun onDownloadComplete() {
                    subject.onNext(DownloadComplete(fileName, filePath))
                    subject.onComplete()
                }

                override fun onError(error: Error?) {
                    subject.onNext(DownloadError(fileName, error))
                    subject.onError(DownloadException(DownloadError(fileName, error)))
                }

            })

        return subject.toFlowable(BackpressureStrategy.LATEST).share()
    }
}

sealed class DownloadResult(val fileName: String)
class DownloadComplete(fileName: String, val filePath: String?) : DownloadResult(fileName)
class DownloadProgress(fileName: String, val currentProgress: Int) : DownloadResult(fileName)
class DownloadError(fileName: String, val error: Error?) : DownloadResult(fileName)

class DownloadException(val downloadError: DownloadError) : Exception()