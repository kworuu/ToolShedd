package com.example.toolshedd.screens.profile

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.toolshedd.R
import com.example.toolshedd.data.Tool

class ToolAdapter(
    context: Context,
    private val tools: ArrayList<Tool>
) : ArrayAdapter<Tool>(context, R.layout.item_tool, tools) {

    // ViewHolder pattern — avoids repeated findViewById calls while scrolling
    private class ViewHolder(view: View) {
        val tvToolName: TextView = view.findViewById(R.id.tvItemToolName)
        val tvToolBrand: TextView = view.findViewById(R.id.tvItemToolBrand)
        val tvToolStatus: TextView = view.findViewById(R.id.tvItemToolStatus)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val holder: ViewHolder
        val view: View

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_tool, parent, false)
            holder = ViewHolder(view)
            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as ViewHolder
        }

        val tool = tools[position]

        holder.tvToolName.text = tool.name
        holder.tvToolBrand.text = tool.brand

        // Style the status badge depending on value
        holder.tvToolStatus.text = tool.status
        when (tool.status) {
            "Available" -> {
                holder.tvToolStatus.setBackgroundResource(R.drawable.bg_badge_green)
                holder.tvToolStatus.setTextColor(Color.parseColor("#3D4E27"))
            }
            "On Loan" -> {
                holder.tvToolStatus.setBackgroundResource(R.drawable.bg_badge)
                holder.tvToolStatus.backgroundTintList =
                    android.content.res.ColorStateList.valueOf(Color.parseColor("#FFE6AC"))
                holder.tvToolStatus.setTextColor(Color.parseColor("#5C4033"))
            }
            else -> { // Unlisted
                holder.tvToolStatus.setBackgroundResource(R.drawable.bg_badge)
                holder.tvToolStatus.backgroundTintList =
                    android.content.res.ColorStateList.valueOf(Color.parseColor("#3D3E3A"))
                holder.tvToolStatus.setTextColor(Color.parseColor("#9E9E9E"))
            }
        }

        return view
    }
}
