package gortea.jgmax.wish_list.screens.add_url

import android.os.Bundle
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
import gortea.jgmax.wish_list.screens.add_url.view.SelectableImageView

@AndroidEntryPoint
class AddUrlFragment :
    AppFragment<AddUrlViewModel, AddUrlViewState, AddUrlViewEvent, AddUrlViewAction>() {
    private var _binding: FragmentAddUrlBinding? = null
    private val binding: FragmentAddUrlBinding
        get() = requireNotNull(_binding)

    private val selectionListener = object : SelectableImageView.SelectionListener {

        override fun inProcess(
            view: SelectableImageView,
            position: SelectableImageView.SelectedPosition
        ) {
            applyState(state.copy(selectedPosition = position))
        }

        override fun onComplete(
            view: SelectableImageView,
            position: SelectableImageView.SelectedPosition
        ) {
            view.bitmap?.let { applyEvent(AddUrlViewEvent.RecognizeText(it, position)) }
        }
    }

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
            text.text = state.recognitionResult ?: ""
        }
    }

    override fun provideView(inflater: LayoutInflater, container: ViewGroup?): View {
        _binding = FragmentAddUrlBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        applyEvent(AddUrlViewEvent.AddUrl("https://www.wildberries.ru/catalog/13724854/detail.aspx?targetUrl=MI"))
        binding.pageIv.setSelectionListener(selectionListener)
        binding.reload.setOnClickListener {
            binding.pageSv.isScrollable = !binding.pageSv.isScrollable
            if (binding.pageSv.isScrollable) {
                binding.pageIv.disableSelection()
            } else {
                binding.pageIv.enableSelection()
            }
        }
    }

    private fun showError(@StringRes message: Int) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}