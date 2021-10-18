package com.gc.myapplication.widget.dialog

import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.gc.myapplication.R
import com.gc.myapplication.base.BaseDialogFragment
import com.gc.myapplication.databinding.DialogErrorBinding

/**
 * Error dialog
 * @author n.quoi
 * @date 10.18.2021
 */

class ErrorDialog : BaseDialogFragment<DialogErrorBinding>() {
    override val layoutId = R.layout.dialog_error

    var message = ObservableField<CharSequence>()
    var negativeText = ObservableInt(R.string.resend)

    companion object {
        private const val MESSAGE_EXTRA = "message"
        private const val NEGATIVE_EXTRA = "negative"

        /**
         * New instance of [ErrorDialog]
         * @param sms error sms
         * @param negativeId negative action text
         * @return [ErrorDialog]
         */
        fun newInstance(sms: String, @StringRes negativeId: Int): ErrorDialog {
            val args = Bundle()

            val fragment = ErrorDialog()
            args.putString(MESSAGE_EXTRA, sms)
            args.putInt(NEGATIVE_EXTRA, negativeId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            message.set(it.getString(MESSAGE_EXTRA))

            val negativeId = it.getInt(NEGATIVE_EXTRA)
            if (negativeId == 0)
                binder.btCancel.visibility = View.GONE
            else {
                negativeText.set(negativeId)
            }
        }
    }
}