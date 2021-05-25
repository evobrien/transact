package com.obregon.countryflags.ui.common

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import com.obregon.countryflags.R
import com.obregon.countryflags.domain.usecase.FlagData
import java.io.File

class FlagDataRecyclerAdapter(
    private val flagData: List<FlagData>,
    private val showSave: Boolean = false,
    private val saveImage: SaveImage? = null
) : RecyclerView.Adapter<FlagDataRecyclerAdapter.FlagDataViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlagDataViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.saved_image_row, parent, false)
        return FlagDataViewHolder(view)
    }

    override fun onBindViewHolder(holder: FlagDataViewHolder, position: Int) {
        val flagData = flagData[position]
        holder.apply {
            ivFlag.load(File(flagData.imagePath))
            tvCountryName.text = flagData.countryName
            tvCountryCode.text = flagData.countryCode
            if (showSave) {
                btnSave.visibility = View.VISIBLE
                btnSave.setOnClickListener { saveImage?.saveImage() }
            }
        }
    }

    override fun getItemCount(): Int {
        return flagData.size
    }

    class FlagDataViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivFlag: ImageView = view.findViewById(R.id.iv_flag)
        val tvCountryName: TextView = view.findViewById(R.id.tv_country_name)
        val tvCountryCode: TextView = view.findViewById(R.id.tv_country_code)
        val btnSave: TextView = view.findViewById(R.id.btn_save)
    }

}