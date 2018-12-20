package com.commit451.driveappfolderviewer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.api.services.drive.model.File

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

    private var titleView: TextView = view.findViewById<View>(R.id.file_title) as TextView
    private var imageView: ImageView = view.findViewById<View>(R.id.file_image) as ImageView
    private var moreView: ImageView = view.findViewById<View>(R.id.file_more) as ImageView

    val popupMenu: PopupMenu

    init {
        popupMenu = PopupMenu(itemView.context, moreView)
        popupMenu.menuInflater.inflate(R.menu.dafv_menu_file, popupMenu.menu)

        moreView.setOnClickListener { popupMenu.show() }
    }

    fun bind(file: File) {
        if (file.mimeType == MIME_TYPE_FOLDER) {
            imageView.setImageResource(R.drawable.dafv_ic_folder_24dp)
        } else {
            imageView.setImageResource(R.drawable.dafv_ic_file_24dp)
        }
        var name = file.name
        file.fileExtension?.let {
            name += ".$it"
        }
        titleView.text = name
    }
}