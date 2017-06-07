package com.blogspot.droidcrib.callregister.ui.activities;

// TODO: Таймпикер в диалоге: дата + время в таб лейауте
// TODO: Пропущенные звонки - в нотификейшене (id нотификейшена - номер телефона)
// TODO: Переделать экран детализации под материал дизайн с координатор лейаутом + две фаб: позвонить, сообщение (смс, вайбер етс)
// TODO: Отработка разрешений в реальном времени - сделать правильно
// TODO: Напоминалка
// TODO: Переделать окно списка звонков: добавить юзерфото + значок заметки и/или напоминалки
// TODO: при срабаптывании нотификейшена для контакта - позвонить ему прямо из приложения


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.blogspot.droidcrib.callregister.R;
import com.blogspot.droidcrib.callregister.ui.adapters.TabsPagerAdapter;
import com.blogspot.droidcrib.callregister.ui.fragments.CallDetailsFragment;
import com.blogspot.droidcrib.callregister.ui.fragments.CallsListFragment;

import static com.blogspot.droidcrib.callregister.contract.Constants.IS_CATCH_INCOMINGS;
import static com.blogspot.droidcrib.callregister.contract.Constants.IS_CATCH_MISSED;
import static com.blogspot.droidcrib.callregister.contract.Constants.IS_CATCH_OUTGOINGS;
import static com.blogspot.droidcrib.callregister.contract.Constants.SHARED_PREFS;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FragmentManager mFragmentManager;
    private Fragment mFragment;
    private boolean doubleBackToExitPressedOnce = false;
    private CheckBox mCheckBoxIncoming;
    private CheckBox mCheckBoxOutgoing;
    private CheckBox mCheckBoxMissed;
    private MenuItem mCheckBoxItemIncoming;
    private MenuItem mCheckBoxItemOutgoing;
    private MenuItem mCheckBoxItemMissed;
    private Boolean isCatchIncomings;
    private Boolean isCatchOutgoings;
    private Boolean isCatchMissed;
    private SharedPreferences mPrefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mPrefs = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView nav = (NavigationView) findViewById(R.id.nav_view);
        nav.setNavigationItemSelectedListener(this);



        // Get elements of navigation view
        mCheckBoxItemIncoming = nav.getMenu().findItem(R.id.nav_incoming);
        mCheckBoxItemOutgoing = nav.getMenu().findItem(R.id.nav_outgoing);
        mCheckBoxItemMissed = nav.getMenu().findItem(R.id.nav_missed);
        mCheckBoxIncoming = (CheckBox) MenuItemCompat.getActionView(mCheckBoxItemIncoming);
        mCheckBoxOutgoing = (CheckBox) MenuItemCompat.getActionView(mCheckBoxItemOutgoing);
        mCheckBoxMissed = (CheckBox) MenuItemCompat.getActionView(mCheckBoxItemMissed);

        //Get here checkbox values from shared prefs and apply to views
        isCatchIncomings = mPrefs.getBoolean(IS_CATCH_INCOMINGS, true);
        isCatchOutgoings = mPrefs.getBoolean(IS_CATCH_OUTGOINGS, true);
        isCatchMissed = mPrefs.getBoolean(IS_CATCH_MISSED, true);

        // Setup textboxes
        mCheckBoxIncoming.setChecked(isCatchIncomings);
        mCheckBoxOutgoing.setChecked(isCatchOutgoings);
        mCheckBoxMissed.setChecked(isCatchMissed);
        mCheckBoxIncoming.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mPrefs.edit().putBoolean(IS_CATCH_INCOMINGS, isChecked).apply();
            }
        });
        mCheckBoxOutgoing.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mPrefs.edit().putBoolean(IS_CATCH_OUTGOINGS, isChecked).apply();
            }
        });
        mCheckBoxMissed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mPrefs.edit().putBoolean(IS_CATCH_MISSED, isChecked).apply();
            }
        });

        //Setup list fragment
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
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

        /**
         *  Moved from SingleFragmentActivity
         */
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_incoming) {

        } else if (id == R.id.nav_outgoing) {

        } else if (id == R.id.nav_missed) {

        } else if (id == R.id.nav_note) {

        } else if (id == R.id.nav_alarm) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Moved from SingleFragmentActivity
     */

    public void setDetailsFragment(long id) {
        mFragment = CallDetailsFragment.newInstance(id);
        mFragmentManager.beginTransaction()
                .replace(R.id.id_fragment_container, mFragment)
                .commit();
    }

    public void setListFragment() {
        mFragment = CallsListFragment.getInstance();
        mFragmentManager.beginTransaction()
                .replace(R.id.id_fragment_container, mFragment)
                .commit();
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
                                ActivityCompat.requestPermissions(MainActivity.this,
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
