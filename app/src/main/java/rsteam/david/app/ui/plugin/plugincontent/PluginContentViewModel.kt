package rsteam.david.app.ui.plugin.plugincontent

import androidx.lifecycle.ViewModel
import io.reactivex.Flowable
import io.reactivex.Single
import rsteam.david.app.data.model.FileTypeContent
import rsteam.david.app.data.repository.DownloadResult
import java.io.FileInputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import android.webkit.MimeTypeMap
import rsteam.david.app.data.repository.PluginContentRepository
import java.lang.Exception

class PluginContentViewModel : ViewModel() {

    fun downloadFile(url: String): Flowable<DownloadResult> = PluginContentRepository().downloadFile(url)

    fun analyseFile(filePath: String?, fileName: String) : Single<List<FileTypeContent>> {
        val fileListMap = mutableMapOf<String, Long>()

        try {
            val fin = FileInputStream("$filePath/$fileName")
            val zin = ZipInputStream(fin)
            var ze: ZipEntry?
            ze = zin.nextEntry
            while (ze != null ) {
                if (ze.isDirectory) {

                } else {
                    val extension = MimeTypeMap.getFileExtensionFromUrl(ze.name)
                    fileListMap[extension] = fileListMap[extension]?:0 + ze.size
                }
                zin.closeEntry()
                ze = zin.nextEntry
            }
            zin.close()

            val fileList = fileListMap.map {
                FileTypeContent(it.key, it.value)
            }

            return Single.just(fileList)
        } catch (e: Exception) {
            return Single.error(Throwable(e))
        }
    }

}