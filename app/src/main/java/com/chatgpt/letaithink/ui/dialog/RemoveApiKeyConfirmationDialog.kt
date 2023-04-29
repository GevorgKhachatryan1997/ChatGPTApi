package com.chatgpt.letaithink.ui.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.chatgpt.letaithink.R

class RemoveApiKeyConfirmationDialog : DialogFragment() {

    companion object {

        val TAG = RemoveApiKeyConfirmationDialog::class.simpleName

        fun newInstance() = RemoveApiKeyConfirmationDialog()
    }

    private var listener: Listener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        listener = (parentFragment ?: requireActivity()) as? Listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.remove_api_key))
            .setMessage(getString(R.string.remove_api_key_confirmation_message))
            .setPositiveButton(R.string.remove) { _, _ ->
                listener?.onRemoveApiKey()
            }
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.cancel()
            }.create()
    }

    fun show(fragmentManager: FragmentManager) {
        show(fragmentManager, TAG)
    }

    fun interface Listener {
        fun onRemoveApiKey()
    }
}