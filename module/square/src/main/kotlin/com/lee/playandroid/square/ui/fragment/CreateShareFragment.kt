package com.lee.playandroid.square.ui.fragment

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.navigation.fragment.findNavController
import com.lee.library.base.BaseNavigationFragment
import com.lee.library.dialog.LoadingDialog
import com.lee.library.extensions.binding
import com.lee.library.extensions.dismiss
import com.lee.library.extensions.show
import com.lee.library.extensions.toast
import com.lee.library.tools.KeyboardTools.parentTouchHideSoftInput
import com.lee.library.viewstate.collectState
import com.lee.playandroid.square.R
import com.lee.playandroid.square.databinding.FragmentCreateShareBinding
import com.lee.playandroid.square.viewmodel.CreateShareViewAction
import com.lee.playandroid.square.viewmodel.CreateShareViewEvent
import com.lee.playandroid.square.viewmodel.CreateShareViewModel
import com.lee.playandroid.square.viewmodel.CreateShareViewState
import kotlinx.coroutines.flow.collect

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
                val title = binding.editShareTitle.text.toString()
                val content = binding.editShareContent.text.toString()
                viewModel.dispatch(CreateShareViewAction.RequestSend(title, content))
            }
            return@setOnEditorActionListener false
        }
    }

    override fun LifecycleCoroutineScope.bindData() {
        launchWhenResumed {
            viewModel.viewEvents.collect { event ->
                when (event) {
                    is CreateShareViewEvent.SendSuccess -> {
                        toast(event.message)
                        dismiss(loadingDialog)
                        setFragmentResult(MyShareFragment.REQUEST_KEY_REFRESH, Bundle.EMPTY)
                        findNavController().popBackStack()
                    }
                    is CreateShareViewEvent.SendFailed -> {
                        toast(event.error.message)
                        dismiss(loadingDialog)
                    }
                }
            }
        }

        launchWhenResumed {
            viewModel.viewStates.collectState(CreateShareViewState::loading) {
                if (it) show(loadingDialog)
            }
        }
    }
}