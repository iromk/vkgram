package pro.xite.dev.vkgram;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.bt_1) Button btOne;
    @BindView(R.id.bt_2) Button btTwo;
    @BindView(R.id.bt_3) Button btTri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
        ButterKnife.bind(this);
    }

    public void onButtonClick(View v) {
        if(v.isEnabled()) {
//            if (btOne.equals(v)) {// .isFocused()) {
            if (btOne.getId() == v.getId()) {// .isFocused()) {
                btTwo.requestFocus();
                btTwo.setEnabled(true);
                btOne.setEnabled(false);
            } else {
                btOne.requestFocus();
                btOne.setEnabled(true);
                btTwo.setEnabled(false);
            }
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
