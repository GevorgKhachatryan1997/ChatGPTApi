package com.chatgpt.letaithink.ui.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.chatgpt.letaithink.R

class InvalidApiKeyDialog : DialogFragment() {

    companion object {
        val TAG = InvalidApiKeyDialog::class.simpleName

        private const val ARG_MESSAGE = "arg.message"

        fun newInstance(message: String): InvalidApiKeyDialog {
            return InvalidApiKeyDialog().apply {
                arguments = bundleOf(
                    ARG_MESSAGE to message
                )
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        listener = (parentFragment ?: requireActivity()) as? Listener
    }

    private var listener: Listener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val message = StringBuilder()
            .append(getString(R.string.your_api_key_is_invalid))
            .append("\n\n")
            .append(requireArguments().getString(ARG_MESSAGE))

        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.error)
            .setMessage(message)
            .setPositiveButton(R.string.update) { _, _ ->
                listener?.onUpdateApiKey()
                dismiss()
            }
            .create()
    }

    fun show(fragmentManager: FragmentManager) {
        show(fragmentManager, TAG)
    }

    fun interface Listener {
        fun onUpdateApiKey()
    }
}