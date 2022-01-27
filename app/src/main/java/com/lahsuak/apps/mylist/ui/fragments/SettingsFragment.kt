package com.lahsuak.apps.mylist.ui.fragments

import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.lahsuak.apps.mylist.BuildConfig
import com.lahsuak.apps.mylist.R
import com.lahsuak.apps.mylist.util.Util.appRating
import com.lahsuak.apps.mylist.util.Util.moreApp
import com.lahsuak.apps.mylist.util.Util.sendFeedbackMail
import com.lahsuak.apps.mylist.util.Util.shareApp

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        val prefFeedback = findPreference<Preference>("feedback")
        val prefShare = findPreference<Preference>("share")
        val prefMoreApp = findPreference<Preference>("more_app")
        val prefVersion = findPreference<Preference>("app_version")
        val prefRating = findPreference<Preference>("rating")
        val prefFont = findPreference<ListPreference>("font_size")

        prefVersion!!.summary = BuildConfig.VERSION_NAME
        val prefManager = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val txtSize = prefManager.getString("font_size","18").toString().toInt()
        when(txtSize){
            16 -> prefFont?.summary ="Small"
            18 -> prefFont?.summary ="Medium"
            20 -> prefFont?.summary ="Large"
            22 -> prefFont?.summary ="Huge"
            //  else -> prefFont.summary = "Medium"
        }

        prefFont?.setOnPreferenceChangeListener { _, newValue ->
            val size = (newValue as String).toInt()
            when(size){
                16 -> prefFont.summary ="Small"
                18 -> prefFont.summary ="Medium"
                20 -> prefFont.summary ="Large"
                22 -> prefFont.summary ="Huge"
              //  else -> prefFont.summary = "Medium"
            }
            true
        }
        prefFeedback?.setOnPreferenceClickListener {
            sendFeedbackMail(requireContext())
            true
        }
        prefShare?.setOnPreferenceClickListener {
            shareApp(requireContext())
            true
        }
        prefMoreApp?.setOnPreferenceClickListener {
            moreApp(requireContext())
            true
        }
        prefRating?.setOnPreferenceClickListener {
            appRating(requireContext())
            true
        }
    }
}