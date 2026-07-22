package com.codencode.myfi.feature.send.data;

import android.net.Uri;

import com.codencode.myfi.feature.send.domain.SharedFile;

import java.util.List;

public interface ShareFolderRepository {
    List<SharedFile> listSharedFiles(Uri folderUri);
}
