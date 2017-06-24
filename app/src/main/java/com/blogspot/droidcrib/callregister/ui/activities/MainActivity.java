package com.blogspot.droidcrib.callregister.ui.activities;

// TODO: Пропущенные звонки - в нотификейшене (id нотификейшена - номер телефона)
// TODO: Переделать экран детализации под материал дизайн с координатор лейаутом + две фаб: позвонить, сообщение (смс, вайбер етс)
// TODO: Отработка разрешений в реальном времени - сделать правильно
// TODO: Напоминалка
// TODO: Переделать окно списка звонков: добавить юзерфото + значок заметки и/или напоминалки
// TODO: при срабаптывании нотификейшена для контакта - позвонить ему прямо из приложения


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.blogspot.droidcrib.callregister.R;
import com.blogspot.droidcrib.callregister.eventbus.AlarmsListLoadFinishedEvent;
import com.blogspot.droidcrib.callregister.eventbus.NewCallEvent;
import com.blogspot.droidcrib.callregister.eventbus.NewNoteEvent;
import com.blogspot.droidcrib.callregister.model.NoteRecord;
import com.blogspot.droidcrib.callregister.ui.adapters.MainTabsPagerAdapter;
import com.blogspot.droidcrib.callregister.ui.adapters.ReminderTabsPagerAdapter;
import com.blogspot.droidcrib.callregister.ui.fragments.AlarmsListFragment;
import com.blogspot.droidcrib.callregister.ui.fragments.CallDetailsFragment;
import com.blogspot.droidcrib.callregister.ui.fragments.CallsListFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import static com.blogspot.droidcrib.callregister.contract.Constants.ACTION_SHOW_ALARM_DETAILS_IN_LIST;
import static com.blogspot.droidcrib.callregister.contract.Constants.EXTRA_ALARM_RECORD_ID;
import static com.blogspot.droidcrib.callregister.contract.Constants.EXTRA_CALL_RECORD_ID;
import static com.blogspot.droidcrib.callregister.contract.Constants.INTENT_TXT;
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
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private String mNoteText;
    private MainTabsPagerAdapter adapter;
    long alarmRecordId;
    String action;
    String extra;


    private static final String TAG = "trace_notifications";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //  Setup TabLayout
        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mTabLayout.addTab(mTabLayout.newTab().setText("Calls"));
        mTabLayout.addTab(mTabLayout.newTab().setText("Alarms"));
        mTabLayout.addTab(mTabLayout.newTab().setText("Notes"));
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        //  Setup ViewPager
        mViewPager = (ViewPager) findViewById(R.id.pager);
        adapter = new MainTabsPagerAdapter(getSupportFragmentManager(), mTabLayout.getTabCount());
        mViewPager.setAdapter(adapter);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout) {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position == 0) {
                    // do something with content
                }
                if (position == 1) {
                    // do something with content
                }
            }
        });

        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
                //Log.d(TAG, "Registered fragment: " + adapter.getRegisteredFragment(mViewPager.getCurrentItem()));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        action = getIntent().getAction();
        alarmRecordId = getIntent().getLongExtra(EXTRA_ALARM_RECORD_ID, -1);
        extra = getIntent().getStringExtra(INTENT_TXT);

        Log.d(TAG, "main activity action received = " + action);
        Log.d(TAG, "main activity extra received = " + alarmRecordId);
        Log.d(TAG, "main activity extra received = " + extra);

        if (action.equals(ACTION_SHOW_ALARM_DETAILS_IN_LIST)) {
            Log.d(TAG, "--- setup alarms tab ");
            mViewPager.setCurrentItem(1);
        }


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


    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);

    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = getIntent();

        Log.d(TAG, "onNewIntent ");
        action = intent.getAction();
        alarmRecordId = intent.getLongExtra(EXTRA_ALARM_RECORD_ID, -1);
        extra = intent.getStringExtra(INTENT_TXT);

        Log.d(TAG, "onResume onNewIntent main activity action received = " + action);
        Log.d(TAG, "onResume onNewIntent main activity extra received = " + alarmRecordId);
        Log.d(TAG, "onResume onNewIntent main activity extra received = " + extra);

        if (action.equals(ACTION_SHOW_ALARM_DETAILS_IN_LIST)) {
            Log.d(TAG, "onNewIntent setup alarms tab ");
            mViewPager.setCurrentItem(1);
        }


//        mViewPager.setCurrentItem(1);
        //Log.d(TAG, "Registered fragment 1: " + adapter.getRegisteredFragment(mViewPager.getCurrentItem()));

    }


    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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
            newMemoDialog();
        } else if (id == R.id.nav_alarm) {
            Intent intent = new Intent(this, NewReminderActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

//    /**
//     * Moved from SingleFragmentActivity
//     */
//
//    public void setDetailsFragment(long id) {
//
//
//        mFragment = CallDetailsFragment.newInstance(id);
//
//        Log.d(TAG, "mFragment is null = " + mFragment);
//        Log.d(TAG, "mFragmentManager is null = " + mFragmentManager);
//
//        mFragmentManager.beginTransaction()
//                .replace(R.id.id_fragment_container, mFragment)
//                .commit();
//    }
//
//    public void setListFragment() {
//        mFragment = CallsListFragment.getInstance();
//        mFragmentManager.beginTransaction()
//                .replace(R.id.id_fragment_container, mFragment)
//                .commit();
//    }


    private void newMemoDialog() {
        // Messages
        String msg = getString(R.string.memo);
        String buttonYes = getString(R.string.close);
        // EditText setup
        final EditText input = new EditText(MainActivity.this);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        input.setLines(5);
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    mNoteText = s.toString();
                }
            }
        });

        final AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setView(input)
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton(buttonYes, new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog,
                                        @SuppressWarnings("unused") final int id) {
                        Log.d(TAG, "Saving new memoShort here :" + mNoteText);
                        NoteRecord.insert(mNoteText, null);
                        EventBus.getDefault().post(new NewNoteEvent());
                    }
                });

        final AlertDialog alert = builder.create();
        alert.show();
        // Center button
        final Button positiveButton = alert.getButton(AlertDialog.BUTTON_POSITIVE);
        ViewGroup.LayoutParams positiveButtonLL = positiveButton.getLayoutParams();
        positiveButtonLL.width = ViewGroup.LayoutParams.MATCH_PARENT;
        positiveButton.setLayoutParams(positiveButtonLL);
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

    @Subscribe
    public void onEvent(AlarmsListLoadFinishedEvent event) {
        Log.d(TAG, "AlarmsListLoadFinishedEvent ");
        AlarmsListFragment fragment = (AlarmsListFragment) adapter.getRegisteredFragment(mViewPager.getCurrentItem());
        fragment.scrollToListItem(alarmRecordId);
    }

}
