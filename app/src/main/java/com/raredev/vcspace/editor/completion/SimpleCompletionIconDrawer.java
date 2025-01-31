package com.raredev.vcspace.editor.completion;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import androidx.annotation.DrawableRes;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.drawable.DrawableCompat;
import com.raredev.vcspace.R;
import com.raredev.vcspace.VCSpaceApplication;
import com.raredev.vcspace.util.Utils;

public class SimpleCompletionIconDrawer {
  public static Drawable draw(SimpleCompletionItemKind kind) {
    Drawable icon =
        kind.equals(SimpleCompletionItemKind.UNKNOWN)
            ? getDrawable(R.drawable.cube)
            : draw(kind.getDisplayChar());
    DrawableCompat.setTint(
        DrawableCompat.wrap(icon),
        kind.getDefaultDisplayBackgroundColor() != 0
            ? (int) kind.getDefaultDisplayBackgroundColor()
            : Utils.isDarkMode() ? Color.WHITE : Color.DKGRAY);
    return icon;
  }

  public static Drawable draw(String kind) {
    Drawable icon = getDrawable(R.drawable.cube);
    switch (kind.toUpperCase()) {
      case "I":
        icon = getDrawable(R.drawable.alpha_i_circle);
        break;
      case "T":
        icon = getDrawable(R.drawable.alpha_t_circle);
        break;
      case "M":
        icon = getDrawable(R.drawable.alpha_m_circle);
        break;
      case "F":
        icon = getDrawable(R.drawable.alpha_f_circle);
        break;
      case "C":
        icon = getDrawable(R.drawable.alpha_m_circle);
        break;
      case "V":
        icon = getDrawable(R.drawable.alpha_v_circle);
        break;
      case "P":
        icon = getDrawable(R.drawable.alpha_p_circle);
        break;
      case "U":
        icon = getDrawable(R.drawable.alpha_u_circle);
        break;
      case "E":
        icon = getDrawable(R.drawable.alpha_e_circle);
        break;
      case "K":
        icon = getDrawable(R.drawable.alpha_k_circle);
        break;
      case "S":
        icon = getDrawable(R.drawable.alpha_s_circle);
        break;
      case "R":
        icon = getDrawable(R.drawable.alpha_r_circle);
        break;
      case "O":
        icon = getDrawable(R.drawable.alpha_o_circle);
        break;
      case "A":
        icon = getDrawable(R.drawable.alpha_a_circle);
        break;
      case "UNKNOWN":
      default:
        return icon = getDrawable(R.drawable.cube);
    }
    return icon;
  }

  private static Drawable getDrawable(@DrawableRes int resId) {
    return AppCompatResources.getDrawable(
        VCSpaceApplication.getInstance().getApplicationContext(), resId);
  }
}
