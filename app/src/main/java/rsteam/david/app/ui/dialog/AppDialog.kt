package rsteam.david.app.ui.dialog

import android.content.Context
import android.view.Window
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatDialog
import kotlinx.android.synthetic.main.element_app_dialog.*
import rsteam.david.app.R

class AppDialog(context: Context) : AppCompatDialog(context) {

    init {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.element_app_dialog)
    }

    fun title(@StringRes title: Int): AppDialog {
        return title(context.getString(title))
    }

    fun title(title: String): AppDialog {
        app_dialog_title.text = title
        return this
    }

    fun positiveButton(@StringRes positiveText: Int, onPositiveClick: ((AppDialog) -> Unit)? = null): AppDialog {
        app_dialog_positive.text = context.getString(positiveText)
        app_dialog_positive.setOnClickListener {
            onPositiveClick?.invoke(this)
        }
        return this
    }

    fun dismiss(onDismissClick: (AppDialog) -> Unit): AppDialog {
        setOnDismissListener { onDismissClick(this) }
        return this
    }

    fun getInput() : Int? {
        if (app_dialog_input.text.isNullOrBlank().not()) {
            return app_dialog_input.text.toString().toInt()
        }
        return null
    }

}
