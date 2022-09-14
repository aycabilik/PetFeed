package com.example.petfeed.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.petfeed.databinding.RowLocationBinding
import com.example.petfeed.models.ModelPlaces
import com.google.android.material.imageview.ShapeableImageView
import java.lang.Exception

class AdapterLocation :RecyclerView.Adapter<AdapterLocation.HolderLocations>{

    private val context: Context
    private val locationArrayList: ArrayList<ModelPlaces>

    private lateinit var binding: RowLocationBinding

    constructor(context: Context, locationArrayList: ArrayList<ModelPlaces>) {
        this.context = context
        this.locationArrayList = locationArrayList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderLocations {
        binding = RowLocationBinding.inflate(LayoutInflater.from(context), parent, false)

        return HolderLocations(binding.root)
    }

    override fun onBindViewHolder(holder: HolderLocations, position: Int) {

        val model = locationArrayList[position]
        val id = model.id
        val street = model.street
        val state = model.state
        val country = model.country
        val placeImage = model.placeImage
        val timestamp = model.timestamp
        val uid = model.uid

        holder.streetTv.text = street
        holder.stateText.text = state
        holder.countryText.text = country

        if (placeImage.equals("")){
            holder.locationPhotoRow.visibility = View.GONE
        }
        else{
            try {
                Glide.with(context)
                    .load(placeImage)
                    .into(holder.locationPhotoRow)
            }
            catch (e: Exception){

            }
        }

    }



    override fun getItemCount(): Int {
        return locationArrayList.size
    }


    inner class HolderLocations(itemView: View):RecyclerView.ViewHolder(itemView){

        var streetTv: TextView = binding.streetTv
        var stateText: TextView = binding.stateText
        var countryText: TextView = binding.countryText
        var locationPhotoRow: ShapeableImageView = binding.locationPhotoRow

    }



}