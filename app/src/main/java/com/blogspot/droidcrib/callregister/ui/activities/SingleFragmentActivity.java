package com.blogspot.droidcrib.callregister.ui.activities;

// TODO: NavigationDrawer с настройками (какие звонки обрабатывать, для всех юзеров, из контакт листа, или не из листа )
// TODO: Цветоваяя схема
// TODO: Окно - панель для выбора действия по результату звонка (отмена, заметка, напоминание). Пропущенные звонки - в нотификейшене
// TODO: Переделать экран детализации
// TODO: Отработка разрешений в реальном времени - сделать правильно
// TODO: Напоминалка
// TODO: Переделать окно списка звонков: добавить юзерфото + значок заметки и/или напоминалки





import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.droidcrib.callregister.R;
import com.blogspot.droidcrib.callregister.telephony.ContactsProvider;
import com.blogspot.droidcrib.callregister.ui.fragments.CallDetailsFragment;
import com.blogspot.droidcrib.callregister.ui.fragments.CallsListFragment;

/**
 *
 */
public class SingleFragmentActivity extends AppCompatActivity {

    private FragmentManager mFragmentManager;
    private Fragment mFragment;
    private Toolbar mToolbar;
    private TextView mToolbarTextHeader;
    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_single_fragment);

        // Explicitly call to get permission in Android 6
        readPhoneStateWrapper();

        mFragmentManager = getSupportFragmentManager();
        mFragment = mFragmentManager.findFragmentById(R.id.id_fragment_container);
        if (mFragment == null) {
            mFragment = CallsListFragment.getInstance();
            mFragmentManager.beginTransaction()
                    .add(R.id.id_fragment_container, mFragment)
                    .commit();
        }

        // Toolbar setup
        mToolbar = (Toolbar) findViewById(R.id.id_toolbar);
        setSupportActionBar(mToolbar);
        mToolbarTextHeader = (TextView) mToolbar.findViewById(R.id.id_text_view_toolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setListFragment();
            }
        });

    }

    @Override
    public void onBackPressed() {

        // Go back to calls list
        Object f = mFragmentManager.findFragmentById(R.id.id_fragment_container);
        if (f instanceof CallDetailsFragment) {
            setListFragment();
            return;
        }

        // Exit program on second click
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, R.string.press_back_to_exit, Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }


    public void setToolbarTextHeader(String text) {
        mToolbarTextHeader.setText(text);
    }

    public void setDetailsFragment(long id) {
        mFragment = CallDetailsFragment.newInstance(id);
        mFragmentManager.beginTransaction()
                .replace(R.id.id_fragment_container, mFragment)
                .commit();
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
    }

    public void setListFragment() {
        mFragment = CallsListFragment.getInstance();
        mFragmentManager.beginTransaction()
                .replace(R.id.id_fragment_container, mFragment)
                .commit();
        mToolbar.setNavigationIcon(null);
    }


    private void readPhoneStateWrapper() {
        int hasReadContactsPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE);
        // Check permission
        if (hasReadContactsPermission != PackageManager.PERMISSION_GRANTED) {
            // Show explanation about permission reason request if denied before
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_PHONE_STATE)) {
                showMessageOKCancel(getResources().getString(R.string.access_phone_state),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(SingleFragmentActivity.this,
                                        new String[]{Manifest.permission.READ_PHONE_STATE},
                                        111);
                            }
                        });
                return;
            }
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE},
                    111);
            return;
        }
        // PERMISSION_GRANTED. Do action here
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        int state = telephonyManager.getCallState();
        Log.d("Tel_EXTRA_STATE", "Call state:" + state);

    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton(getResources().getString(R.string.ok), okListener)
                .setNegativeButton(getResources().getString(R.string.cancel), null)
                .create()
                .show();
    }

}
