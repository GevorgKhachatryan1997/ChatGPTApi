package com.chatgpt.letaithink.ui.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.chatgpt.letaithink.R

class LogoutDialog : DialogFragment() {

    companion object {
        val TAG = LogoutDialog::class.simpleName
    }

    private var listener: Listener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        listener = (parentFragment ?: requireActivity()) as? Listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.logout)
            .setMessage(getString(R.string.logut_message))
            .setPositiveButton(R.string.logout) { _, _ -> listener?.onDialogLogout() }
            .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                dialog.cancel()
            }.create()
    }

    fun interface Listener {
        fun onDialogLogout()
    }
}