package com.codencode.myfi.filereader.storage;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.util.Log;

import com.codencode.myfi.filereader.model.FileEntry;

import java.util.ArrayList;
import java.util.List;

public class DirectoryManager {
    private static final String TAG = "DirectoryManager";
    private final Context context;

    public DirectoryManager(Context context) {
        this.context = context;
    }

    /**
     * Lists all file names within a chosen directory URI.
     */
    public List<FileEntry> getAllFileNames(Uri rootUri) {
        List<FileEntry> fileList = new ArrayList<>();

        Uri childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(rootUri,
                DocumentsContract.getTreeDocumentId(rootUri));

        // We request Name, Size, and MIME Type
        String[] projection = {
                DocumentsContract.Document.COLUMN_DISPLAY_NAME,
                DocumentsContract.Document.COLUMN_SIZE,
                DocumentsContract.Document.COLUMN_MIME_TYPE,
                DocumentsContract.Document.COLUMN_DOCUMENT_ID
        };

        try (Cursor cursor = context.getContentResolver().query(childrenUri,
                projection, null, null, null)) {

            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_DISPLAY_NAME);
                int sizeIndex = cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_SIZE);
                int mimeIndex = cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_MIME_TYPE);
                int idIndex = cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_DOCUMENT_ID);

                do {
                    String name = cursor.getString(nameIndex);
                    long size = cursor.getLong(sizeIndex);
                    String mimeType = cursor.getString(mimeIndex);
                    String docId = cursor.getString(idIndex);

                    // Create a specific URI for this individual file
                    Uri fileUri = DocumentsContract.buildDocumentUriUsingTree(rootUri, docId);

                    // Check if it's a directory
                    boolean isDirectory = DocumentsContract.Document.MIME_TYPE_DIR.equals(mimeType);

                    fileList.add(new FileEntry(name, fileUri, mimeType, size, isDirectory, fileList.size()));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to list files for URI: " + rootUri, e);
            return new ArrayList<>(); // Return empty list so app doesn't crash
        }

        return fileList;
    }
}
