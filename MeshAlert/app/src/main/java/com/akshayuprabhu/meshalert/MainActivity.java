package com.akshayuprabhu.meshalert;

import android.content.Intent;
import android.provider.Telephony;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import cn.meshee.fclib.api.FCClient;
import cn.meshee.fclib.api.FcService;
import cn.meshee.fclib.api.Observer;
import cn.meshee.fclib.api.RequestCallback;
import cn.meshee.fclib.api.contact.model.Contact;
import cn.meshee.fclib.api.conversation.ConversationService;
import cn.meshee.fclib.api.conversation.model.Conversation;
import cn.meshee.fclib.api.log.TVLog;
import cn.meshee.fclib.api.message.FcMessageBuilder;
import cn.meshee.fclib.api.message.MessageService;
import cn.meshee.fclib.api.message.model.FcMessage;
import cn.meshee.fclib.api.network.NetworkService;
import cn.meshee.fclib.api.network.NetworkServiceObserve;
import cn.meshee.fclib.api.network.model.NetworkEvent;
import cn.meshee.fclib.api.network.model.Role;
import cn.meshee.fclib.api.network.model.RoleEvent;

public class MainActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /*Toast for debugging purposes*/
    Toast toast;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    String tagOfTab2,tagOfTab1;

    public void setTabFragmentB(String t){
        tagOfTab2 = t;
    }

    public String getTabFragmentB(){
        return tagOfTab2;
    }

    public void setTabFragmentA(String t){
        tagOfTab1 = t;
    }

    public String getTabFragmentA(){
        return tagOfTab1;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toast = new Toast(getApplicationContext());
        // Initializing FClib
        initializeFClib();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

//  Debugging
//        Log.i("\n\n\nSERVER_MANAGER_TITLE\n\n\n\n",mSectionsPagerAdapter.getPageTitle(0).toString());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ConversationService conversationService = FCClient.getService(ConversationService.class);
                List<MeshAlertContact> listOfContactsToSend = ContactManager.getInstance().getContacts();

                String connectionUrl =
                        "jdbc:sqlserver://meshserver.database.windows.net:1433;"
                                + "database=MeshDB;"
                                + "user=meshadmin@meshserver;"
                                + "password=n!tk@2k19;"
                                + "encrypt=true;"
                                + "trustServerCertificate=false;"
                                + "hostNameInCertificate=*.database.windows.net;"
                                + "loginTimeout=30;";

                ResultSet resultSet = null;

                try (Connection connection = DriverManager.getConnection(connectionUrl);
                     Statement statement = connection.createStatement();) {

                    // Create and execute a SELECT SQL statement.
                    String selectSql = "SELECT * FROM reliefcamps";
                    resultSet = statement.executeQuery(selectSql);

                    // Print results from select statement
                    while (resultSet.next()) {
                        for(int i=0;i<listOfContactsToSend.size();i++){
                            Conversation conversation = conversationService.createP2PConverstaion(listOfContactsToSend.get(i).getContact() , listOfContactsToSend.get(i).getDisplayNameSpelling().toString() , null );
                            sendTextMsg(conversation.getConversationId(),"Relief camps details - " + resultSet.getString(0) + " " + resultSet.getString(2));
                        }
                    }
                }

                // Handle any errors that may have occurred.
                catch (SQLException e) {
                    e.printStackTrace();
                }


//                sendTextMsg("sample message");
                Snackbar.make(view, "Sent all the messages", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    public FcMessage sendTextMsg(String convId, String messageText) {
        FcMessage fcMessage = FcMessageBuilder.createTextMessage(convId, messageText);
        FCClient.getService(MessageService.class).sendMessage(fcMessage, new RequestCallback<FcMessage>() {

            @Override
            public void onSuccess(FcMessage fcMessage) {
            }

            @Override
            public void onFailed(int code) {
            }

            @Override
            public void onException(Exception ex) {
                TVLog.log(String.format("Exception: %s", ex));
            }
        });
        return fcMessage;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
        getMenuInflater().inflate(R.menu.create_network, menu);

        return true;
    }

    public void initializeFClib(){
        FCClient.init(getApplication());
        startFcService();
        FCClient.bindAccount();
    }

    private void startFcService() {
        Intent intent = new Intent(this, FcService.class);
        startService(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

//        toast.makeText(getApplicationContext(), "The id of the item selected is "+Integer.toString(id) , Toast.LENGTH_SHORT).show();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }else if( id == R.id.create_network){
            createNetwork();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    Tab1General tab1 = new Tab1General();
                    return tab1;
                case 1:
                    Tab2Neighbours tab2 = new Tab2Neighbours();
                    return tab2;
                case 2:
                    Tab3Discover tab3 = new Tab3Discover();
                    return tab3;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        private String tabTitles[] = new String[]{"General","Neighbours","Discover"};

        @Override
        public CharSequence getPageTitle(int position){
            return tabTitles[position];
        }

    }

    private Observer<NetworkEvent> observer = null;

    private Observer<RoleEvent> roleEventObserver = null;


    public void createNetwork() {
        Toast toast = new Toast(getApplicationContext());

        NetworkService networkService = FCClient.getService(NetworkService.class);
        if (networkService != null) {
            NetworkServiceObserve networkServiceObserve = FCClient.getService(NetworkServiceObserve.class);
            observer = new Observer<NetworkEvent>() {

                @Override
                public void onEvent(NetworkEvent networkEvent) {
                    Toast toast = new Toast(getApplicationContext());

                    if (networkEvent == null)
                        return;
                    NetworkEvent.NetworkEventType networkEventType = networkEvent.getEventType();
                    if (networkEventType.equals(NetworkEvent.NetworkEventType.NETWORK_EVENT_CREATE)) {
                        TVLog.log(String.format("network %s created success with groupName %s", networkEvent.getNetworkPointId(), networkEvent.getGroupName()));

                        toast.makeText(getApplicationContext(), String.format("network %s created success with groupName %s", networkEvent.getNetworkPointId(), networkEvent.getGroupName()) , Toast.LENGTH_SHORT).show();

                    } else if (networkEventType.equals(NetworkEvent.NetworkEventType.NETWORK_EVENT_DESTROY)) {
                        TVLog.log(String.format("network %s destory success with groupName %s", networkEvent.getNetworkPointId(), networkEvent.getGroupName()));

//                        Toast toast = new Toast(getApplicationContext());
                        toast.makeText(getApplicationContext(), String.format("network %s destory success with groupName %s", networkEvent.getNetworkPointId(), networkEvent.getGroupName()) , Toast.LENGTH_SHORT).show();

                    }
                }
            };
            roleEventObserver = new Observer<RoleEvent>() {

                @Override
                public void onEvent(RoleEvent event) {
                    if (event == null)
                        return;
                    Role role = event.getNewRole();
//                    IMainAtView view = getView();
//                    if ((role == Role.Idle || role == Role.Member) && view != null) {
//                        if (view.isDiscoveryFgSelected()) {
//                            softScan();
//                        }
//                    }
                }
            };
            networkServiceObserve.observeNetworkEvent(observer, true);
            networkServiceObserve.observeRoleUpdate(roleEventObserver, true);
//            toast.makeText(getApplicationContext(), "Calling create network" , Toast.LENGTH_LONG).show();
            Log.i("INFO OF CREATE NETWORK","\n\n\n\n\n\n\nbefore calling create network method\n\n\n\n");
            networkService.createNetwork(randomString(8), false);
//            toast.makeText(getApplicationContext(), "Called create network" , Toast.LENGTH_SHORT).show();
            Log.i("INFO OF CREATE NETWORK","\n\n\n\n\n\n\nCame out of create network method\n\n\n\n");
        }else{
            toast.makeText(getApplicationContext(), "Network Service is NULL" , Toast.LENGTH_SHORT).show();

        }
    }

    public void restartAutoJoinNetwork() {
        FCClient.getService(NetworkService.class).restartAutoJoinNetwork();
    }

    public void stopAutoJoinNetwork() {
        FCClient.getService(NetworkService.class).stopAutoJoinNetwork();
    }


    public static String randomString(int length) {
        assert (length > 0);
        final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        SecureRandom rnd = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        }


        return sb.toString();
    }




}
