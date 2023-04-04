package com.chatgpt.letaithink.ui.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.chatgpt.letaithink.R

class ExceededYourQuota : DialogFragment() {

    companion object {
        val TAG = ExceededYourQuota::class.simpleName

        private const val ARG_MESSAGE = "arg.message"

        fun newInstance(message: String): ExceededYourQuota {
            return ExceededYourQuota().apply {
                arguments = bundleOf(
                    ARG_MESSAGE to message
                )
            }
        }
    }

    private var listener: Listener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        listener = (parentFragment ?: requireActivity()) as? Listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.quota_exceeded)
            .setMessage(requireArguments().getString(ARG_MESSAGE))
            .setPositiveButton(android.R.string.ok) { _, _ ->
                dismiss()
            }
            .setNegativeButton(R.string.check) { _, _ ->
                listener?.onCheckPlans()
            }
            .create()
    }

    fun show(fragmentManager: FragmentManager) {
        show(fragmentManager, TAG)
    }

    fun interface Listener {
        fun onCheckPlans()
    }
}