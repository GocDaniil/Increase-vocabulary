package goc.app.manydefinitionswords;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

public class DictionaryActivity extends AppCompatActivity {

    SQLiteDatabase db;
    String frequency, theme;
    TextView word, definition;
    Button other;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictionary);

        word = findViewById(R.id.word);
        definition = findViewById(R.id.definition);
        other = findViewById(R.id.other);

        frequency = getIntent().getStringExtra("key_frequency");
        theme = getIntent().getStringExtra("key_theme");

        try {
            copyDatabaseIfNotExists();
        } catch (RuntimeException e) {
            Log.e("DB", "Copy failed", e);
            // Показываем пользователю и аккуратно закрываем Activity — далее приложение не сможет работать без БД
            Toast.makeText(this, "Не удалось подготовить базу: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
            return;
        }


        db = SQLiteDatabase.openDatabase(
                getDatabasePath("1.db").getPath(),
                null,
                SQLiteDatabase.OPEN_READONLY
        );

        randWord();

        other.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                randWord();
            }
        });

        ImageButton back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent result = new Intent();
                result.putExtra("resetTheme", true);
                setResult(RESULT_OK, result);
                finish();
            }
        });
    }

    private void randWord() {

        String sql =
                "SELECT d.word, d.definition " +
                        "FROM definition d " +
                        "JOIN theme t ON d.theme_id = t.id " +
                        "JOIN frequency f ON t.frequency_id = f.id " +
                        "WHERE f.type = ? AND t.type = ? " +
                        "ORDER BY RANDOM() LIMIT 1";

        Cursor cursor = db.rawQuery(sql, new String[]{frequency, theme});


        if (cursor.moveToFirst()) {
            word.setText(cursor.getString(0));
            definition.setText(cursor.getString(1));
        }

        cursor.close();
    }

    private void copyDatabaseIfNotExists() {
        String dbName = "1.db";
        File dbFile = getDatabasePath(dbName);

        if (dbFile.exists()) {
            Log.d("DB", "Database already exists: " + dbFile.getAbsolutePath());
            return;
        }

        File parent = dbFile.getParentFile();
        if (parent != null && !parent.exists()) {
            boolean made = parent.mkdirs();
            Log.d("DB", "Created databases dir: " + made + " -> " + parent.getAbsolutePath());
        }

        AssetManager am = getAssets();
        String[] candidates = new String[]{
                "databases/" + dbName, // предпочитаемый путь
                dbName                  // запасной — если файл лежит прямо в assets
        };

        InputStream is = null;
        try {
            for (String candidate : candidates) {
                try {
                    is = am.open(candidate);
                    Log.d("DB", "Found asset at: " + candidate);
                    break;
                } catch (IOException e) {
                    Log.d("DB", "Asset not found at: " + candidate);
                }
            }

            if (is == null) {
                // для отладки — вывести список assets и содержимое папки databases (если есть)
                try {
                    String[] root = am.list("");
                    Log.d("DB", "assets root: " + Arrays.toString(root));
                    String[] dbs = am.list("databases");
                    Log.d("DB", "assets/databases: " + Arrays.toString(dbs));
                } catch (IOException ignored) {
                }

                Toast.makeText(this, "Database asset not found. Поместите 1.db в app/src/main/assets/databases/ или в app/src/main/assets/", Toast.LENGTH_LONG).show();
                throw new RuntimeException("Asset not found: databases/" + dbName + " or " + dbName);
            }

            try (OutputStream os = new FileOutputStream(dbFile)) {
                byte[] buffer = new byte[4096];
                int length;
                while ((length = is.read(buffer)) > 0) {
                    os.write(buffer, 0, length);
                }
                os.flush();
                Log.d("DB", "Database copied to: " + dbFile.getAbsolutePath());
            } finally {
                try {
                    is.close();
                } catch (IOException ignored) {
                }
            }

        } catch (IOException e) {
            Log.e("DB", "Failed to copy database from assets", e);
            Toast.makeText(this, "Ошибка копирования базы данных: " + e.getMessage(), Toast.LENGTH_LONG).show();
            throw new RuntimeException("Ошибка копирования базы данных", e);
        }
    }
}

