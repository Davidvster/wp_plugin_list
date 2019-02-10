package rsteam.david.app.data.model.utils

import com.google.gson.*
import rsteam.david.app.data.arch.ApiModule.gson
import rsteam.david.app.data.model.Plugin
import rsteam.david.app.data.model.wrappers.PluginWrapper
import java.lang.reflect.Type

class PluginDeserializer : JsonDeserializer<PluginWrapper> {

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): PluginWrapper {
        val pluginList = mutableListOf<Plugin>()
        if (json.isJsonArray) {
            (json as JsonArray).forEach {
                pluginList.add(gson.fromJson(it, Plugin::class.java))
            }
        } else {
            (json as JsonObject).entrySet().forEach {
                pluginList.add(gson.fromJson(it.value, Plugin::class.java))
            }
        }
        return PluginWrapper(pluginList)
    }

}