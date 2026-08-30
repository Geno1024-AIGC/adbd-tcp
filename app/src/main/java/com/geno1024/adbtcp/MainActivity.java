package com.geno1024.adbtcp;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends Activity {

    private static final String PORT_PROP = "service.adb.tcp.port";
    private static final String ADBD_STATE_PROP = "init.svc.adbd";

    private EditText portInput;
    private Button setPortButton;
    private Button toggleButton;
    private TextView statusText;

    private interface CommandCallback {
        void done(int exitCode, String output);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        portInput = findViewById(R.id.port_input);
        setPortButton = findViewById(R.id.button_set_port);
        toggleButton = findViewById(R.id.button_toggle_adbd);
        statusText = findViewById(R.id.text_status);

        setPortButton.setOnClickListener(v -> setPort());
        toggleButton.setOnClickListener(v -> toggleAdbd());
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshStatus();
    }

    private void setPort() {
        String port = portInput.getText().toString().trim();
        if (port.isEmpty()) {
            statusText.setText(R.string.port_missing);
            return;
        }
        setPortButton.setEnabled(false);
        su("setprop " + PORT_PROP + " " + port, (code, out) -> {
            if (isFinishing()) {
                return;
            }
            setPortButton.setEnabled(true);
            if (code == 0) {
                statusText.setText(getString(R.string.port_set, port));
                refreshStatus();
            } else {
                statusText.setText(getString(R.string.su_failed, code, out));
            }
        });
    }

    private void toggleAdbd() {
        toggleButton.setEnabled(false);
        su("getprop " + ADBD_STATE_PROP, (readCode, state) -> {
            boolean running = isAdbdRunning(state);
            String action = running ? "stop" : "start";
            su("setprop ctl." + action + " adbd", (code, out) -> {
                if (isFinishing()) {
                    return;
                }
                toggleButton.setEnabled(true);
                refreshStatus();
            });
        });
    }

    private void refreshStatus() {
        su("getprop " + PORT_PROP, (portCode, port) -> {
            su("getprop " + ADBD_STATE_PROP, (stateCode, state) -> {
                if (isFinishing()) {
                    return;
                }
                if (stateCode != 0) {
                    statusText.setText(getString(R.string.su_failed, stateCode, state));
                    return;
                }
                boolean running = isAdbdRunning(state);
                toggleButton.setText(running ? R.string.stop_adbd : R.string.start_adbd);
                String portText = port.trim().isEmpty() ? getString(R.string.unset) : port.trim();
                statusText.setText(getString(R.string.status_format, portText, state.trim()));
            });
        });
    }

    private static boolean isAdbdRunning(String state) {
        return "running".equals(state) || "restarting".equals(state);
    }

    private void su(String command, CommandCallback callback) {
        new Thread(() -> {
            int exitCode;
            String output;
            try {
                Process process = new ProcessBuilder("su", "-c", command)
                        .redirectErrorStream(true)
                        .start();
                output = readAll(process.getInputStream());
                exitCode = process.waitFor();
            } catch (IOException e) {
                exitCode = -1;
                output = e.getMessage();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                exitCode = -1;
                output = "interrupted";
            }
            final int code = exitCode;
            final String text = output.trim();
            runOnUiThread(() -> callback.done(code, text));
        }).start();
    }

    private static String readAll(InputStream in) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] chunk = new byte[1024];
        int n;
        while ((n = in.read(chunk)) != -1) {
            buffer.write(chunk, 0, n);
        }
        return buffer.toString("UTF-8");
    }
}