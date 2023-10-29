package ru.netology.social_network.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import ru.netology.social_network.R
import ru.netology.social_network.model.MapModel

class MapAdapter(var mCtx: Context, var resource: Int, var items: List<MapModel>) :
    ArrayAdapter<MapModel>(mCtx, resource, items) {
    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val LayoutInflater: LayoutInflater = LayoutInflater.from(mCtx)
        val view: View = LayoutInflater.inflate(resource, null)
        val textView: TextView = view.findViewById(R.id.button_location)
        val lat: TextView = view.findViewById(R.id.lat)
        val lon: TextView = view.findViewById(R.id.lon)
        val mItems: MapModel = items[position]
        textView.text = mItems.title
        lat.text = mItems.lat
        lon.text = mItems.lon
        return view
    }
}