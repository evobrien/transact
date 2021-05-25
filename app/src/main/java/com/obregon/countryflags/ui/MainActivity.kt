package com.obregon.countryflags.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.obregon.countryflags.R
import com.obregon.countryflags.databinding.ActivityMainBinding
import com.obregon.countryflags.ui.image_search.FindFlagScreen
import com.obregon.countryflags.ui.saved_image.SavedFlagScreen
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val titles = arrayOf("Search", "Saved")
    private lateinit var binding: ActivityMainBinding
    private lateinit var pagerAdapter: FlagTabsPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(this.layoutInflater)
        setContentView(binding.root)

        pagerAdapter = FlagTabsPagerAdapter(this.supportFragmentManager, this.lifecycle)
        binding.pager.adapter = pagerAdapter

        TabLayoutMediator(binding.tabLayout, binding.pager) { tab, position ->
            tab.text = titles[position]
        }.attach()
    }


    private fun setupActionBar() {
        Timber.i("BackstackEntryCount ${this.supportFragmentManager.backStackEntryCount}")
        if (this.supportFragmentManager.backStackEntryCount > 0) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            this.supportActionBar?.setDisplayShowHomeEnabled(false)

        } else {
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
            this.supportActionBar?.setIcon(R.drawable.ic_drawer)
            this.supportActionBar?.setHomeButtonEnabled(true)
            this.supportActionBar?.setDisplayShowHomeEnabled(true)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        setupActionBar()
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                this.onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }
}

class FlagTabsPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(
        fragmentManager,
        lifecycle
    ) {
    companion object {
        private const val TAB_COUNT = 2
        private const val SEARCH_FLAG_TAB = 0
    }

    override fun getItemCount(): Int = TAB_COUNT

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            SEARCH_FLAG_TAB -> FindFlagScreen()
            else -> SavedFlagScreen()
        }
    }

}