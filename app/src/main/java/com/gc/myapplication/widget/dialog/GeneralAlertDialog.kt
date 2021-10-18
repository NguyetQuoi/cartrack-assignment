package com.gc.myapplication.widget.dialog

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.SpannableString
import android.text.Spanned
import android.view.View
import androidx.annotation.StringRes
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.gc.myapplication.R
import com.gc.myapplication.base.BaseDialogFragment
import com.gc.myapplication.databinding.DialogGeneralAlertBinding

/**
 * General class for alert dialog
 * @author n.quoi
 * @date 10.18.2021
 */

open class GeneralAlertDialog : BaseDialogFragment<DialogGeneralAlertBinding>() {

    var title = ObservableField<CharSequence>()
    var message = ObservableField<CharSequence>()
    var negativeText = ObservableInt(android.R.string.cancel)
    var positiveText = ObservableInt()

    override val layoutId: Int
        get() = R.layout.dialog_general_alert

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            title.set(resourceProvider.getString(it.getInt(TITLE_EXTRA)))

            if (it.getBoolean(INT_TYPE_EXTRA)) {
                message.set(resourceProvider.getString(it.getInt(MESSAGE_EXTRA)))
            } else {
                val sms = it.getString(MESSAGE_EXTRA)
                if (it.getBoolean(LOAD_HTML_EXTRA)) {
                    message.set(fromHtml(sms))
                } else {
                    message.set(sms)
                }
            }

            positiveText.set(it.getInt(POSITIVE_EXTRA))

            val negativeId = it.getInt(NEGATIVE_EXTRA)
            if (negativeId == 0)
                binder.btCancel.visibility = View.GONE
            else {
                negativeText.set(negativeId)
                binder.btCancel.setOnClickListener { dismiss() }
            }
        }
    }


    @Suppress("DEPRECATION")
    private fun fromHtml(html: String?): Spanned {
        return when {
            html == null ->
                SpannableString("")
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ->
                Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY)
            else -> Html.fromHtml(html)
        }
    }

    /**
     * Companion obj for General Alert Dialog
     */
    companion object {

        const val TITLE_EXTRA = "title"
        const val MESSAGE_EXTRA = "message"
        const val NEGATIVE_EXTRA = "negative"
        const val POSITIVE_EXTRA = "positive"
        const val INT_TYPE_EXTRA = "int_type"
        const val LOAD_HTML_EXTRA = "load_html"

        /**
         * New instance for dialog
         * @param title resource string id of title
         * @param message resource string id of message
         * @param positiveText resource string id of positiveText
         * @param negativeText resource string id of negativeText
         * @return GeneralAlertDialog
         */
        fun newInstance(@StringRes title: Int, @StringRes message: Int, @StringRes positiveText: Int,
                        @StringRes negativeText: Int = 0): GeneralAlertDialog {
            val args = Bundle()
            val fragment = GeneralAlertDialog()

            args.putInt(TITLE_EXTRA, title)
            args.putInt(MESSAGE_EXTRA, message)
            args.putInt(NEGATIVE_EXTRA, negativeText)
            args.putInt(POSITIVE_EXTRA, positiveText)
            args.putBoolean(INT_TYPE_EXTRA, true)
            fragment.arguments = args

            return fragment
        }

        /**
         * New instance for dialog
         * @param title resource string id of title
         * @param message resource string id of message
         * @param positiveText resource string id of positiveText
         * @param negativeText resource string id of negativeText
         * @param needLoadWithHtml [Boolean]
         * @return GeneralAlertDialog
         */
        fun newInstance(@StringRes title: Int, message: String, @StringRes positiveText: Int,
                        @StringRes negativeText: Int = 0, needLoadWithHtml: Boolean = false): GeneralAlertDialog {
            val args = Bundle()
            val fragment = GeneralAlertDialog()
            args.putInt(TITLE_EXTRA, title)
            args.putString(MESSAGE_EXTRA, message)
            args.putInt(NEGATIVE_EXTRA, negativeText)
            args.putInt(POSITIVE_EXTRA, positiveText)
            args.putBoolean(INT_TYPE_EXTRA, false)
            args.putBoolean(LOAD_HTML_EXTRA, needLoadWithHtml)
            fragment.arguments = args
            return fragment
        }
    }
}
