package gortea.jgmax.wish_list.screens.preference

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.preference.PreferenceFragmentCompat
import gortea.jgmax.wish_list.R

class PreferenceFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.fragment_preference, rootKey)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        val color = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requireContext().getColor(R.color.background_color)
        } else {
            resources.getColor(R.color.background_color)
        }

        view?.setBackgroundColor(color)
        return view
    }
}
