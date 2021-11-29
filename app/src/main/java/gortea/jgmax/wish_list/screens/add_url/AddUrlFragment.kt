package gortea.jgmax.wish_list.screens.add_url

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import gortea.jgmax.wish_list.databinding.FragmentAddUrlBinding
import gortea.jgmax.wish_list.mvi.view.AppFragment
import gortea.jgmax.wish_list.screens.add_url.action.AddUrlViewAction
import gortea.jgmax.wish_list.screens.add_url.event.AddUrlViewEvent
import gortea.jgmax.wish_list.screens.add_url.state.AddUrlViewState

@AndroidEntryPoint
class AddUrlFragment :
    AppFragment<AddUrlViewModel, AddUrlViewState, AddUrlViewEvent, AddUrlViewAction>() {
    private var _binding: FragmentAddUrlBinding? = null
    private val binding: FragmentAddUrlBinding
        get() = requireNotNull(_binding)

    override val viewModel: AddUrlViewModel by viewModels()

    override fun renderAction(action: AddUrlViewAction) {
        when (action) {
            is AddUrlViewAction.ShowError -> {
                showError(action.message)
            }
        }
    }

    override fun renderState(state: AddUrlViewState) {
        binding.apply {
            loadingPb.isVisible = state.isLoading
            loadingPb.progress = state.loadingProgress
            pageIv.isVisible = !state.isLoading
            state.bitmap?.let { pageIv.setImageBitmap(it) }
        }
        Log.e("state", state.toString())
    }

    override fun provideView(inflater: LayoutInflater, container: ViewGroup?): View {
        _binding = FragmentAddUrlBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        event(AddUrlViewEvent.AddUrl("https://kazanexpress.ru/product/Bryuki-polukombinezon-Sela-zimnie-900622"))
    }

    private fun showError(@StringRes message: Int) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}