package com.example.mailinglist.view.adapter

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.mailinglist.Application
import com.example.mailinglist.CacheManager
import com.example.mailinglist.R


class ImageGalleryAdapter(private val images: List<String>) :
    RecyclerView.Adapter<ImageGalleryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.image_gallery_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val imageName: String = images[position]
        val cacheManager = CacheManager()
        val image: ByteArray? = cacheManager.retrieveData(Application.context, imageName)

        if (image != null) {
            val imageView = holder.imageView

            val bitmap =
                BitmapFactory.decodeByteArray(image, 0, image.size)
            imageView.setImageBitmap(
                bitmap
            )
        }
    }

    override fun getItemCount(): Int {
        return images.count()
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val imageView: ImageView = itemView.findViewById(R.id.galleryItemImageView)
    }
}
