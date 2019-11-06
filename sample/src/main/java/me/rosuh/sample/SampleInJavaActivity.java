package me.rosuh.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import me.rosuh.filepicker.config.FilePickerManager;

public class SampleInJavaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_in_java);
        FilePickerManager.INSTANCE
                .from(this)
                .getConfirmText$filepicker_debug()
    }
}
