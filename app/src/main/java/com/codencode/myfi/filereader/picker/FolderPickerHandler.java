package com.codencode.myfi.filereader.picker;

import android.net.Uri;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

public class FolderPickerHandler implements DefaultLifecycleObserver {

    private final ActivityResultRegistry registry;
    private ActivityResultLauncher<Uri> launcher;
    private final OnFolderSelectedListener listener;

    // Interface to communicate back to the Activity
    public interface OnFolderSelectedListener {
        void onFolderSelected(Uri uri);
    }

    public FolderPickerHandler(@NonNull ActivityResultRegistry registry, OnFolderSelectedListener listener) {
        this.registry = registry;
        this.listener = listener;
    }

    @Override
    public void onCreate(@NonNull LifecycleOwner owner) {
        // Registering using the registry directly
        launcher = registry.register("folder_picker_key", owner,
                new ActivityResultContracts.OpenDocumentTree(), uri -> {
                    if (uri != null && listener != null) {
                        listener.onFolderSelected(uri);
                    }
                });
    }

    public void openPicker() {
        if (launcher != null) {
            launcher.launch(null);
        }
    }
}
