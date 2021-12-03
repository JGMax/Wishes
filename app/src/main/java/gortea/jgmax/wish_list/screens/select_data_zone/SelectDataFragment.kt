package gortea.jgmax.wish_list.screens.select_data_zone

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import gortea.jgmax.wish_list.databinding.FragmentSelectDataZoneBinding
import gortea.jgmax.wish_list.mvi.view.AppFragment
import gortea.jgmax.wish_list.screens.select_data_zone.action.SelectDataViewAction
import gortea.jgmax.wish_list.screens.select_data_zone.event.SelectDataViewEvent
import gortea.jgmax.wish_list.screens.select_data_zone.state.SelectDataViewState
import gortea.jgmax.wish_list.screens.select_data_zone.view.SelectableImageView


@AndroidEntryPoint
class SelectDataFragment :
    AppFragment<SelectDataViewModel, SelectDataViewState, SelectDataViewEvent, SelectDataViewAction>() {
    private var _binding: FragmentSelectDataZoneBinding? = null
    private val binding: FragmentSelectDataZoneBinding
        get() = requireNotNull(_binding)

    private val selectionListener = object : SelectableImageView.SelectionListener {
        override fun inProcess(
            bitmap: Bitmap?,
            position: SelectableImageView.SelectedPosition
        ) {
            applyState(state.copy(selectedPosition = position))
        }

        override fun onComplete(
            bitmap: Bitmap?,
            position: SelectableImageView.SelectedPosition
        ) {
            bitmap?.let { applyEvent(SelectDataViewEvent.RecognizeText(it, position)) }
        }
    }

    override val viewModel: SelectDataViewModel by viewModels()

    override fun renderAction(action: SelectDataViewAction) {
        when (action) {
            is SelectDataViewAction.ShowMessage -> {
                showError(action.message)
            }
        }
    }

    override fun renderState(state: SelectDataViewState) {
        _binding?.apply {
            loadingPb.isVisible = state.isLoading
            loadingPb.progress = state.loadingProgress

            pageIv.isVisible = !state.isLoading
            state.bitmap?.let {
                Log.e("bitmap", it.toString())
                pageIv.setImageBitmap(it)
            }
            if (state.isSelectionActive) {
                pageIv.enableSelection()
            } else {
                pageIv.disableSelection()
            }

            resultTv.text = state.recognitionResultText
            applyFab.setImageResource(state.fabResource)
            applyFab.isEnabled = state.isFabEnabled
        }
    }

    override fun provideView(inflater: LayoutInflater, container: ViewGroup?): View {
        _binding = FragmentSelectDataZoneBinding.inflate(inflater, container, false)
        binding.pageIv.attachBitmapStorage(viewModel.bitmapStorage)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        handleInitialState()
        handleBackPressed()
        binding.apply {
            pageIv.setSelectionListener(selectionListener)
            applyFab.setOnClickListener { applyEvent(SelectDataViewEvent.OnFABClick) }
        }
    }

    private fun handleInitialState() {
        val url = arguments?.getString(SelectDataViewModel.URL_ARG)
        val isLoading = arguments?.getBoolean(SelectDataViewModel.IS_LOADING_ARG) ?: false
        val progress = arguments?.getInt(SelectDataViewModel.LOADING_PROGRESS_ARG) ?: 0
        if (url == null) {
            applyEvent(SelectDataViewEvent.UrlIsNull)
        } else {
            if (state.url != url) {
                applyState(
                    state.copy(
                        url = url,
                        isLoading = isLoading,
                        loadingProgress = progress
                    )
                )
            }
            applyEvent(SelectDataViewEvent.LoadUrl)
        }
    }

    private fun handleBackPressed() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                applyEvent(SelectDataViewEvent.OnBackPressed)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    private fun showError(@StringRes message: Int) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}