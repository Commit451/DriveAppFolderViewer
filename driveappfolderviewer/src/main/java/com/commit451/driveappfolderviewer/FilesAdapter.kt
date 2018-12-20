package com.commit451.driveappfolderviewer

import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.google.api.services.drive.model.File

/**
 * Adapter which shows the files
 */
internal class FilesAdapter(private val listener: Listener) : RecyclerView.Adapter<FileViewHolder>() {

    private val files = mutableListOf<File>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val fileViewHolder = FileViewHolder.inflate(parent)
        fileViewHolder.itemView.setOnClickListener { view ->
            val metadata = view.tag as File
            listener.onFileClicked(metadata)
        }
        fileViewHolder.popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { menuItem ->
            if (menuItem.itemId == R.id.action_delete) {
                listener.onDeleteClicked(fileViewHolder.itemView.tag as File)
                return@OnMenuItemClickListener true
            }
            if (menuItem.itemId == R.id.action_size) {
                listener.onSizeClicked(fileViewHolder.itemView.tag as File)
                return@OnMenuItemClickListener true
            }
            false
        })
        return fileViewHolder
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        val metadata = files[position]
        holder.itemView.tag = metadata
        holder.bind(metadata)
    }

    override fun getItemCount(): Int {
        return files.size
    }

    fun setFiles(files: Collection<File>) {
        this.files.clear()
        this.files.addAll(files)
        notifyDataSetChanged()
    }

    fun clearFiles() {
        files.clear()
        notifyDataSetChanged()
    }

    interface Listener {
        fun onDeleteClicked(file: File)

        fun onFileClicked(file: File)

        fun onSizeClicked(file: File)
    }
}
