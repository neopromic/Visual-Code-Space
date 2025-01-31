package com.raredev.vcspace.fragments.filemanager.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.raredev.vcspace.databinding.LayoutFileItemBinding;
import com.raredev.vcspace.fragments.filemanager.models.FileModel;
import com.raredev.vcspace.fragments.filemanager.viewmodel.FileListViewModel;

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.VH> {

  private FileListViewModel viewModel;
  private FileListener fileListener;

  public FileAdapter(FileListViewModel viewModel) {
    this.viewModel = viewModel;
  }

  @NonNull
  @Override
  public VH onCreateViewHolder(ViewGroup parent, int arg1) {
    return new VH(
        LayoutFileItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
  }

  @Override
  public void onBindViewHolder(VH holder, int position) {
    holder.itemView.setAnimation(
        AnimationUtils.loadAnimation(holder.itemView.getContext(), android.R.anim.fade_in));

    FileModel file = viewModel.getFiles().get(position);

    holder.tv_name.setText(file.getName());

    holder.img_icon.setImageResource(file.getIcon());
    
    if (file.getName().endsWith(".html")) {
      
    }

    holder.itemView.setOnClickListener(
        (v) -> {
          if (fileListener != null) {
            fileListener.onFileClick(file, v);
          }
        });

    holder.itemView.setOnLongClickListener(
        (v) -> {
          if (fileListener != null) {
            fileListener.onFileLongClick(file, v);
          }
          return true;
        });

    holder.img_menu.setOnClickListener(
        v -> {
          if (fileListener != null) {
            fileListener.onFileMenuClick(file, v);
          }
        });
  }

  @Override
  public int getItemCount() {
    return viewModel.getFiles().size();
  }

  public void refreshFiles() {
    notifyDataSetChanged();
  }

  public void setFileListener(FileListener listener) {
    this.fileListener = listener;
  }

  public interface FileListener {
    void onFileClick(FileModel file, View v);

    void onFileLongClick(FileModel file, View v);

    void onFileMenuClick(FileModel file, View v);
  }

  public class VH extends RecyclerView.ViewHolder {
    ShapeableImageView img_icon, img_menu;
    MaterialTextView tv_name;

    public VH(LayoutFileItemBinding binding) {
      super(binding.getRoot());
      img_icon = binding.imgIcon;
      tv_name = binding.fileName;
      img_menu = binding.imgMenu;
    }
  }
}
