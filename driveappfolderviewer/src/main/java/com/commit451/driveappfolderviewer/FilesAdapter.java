package com.commit451.driveappfolderviewer;

import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import com.google.android.gms.drive.Metadata;

import java.util.ArrayList;

/**
 * Adapter which shows the files
 */
public class FilesAdapter extends RecyclerView.Adapter<FileViewHolder> {

    private ArrayList<Metadata> mMetadatas;
    private Listener mListener;

    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Metadata metadata = (Metadata) view.getTag();
            mListener.onFileClicked(metadata);
        }
    };

    public FilesAdapter(Listener listener) {
        mMetadatas = new ArrayList<>();
        mListener = listener;
    }

    @Override
    public FileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final FileViewHolder fileViewHolder = FileViewHolder.inflate(parent);
        fileViewHolder.itemView.setOnClickListener(mOnClickListener);
        fileViewHolder.popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.action_delete) {
                    mListener.onDeleteClicked((Metadata) fileViewHolder.itemView.getTag());
                    return true;
                }
                if (menuItem.getItemId() == R.id.action_size) {
                    mListener.onSizeClicked((Metadata) fileViewHolder.itemView.getTag());
                    return true;
                }
                return false;
            }
        });
        return fileViewHolder;
    }

    @Override
    public void onBindViewHolder(FileViewHolder holder, int position) {
        Metadata metadata = mMetadatas.get(position);
        holder.itemView.setTag(metadata);
        holder.bind(metadata);
    }

    @Override
    public int getItemCount() {
        return mMetadatas.size();
    }

    public void setMetadatas(ArrayList<Metadata> metadatas) {
        mMetadatas.clear();
        mMetadatas.addAll(metadatas);
        notifyDataSetChanged();
    }

    public void clearMetadatas() {
        mMetadatas.clear();
        notifyDataSetChanged();
    }

    public interface Listener {
        void onDeleteClicked(Metadata metadata);
        void onFileClicked(Metadata metadata);
        void onSizeClicked(Metadata metadata);
    }
}
