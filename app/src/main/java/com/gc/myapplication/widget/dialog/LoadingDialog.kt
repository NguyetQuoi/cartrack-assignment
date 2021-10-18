package com.gc.myapplication.widget.dialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import com.gc.myapplication.R

/**
 * A loading dialog: show it when app is doing a background task, and UI-main-thread have to wait the task complete
 * @author n.quoi
 * @date 10.17.2021
 */

class LoadingDialog : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.window?.let {
            it.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            val view = inflater.inflate(R.layout.dialog_loading, container, false)
            it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            return view
        }
        return null
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }
}
