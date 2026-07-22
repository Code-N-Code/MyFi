package com.codencode.myfi.feature.send.data;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.util.Log;

import com.codencode.myfi.feature.send.domain.SharedFile;

import java.util.ArrayList;
import java.util.List;

public class SafShareFolderRepository implements ShareFolderRepository {
    private static final String TAG = "SafShareFolderRepository";

    private static final String[] DOCUMENT_PROJECTION = {
            DocumentsContract.Document.COLUMN_DISPLAY_NAME,
            DocumentsContract.Document.COLUMN_SIZE,
            DocumentsContract.Document.COLUMN_MIME_TYPE,
            DocumentsContract.Document.COLUMN_DOCUMENT_ID
    };

    private final Context context;

    public SafShareFolderRepository(Context context) {
        this.context = context;
    }

    @Override
    public List<SharedFile> listSharedFiles(Uri folderUri) {
        List<SharedFile> sharedFiles = new ArrayList<>();
        Uri childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(
                folderUri,
                DocumentsContract.getTreeDocumentId(folderUri)
        );

        try (Cursor cursor = context.getContentResolver().query(
                childrenUri,
                DOCUMENT_PROJECTION,
                null,
                null,
                null
        )) {
            if (cursor == null || !cursor.moveToFirst()) {
                return sharedFiles;
            }

            int nameIndex = cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_DISPLAY_NAME);
            int sizeIndex = cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_SIZE);
            int mimeTypeIndex = cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_MIME_TYPE);
            int documentIdIndex = cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_DOCUMENT_ID);

            do {
                String name = cursor.getString(nameIndex);
                long sizeBytes = cursor.getLong(sizeIndex);
                String mimeType = cursor.getString(mimeTypeIndex);
                String documentId = cursor.getString(documentIdIndex);
                Uri fileUri = DocumentsContract.buildDocumentUriUsingTree(folderUri, documentId);
                boolean directory = DocumentsContract.Document.MIME_TYPE_DIR.equals(mimeType);

                sharedFiles.add(new SharedFile(
                        name,
                        fileUri,
                        mimeType,
                        sizeBytes,
                        directory,
                        sharedFiles.size()
                ));
            } while (cursor.moveToNext());
        } catch (Exception exception) {
            Log.e(TAG, "Failed to list files for URI: " + folderUri, exception);
            return new ArrayList<>();
        }

        return sharedFiles;
    }
}
