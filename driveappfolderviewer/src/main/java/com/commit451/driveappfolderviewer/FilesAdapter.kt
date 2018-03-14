package com.commit451.driveappfolderviewer

import android.support.v7.widget.RecyclerView
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu

import com.google.android.gms.drive.Metadata

import java.util.ArrayList

/**
 * Adapter which shows the files
 */
internal class FilesAdapter(private val mListener: Listener) : RecyclerView.Adapter<FileViewHolder>() {

    private val mMetadatas: ArrayList<Metadata> = ArrayList()

    private val mOnClickListener = View.OnClickListener { view ->
        val metadata = view.tag as Metadata
        mListener.onFileClicked(metadata)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val fileViewHolder = FileViewHolder.inflate(parent)
        fileViewHolder.itemView.setOnClickListener(mOnClickListener)
        fileViewHolder.popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { menuItem ->
            if (menuItem.itemId == R.id.action_delete) {
                mListener.onDeleteClicked(fileViewHolder.itemView.tag as Metadata)
                return@OnMenuItemClickListener true
            }
            if (menuItem.itemId == R.id.action_size) {
                mListener.onSizeClicked(fileViewHolder.itemView.tag as Metadata)
                return@OnMenuItemClickListener true
            }
            false
        })
        return fileViewHolder
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        val metadata = mMetadatas[position]
        holder.itemView.tag = metadata
        holder.bind(metadata)
    }

    override fun getItemCount(): Int {
        return mMetadatas.size
    }

    fun setMetadatas(metadatas: ArrayList<Metadata>) {
        mMetadatas.clear()
        mMetadatas.addAll(metadatas)
        notifyDataSetChanged()
    }

    fun clearMetadatas() {
        mMetadatas.clear()
        notifyDataSetChanged()
    }

    interface Listener {
        fun onDeleteClicked(metadata: Metadata)

        fun onFileClicked(metadata: Metadata)

        fun onSizeClicked(metadata: Metadata)
    }
}
