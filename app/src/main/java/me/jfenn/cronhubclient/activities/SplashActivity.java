package me.jfenn.cronhubclient.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import me.jfenn.cronhubclient.CronHub;
import me.jfenn.cronhubclient.R;
import me.jfenn.cronhubclient.data.PreferenceData;
import me.jfenn.cronhubclient.data.request.MonitorListRequest;
import me.jfenn.cronhubclient.data.request.Request;

public class SplashActivity extends AppCompatActivity implements Request.OnInitListener {

    private CronHub cronHub;

    private View signInView;
    private EditText apiKeyView;
    private View signInButtonView;
    private TextView linksView;

    private String key;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        cronHub = (CronHub) getApplicationContext();

        signInView = findViewById(R.id.signin);
        apiKeyView = findViewById(R.id.apiKey);
        signInButtonView = findViewById(R.id.signInButton);
        linksView = findViewById(R.id.links);

        key = PreferenceData.API_KEY.getValue(this);
        if (key != null && key.length() > 0)
            startRequest();
        else signInView.setVisibility(View.VISIBLE);

        signInButtonView.setOnClickListener(v -> {
            if (apiKeyView.getText().toString().length() > 0) {
                signInView.setVisibility(View.GONE);
                key = apiKeyView.getText().toString();
                startRequest();
            } else Toast.makeText(SplashActivity.this, "Error: missing API key", Toast.LENGTH_SHORT).show();
        });

        linksView.setText(Html.fromHtml(getString(R.string.msg_links)));
        linksView.setMovementMethod(new LinkMovementMethod());
    }

    private void startRequest() {
        MonitorListRequest request = new MonitorListRequest();
        request.addOnInitListener(this);
        cronHub.addRequest(request, key);
    }

    @Override
    public void onInit(Request data) {
        if (data instanceof MonitorListRequest) {
            PreferenceData.API_KEY.setValue(SplashActivity.this, key);
            startActivity(new Intent(this, MainActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }
    }

    @Override
    public void onFailure(Request data, String message) {
        findViewById(R.id.signin).setVisibility(View.VISIBLE);
        Toast.makeText(this, "Authentication failed: " + message, Toast.LENGTH_SHORT).show();
    }
}
