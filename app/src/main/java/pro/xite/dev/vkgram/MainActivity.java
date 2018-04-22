package pro.xite.dev.vkgram;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.textview_main_sample)
    TextView textviewSample;
    @BindView(R.id.textlayout_main_useremail)
    TextInputLayout inputLayoutUserEmail;
    @BindView(R.id.edittext_main_useremail)
    AppCompatEditText edittextUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
        ButterKnife.bind(this);
        EmailInputActionHandler editTextActionListenet = new EmailInputActionHandler(this);
        edittextUserEmail.setOnEditorActionListener(editTextActionListenet);
        edittextUserEmail.setOnFocusChangeListener(editTextActionListenet);
        edittextUserEmail.addTextChangedListener(editTextActionListenet);
    }

    public void onButtonClick(View v) {
        if (!isValidEmail(edittextUserEmail.getText())) {
            edittextUserEmail.setError("Incorrect input");
        } else {
            edittextUserEmail.setError(null);
        }
    }

    private boolean isValidEmail(CharSequence userInput) {
        if (userInput == null) return false;
        return Patterns.EMAIL_ADDRESS.matcher(userInput).matches();
    }

    private static final class EmailInputActionHandler
            implements TextWatcher, TextView.OnEditorActionListener, View.OnFocusChangeListener {

        private final WeakReference<MainActivity> activity;

        private final Pattern[] patterns = // in order of email completeness
                {
                        Pattern.compile("[a-zA-Z0-9+._%\\-+]{1,256}"),
                        Pattern.compile("[a-zA-Z0-9+._%\\-+]{1,256}\\@"),
                        Pattern.compile("[a-zA-Z0-9+._%\\-+]{1,256}\\@[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}"),
                        Pattern.compile("[a-zA-Z0-9+._%\\-+]{1,256}\\@([a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}\\.)+"),
                        Patterns.EMAIL_ADDRESS,
                };
        private final int NO_INPUT = 0;
        private final int TOTALLY_WRONG = 1;
        private final int TOTALLY_SIMILAR = patterns.length;

        private int similarity = 0;

        EmailInputActionHandler(MainActivity activity) {
            this.activity = new WeakReference<>(activity);
        }

        private int emailSimilarityIndex(CharSequence s) {
            similarity = NO_INPUT;
            if (s.length() > 0) {
                similarity = TOTALLY_WRONG;
                for (int i = TOTALLY_WRONG; i <= TOTALLY_SIMILAR; i++)
                    if (patterns[i - 1].matcher(s).matches()) {
                        similarity = i;
                        break;
                    }
            }
            Log.i(TAG, String.format("emailSimilarityIndex: %d", similarity));
            return similarity;
        }

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            return true;
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (activity.get() == null) return;
            Log.w(TAG, String.format("onFocusChange: %b", hasFocus));
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            if (activity.get() == null) return;

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (activity.get() == null) return;

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (activity.get() == null) return;
            if (s.length() == 0) {
                resetInputError();
                return;
            }
            if (emailSimilarityIndex(s) == TOTALLY_SIMILAR) {
                dismissError();
            } else {
                showError("Keep typing");
                Log.d(TAG, String.format("afterTextChanged: errorous %d", emailSimilarityIndex(s)));

            }
        }

        private void showError(final CharSequence msg) {
            activity.get().inputLayoutUserEmail.setError(msg);
            setEditColor(similarity);
        }

        private void dismissError() {
            activity.get().inputLayoutUserEmail.setError(null);
            setEditColor(TOTALLY_SIMILAR);
        }

        private void resetInputError() {
            activity.get().inputLayoutUserEmail.setError(null);
            setEditColor(0);
        }

        @SuppressLint("RestrictedApi")
        private void setEditColor(final int colorIndex) {
            final TypedArray colors = activity.get().getResources().obtainTypedArray(R.array.inputColorsOfSatisfaction);
            final ColorStateList colorStateList = ColorStateList.valueOf(colors.getColor(colorIndex, 0));
            activity.get().edittextUserEmail.setSupportBackgroundTintList(colorStateList);
            colors.recycle();
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
