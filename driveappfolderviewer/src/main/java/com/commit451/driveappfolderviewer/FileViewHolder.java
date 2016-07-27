package com.commit451.driveappfolderviewer;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.Metadata;

public class FileViewHolder extends RecyclerView.ViewHolder {

    public static FileViewHolder inflate(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_file, parent, false);
        return new FileViewHolder(view);
    }

    TextView mTitleView;
    ImageView mImageView;
    ImageView mMoreView;

    public final PopupMenu popupMenu;

    public FileViewHolder(View view) {
        super(view);
        mTitleView = (TextView) view.findViewById(R.id.file_title);
        mImageView = (ImageView) view.findViewById(R.id.file_image);
        mMoreView = (ImageView) view.findViewById(R.id.file_more);

        popupMenu = new PopupMenu(itemView.getContext(), mMoreView);
        popupMenu.getMenuInflater().inflate(R.menu.menu_file, popupMenu.getMenu());

        mMoreView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupMenu.show();
            }
        });
    }

    public void bind(Metadata metadata) {
        if (metadata.getMimeType().equals(DriveFolder.MIME_TYPE)) {
            mImageView.setImageResource(R.drawable.ic_folder_24dp);
        } else {
            mImageView.setImageResource(R.drawable.ic_file_24dp);
        }
        mTitleView.setText(metadata.getTitle());
    }
}