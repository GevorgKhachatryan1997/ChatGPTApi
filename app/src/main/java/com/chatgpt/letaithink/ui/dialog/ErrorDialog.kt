package com.chatgpt.letaithink.ui.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.chatgpt.letaithink.R

class ErrorDialog : DialogFragment() {

    companion object {
        val TAG = ErrorDialog::class.simpleName

        private const val ARG_MESSAGE = "arg.message"

        fun newInstance(message: String): ErrorDialog {
            return ErrorDialog().apply {
                arguments = bundleOf(
                    ARG_MESSAGE to message
                )
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val message = requireArguments().getString(ARG_MESSAGE)
        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.error)
            .setMessage(message)
            .setPositiveButton(R.string.ok) { _, _ -> dismiss() }
            .create()
    }

    fun show(fragmentManager: FragmentManager) {
        show(fragmentManager, TAG)
    }
}