package com.codencode.myfi.feature.send.ui;

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
import androidx.lifecycle.ViewModelProvider;

import com.codencode.myfi.R;
import com.codencode.myfi.feature.send.domain.ShareSession;
import com.codencode.myfi.feature.send.domain.TransferProgress;

public class SendFragment extends Fragment {
    private static final String TAG = "ShareServer";

    private FolderPickerContract folderPickerContract;
    private SendViewModel viewModel;
    private String lastShownServerUrl;

    private TextView serverStatusText;
    private TextView transferSpeedText;
    private TextView serverUrlText;
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

        viewModel = new ViewModelProvider(this).get(SendViewModel.class);
        bindViews(view);
        configureActions(view);
        viewModel.getUiState().observe(getViewLifecycleOwner(), this::render);
        viewModel.onScreenStarted();
    }

    private void bindViews(View view) {
        serverStatusText = view.findViewById(R.id.serverStatus);
        transferSpeedText = view.findViewById(R.id.tv_transfer_speed);
        serverUrlText = view.findViewById(R.id.tv_server_url);
        serverToggleButton = view.findViewById(R.id.server_on_off_button);
        qrCodeView = view.findViewById(R.id.imgQRCode);
        transferProgressBar = view.findViewById(R.id.transfer_progress_bar);
        serverStatusIndicator = view.findViewById(R.id.server_status_view);
    }

    private void configureActions(View view) {
        Button selectFolderButton = view.findViewById(R.id.btn_select_folder);
        selectFolderButton.setOnClickListener(ignored -> folderPickerContract.openPicker());
        serverToggleButton.setOnClickListener(ignored -> viewModel.onServerToggleClicked());
    }

    private void render(SendUiState state) {
        ShareSession session = state.getShareSession();
        serverStatusText.setText(state.getStatusMessage());
        renderServerState(session);
        renderTransfer(state.getTransferProgress());

        if (state.getQrCode() != null) {
            qrCodeView.setImageBitmap(state.getQrCode());
        }
        
        if (session.getServerUrl() != null) {
            serverUrlText.setText(session.getServerUrl());
            serverUrlText.setVisibility(View.VISIBLE);
        } else {
            serverUrlText.setVisibility(View.INVISIBLE);
        }
    }

    private void renderServerState(ShareSession session) {
        if (session.isServerRunning()) {
            serverStatusIndicator.setBackgroundResource(android.R.color.holo_green_light);
            serverToggleButton.setImageResource(R.drawable.power_on_state);
            showServerStartedMessage(session.getServerUrl());
            return;
        }

        serverStatusIndicator.setBackgroundResource(android.R.color.holo_red_light);
        serverToggleButton.setImageResource(R.drawable.power_off_state);
        lastShownServerUrl = null;
    }

    private void renderTransfer(@Nullable TransferProgress progress) {
        if (progress == null) {
            transferProgressBar.setVisibility(View.GONE);
            transferSpeedText.setVisibility(View.GONE);
            return;
        }

        transferProgressBar.setVisibility(View.VISIBLE);
        transferProgressBar.setProgress(progress.getPercentage());
        transferSpeedText.setVisibility(View.VISIBLE);
        transferSpeedText.setText(progress.getSpeed());
    }

    private void showServerStartedMessage(@Nullable String serverUrl) {
        if (serverUrl == null || serverUrl.equals(lastShownServerUrl)) {
            return;
        }

        lastShownServerUrl = serverUrl;
        Log.d(TAG, "Server started at " + serverUrl);
        Toast.makeText(requireContext(), "Server started at: " + serverUrl, Toast.LENGTH_LONG).show();
    }

    private void handleFolderSelection(Uri uri) {
        viewModel.onFolderSelected(uri);
    }

    @Override
    public void onDestroy() {
        viewModel.onScreenDestroyed();
        super.onDestroy();
    }
}
