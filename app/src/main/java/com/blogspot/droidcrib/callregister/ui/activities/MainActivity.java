package com.blogspot.droidcrib.callregister.ui.activities;

// TODO: Отработка разрешений в реальном времени - сделать правильно
// TODO: vibration on reminder


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.blogspot.droidcrib.callregister.R;
import com.blogspot.droidcrib.callregister.eventbus.AlarmsListLoadFinishedEvent;
import com.blogspot.droidcrib.callregister.eventbus.NewNoteEvent;
import com.blogspot.droidcrib.callregister.model.NoteRecord;
import com.blogspot.droidcrib.callregister.ui.adapters.MainTabsPagerAdapter;
import com.blogspot.droidcrib.callregister.ui.fragments.AlarmsListFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.blogspot.droidcrib.callregister.contract.Constants.ACTION_SHOW_ALARM_DETAILS_IN_LIST;
import static com.blogspot.droidcrib.callregister.contract.Constants.EXTRA_ALARM_RECORD_ID;
import static com.blogspot.droidcrib.callregister.contract.Constants.INTENT_TXT;
import static com.blogspot.droidcrib.callregister.contract.Constants.IS_CATCH_INCOMINGS;
import static com.blogspot.droidcrib.callregister.contract.Constants.IS_CATCH_MISSED;
import static com.blogspot.droidcrib.callregister.contract.Constants.IS_CATCH_OUTGOINGS;
import static com.blogspot.droidcrib.callregister.contract.Constants.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS;
import static com.blogspot.droidcrib.callregister.contract.Constants.SHARED_PREFS;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private SharedPreferences mPrefs;
    private ViewPager mViewPager;
    private String mNoteText;
    private MainTabsPagerAdapter adapter;
    private long mAlarmRecordId;
    private FloatingActionButton mFab;
    private int mPagePosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mPrefs = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        mFab = (FloatingActionButton) findViewById(R.id.fab_main);

        //  Setup TabLayout
        TabLayout mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mTabLayout.addTab(mTabLayout.newTab().setIcon(R.drawable.ic_call_white_24dp));
        mTabLayout.addTab(mTabLayout.newTab().setIcon(R.drawable.ic_alarm_white_24dp));
        mTabLayout.addTab(mTabLayout.newTab().setIcon(R.drawable.ic_message_white_24dp));
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        //  Setup ViewPager
        mViewPager = (ViewPager) findViewById(R.id.pager);
        adapter = new MainTabsPagerAdapter(getSupportFragmentManager(), mTabLayout.getTabCount());
        mViewPager.setAdapter(adapter);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout) {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                mPagePosition = position;
                if (position == 0) {
                    mFab.setVisibility(View.INVISIBLE);
                }
                if (position == 1) {
                    mFab.setVisibility(View.VISIBLE);
                    mFab.setImageResource(R.drawable.ic_alarm_add_white_24dp);
                }
                if (position == 2) {
                    mFab.setVisibility(View.VISIBLE);
                    mFab.setImageResource(R.drawable.ic_note_add_white_24dp);
                }
            }
        });

        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        // Set NavigationView
        NavigationView nav = (NavigationView) findViewById(R.id.nav_view);
        nav.setNavigationItemSelectedListener(this);

        // Get elements of navigation view
        MenuItem mCheckBoxItemIncoming = nav.getMenu().findItem(R.id.nav_incoming);
        MenuItem mCheckBoxItemOutgoing = nav.getMenu().findItem(R.id.nav_outgoing);
        MenuItem mCheckBoxItemMissed = nav.getMenu().findItem(R.id.nav_missed);
        CheckBox mCheckBoxIncoming = (CheckBox) MenuItemCompat.getActionView(mCheckBoxItemIncoming);
        CheckBox mCheckBoxOutgoing = (CheckBox) MenuItemCompat.getActionView(mCheckBoxItemOutgoing);
        CheckBox mCheckBoxMissed = (CheckBox) MenuItemCompat.getActionView(mCheckBoxItemMissed);

        //Get here checkbox values from shared prefs and apply to views
        Boolean isCatchIncomings = mPrefs.getBoolean(IS_CATCH_INCOMINGS, true);
        Boolean isCatchOutgoings = mPrefs.getBoolean(IS_CATCH_OUTGOINGS, true);
        Boolean isCatchMissed = mPrefs.getBoolean(IS_CATCH_MISSED, true);

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

        // Explicitly call to get permission in Android 6
        requestAllPermissionsAtFirstStart();

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mPagePosition == 1) {
                    Intent i = new Intent(MainActivity.this, NewReminderActivity.class);
                    startActivity(i);
                }
                if (mPagePosition == 2) {
                    newMemoDialog();
                }
            }
        });
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
        String mIntentAction = intent.getAction();
        mAlarmRecordId = intent.getLongExtra(EXTRA_ALARM_RECORD_ID, -1);
        if (mIntentAction.equals(ACTION_SHOW_ALARM_DETAILS_IN_LIST)) {
            mViewPager.setCurrentItem(1);
        }
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


    //////////////////////////////////////
    // Options menu callbacks
    //////////////////////////////////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the mIntentAction bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
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


    private void newMemoDialog() {
        // Messages
        String msg = getString(R.string.new_note);
        String buttonYes = getString(R.string.new_note_done);
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
                mNoteText = s.toString();
            }
        });

        final AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setView(input)
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton(buttonYes, new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog,
                                        @SuppressWarnings("unused") final int id) {
                        if (mNoteText != null && mNoteText.length() > 0) {
                            NoteRecord.insert(mNoteText, null);
                            EventBus.getDefault().post(new NewNoteEvent());
                        }
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


    //////////////////////////////////////
    // Permissions API 23
    //////////////////////////////////////

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton(getResources().getString(R.string.ok), okListener)
                .setNegativeButton(getResources().getString(R.string.cancel), null)
                .create()
                .show();
    }


    private void requestAllPermissionsAtFirstStart() {
        String needGrandAccess = getResources().getString(R.string.perm_access_info);
        String readPhoneState = getResources().getString(R.string.perm_read_phone_state);
        String readContacts = getResources().getString(R.string.perm_read_contacts);

        List<String> permissionsNeeded = new ArrayList<String>();

        final List<String> permissionsList = new ArrayList<String>();
        if (!addPermission(permissionsList, Manifest.permission.READ_PHONE_STATE))
            permissionsNeeded.add(readPhoneState);
        if (!addPermission(permissionsList, Manifest.permission.READ_CONTACTS))
            permissionsNeeded.add(readContacts);

        if (permissionsList.size() > 0) {
            if (permissionsNeeded.size() > 0) {
                // Need Rationale
                String message = needGrandAccess + permissionsNeeded.get(0);
                for (int i = 1; i < permissionsNeeded.size(); i++)
                    message = message + ", " + permissionsNeeded.get(i);
                showMessageOKCancel(message,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        permissionsList.toArray(new String[permissionsList.size()]),
                                        REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                            }
                        });
                return;
            }
            ActivityCompat.requestPermissions(MainActivity.this,
                    permissionsList.toArray(new String[permissionsList.size()]),
                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            return;
        }
        // All Permissions Granted

    }

    private boolean addPermission(List<String> permissionsList, String permission) {
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            // Check for Rationale Option
            if (!ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permission))
                return false;
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                Map<String, Integer> perms = new HashMap<String, Integer>();
                // Initial
                perms.put(Manifest.permission.READ_PHONE_STATE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_CONTACTS, PackageManager.PERMISSION_GRANTED);
                // Fill with results
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                // Check for all perms
                if (perms.get(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                    // All Permissions Granted

                } else {
                    // Permission Denied
                    Toast.makeText(MainActivity.this, "Some Permission is Denied", Toast.LENGTH_SHORT)
                            .show();
                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    @Subscribe
    public void onEvent(AlarmsListLoadFinishedEvent event) {
        AlarmsListFragment fragment = (AlarmsListFragment) adapter.getRegisteredFragment(mViewPager.getCurrentItem());
        fragment.scrollToListItem(mAlarmRecordId);
    }


}
