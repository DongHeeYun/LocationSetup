package com.locationsetup;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements ListFragment.OnAddButtonClickListener,
        MapFragment.OnAddButtonClickListener {

    private final String TAG = MainActivity.class.getSimpleName();
    private final String PREF_NAME = "options";
    private final String PREF_SYNC = "synchronization";
    private final int PAGE_NUMBER = 2;

    public final int ON_DO_NOT_DISTURB_CALLBACK_CODE = 1001;
    public static final int REQ_START_MAIN = 2001;
    public static final int CODE_WRITE_SETTINGS_PERMISSION = 3001;
    public static final int REQUEST_ADD_ITEM = 4001;
    public static final int REQUEST_UPDATE_ITEM = 5001;
    public static boolean isSynchronized;

    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;
    private Switch switchBtn;

    FirebaseManager mFirebaseManager;
    FileManager mFileManager;

    NotificationManager notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager.isNotificationPolicyAccessGranted()) {
            requestWriteSettingsPermission(this);
        } else{
            Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
            startActivityForResult(intent, ON_DO_NOT_DISTURB_CALLBACK_CODE);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mFileManager.saveFile();
        Log.i(TAG, "save items in internal storage");
    }

    public void requestWriteSettingsPermission(Activity context){
        boolean permission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permission = Settings.System.canWrite(context);
        } else {
            permission = ContextCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_SETTINGS) == PackageManager.PERMISSION_GRANTED;
        }
        if (permission) {
            initialize();
        } else {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + context.getPackageName()));
                context.startActivityForResult(intent, CODE_WRITE_SETTINGS_PERMISSION);
            } else {
                ActivityCompat.requestPermissions(context, new String[]{ android.Manifest.permission.WRITE_SETTINGS },
                        CODE_WRITE_SETTINGS_PERMISSION);
            }
        }
    }

    public void initialize() {
        mFirebaseManager = FirebaseManager.getInstance();
        mFileManager = FileManager.getFileManager(this);

        SharedPreferences pref = getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE);
        isSynchronized = pref.getBoolean(PREF_SYNC, true);
        Log.d(TAG, "synchronized:" + isSynchronized);

        if (isSynchronized) {
            FirebaseUser user = mFirebaseManager.getUser();
            if (user == null) {
                isSynchronized = false;
                Toast.makeText(MainActivity.this, R.string.request_auth, Toast.LENGTH_SHORT).show();
            } else {
                mFirebaseManager.loadItems(user);
            }
        } else {
            mFileManager.getFile();
            mFirebaseManager.notifyItemChange();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        actionBar.setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(mNavItemSelectedListener);

        MenuItem switchItem = navigationView.getMenu().findItem(R.id.sync);
        switchBtn = (Switch) switchItem.getActionView();
        switchBtn.setOnCheckedChangeListener(mCheckedChangedListener);
        switchBtn.setChecked(isSynchronized);

        MainPagerAdapter mPagerAdapter = new MainPagerAdapter(getSupportFragmentManager());
        ViewPager mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mViewPager.setAdapter(mPagerAdapter);
        TabLayout mTab = (TabLayout) findViewById(R.id.tabs);
        mTab.setupWithViewPager(mViewPager);

        updateUI();
    }


    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences pref = getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(PREF_SYNC, isSynchronized);
        editor.commit();
        Log.d(TAG, "sync:" + isSynchronized);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.action_settings:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class MainPagerAdapter extends FragmentPagerAdapter {

        public MainPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch(position) {
                case 0:
                    return ListFragment.newInstance();
                case 1:
                    return MapFragment.newInstance();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return PAGE_NUMBER;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "목록";
                case 1:
                    return "지도";
                default:
                    return null;
            }
        }
    }

    /*private void authProcess() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            firebaseManager.signOut();
            updateUI();
        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, REQ_START_MAIN);
        }
    }*/

    public void updateUI() {
        View headerView = navigationView.getHeaderView(0);

        FirebaseUser user = mFirebaseManager.getUser();
        if (user != null) {
            TextView t_name = headerView.findViewById(R.id.display_name);
            TextView t_email = headerView.findViewById(R.id.email_addr);
            t_name.setText(user.getDisplayName());
            t_email.setText(user.getEmail());
            navigationView.getMenu().findItem(R.id.sign)
                    .setIcon(R.drawable.ic_sign_in)
                    .setTitle(R.string.signed_out);
        } else {
            TextView t_name = headerView.findViewById(R.id.display_name);
            TextView t_email = headerView.findViewById(R.id.email_addr);
            t_name.setText("");
            t_email.setText(R.string.local_user);
            navigationView.getMenu().findItem(R.id.sign)
                    .setIcon(R.drawable.ic_sign_out)
                    .setTitle(R.string.signed_in);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQ_START_MAIN:
                if (resultCode == RESULT_OK) {
                    updateUI();
                }
                break;
            case ON_DO_NOT_DISTURB_CALLBACK_CODE:
                if (!notificationManager.isNotificationPolicyAccessGranted()) {
                    Toast.makeText(MainActivity.this, R.string.permission_denied_notify, Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    requestWriteSettingsPermission(this);
                }
                break;
            case CODE_WRITE_SETTINGS_PERMISSION:
                if (Settings.System.canWrite(this)) {
                    initialize();
                } else {
                    Toast.makeText(MainActivity.this, R.string.permission_denied_settings, Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            case REQUEST_ADD_ITEM:
                if (resultCode == RESULT_OK) {
                    mFirebaseManager.notifyItemChange();
                }
                break;
            case REQUEST_UPDATE_ITEM:
                if (resultCode == RESULT_OK) {
                    mFirebaseManager.notifyItemChange();
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CODE_WRITE_SETTINGS_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initialize();
        }
    }

    NavigationView.OnNavigationItemSelectedListener mNavItemSelectedListener = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(MenuItem menuItem) {
            mDrawerLayout.closeDrawers();

            int id = menuItem.getItemId();
            FirebaseUser user = mFirebaseManager.getUser();
            switch (id) {
                case R.id.save:
                    if (user == null) {
                        Toast.makeText(MainActivity.this, R.string.request_auth, Toast.LENGTH_SHORT).show();
                        break;
                    }
                    mFirebaseManager.saveCurrentItems(user);
                    break;
                case R.id.load:
                    if (user == null) {
                        Toast.makeText(MainActivity.this, R.string.request_auth, Toast.LENGTH_SHORT).show();
                        break;
                    }
                    mFirebaseManager.loadItems(user);
                    break;
                case R.id.sync:
                    onSwitchButtonClicked();
                    break;
                case R.id.sign:
                    if (mFirebaseManager.authProcess()) {
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivityForResult(intent, REQ_START_MAIN);
                    } else {
                        isSynchronized = false;
                        switchBtn.setChecked(isSynchronized);
                        updateUI();
                    }
                    break;
            }
            return true;
        }
    };

    private void onSwitchButtonClicked() {
        FirebaseUser user = mFirebaseManager.getUser();
        if (user == null) {
            isSynchronized = false;
            switchBtn.setChecked(isSynchronized);
            Toast.makeText(MainActivity.this, R.string.request_auth, Toast.LENGTH_SHORT).show();
            return;
        }
        if (isSynchronized) {
            switchBtn.setChecked(false);
            isSynchronized = false;
        } else {
            switchBtn.setChecked(true);
            isSynchronized = true;
            mFirebaseManager.saveCurrentItems(user);
        }
    }

    CompoundButton.OnCheckedChangeListener mCheckedChangedListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            onSwitchButtonClicked();
        }
    };

    @Override
    public void onAddButtonClicked(int type, int position) {
        startAddItem(type, position);
    }

    public void startAddItem(int type, int position) {
        Intent intent =  new Intent(this, SettingActivity.class);
        int requestCode;
        if (type == 0) {
            requestCode = REQUEST_ADD_ITEM;
        } else {
            requestCode = REQUEST_UPDATE_ITEM;
            intent.putExtra("item", FileManager.items.get(position));
        }
        startActivityForResult(intent, requestCode);
    }

}
