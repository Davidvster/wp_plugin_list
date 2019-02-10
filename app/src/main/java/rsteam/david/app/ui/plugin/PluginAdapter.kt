package rsteam.david.app.ui.plugin

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.element_plugin.view.*
import rsteam.david.app.R
import rsteam.david.app.data.model.Plugin

class PluginAdapter(private val context: Context) :
    PagedListAdapter<Plugin, PluginAdapter.PluginViewHolder>(DIFF_CALLBACK) {

    var onPluginClicked: ((Plugin) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PluginViewHolder {
        return PluginViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.element_plugin,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: PluginViewHolder, position: Int) {
        if (position == currentList?.size) {
            holder.itemView.element_plugin_name.text = context.getString(R.string.plugin_loading)
            holder.itemView.element_plugin_screenshot.visibility = View.GONE
            holder.itemView.setOnClickListener(null)
        } else {
            getItem(position).let { plugin ->
                holder.itemView.element_plugin_name.text = plugin!!.name
                holder.itemView.element_plugin_screenshot.visibility = View.VISIBLE
                Glide.with(context)
                    .applyDefaultRequestOptions(
                        RequestOptions()
                            .centerCrop()
                            .placeholder(R.drawable.ic_image_placeholder)
                            .error(R.drawable.ic_image_error)
                    )
                    .load(plugin.screenshots.screenshots.firstOrNull()?.src)
                    .into(holder.itemView.element_plugin_screenshot)
                holder.itemView.setOnClickListener { onPluginClicked?.invoke(plugin) }
            }
        }
    }

    override fun getItemCount(): Int {
        return if (currentList.isNullOrEmpty().not()) {
            currentList?.size?.plus(1)?:0
        } else {
            0
        }
    }

    inner class PluginViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    companion object {
        private val DIFF_CALLBACK = object :
            DiffUtil.ItemCallback<Plugin>() {
            override fun areItemsTheSame(
                oldPlugin: Plugin,
                newPlugin: Plugin
            ): Boolean =
                oldPlugin.hashCode() == newPlugin.hashCode()

            override fun areContentsTheSame(
                oldPlugin: Plugin,
                newPlugin: Plugin
            ): Boolean =
                oldPlugin == newPlugin
        }
    }
}