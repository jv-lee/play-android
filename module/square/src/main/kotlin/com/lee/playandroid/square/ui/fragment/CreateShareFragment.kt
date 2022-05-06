package com.lee.playandroid.square.ui.fragment

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.lee.library.base.BaseNavigationFragment
import com.lee.library.dialog.LoadingDialog
import com.lee.library.extensions.binding
import com.lee.library.extensions.dismiss
import com.lee.library.extensions.show
import com.lee.library.extensions.toast
import com.lee.library.viewstate.observeState
import com.lee.library.tools.KeyboardTools.parentTouchHideSoftInput
import com.lee.playandroid.square.R
import com.lee.playandroid.square.databinding.FragmentCreateShareBinding
import com.lee.playandroid.square.viewmodel.CreateShareViewModel

/**
 * @author jv.lee
 * @date 2021/12/13
 * @description 创建分享页面
 */
class CreateShareFragment : BaseNavigationFragment(R.layout.fragment_create_share) {

    private val viewModel by viewModels<CreateShareViewModel>()

    private val binding by binding(FragmentCreateShareBinding::bind)

    private val loadingDialog by lazy { LoadingDialog(requireContext()) }

    override fun bindView() {
        requireActivity().parentTouchHideSoftInput(binding.root)

        binding.editShareContent.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                viewModel.requestSendShare(
                    binding.editShareTitle.text.toString(),
                    binding.editShareContent.text.toString()
                )
            }
            return@setOnEditorActionListener false
        }
    }

    override fun bindData() {
        viewModel.sendLive.observeState<String>(viewLifecycleOwner, success = {
            toast(it)
            dismiss(loadingDialog)
            setFragmentResult(MyShareFragment.REQUEST_KEY_REFRESH, Bundle.EMPTY)
            findNavController().popBackStack()
        }, error = {
            toast(it.message)
            dismiss(loadingDialog)
        }, loading = {
            show(loadingDialog)
        })
    }
}