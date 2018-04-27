package pro.xite.dev.vkgram;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.StyleRes;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

public class ThemeSelectActivity extends AppCompatActivity {

    private static final String TAG = ThemeSelectActivity.class.getSimpleName();
    private @StyleRes int newTheme = Integer.MIN_VALUE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null) {
            newTheme = savedInstanceState.getInt("A");
            if(newTheme != Integer.MIN_VALUE)
                setTheme(newTheme);
        }
        setContentView(R.layout.activity_theme_select);
    }

    public void onButtonClick(View view) {
        newTheme = R.style.VkgramThemeIndigo;
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
        if(newTheme != Integer.MIN_VALUE) {
            Intent result = new Intent();
            result.putExtra("A", newTheme);
            setResult(2, result);
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
        outState.putInt("A", newTheme);
    }




}
