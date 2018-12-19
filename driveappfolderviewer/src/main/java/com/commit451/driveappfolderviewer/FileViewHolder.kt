package com.commit451.driveappfolderviewer

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView

import com.google.android.gms.drive.DriveFolder
import com.google.android.gms.drive.Metadata

/**
 * View folder for file and folders
 */
internal class FileViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    companion object {

        fun inflate(parent: ViewGroup): FileViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.dafv_item_file, parent, false)
            return FileViewHolder(view)
        }
    }

    var titleView: TextView = view.findViewById<View>(R.id.file_title) as TextView
    var imageView: ImageView = view.findViewById<View>(R.id.file_image) as ImageView
    var moreView: ImageView = view.findViewById<View>(R.id.file_more) as ImageView

    val popupMenu: PopupMenu

    init {
        popupMenu = PopupMenu(itemView.context, moreView)
        popupMenu.menuInflater.inflate(R.menu.dafv_menu_file, popupMenu.menu)

        moreView.setOnClickListener { popupMenu.show() }
    }

    fun bind(metadata: Metadata) {
        if (metadata.mimeType == DriveFolder.MIME_TYPE) {
            imageView.setImageResource(R.drawable.dafv_ic_folder_24dp)
        } else {
            imageView.setImageResource(R.drawable.dafv_ic_file_24dp)
        }
        titleView.text = metadata.title
    }
}