package com.codencode.myfi;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.codencode.myfi.filereader.model.FileEntry;
import com.codencode.myfi.filereader.picker.FolderPickerHandler;
import com.codencode.myfi.filereader.storage.DirectoryManager;
import com.codencode.myfi.server.FileServer;
import com.codencode.myfi.utils.NetworkUtility;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // Hello World test
    private FileServer helloWorldServer;
    public static final int PORT = 8080;
    private boolean serverState = false;

    private DirectoryManager directoryManager;
    private TextView tvFileList;
    private FolderPickerHandler folderPickerHandler;

    ImageButton serverOnOffButton;
    ImageView qrCodeView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Initialize UI and Logic
        directoryManager = new DirectoryManager(this);
        tvFileList = findViewById(R.id.tv_file_list);
        Button btnSelectFolder = findViewById(R.id.btn_select_folder);
        serverOnOffButton = findViewById(R.id.server_on_off_button);

        qrCodeView = findViewById(R.id.imgQRCode);

        // 1. Initialize the handler
        folderPickerHandler = new FolderPickerHandler(
                getActivityResultRegistry(),
                uri -> handleFolderSelection(uri) // Callback logic
        );

        // 2. Link it to the Activity's lifecycle
        getLifecycle().addObserver(folderPickerHandler);

        // 2. Set the trigger
        btnSelectFolder.setOnClickListener(v -> folderPickerHandler.openPicker());

        serverOnOffButton.setOnClickListener(v -> {
            if(!serverState) {
                startServer();
            } else {
                stopServer();
            }
        });
        helloWorldServer = new FileServer(PORT, this);
        helloWorldServer.setEventListener(
                percentage -> {
                    if(percentage == 100) {
                        tvFileList.setText("File transfer Complete");
                    } else {
                        tvFileList.setText("File transfer in progress: " + percentage + "%");
                    }
                }
        );
        startServer();
    }

    public void startServer() {
        try {
            helloWorldServer.start();
            serverState = true;
            serverOnOffButton.setImageResource(R.drawable.power_on_state);
            Bitmap bitmap = NetworkUtility.generateQRCode("http://" + NetworkUtility.getHotspotIPAddress() + ":" + PORT);
            qrCodeView.setImageBitmap(bitmap);

            Log.d("NanoHTTPD", "Server started on port " + PORT);
            Toast.makeText(this, "Server started at: " + NetworkUtility.getHotspotIPAddress() + ":" + PORT, Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            serverState = false;
            serverOnOffButton.setImageResource(R.drawable.power_off_state);
            throw new RuntimeException(e);
        }
    }

    private void stopServer() {
        if (helloWorldServer != null) {
            helloWorldServer.stop();
            serverState = false;
            serverOnOffButton.setImageResource(R.drawable.power_off_state);
            Log.d("NanoHTTPD", "Server stopped.");
        }
    }

    private void handleFolderSelection(Uri uri) {
        // Optional: Persist permissions so you don't have to ask again later
        final int takeFlags = android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;
        getContentResolver().takePersistableUriPermission(uri, takeFlags);

        // 3. Delegate the data fetching to the Manager
        List<FileEntry> fileNames = directoryManager.getAllFileNames(uri);

        // 4. Update the UI
        updateUI(fileNames);
    }

    private void updateUI(List<FileEntry> fileEntries) {
        if (fileEntries.isEmpty()) {
            tvFileList.setText("No files found in this folder.");
            return;
        }

        helloWorldServer.setFileMap(fileEntries);

        tvFileList.setText("" + fileEntries.size() + " files are being shared.");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopServer();
    }
}