package com.example.mailinglist.view.adapter

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.mailinglist.R


class ImageGalleryAdapter(private val images: List<Bitmap>) :
    RecyclerView.Adapter<ImageGalleryAdapter.ViewHolder>() {

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val imageView: ImageView = itemView.findViewById(R.id.galleryItemImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.image_gallery_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val image: Bitmap = images[position]
        val imageView = holder.imageView
        imageView.setImageBitmap(image)
    }

    override fun getItemCount(): Int {
        return images.count()
    }
}
