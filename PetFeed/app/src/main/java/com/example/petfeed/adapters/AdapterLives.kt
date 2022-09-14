package com.example.petfeed.adapters

import android.content.Context
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.petfeed.models.ModelLives
import com.example.petfeed.databinding.RowLiveBinding
import com.google.android.material.imageview.ShapeableImageView
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

class AdapterLives :RecyclerView.Adapter<AdapterLives.HolderLives>{

    private val context: Context
    private val livesArrayList: ArrayList<ModelLives>

    private lateinit var binding: RowLiveBinding

    constructor(context: Context, livesArrayList: ArrayList<ModelLives>) {
        this.context = context
        this.livesArrayList = livesArrayList
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderLives {

        binding = RowLiveBinding.inflate(LayoutInflater.from(context), parent, false)

        return HolderLives(binding.root)
    }

    override fun onBindViewHolder(holder: HolderLives, position: Int) {

        val model = livesArrayList[position]
        val id = model.id
        val name = model.name
        val text = model.text
        val liveImage = model.liveImage
        val uid = model.uid
        val timestamp = model.timestamp

        val formattedDate = formatTimeStamp(timestamp)

        holder.livesText.text = text
        holder.nameTv.text = name
        holder.time.text = formattedDate

        if (liveImage.equals("")){
            holder.livePhotoRow.visibility = View.GONE
        }
        else{
            try {
                Glide.with(context)
                    .load(liveImage)
                    .into(holder.livePhotoRow)

            }
            catch (e: Exception){

            }
        }

    }

    override fun getItemCount(): Int {
        return livesArrayList.size
    }

    inner class HolderLives(itemView: View):RecyclerView.ViewHolder(itemView){

        var nameTv:TextView = binding.nameTv
        var livesText:TextView = binding.livesText
        var livePhotoRow:ShapeableImageView = binding.livePhotoRow
        var time:TextView = binding.time
    }


    fun formatTimeStamp(timestamp: Long): String{
        val cal = Calendar.getInstance(Locale.ENGLISH)
        cal.timeInMillis = timestamp
        cal.add(Calendar.HOUR, 3)
        return DateFormat.format("dd/MM/yyyy HH:mm",cal).toString()
    }
}