package com.chatgpt.letaithink.ui.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.chatgpt.letaithink.R

class DeleteChatConfirmationDialog : DialogFragment() {

    companion object {

        private const val ARG_SESSION_ID = "arg.session_id"

        val TAG = DeleteChatConfirmationDialog::class.simpleName

        fun newInstance(sessionId: String) = DeleteChatConfirmationDialog().apply {
            arguments = bundleOf(ARG_SESSION_ID to sessionId)
        }
    }

    private var listener: Listener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        listener = (parentFragment ?: requireActivity()) as? Listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.delete_chat))
            .setMessage(getString(R.string.chat_delete_confirmation_dialog_message))
            .setPositiveButton(R.string.delete) { _, _ ->
                requireArguments().getString(ARG_SESSION_ID)?.let {
                    listener?.onDialogChatDelete(it)
                }
            }
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.cancel()
            }.create()
    }

    fun show(fragmentManager: FragmentManager) {
        show(fragmentManager, TAG)
    }

    fun interface Listener {
        fun onDialogChatDelete(sessionId: String)
    }
}