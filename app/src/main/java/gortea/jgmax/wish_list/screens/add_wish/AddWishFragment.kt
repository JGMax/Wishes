package gortea.jgmax.wish_list.screens.add_wish

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import gortea.jgmax.wish_list.databinding.FragmentAddWishBinding
import gortea.jgmax.wish_list.mvi.view.AppFragment
import gortea.jgmax.wish_list.screens.add_wish.action.AddWishViewAction
import gortea.jgmax.wish_list.screens.add_wish.data.StringWrapper
import gortea.jgmax.wish_list.screens.add_wish.event.AddWishViewEvent
import gortea.jgmax.wish_list.screens.add_wish.state.AddWishViewState
import gortea.jgmax.wish_list.screens.extensions.setReadOnly
import gortea.jgmax.wish_list.screens.extensions.setTextIfNoFocus
import gortea.jgmax.wish_list.screens.extensions.toEditable


@AndroidEntryPoint
class AddWishFragment :
    AppFragment<AddWishViewModel, AddWishViewState, AddWishViewEvent, AddWishViewAction>() {
    private var _binding: FragmentAddWishBinding? = null
    private val binding: FragmentAddWishBinding
        get() = requireNotNull(_binding)

    private val urlWrapper = StringWrapper()
    private val titleWrapper = StringWrapper()
    private val targetPriceWrapper = StringWrapper()

    override val viewModel: AddWishViewModel by viewModels()

    override fun renderAction(action: AddWishViewAction) {
        when (action) {
            is AddWishViewAction.ShowMessage -> {
                showMessage(action.message)
            }
        }
    }

    private fun showMessage(@StringRes message: Int) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun renderState(state: AddWishViewState) {
        _binding?.apply {
            urlInput.setTextIfNoFocus(state.wish.url)
            titleInput.setTextIfNoFocus(state.wish.title)
            targetPriceInput.setTextIfNoFocus(state.wish.targetPrice)
            currentPriceValue.text = state.getCurrentPriceValue(resources)
            currentPriceHint.visibility = state.currentPriceHintVisibility

            acceptBtn.isEnabled = state.isAcceptButtonEnabled
            acceptBtn.setText(state.acceptButtonText)

            urlAcceptedGroup.isVisible = state.isFieldsVisible

            loadingPb.isVisible = state.isLoading
            loadingPb.progress = state.loadingProgress
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        applyEvent(AddWishViewEvent.LoadUrl)
        _binding?.apply {
            urlInput.addTextChangedListener(TextChangeListener(urlWrapper))
            titleInput.addTextChangedListener(TextChangeListener(titleWrapper))
            targetPriceInput.addTextChangedListener(TextChangeListener(targetPriceWrapper))
            targetPriceInput.transformationMethod = NumericKeyBoardTransformation()
            currentPriceLayout.setOnClickListener { applyEvent(AddWishViewEvent.OnPriceSelectionClick) }
            acceptBtn.setOnClickListener { applyEvent(AddWishViewEvent.OnAcceptClick) }
        }
    }

    override fun provideView(inflater: LayoutInflater, container: ViewGroup?): View {
        _binding = FragmentAddWishBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private inner class TextChangeListener(private val stringWrapper: StringWrapper) : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            stringWrapper.string = p0?.toString()
            val url = urlWrapper.string ?: state.wish.url
            val title = titleWrapper.string ?: state.wish.title
            val price = targetPriceWrapper.string ?: state.wish.targetPrice
            applyState(
                state.copy(
                    wish = state.wish.copy(
                        url = url,
                        title = title,
                        targetPrice = price
                    )
                )
            )
        }

        override fun afterTextChanged(p0: Editable?) {}
    }

    private class NumericKeyBoardTransformation : PasswordTransformationMethod() {
        override fun getTransformation(source: CharSequence, view: View): CharSequence {
            return source
        }
    }
}