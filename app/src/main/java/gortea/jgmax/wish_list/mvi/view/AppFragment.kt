package gortea.jgmax.wish_list.mvi.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

abstract class AppFragment<VM : AppFragmentViewModel<S, E, A, *, *, *>, S : ViewState, E : ViewEvent, A : ViewAction> :
    Fragment() {
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    protected abstract val viewModel: VM

    protected abstract fun renderAction(action: A)
    protected abstract fun renderState(state: S)

    protected abstract fun provideView(inflater: LayoutInflater, container: ViewGroup?): View

    protected fun event(event: E) {
        viewModel.handleEvent(event)
    }

    @CallSuper
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = provideView(inflater, container)
        viewModel.actionFlow
            .onEach { renderAction(it) }
            .launchIn(coroutineScope)
        viewModel.stateFlow
            .onEach { renderState(it) }
            .launchIn(coroutineScope)
        return view
    }

    @CallSuper
    override fun onDestroyView() {
        coroutineScope.cancel()
        super.onDestroyView()
    }
}