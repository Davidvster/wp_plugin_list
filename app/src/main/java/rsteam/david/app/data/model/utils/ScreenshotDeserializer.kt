package rsteam.david.app.data.model.utils

import com.google.gson.*
import rsteam.david.app.data.arch.ApiModule.gson
import rsteam.david.app.data.model.Screenshot
import rsteam.david.app.data.model.wrappers.ScreenshotWrapper
import java.lang.reflect.Type

class ScreenshotDeserializer : JsonDeserializer<ScreenshotWrapper> {

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): ScreenshotWrapper {
        val screenshotList = mutableListOf<Screenshot>()
        if (json.isJsonArray) {
            (json as JsonArray).forEach {
                screenshotList.add(gson.fromJson(it, Screenshot::class.java))
            }
        } else {
            (json as JsonObject).entrySet().forEach {
                screenshotList.add(gson.fromJson(it.value, Screenshot::class.java))
            }
        }
        return ScreenshotWrapper(screenshotList)
    }

}