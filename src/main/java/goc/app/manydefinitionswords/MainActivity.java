package goc.app.manydefinitionswords;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    String selectedFrequency = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        Button everyday = findViewById(R.id.everyday);
        Button frequent = findViewById(R.id.frequent);
        Button rare = findViewById(R.id.rare);

        Intent intent = new Intent(MainActivity.this, CategoriesActivity.class);

        ActivityResultLauncher<Intent> launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {

                    if (result.getResultCode() == Activity.RESULT_OK
                    && result.getData() != null) {

                        Intent data = result.getData();

                        if (data.getBooleanExtra("resetFrequency", false)) {
                            selectedFrequency = null;
                        }

                    }
                }
        );

        everyday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedFrequency = "everyday";
                intent.putExtra("key_frequency", selectedFrequency);
                launcher.launch(intent);
            }
        });
        frequent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedFrequency = "frequent";
                intent.putExtra("key_frequency", selectedFrequency);
                launcher.launch(intent);

            }
        });
        rare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedFrequency = "rare";
                intent.putExtra("key_frequency", selectedFrequency);
                launcher.launch(intent);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}