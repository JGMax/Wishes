package gortea.jgmax.wish_list.screens.wish_list

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import androidx.work.WorkManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import gortea.jgmax.wish_list.R
import gortea.jgmax.wish_list.app.data.repository.models.wish.WishModel
import gortea.jgmax.wish_list.databinding.FragmentWishListBinding
import gortea.jgmax.wish_list.mvi.view.AppFragment
import gortea.jgmax.wish_list.screens.wish_list.action.WishListViewAction
import gortea.jgmax.wish_list.screens.wish_list.data.WishData
import gortea.jgmax.wish_list.screens.wish_list.event.WishListViewEvent
import gortea.jgmax.wish_list.screens.wish_list.list.adapter.WishListAdapter
import gortea.jgmax.wish_list.screens.wish_list.list.helper.SwipeHelper
import gortea.jgmax.wish_list.screens.wish_list.list.item.WishDataWrapper
import gortea.jgmax.wish_list.screens.wish_list.state.WishListViewState


@AndroidEntryPoint
class WishListFragment :
    AppFragment<WishListViewModel, WishListViewState, WishListViewEvent, WishListViewAction>() {
    private var _binding: FragmentWishListBinding? = null
    private val binding: FragmentWishListBinding
        get() = requireNotNull(_binding)
    private val footerItem = WishDataWrapper(
        data = WishData.Empty,
        onClick = { },
        viewType = R.layout.item_footer
    )

    private val adapter = WishListAdapter()

    override val viewModel: WishListViewModel by viewModels()

    override fun renderAction(action: WishListViewAction) {
        when (action) {
            is WishListViewAction.OpenUrlInBrowser -> {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(action.url)
                startActivity(intent)
            }
            is WishListViewAction.WishDeleted -> {
                restoreItemSnackbar(action.wish)
            }
            is WishListViewAction.EnqueueWork -> {
                WorkManager
                    .getInstance(requireContext())
                    .enqueue(action.request)
                binding.refreshLayout.isRefreshing = false
                showMessage(R.string.refresh_started)
            }
        }
    }

    override fun renderState(state: WishListViewState) {
        val list = state.list.map {
            WishDataWrapper(
                data = it,
                onClick = { item -> applyEvent(WishListViewEvent.OnItemWishClick(item.url)) },
                viewType = R.layout.item_wish
            )
        }
        adapter.submitList(if (list.isNotEmpty()) list + footerItem else list)
    }

    override fun provideView(inflater: LayoutInflater, container: ViewGroup?): View {
        _binding = FragmentWishListBinding.inflate(inflater, container, false)
        binding.wishesRv.let {
            it.adapter = adapter
            it.layoutManager = LinearLayoutManager(context)
        }
        initSwipeToDelete()
        return binding.root
    }

    private fun initSwipeToDelete() {
        val swipeToDeleteCallback = SwipeHelper {
            applyEvent(WishListViewEvent.OnDeleteWishClick(adapter.get(it).data.url))
        }
        ItemTouchHelper(swipeToDeleteCallback).attachToRecyclerView(binding.wishesRv)
    }

    private fun restoreItemSnackbar(item: WishModel) {
        Snackbar.make(
            binding.root,
            R.string.item_removed,
            Snackbar.LENGTH_LONG
        ).setAction(R.string.undo) {
            applyEvent(WishListViewEvent.AddItem(item))
        }.show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            refreshLayout.setOnRefreshListener { applyEvent(WishListViewEvent.RefreshList) }
            addFab.setOnClickListener { applyEvent(WishListViewEvent.OnAddWishClick) }
            preferenceBtn.setOnClickListener {
                startSettingsAnimation(it)
                applyEvent(WishListViewEvent.OnPreferencesClick)
            }
        }
    }

    private fun startSettingsAnimation(view: View) {
        val material = (view as? MaterialButton)
        val vectorIcon =
            AnimatedVectorDrawableCompat.create(view.context, R.drawable.animated_settings)
        material?.icon = vectorIcon
        vectorIcon?.start()
    }

    private fun showMessage(@StringRes message: Int) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
