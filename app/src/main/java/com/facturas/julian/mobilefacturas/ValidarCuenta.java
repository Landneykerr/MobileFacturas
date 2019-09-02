package com.facturas.julian.mobilefacturas;

import android.content.Intent;
import android.graphics.PointF;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.List;

import at.nineyards.anyline.camera.AnylineViewConfig;
import at.nineyards.anyline.modules.ocr.AnylineOcrConfig;
import at.nineyards.anyline.modules.ocr.AnylineOcrError;
import at.nineyards.anyline.modules.ocr.AnylineOcrListener;
import at.nineyards.anyline.modules.ocr.AnylineOcrResult;
import at.nineyards.anyline.modules.ocr.AnylineOcrScanView;

public class ValidarCuenta extends AppCompatActivity {

    private static final String TAG = ValidarCuenta.class.getSimpleName();
    private AnylineOcrScanView scanView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set the flag to keep the screen on (otherwise the screen may go dark during scanning)
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_validar_cuenta);

        String lic = getString(R.string.anyline_license);
        scanView = (AnylineOcrScanView) findViewById(R.id.scan_view);

        scanView.copyTrainedData("tessdata/eng_no_dict.traineddata", "d142032d86da1be4dbe22dce2eec18d7");
        scanView.copyTrainedData("tessdata/deu.traineddata", "2d5190b9b62e28fa6d17b728ca195776");

        // see ScanIbanActivity for a more detailed description
        AnylineOcrConfig anylineOcrConfig = new AnylineOcrConfig();
        anylineOcrConfig.setTesseractLanguages("eng_no_dict", "deu");
        anylineOcrConfig.setCharWhitelist("ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-.");
        anylineOcrConfig.setMinCharHeight(15);
        anylineOcrConfig.setMaxCharHeight(70);
        anylineOcrConfig.setMinConfidence(75);
        anylineOcrConfig.setValidationRegex("^([A-Z]+\\s*-*\\s*)?[0-9A-Z-\\s\\.]{3,}$");
        anylineOcrConfig.setScanMode(AnylineOcrConfig.ScanMode.LINE);
        anylineOcrConfig.setRemoveSmallContours(false);
        anylineOcrConfig.setRemoveWhitespaces(false);
        scanView.setAnylineOcrConfig(anylineOcrConfig);

        scanView.setConfig(new AnylineViewConfig(this, "record_view_config.json"));

        scanView.initAnyline(lic, new AnylineOcrListener() {
            @Override
            public void onReport(String identifier, Object value) {
            }

            @Override
            public boolean onTextOutlineDetected(List<PointF> list) {
                return false;
            }

            @Override
            public void onResult(AnylineOcrResult result) {
                if (result.getText() != null && !result.getText().isEmpty()) {
                    /*Intent i = new Intent(MainActivity.this, RecordSearchActivity.class);
                    i.putExtra(RecordSearchActivity.RECORD_INPUT, result.getText().trim());
                    startActivity(i);*/
                    Toast.makeText(ValidarCuenta.this,result.getText().trim(), Toast.LENGTH_SHORT).show();
                    finish(true, result.getText().trim());
                    //onPause();
                }
            }

            @Override
            public void onAbortRun(AnylineOcrError code, String message) {
            }
        });

        // disable the reporting if set to off in preferences
        scanView.setReportingEnabled(PreferenceManager.getDefaultSharedPreferences(this).getBoolean(SettingsFragment
                .KEY_PREF_REPORTING_ON, true));
    }
    public void finish(boolean _caso, String cuenta) {
        Intent data = new Intent();
        data.putExtra("response", _caso);
        data.putExtra("cuenta", cuenta);
        setResult(RESULT_OK, data);
        super.finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

        scanView.startScanning();
    }

    @Override
    protected void onPause() {
        super.onPause();

        scanView.cancelScanning();
        scanView.releaseCameraInBackground();
    }

}
