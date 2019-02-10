package rsteam.david.app.data.model

import com.google.gson.annotations.SerializedName
import rsteam.david.app.data.model.wrappers.ScreenshotWrapper

data class Plugin(val name: String,
                  val homepage: String,
                  @SerializedName("download_link")
                  val downloadLink: String,
                  val screenshots: ScreenshotWrapper)