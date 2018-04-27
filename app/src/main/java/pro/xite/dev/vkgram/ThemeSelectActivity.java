package pro.xite.dev.vkgram;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.StyleRes;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

public class ThemeSelectActivity extends AppCompatActivity {

    public static final int NONE = Integer.MIN_VALUE;
    public static final int RESULT_THEME_CHANGED = 5342;
    public static final int RESULT_NOTHING_CHANGED = 41902;

    public static final String KEY_THEME_ID = "ThemeSelectActivity.ThemeId";

    private static final String TAG = ThemeSelectActivity.class.getSimpleName();
    private @StyleRes int newTheme = Integer.MIN_VALUE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setResult(RESULT_NOTHING_CHANGED);
        if(savedInstanceState != null) {
            newTheme = savedInstanceState.getInt(KEY_THEME_ID);
            if(newTheme != NONE)
                setTheme(newTheme);
        } else {
            final SharedPreferences prefSettings = getSharedPreferences("HW2.SETTINGS", MODE_PRIVATE);
            final @StyleRes int savedTheme = prefSettings.getInt(KEY_THEME_ID, NONE);
            if(savedTheme != NONE)
                setTheme(savedTheme);
        }
        setContentView(R.layout.activity_theme_select);
    }

    public void onButtonClick(View view) {
        if(view.getId() == R.id.btn_indigo)
            newTheme = R.style.VkgramThemeIndigo;
        if(view.getId() == R.id.btn_greengo)
            newTheme = R.style.VkgramThemeGreengo;
        recreate();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.w(TAG, "onOptionsItemSelected: ");
        finish();
        return true;
    }

    @Override
    protected void onDestroy() {
        Log.w(TAG, "onDestroy: ");
        super.onDestroy();
    }

    @Override
    public void finish() {
        Log.w(TAG, "finish: ");
        if(newTheme != NONE) {
            Intent result = new Intent();
            result.putExtra(KEY_THEME_ID, newTheme);
            setResult(RESULT_THEME_CHANGED, result);
        }
        super.finish();
    }

    @Override
    public void onBackPressed() {
        Log.w(TAG, "onBackPressed: ");
        super.onBackPressed();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_THEME_ID, newTheme);
    }




}
