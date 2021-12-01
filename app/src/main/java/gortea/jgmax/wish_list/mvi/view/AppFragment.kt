package gortea.jgmax.wish_list.mvi.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

abstract class AppFragment<VM : AppFragmentViewModel<VS, VE, VA, *, *, *>, VS : ViewState, VE : ViewEvent, VA : ViewAction> :
    Fragment() {
    protected abstract val viewModel: VM

    protected val state: VS
        get() = viewModel.stateFlow.value

    protected abstract fun renderAction(action: VA)
    protected abstract fun renderState(state: VS)

    protected abstract fun provideView(inflater: LayoutInflater, container: ViewGroup?): View

    protected fun applyEvent(event: VE) {
        viewModel.handleEvent(event)
    }

    protected fun applyState(state: VS) {
        viewModel.handleState(state)
    }

    @CallSuper
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = provideView(inflater, container)
        lifecycleScope.launch {
            launch {
                viewModel.actionFlow
                    .filterNotNull()
                    .onEach { renderAction(it) }
                    .collect()
            }
            launch {
                viewModel.stateFlow
                    .filterNotNull()
                    .onEach { renderState(it) }
                    .collect()
            }
        }
        return view
    }
}