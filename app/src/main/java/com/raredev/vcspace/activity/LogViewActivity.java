package com.raredev.vcspace.activity;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AlertDialog;
import com.raredev.vcspace.R;
import com.raredev.vcspace.databinding.ActivityLogViewBinding;
import com.raredev.vcspace.progressdialog.ProgressDialog;
import com.raredev.vcspace.task.TaskExecutor;
import com.raredev.vcspace.util.DialogUtils;
import com.raredev.vcspace.util.ILogger;
import io.github.rosemoe.sora.langs.textmate.TextMateLanguage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class LogViewActivity extends BaseActivity implements ILogger.Observer {
  private final String LOG_TAG = LogViewActivity.class.getSimpleName();
  private ActivityLogViewBinding binding;

  @Override
  public View getLayout() {
    binding = ActivityLogViewBinding.inflate(getLayoutInflater());
    return binding.getRoot();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setSupportActionBar(binding.toolbar);
    binding.toolbar.setNavigationOnClickListener((v) -> onBackPressed());
    binding.editor.setEditorLanguage(TextMateLanguage.create("text.log", false));
    binding.editor.getProps().autoIndent = false;
    binding.editor.setEditable(false);
    binding.editor.configureEditor();

    binding.fab.setOnClickListener(
        v -> {
          binding.editor.setText("");
          ILogger.clear();
        });
    ILogger.addObserver(this);
  }

  @Override
  protected void onDestroy() {
    binding.editor.release();
    ILogger.addObserver(null);
    super.onDestroy();
  }

  @Override
  public void onLogUpdated(File logFile) {
    ProgressDialog progress =
        DialogUtils.newProgressDialog(
            this, getString(R.string.loading), getString(R.string.loading_log_file));
    progress.setCancelable(false);
    AlertDialog dialog = progress.create();
    dialog.show();
    TaskExecutor.executeAsyncProvideError(
        () -> {
          try {
            BufferedReader reader = new BufferedReader(new FileReader(logFile));
            String line;
            while ((line = reader.readLine()) != null) {
              appendText(line + "\n");
            }
            reader.close();
          } catch (IOException e) {
            e.printStackTrace();
          }
          return null;
        },
        (result, error) -> {
          dialog.cancel();
          if (error != null) ILogger.error(LOG_TAG, error.toString());
        });
  }

  private int appendText(String text) {
    final var content = binding.editor.getText();
    if (binding.editor.getLineCount() <= 0) {
      return 0;
    }
    final int line = binding.editor.getLineCount() - 1;
    int col = content.getColumnCount(line);
    if (col < 0) {
      col = 0;
    }
    content.insert(line, col, text);
    return line;
  }
}
