package com.gsinsight.qrcodereader;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.gsinsight.qrcodereader.databinding.ActivityResultBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ResultActivity extends AppCompatActivity {

    private ActivityResultBinding binding;
    private String qrContent;
    private String qrFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getIntentData();
        setupUI();
        setupEventListeners();
    }

    private void getIntentData() {
        Intent intent = getIntent();
        qrContent = intent.getStringExtra("qr_content");
        qrFormat = intent.getStringExtra("qr_format");

        if (qrContent == null) {
            qrContent = "내용을 읽을 수 없습니다.";
        }
        if (qrFormat == null) {
            qrFormat = "UNKNOWN";
        }
    }

    private void setupUI() {
        // QR 코드 내용 표시
        binding.qrContentText.setText(qrContent);

        // QR 코드 타입 표시
        String qrType = getQRTypeDescription(qrContent);
        binding.qrTypeText.setText(qrType);

        // 스캔 시간 표시
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        binding.scanTimeText.setText(sdf.format(new Date()));

        // URL인 경우 버튼 텍스트 변경
        if (qrType.equals("URL")) {
            binding.shareButton.setText("열기");
            binding.shareButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_open_in_browser, 0, 0);
        }
    }

    private void setupEventListeners() {
        // 복사 버튼
        binding.copyButton.setOnClickListener(v -> copyToClipboard());

        // 공유/열기 버튼 - QR 타입에 따라 다른 동작
        binding.shareButton.setOnClickListener(v -> {
            String qrType = getQRTypeDescription(qrContent);
            if (qrType.equals("URL")) {
                openUrl(qrContent);
            } else if (qrType.equals("전화번호")) {
                dialPhone(qrContent);
            } else if (qrType.equals("이메일")) {
                sendEmail(qrContent);
            } else if (qrType.equals("SMS")) {
                sendSms(qrContent);
            } else {
                shareContent();
            }
        });

        // 다시 스캔 버튼
        binding.scanAgainButton.setOnClickListener(v -> scanAgain());
    }

    private String getQRTypeDescription(String content) {
        if (content == null || content.isEmpty()) return "텍스트";

        String lowerContent = content.toLowerCase().trim();

        if (lowerContent.startsWith("http://") || lowerContent.startsWith("https://")) {
            return "URL";
        } else if (lowerContent.startsWith("tel:") || (content.matches("^[+]?[0-9\\-\\s\\(\\)]+$") && content.replaceAll("[^0-9]", "").length() >= 8)) {
            return "전화번호";
        } else if (lowerContent.startsWith("mailto:") || (content.contains("@") && content.contains(".") && !content.contains(" "))) {
            return "이메일";
        } else if (lowerContent.startsWith("sms:") || lowerContent.startsWith("smsto:")) {
            return "SMS";
        } else if (lowerContent.startsWith("wifi:")) {
            return "WiFi";
        } else if (lowerContent.startsWith("geo:") || lowerContent.contains("maps.google.com") || lowerContent.contains("maps.app.goo.gl")) {
            return "위치";
        } else if (content.matches("^\\d+$")) {
            return "숫자";
        } else if (lowerContent.startsWith("begin:vcard") || lowerContent.contains("vcard")) {
            return "연락처";
        } else if (lowerContent.startsWith("begin:vevent") || lowerContent.contains("vevent")) {
            return "이벤트";
        } else {
            return "텍스트";
        }
    }

    private void copyToClipboard() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("QR Code Content", qrContent);
        clipboard.setPrimaryClip(clip);

        Toast.makeText(this, "클립보드에 복사되었습니다", Toast.LENGTH_SHORT).show();
    }

    private void shareContent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, qrContent);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "QR 코드 내용");

        try {
            startActivity(Intent.createChooser(shareIntent, "QR 코드 내용 공유"));
        } catch (Exception e) {
            Toast.makeText(this, "공유할 수 있는 앱이 없습니다", Toast.LENGTH_SHORT).show();
        }
    }

    private void openUrl(String url) {
        try {
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "https://" + url;
            }
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "URL을 열 수 없습니다", Toast.LENGTH_SHORT).show();
        }
    }

    private void dialPhone(String phoneNumber) {
        try {
            String cleanNumber = phoneNumber.replace("tel:", "");
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + cleanNumber));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "전화 앱을 열 수 없습니다", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendEmail(String email) {
        try {
            String cleanEmail = email.replace("mailto:", "");
            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + cleanEmail));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "이메일 앱을 열 수 없습니다", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendSms(String sms) {
        try {
            String cleanSms = sms.replace("sms:", "").replace("smsto:", "");
            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + cleanSms));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "SMS 앱을 열 수 없습니다", Toast.LENGTH_SHORT).show();
        }
    }

    private void scanAgain() {
        finish(); // 현재 액티비티를 종료하여 메인 화면으로 돌아감
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}