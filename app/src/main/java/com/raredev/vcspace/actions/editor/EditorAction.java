package com.raredev.vcspace.actions.editor;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import com.raredev.vcspace.actions.Action;
import com.raredev.vcspace.actions.ActionData;

public abstract class EditorAction extends Action {
  
  public EditorAction() {
    location = Action.Location.EDITOR;
  }

  @Override
  public void update(@NonNull ActionData data) {
    visible = true;
    title = getTitle();
  }

  @StringRes
  public abstract int getTitle();
}
