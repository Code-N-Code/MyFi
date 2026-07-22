package com.codencode.myfi;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.codencode.myfi.filereader.picker.FolderPickerHandler;
import com.codencode.myfi.server.FileServer;
import com.codencode.myfi.core.network.LocalAddressProvider;
import com.codencode.myfi.core.network.QrCodeGenerator;
import com.codencode.myfi.feature.send.data.SafShareFolderRepository;
import com.codencode.myfi.feature.send.data.ShareFolderRepository;
import com.codencode.myfi.feature.send.domain.SharedFile;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // Hello World test
    private FileServer helloWorldServer;
    public static final int PORT = 8080;
    private boolean serverState = false;

    private ShareFolderRepository shareFolderRepository;
    private TextView tvFileList, tvServerStatus, tvTransferSpeed;
    private FolderPickerHandler folderPickerHandler;

    ImageButton serverOnOffButton;
    ImageView qrCodeView;
    ProgressBar transferProgressBar;
    View serverStatusView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Initialize UI and Logic
        shareFolderRepository = new SafShareFolderRepository(this);
        //tvFileList = findViewById(R.id.tv_file_list);
        tvServerStatus = findViewById(R.id.serverStatus);
        tvTransferSpeed = findViewById(R.id.tv_transfer_speed);
        Button btnSelectFolder = findViewById(R.id.btn_select_folder);
        serverOnOffButton = findViewById(R.id.server_on_off_button);

        qrCodeView = findViewById(R.id.imgQRCode);
        transferProgressBar = findViewById(R.id.transfer_progress_bar);
        serverStatusView = findViewById(R.id.server_status_view);

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
                (percentage, speed) -> {
                    if(percentage == 100) {
                        //tvFileList.setText("File transfer Complete");
                        tvServerStatus.setText("Online (Transfer Complete)");
                        transferProgressBar.setVisibility(View.GONE);
                        tvTransferSpeed.setVisibility(View.GONE);
                    } else {
                        //tvFileList.setText("File transfer in progress: " + percentage + "%");
                        tvServerStatus.setText("Online (Transfer in Progress)");
                        transferProgressBar.setVisibility(View.VISIBLE);
                        transferProgressBar.setProgress(percentage);
                        tvTransferSpeed.setVisibility(View.VISIBLE);
                        tvTransferSpeed.setText(speed);
                    }
                }
        );
        initUi();
        startServer();
    }

    void initUi() {
        transferProgressBar.setVisibility(View.GONE);
        tvTransferSpeed.setVisibility(View.GONE);
    }

    public void startServer() {
        try {
            helloWorldServer.start();
            serverState = true;
            serverStatusView.setBackgroundResource(android.R.color.holo_green_light);
            serverOnOffButton.setImageResource(R.drawable.power_on_state);
            String serverAddress = LocalAddressProvider.getHotspotIpv4Address();
            Bitmap bitmap = QrCodeGenerator.generate("http://" + serverAddress + ":" + PORT);
            qrCodeView.setImageBitmap(bitmap);

            Log.d("NanoHTTPD", "Server started on port " + PORT);
            Toast.makeText(this, "Server started at: " + serverAddress + ":" + PORT, Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            serverState = false;
            serverOnOffButton.setImageResource(R.drawable.power_off_state);
            serverStatusView.setBackgroundResource(android.R.color.holo_red_light);
            throw new RuntimeException(e);
        }
    }

    private void stopServer() {
        if (helloWorldServer != null) {
            helloWorldServer.stop();
            serverState = false;
            serverOnOffButton.setImageResource(R.drawable.power_off_state);
            serverStatusView.setBackgroundResource(android.R.color.holo_red_light);
            Log.d("NanoHTTPD", "Server stopped.");
        }
    }

    private void handleFolderSelection(Uri uri) {
        // Optional: Persist permissions so you don't have to ask again later
        final int takeFlags = android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;
        getContentResolver().takePersistableUriPermission(uri, takeFlags);

        // 3. Delegate the data fetching to the Manager
        List<SharedFile> sharedFiles = shareFolderRepository.listSharedFiles(uri);

        // 4. Update the UI
        updateUI(sharedFiles);
    }

    private void updateUI(List<SharedFile> sharedFiles) {
        if (sharedFiles.isEmpty()) {
            //tvFileList.setText("No files found in this folder.");
            return;
        }

        helloWorldServer.setFileMap(sharedFiles);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopServer();
    }
}
