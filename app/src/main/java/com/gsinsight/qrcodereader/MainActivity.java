package com.gsinsight.qrcodereader;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// MainActivity.java에서 import 추가
import com.gsinsight.qrcodereader.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "QRScanner";
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;

    private ActivityMainBinding binding;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private BarcodeScanner barcodeScanner;
    private ExecutorService cameraExecutor;
    private boolean isFlashOn = false;
    private ProcessCameraProvider cameraProvider;
    private boolean isScanning = true;

    // 권한 요청 런처
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    startCamera();
                } else {
                    Toast.makeText(this, "카메라 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                    finish();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initializeComponents();
        setupEventListeners();
        checkCameraPermission();
    }

    private void initializeComponents() {
        // ML Kit 바코드 스캐너 초기화
        BarcodeScannerOptions options = new BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                .build();
        barcodeScanner = BarcodeScanning.getClient(options);

        // 카메라 실행자 초기화
        cameraExecutor = Executors.newSingleThreadExecutor();

        // 카메라 프로바이더 준비
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
    }

    private void setupEventListeners() {
        // 플래시 토글 버튼
        binding.flashButton.setOnClickListener(v -> toggleFlash());
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void startCamera() {
        cameraProviderFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderFuture.get();
                bindCameraUseCases();
                updateStatusText("스캔 준비 완료");
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "카메라 시작 실패", e);
                Toast.makeText(this, "카메라를 시작할 수 없습니다.", Toast.LENGTH_LONG).show();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void bindCameraUseCases() {
        // 프리뷰 설정
        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(binding.previewView.getSurfaceProvider());

        // 이미지 분석 설정 (QR 코드 스캔)
        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();

        imageAnalysis.setAnalyzer(cameraExecutor, this::analyzeImage);

        // 카메라 선택 (후면 카메라)
        CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

        try {
            // 기존 바인딩 해제
            cameraProvider.unbindAll();

            // 새 바인딩 적용
            cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis);

        } catch (Exception e) {
            Log.e(TAG, "카메라 바인딩 실패", e);
        }
    }

    private void analyzeImage(@NonNull ImageProxy imageProxy) {
        if (!isScanning) {
            imageProxy.close();
            return;
        }

        @SuppressWarnings("UnsafeOptInUsageError")
        InputImage image = InputImage.fromMediaImage(
                imageProxy.getImage(),
                imageProxy.getImageInfo().getRotationDegrees()
        );

        Task<List<Barcode>> result = barcodeScanner.process(image)
                .addOnSuccessListener(this::processBarcodes)
                .addOnFailureListener(e -> {
                    Log.e(TAG, "바코드 스캔 실패", e);
                    updateStatusText("스캔 중 오류 발생");
                })
                .addOnCompleteListener(task -> imageProxy.close());
    }

    private void processBarcodes(List<Barcode> barcodes) {
        for (Barcode barcode : barcodes) {
            String rawValue = barcode.getRawValue();
            if (rawValue != null && !rawValue.isEmpty()) {
                // 스캔 성공 - 결과 화면으로 이동
                isScanning = false;
                updateStatusText("QR 코드 인식 완료!");

                Intent intent = new Intent(this, ResultActivity.class);
                intent.putExtra("qr_content", rawValue);
                intent.putExtra("qr_format", getQRFormat(barcode));
                startActivity(intent);
                return;
            }
        }

        // QR 코드가 감지되었지만 내용이 없는 경우
        if (!barcodes.isEmpty()) {
            updateStatusText("QR 코드를 더 명확하게 맞춰주세요");
        }
    }

    private String getQRFormat(Barcode barcode) {
        switch (barcode.getFormat()) {
            case Barcode.FORMAT_QR_CODE:
                return "QR_CODE";
            case Barcode.FORMAT_DATA_MATRIX:
                return "DATA_MATRIX";
            case Barcode.FORMAT_PDF417:
                return "PDF417";
            case Barcode.FORMAT_AZTEC:
                return "AZTEC";
            default:
                return "UNKNOWN";
        }
    }

    private void toggleFlash() {
        // 플래시 기능 구현 (CameraX 사용)
        isFlashOn = !isFlashOn;

        if (isFlashOn) {
            binding.flashButton.setImageResource(R.drawable.ic_flash_on);
            Toast.makeText(this, "플래시 켜짐", Toast.LENGTH_SHORT).show();
        } else {
            binding.flashButton.setImageResource(R.drawable.ic_flash_off);
            Toast.makeText(this, "플래시 꺼짐", Toast.LENGTH_SHORT).show();
        }

        // 실제 플래시 제어는 Camera2 API나 CameraX의 고급 기능 필요
        // 여기서는 UI 업데이트만 수행
    }

    private void updateStatusText(String status) {
        runOnUiThread(() -> binding.statusText.setText(status));
    }

    @Override
    protected void onResume() {
        super.onResume();
        isScanning = true;
        updateStatusText("QR 코드를 찾는 중...");
    }

    @Override
    protected void onPause() {
        super.onPause();
        isScanning = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraExecutor != null) {
            cameraExecutor.shutdown();
        }
        if (barcodeScanner != null) {
            barcodeScanner.close();
        }
    }
}