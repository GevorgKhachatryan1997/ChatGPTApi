package com.chatgpt.letaithink.ui.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.chatgpt.letaithink.R

class SubscriptionExpiredDialog : DialogFragment() {

    companion object {
        val TAG = SubscriptionExpiredDialog::class.simpleName

        fun newInstance() = SubscriptionExpiredDialog()
    }

    private var listener: Listener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        listener = (parentFragment ?: requireActivity()) as? Listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.subscription)
            .setMessage(R.string.subscription_dialog_message)
            .setPositiveButton(R.string.ok) { _, _ ->
                dismiss()
            }
            .setNegativeButton(getString(R.string.subscribe)) { _, _ ->
                listener?.onDialogSubscribe()
            }
            .create()
    }

    fun show(fragmentManager: FragmentManager) {
        show(fragmentManager, TAG)
    }

    fun interface Listener {
        fun onDialogSubscribe()
    }
}