package com.codencode.myfi.feature.send.ui;

import android.net.Uri;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

public final class FolderPickerContract {
    private final ActivityResultLauncher<Uri> launcher;

    public FolderPickerContract(Fragment fragment, OnFolderSelectedListener listener) {
        launcher = fragment.registerForActivityResult(
                new ActivityResultContracts.OpenDocumentTree(),
                uri -> {
                    if (uri != null) {
                        listener.onFolderSelected(uri);
                    }
                }
        );
    }

    public void openPicker() {
        launcher.launch(null);
    }

    public interface OnFolderSelectedListener {
        void onFolderSelected(Uri uri);
    }
}
