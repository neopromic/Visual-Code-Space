package com.raredev.vcspace.util;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {

  private static File deviceDirectory;

  public static File getDeviceDirectory() {
    if (deviceDirectory == null) {
      deviceDirectory = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
    }
    return deviceDirectory;
  }

  public static boolean isValidTextFile(String filename) {
    return !filename.matches(
        ".*\\.(bin|ttf|png|jpe?g|bmp|mp4|mp3|m4a|iso|so|zip|rar|jar|dex|odex|vdex|7z|apk|apks|xapk)$");
  }

  public static boolean rename(String filePath, String name) {
    File file = new File(filePath);

    if (file.exists()) {
      return file.renameTo(new File(file.getParentFile(), name));
    }
    return false;
  }

  public static boolean makeDir(String path) {
    File file = new File(path);
    if (!file.exists()) {
      return file.mkdirs();
    }
    return false;
  }

  public static boolean writeFile(String path, String text) {
    File file =
        new File(
            path.substring(0, path.lastIndexOf('/')), path.substring(path.lastIndexOf('/') + 1));

    try (Writer writer =
        new BufferedWriter(
            new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
      writer.write(text);
      return true;
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
  }

  public static boolean delete(String path) {
    return delete(new File(path));
  }

  public static boolean delete(File file) {
    if (!file.exists()) return false;

    if (file.isFile()) {
      return file.delete();
    }

    File[] fileArr = file.listFiles();

    if (fileArr != null) {
      for (File subFile : fileArr) {
        if (subFile.isDirectory()) {
          delete(subFile);
        }

        if (subFile.isFile()) {
          subFile.delete();
        }
      }
    }

    return file.delete();
  }

  public static String readFile(String path) {
    return readFile(new File(path));
  }

  public static String readFile(File file) {
    StringBuilder sb = new StringBuilder();
    FileReader fr = null;
    try {
      fr = new FileReader(file);

      char[] buff = new char[1024];
      int length = 0;

      while ((length = fr.read(buff)) > 0) {
        sb.append(new String(buff, 0, length));
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (fr != null) {
        try {
          fr.close();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }

    return sb.toString();
  }

  public static String readAssetFile(Context ctx, String path) {
    String str = "";
    try {
      BufferedReader myReader =
          new BufferedReader(new InputStreamReader(ctx.getAssets().open(path)));
      String aDataRow = "";
      while ((aDataRow = myReader.readLine()) != null) {
        str += aDataRow + "\n";
      }
      myReader.close();
    } catch (IOException e) {
    }
    return str;
  }

  /**
   * Get a file from a Uri. Framework Documents, as well as the _data field for the MediaStore and
   * other file-based ContentProviders.
   *
   * @param context The context.
   * @param uri The Uri to query.
   */
  public static File getFileFromUri(final Context context, final Uri uri) throws IOException {

    String path = null;

    // DocumentProvider
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      if (DocumentsContract.isDocumentUri(context, uri)) {

        // ExternalStorageProvider
        if (isExternalStorageDocument(uri)) {
          final String docId = DocumentsContract.getDocumentId(uri);
          final String[] split = docId.split(":");
          final String type = split[0];

          if ("primary".equalsIgnoreCase(type)) {
            path = Environment.getExternalStorageDirectory() + "/" + split[1];
          }

          // TODO handle non-primary volumes

        } else if (isDownloadsDocument(uri)) { // DownloadsProvider

          final String id = DocumentsContract.getDocumentId(uri);
          final Uri contentUri =
              ContentUris.withAppendedId(
                  Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

          path = getDataColumn(context, contentUri, null, null);

        } else if (isMediaDocument(uri)) { // MediaProvider

          final String docId = DocumentsContract.getDocumentId(uri);
          final String[] split = docId.split(":");
          final String type = split[0];

          Uri contentUri = null;
          if ("image".equals(type)) {
            contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
          } else if ("video".equals(type)) {
            contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
          } else if ("audio".equals(type)) {
            contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
          }

          final String selection = "_id=?";
          final String[] selectionArgs = new String[] {split[1]};

          path = getDataColumn(context, contentUri, selection, selectionArgs);

        } else if (isGoogleDrive(uri)) { // Google Drive
          String TAG = "isGoogleDrive";
          path = TAG;
          final String docId = DocumentsContract.getDocumentId(uri);
          final String[] split = docId.split(";");
          final String acc = split[0];
          final String doc = split[1];

          /*
           * @details google drive document data. - acc , docId.
           * */

          return saveFileIntoExternalStorageByUri(context, uri);
        } // MediaStore (and general)
      } else if ("content".equalsIgnoreCase(uri.getScheme())) {
        path = getDataColumn(context, uri, null, null);
      }
      // File
      else if ("file".equalsIgnoreCase(uri.getScheme())) {
        path = uri.getPath();
      }

      return new File(path);
    } else {

      Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
      return new File(cursor.getString(cursor.getColumnIndex("_data")));
    }
  }

  /**
   * @param uri The Uri to check.
   * @return Whether the Uri authority is GoogleDrive.
   */
  public static boolean isGoogleDrive(Uri uri) {
    return uri.getAuthority().equalsIgnoreCase("com.google.android.apps.docs.storage");
  }

  /**
   * Get the value of the data column for this Uri. This is useful for MediaStore Uris, and other
   * file-based ContentProviders.
   *
   * @param context The context.
   * @param uri The Uri to query.
   * @param selection (Optional) Filter used in the query.
   * @param selectionArgs (Optional) Selection arguments used in the query.
   * @return The value of the _data column, which is typically a file path.
   */
  public static String getDataColumn(
      Context context, Uri uri, String selection, String[] selectionArgs) {

    Cursor cursor = null;
    final String column = MediaStore.Images.Media.DATA;
    final String[] projection = {column};

    try {
      cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
      if (cursor != null && cursor.moveToFirst()) {
        final int column_index = cursor.getColumnIndexOrThrow(column);
        return cursor.getString(column_index);
      }
    } finally {
      if (cursor != null) cursor.close();
    }
    return null;
  }

  /**
   * @param uri The Uri to check.
   * @return Whether the Uri authority is ExternalStorageProvider.
   */
  public static boolean isExternalStorageDocument(Uri uri) {
    return "com.android.externalstorage.documents".equals(uri.getAuthority());
  }

  /**
   * @param uri The Uri to check.
   * @return Whether the Uri authority is DownloadsProvider.
   */
  public static boolean isDownloadsDocument(Uri uri) {
    return "com.android.providers.downloads.documents".equals(uri.getAuthority());
  }

  /**
   * @param uri The Uri to check.
   * @return Whether the Uri authority is MediaProvider.
   */
  public static boolean isMediaDocument(Uri uri) {
    return "com.android.providers.media.documents".equals(uri.getAuthority());
  }

  public static File makeEmptyFileIntoExternalStorageWithTitle(String title) {
    String root = Environment.getExternalStorageDirectory().getAbsolutePath();
    return new File(root, title);
  }

  public static String getFileName(Context context, Uri uri) {
    String result = null;
    if (uri.getScheme().equals("content")) {
      Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
      try {
        if (cursor != null && cursor.moveToFirst()) {
          result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
        }
      } finally {
        cursor.close();
      }
    }
    if (result == null) {
      result = uri.getPath();
      int cut = result.lastIndexOf('/');
      if (cut != -1) {
        result = result.substring(cut + 1);
      }
    }
    return result;
  }

  public static File saveFileIntoExternalStorageByUri(Context context, Uri uri) throws IOException {
    InputStream inputStream = context.getContentResolver().openInputStream(uri);
    int originalSize = inputStream.available();

    BufferedInputStream bis = null;
    BufferedOutputStream bos = null;
    String fileName = getFileName(context, uri);
    File file = makeEmptyFileIntoExternalStorageWithTitle(fileName);
    bis = new BufferedInputStream(inputStream);
    bos = new BufferedOutputStream(new FileOutputStream(file, false));

    byte[] buf = new byte[originalSize];
    bis.read(buf);
    do {
      bos.write(buf);
    } while (bis.read(buf) != -1);

    bos.flush();
    bos.close();
    bis.close();

    return file;
  }

  public static void clearAppCache(Context context) {
    try {
      File dir = context.getCacheDir();
      delete(dir);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
