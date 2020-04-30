package me.rosuh.sample;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.jetbrains.annotations.NotNull;

import me.rosuh.filepicker.adapter.FileListAdapter;
import me.rosuh.filepicker.config.FileItemOnClickListener;
import me.rosuh.filepicker.config.FilePickerManager;

public class SampleInJavaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_activity_main);
        FilePickerManager.INSTANCE
                .from(this)
                .setItemClickListener(new FileItemOnClickListener() {
                    @Override
                    public void onItemClick(@NotNull FileListAdapter itemAdapter, @NotNull View itemView, int position) {
                        if (itemAdapter.getDataList() != null) {
                            Toast.makeText(SampleInJavaActivity.this, itemAdapter.getDataList().get(position).getFileName(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onItemChildClick(@NotNull FileListAdapter itemAdapter, @NotNull View itemView, int position) {
                        if (itemAdapter.getDataList() != null) {
                            Toast.makeText(SampleInJavaActivity.this, itemAdapter.getDataList().get(position).getFileName(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onItemLongClick(@NotNull FileListAdapter itemAdapter, @NotNull View itemView, int position) {
                        if (itemAdapter.getDataList() != null) {
                            Toast.makeText(SampleInJavaActivity.this, itemAdapter.getDataList().get(position).getFileName(), Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .enableSingleChoice()
                .forResult(101);
//        FilePickerManager.INSTANCE
    }
}
