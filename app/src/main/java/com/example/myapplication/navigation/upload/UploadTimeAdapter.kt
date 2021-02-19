package com.example.myapplication.navigation.upload

import android.annotation.SuppressLint
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.data.datasource.remote.api.RecipeDTO

class UploadTimeAdapter(
    val timeList: ArrayList<RecipeDTO.Time>,
    var saveTime: String?,
    val itemClick: (Int) -> Unit
) :
    RecyclerView.Adapter<UploadTimeAdapter.UploadTimeHolder>() {

    private var currentPosition = -1

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UploadTimeAdapter.UploadTimeHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.time_list_item, parent, false)
        return UploadTimeAdapter.UploadTimeHolder(view)
    }

    override fun getItemCount(): Int {
        return timeList.size
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: UploadTimeAdapter.UploadTimeHolder, position: Int) {
        holder.name.text = timeList[position].timeName

        holder.itemView.setOnClickListener {
            currentPosition = position
            saveTime = timeList[position].timeName
            Log.d("savetime", saveTime + " savetime")
            notifyDataSetChanged()
        }

        if (currentPosition == position) {
            itemClick(currentPosition)

            saveTime = timeList[position].timeName
            Log.d("savetime", saveTime + " savetime")
            holder.name.setTextColor(Color.parseColor("#FF7051"))
        } else {
            holder.name.setTextColor(Color.parseColor("#8E8E93"))
        }
    }

    class UploadTimeHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name = itemView.findViewById<TextView>(R.id.tv_time_name)
    }
}