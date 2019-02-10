package rsteam.david.app.data.api

import io.reactivex.Flowable
import retrofit2.http.GET
import retrofit2.http.Query
import rsteam.david.app.data.model.response.PluginsResponse

interface PluginService {

    @GET("/plugins/info/1.1/?action=query_plugins")
    fun getPlugins(@Query("request[page]") page: Int, @Query("request[per_page]") perPage: Int,  @Query("request[browse]") browse: String = "new"): Flowable<PluginsResponse>

}