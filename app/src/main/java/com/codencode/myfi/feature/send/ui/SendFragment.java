package com.codencode.myfi.feature.send.ui;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.codencode.myfi.R;
import com.codencode.myfi.core.network.LocalAddressProvider;
import com.codencode.myfi.core.network.QrCodeGenerator;
import com.codencode.myfi.feature.send.data.SafShareFolderRepository;
import com.codencode.myfi.feature.send.data.ShareFolderRepository;
import com.codencode.myfi.feature.send.domain.SharedFile;
import com.codencode.myfi.feature.send.server.ShareServer;

import java.io.IOException;
import java.util.List;

public class SendFragment extends Fragment {
    private static final int PORT = 8080;
    private static final String TAG = "ShareServer";

    private ShareServer shareServer;
    private ShareFolderRepository shareFolderRepository;
    private FolderPickerContract folderPickerContract;
    private boolean serverRunning;

    private TextView serverStatusText;
    private TextView transferSpeedText;
    private ImageButton serverToggleButton;
    private ImageView qrCodeView;
    private ProgressBar transferProgressBar;
    private View serverStatusIndicator;

    public SendFragment() {
        super(R.layout.fragment_send);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        folderPickerContract = new FolderPickerContract(this, this::handleFolderSelection);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        shareFolderRepository = new SafShareFolderRepository(requireContext());
        bindViews(view);
        configureActions(view);
        configureServer();
        initializeUi();
        startServer();
    }

    private void bindViews(View view) {
        serverStatusText = view.findViewById(R.id.serverStatus);
        transferSpeedText = view.findViewById(R.id.tv_transfer_speed);
        serverToggleButton = view.findViewById(R.id.server_on_off_button);
        qrCodeView = view.findViewById(R.id.imgQRCode);
        transferProgressBar = view.findViewById(R.id.transfer_progress_bar);
        serverStatusIndicator = view.findViewById(R.id.server_status_view);
    }

    private void configureActions(View view) {
        Button selectFolderButton = view.findViewById(R.id.btn_select_folder);
        selectFolderButton.setOnClickListener(ignored -> folderPickerContract.openPicker());

        serverToggleButton.setOnClickListener(ignored -> {
            if (serverRunning) {
                stopServer();
            } else {
                startServer();
            }
        });
    }

    private void configureServer() {
        shareServer = new ShareServer(PORT, requireContext());
        shareServer.setDownloadProgressListener((percentage, speed) -> {
            if (percentage == 100) {
                serverStatusText.setText("Online (Transfer Complete)");
                transferProgressBar.setVisibility(View.GONE);
                transferSpeedText.setVisibility(View.GONE);
            } else {
                serverStatusText.setText("Online (Transfer in Progress)");
                transferProgressBar.setVisibility(View.VISIBLE);
                transferProgressBar.setProgress(percentage);
                transferSpeedText.setVisibility(View.VISIBLE);
                transferSpeedText.setText(speed);
            }
        });
    }

    private void initializeUi() {
        transferProgressBar.setVisibility(View.GONE);
        transferSpeedText.setVisibility(View.GONE);
    }

    private void startServer() {
        try {
            shareServer.start();
            serverRunning = true;
            serverStatusIndicator.setBackgroundResource(android.R.color.holo_green_light);
            serverToggleButton.setImageResource(R.drawable.power_on_state);

            String serverAddress = LocalAddressProvider.getHotspotIpv4Address();
            Bitmap qrCode = QrCodeGenerator.generate("http://" + serverAddress + ":" + PORT);
            qrCodeView.setImageBitmap(qrCode);

            Log.d(TAG, "Server started on port " + PORT);
            Toast.makeText(
                    requireContext(),
                    "Server started at: " + serverAddress + ":" + PORT,
                    Toast.LENGTH_LONG
            ).show();
        } catch (IOException exception) {
            serverRunning = false;
            serverToggleButton.setImageResource(R.drawable.power_off_state);
            serverStatusIndicator.setBackgroundResource(android.R.color.holo_red_light);
            throw new RuntimeException(exception);
        }
    }

    private void stopServer() {
        if (shareServer == null) {
            return;
        }

        shareServer.stop();
        serverRunning = false;
        serverToggleButton.setImageResource(R.drawable.power_off_state);
        serverStatusIndicator.setBackgroundResource(android.R.color.holo_red_light);
        Log.d(TAG, "Server stopped.");
    }

    private void handleFolderSelection(Uri uri) {
        int takeFlags = android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;
        requireContext().getContentResolver().takePersistableUriPermission(uri, takeFlags);

        List<SharedFile> sharedFiles = shareFolderRepository.listSharedFiles(uri);
        if (!sharedFiles.isEmpty()) {
            shareServer.setSharedFiles(sharedFiles);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopServer();
    }
}
