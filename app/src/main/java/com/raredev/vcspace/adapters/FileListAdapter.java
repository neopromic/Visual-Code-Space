package com.raredev.vcspace.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.raredev.vcspace.R;
import com.raredev.vcspace.databinding.LayoutFileItemBinding;
import com.raredev.vcspace.models.FileExtension;
import com.raredev.vcspace.ui.viewmodel.FileListViewModel;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FileListAdapter extends RecyclerView.Adapter<FileListAdapter.VH> {

  private FileListViewModel viewModel;
  private FileListener fileListener;

  public FileListAdapter(FileListViewModel viewModel) {
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

    File file = viewModel.getFiles().get(position);

    if (!viewModel.getSelectedFiles().contains(file)) {
      holder.itemView.setBackgroundColor(Color.TRANSPARENT);
    } else {
      holder.itemView.setBackgroundColor(Color.parseColor("#2A00D4FF"));
    }

    holder.tv_name.setText(file.getName());
    if (file.isFile()) {
      holder.img_icon.setImageResource(FileExtension.Factory.forFile(file).getIcon());
    } else {
      holder.img_icon.setImageResource(R.drawable.ic_folder);
    }

    if (position == 0) {
      holder.img_icon.setImageResource(R.drawable.ic_folder);
      holder.img_menu.setVisibility(View.GONE);
    }

    holder.itemView.setOnClickListener(
        (v) -> {
          if (isFilesSelected()) {
            selectFile(holder.itemView, file);
            return;
          }
          if (fileListener != null) {
            fileListener.onFileClick(file, v);
          }
        });

    holder.itemView.setOnLongClickListener(
        (v) -> {
          selectFile(holder.itemView, file);
          if (fileListener != null) {
            fileListener.onFileLongClick(file, v);
          }
          return true;
        });

    holder.img_icon.setOnClickListener(
        v -> {
          selectFile(holder.itemView, file);
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
    viewModel.getSelectedFiles().clear();
    notifyDataSetChanged();
  }

  public void selectFile(View v, File file) {
    if (file == viewModel.getFiles().get(0)) {
      return;
    }
    if (viewModel.getSelectedFiles().contains(file)) {
      v.setBackgroundColor(Color.TRANSPARENT);
      viewModel.removeSelectFile(file);
    } else {
      v.setBackgroundColor(Color.parseColor("#2A00D4FF"));
      viewModel.selectFile(file);
    }
  }

  public boolean isFilesSelected() {
    return !viewModel.getSelectedFiles().isEmpty();
  }

  public List<File> getSelectedFiles() {
    return viewModel.getSelectedFiles();
  }

  public void setFileListener(FileListener listener) {
    this.fileListener = listener;
  }

  public interface FileListener {
    void onFileClick(File file, View v);

    void onFileLongClick(File file, View v);
    
    void onFileMenuClick(File file, View v);
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
