package goc.app.manydefinitionswords;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import kotlinx.coroutines.MainCoroutineDispatcher;


public class CategoriesActivity extends AppCompatActivity {

    String frequency, theme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        frequency = getIntent().getStringExtra("key_frequency");

        int[] buttonsIds = {
                R.id.home, R.id.food, R.id.people, R.id.health, R.id.nature,
                R.id.society, R.id.work, R.id.leisure, R.id.travel, R.id.tech
        };

        for (int id : buttonsIds) {

            Button btn = findViewById(id);
            btn.setOnClickListener(v ->{
                theme = getResources().getResourceEntryName(v.getId());

                openActivityDict(frequency, theme);
            });
        }

        ImageButton back = findViewById(R.id.back);
        back.setOnClickListener(v -> {

            Intent result = new Intent();
            result.putExtra("resetFrequency", true);
            setResult(RESULT_OK, result);
            finish();
        });
    }

    public void openActivityDict(String frequency, String theme) {

        Intent intent = new Intent(CategoriesActivity.this, DictionaryActivity.class);
        intent.putExtra("key_frequency", frequency);
        intent.putExtra("key_theme", theme);
        startActivity(intent);
    }
}