package pro.xite.dev.vkgram;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.StyleRes;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private @StyleRes int theme = R.style.VkgramThemeGreengo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final SharedPreferences prefSettings = getSharedPreferences("HW2.SETTINGS", MODE_PRIVATE);
        if(savedInstanceState != null) {
            theme = savedInstanceState.getInt(ThemeSelectActivity.KEY_THEME_ID);
            prefSettings.edit().putInt(ThemeSelectActivity.KEY_THEME_ID, theme).apply();
        } else {
            @StyleRes int savedTheme = prefSettings.getInt(ThemeSelectActivity.KEY_THEME_ID, ThemeSelectActivity.NONE);
            if(savedTheme != ThemeSelectActivity.NONE)
                theme = savedTheme;
        }
        setTheme(theme);
        setContentView(R.layout.content_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, ThemeSelectActivity.class);
            startActivityForResult(intent, 0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ThemeSelectActivity.KEY_THEME_ID, theme);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.w(TAG, "onActivityResult: ");
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == ThemeSelectActivity.RESULT_THEME_CHANGED && data != null) {
            final @StyleRes int theme = data.getIntExtra(ThemeSelectActivity.KEY_THEME_ID, ThemeSelectActivity.NONE);
            if (theme != ThemeSelectActivity.NONE) {
                this.theme = theme;
                recreate();
            }
        }
    }
}
