package com.akshayuprabhu.meshalert;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import cn.meshee.fclib.api.FCClient;
import cn.meshee.fclib.api.Observer;
import cn.meshee.fclib.api.conversation.ConversationServiceObserve;
import cn.meshee.fclib.api.log.TVLog;
import cn.meshee.fclib.api.message.MessageServiceObserve;
import cn.meshee.fclib.api.message.model.FcMessage;
import cn.meshee.fclib.api.network.NetworkService;
import cn.meshee.fclib.api.network.NetworkServiceObserve;
import cn.meshee.fclib.api.network.model.ConnectionState;
import cn.meshee.fclib.api.network.model.NetworkPoint;
import cn.meshee.fclib.api.network.model.Role;
import cn.meshee.fclib.api.network.model.RoleEvent;

public class Tab3Discover extends Fragment {

    private volatile ConnectionState connectionState = null;

    private Observer<ConnectionState> connectionEventListener = null;

    private Observer<List<NetworkPoint>> networkUpdateListener = null;

    Observer<List<FcMessage>> messageObserver;

    private Observer<RoleEvent> roleEventListener = null;

    private WifiListAdapter wifiListAdapter;

    View rootView,rootViewForTab2;

    ListView mRvContacts;

    private ContactManager contactManager;

    public void joinNetwork(ConnectionState state) {
        if (state == null)
            return;
        NetworkService networkService = FCClient.getService(NetworkService.class);
        connectionState = state;
        networkService.joinNetwork(state.getNetworkPoint());
    }

    public ConnectionState getLastState() {
        return connectionState;
    }

    public void action() {
        Role publicState = FCClient.getService(NetworkService.class).getRole();
        if (publicState == Role.Idle) {
            FCClient.getService(NetworkService.class).scan();
//            updateButtonStatus();
        } else {
            TVLog.log("back to idle from menu");
            FCClient.getService(NetworkService.class).backToIdle();
        }
    }

    private void switchToNetworkMode(Role currentRole) {
        if (currentRole != null) {
            if (currentRole.equals(Role.Idle)) {
                switchToIdleMode(Role.Idle.name());
                Toast.makeText(getActivity(), "debugging 1 - switched to idle state", Toast.LENGTH_SHORT).show();
            } else if (currentRole.equals(Role.Member)) {
                switchToMemberMode(currentRole.name());

//                FCClient.getService(NetworkService.class).joinNetwork();
                Toast.makeText(getActivity(), "debugging 2"+ connectionState.getState().toString() , Toast.LENGTH_SHORT).show();

//                connectionState.getState().toString();
//                ProgressBar pbar = (ProgressBar) rootView.findViewById(R.id.progress);
//                if (connectionState.getState().equals(ConnectionState.State.CONNECTED))
//                    pbar.setVisibility(View.GONE);

//                try{
//                    while(!connectionState.getState().equals(ConnectionState.State.CONNECTED)){
//                        Thread.sleep(100);
//                    }
//                }catch (InterruptedException e){
//                    e.printStackTrace();
//                }

//                mRvContacts = (ListView) rootView.findViewById(R.id.rvContacts);
//                if (contactManager == null) {
//                    contactManager = ContactManager.getInstance();
//                }
//                contactManager.refreshContacts();
//                List<MeshAlertContact> listOfNearbyContacts = contactManager.getContacts();
//                List<String> namesOfListOfNearbyContacts = new ArrayList<String>();
//                namesOfListOfNearbyContacts.add("sample contact");
//                for(int i=0;i<listOfNearbyContacts.size();i++){
//                    namesOfListOfNearbyContacts.add(listOfNearbyContacts.get(i).getDisplayNameSpelling().toString());
//                }
//                Toast.makeText(getActivity(), namesOfListOfNearbyContacts.toString(), Toast.LENGTH_SHORT).show();
//
//
//                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity() , android.R.layout.simple_list_item_1 , namesOfListOfNearbyContacts);
//                mRvContacts.setAdapter(adapter);


            } else if (currentRole.equals(Role.Owner)) {
                switchToOwnerMode(Role.Owner.name());
                Toast.makeText(getActivity(), "debugging 3 - switched to owner state ", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void recieveMessages(){
//        ConversationServiceObserve observeConversation = FCClient.getService(ConversationServiceObserve.class);
//        observeConversation.observeConversationChange(conversationChanges, true);

        messageObserver = new Observer<List<FcMessage>>() {

            @Override
            public void onEvent(List<FcMessage> fcMessages) {
                for (FcMessage message : fcMessages) {
                    if (message.isReceived()) {



                        String TabOfFragmentA = ((MainActivity)getActivity()).getTabFragmentA();

                        Tab1General fragment1 = (Tab1General) getActivity()
                                .getSupportFragmentManager()
                                .findFragmentByTag(TabOfFragmentA);
                        fragment1.loadAlertsAndReliefCamps( message.getContent() );

//                        Toast.makeText(getContext(), message.get, Toast.LENGTH_SHORT).show();
//                        loadAlertsAndReliefCamps(message);
                    }
                }
            }
        };

        MessageServiceObserve observeMessage = FCClient.getService(MessageServiceObserve.class);
        observeMessage.observeIncomeMessage(messageObserver, true);

    }

//    private volatile Map<String, FreechatConversation> mConversationMap = new HashMap<>(10);
//
//    private Observer<Void> conversationChanges = new Observer<Void>() {
//        @Override
//        public void onEvent(Void aVoid) {
//            refreshConversations();
//        }
//    };
//
//    public Observer<List<FcMessage>> getMessageObserver() {
//        return messageObserver;
//    }


    private void registerEventListeners() {
        NetworkServiceObserve networkServiceObserve = FCClient.getService(NetworkServiceObserve.class);
        this.connectionEventListener = new Observer<ConnectionState>() {

            @Override
            public void onEvent(ConnectionState connectState) {
                TVLog.log(String.format("Network point of %s's connection state is changed to %s", connectState.getNetworkPoint().getNetworkPointId(), connectState.getState()));
                    if (connectionState != null && connectionState.getNetworkPoint().getNetworkPointId().equals(connectState.getNetworkPoint().getNetworkPointId())) {
                        connectionState.setState(connectState.getState());
                    }
                    onConnectionStateEvent(connectState);
            }
        };
        networkServiceObserve.observeConnectionEvent(connectionEventListener, true);
        this.networkUpdateListener = new Observer<List<NetworkPoint>>() {

            @Override
            public void onEvent(List<NetworkPoint> networkPoints) {
                Log.i("\n\nNetwork pts\n",networkPoints.toString());
                    onNetworkPointsUpdated(networkPoints);
            }
        };
        networkServiceObserve.observeNetworkPointsUpdate(this.networkUpdateListener, true);
        this.roleEventListener = new Observer<RoleEvent>() {

            @Override
            public void onEvent(RoleEvent event) {
                    switchToNetworkMode(event.getNewRole());
            }
        };
        networkServiceObserve.observeRoleUpdate(this.roleEventListener, true);
    }

    private void unregisterEventListeners() {
        NetworkServiceObserve networkServiceObserve = FCClient.getService(NetworkServiceObserve.class);
        if (networkServiceObserve != null) {
            if (this.networkUpdateListener != null) {
                networkServiceObserve.observeNetworkPointsUpdate(this.networkUpdateListener, false);
            }
            if (this.connectionEventListener != null) {
                networkServiceObserve.observeConnectionEvent(this.connectionEventListener, false);
            }
            if (this.roleEventListener != null) {
                networkServiceObserve.observeRoleUpdate(this.roleEventListener, false);
            }
        }
    }

    public void switchToIdleMode(String roleName) {
        ListView wifiListView = (ListView) rootView.findViewById(R.id.list_of_networks);
        Button button = (Button) rootView.findViewById(R.id.scan_network);
        TextView wifiListEmptyView = (TextView) rootView.findViewById(R.id.emptyWifiList);
        if (button != null) {
            button.setVisibility(View.VISIBLE);
            button.setText(String.format("%s (%s)", "Scan Network", "idle_state" ));
            wifiListEmptyView.setText("no_network");
        }
        if (wifiListView != null) {
            wifiListView.setAdapter(wifiListAdapter);
            wifiListView.setVisibility(View.VISIBLE);
        }
    }

    public void switchToMemberMode(String roleName) {
        ListView wifiListView = (ListView) rootView.findViewById(R.id.list_of_networks);
        Button button = (Button) rootView.findViewById(R.id.scan_network);
        if (wifiListView != null) {
            wifiListView.setAdapter(wifiListAdapter);
        }
        if (button != null) {
            if (!button.isEnabled()) {
                button.setEnabled(true);
            }
            button.setText(String.format("%s (%s)", "back_to_idle", "member_state"));
        }
    }

    public void switchToOwnerMode(String roleName) {
        ListView wifiListView = (ListView) rootView.findViewById(R.id.list_of_networks);
        Button button = (Button) rootView.findViewById(R.id.scan_network);
        TextView wifiListEmptyView = (TextView) rootView.findViewById(R.id.emptyWifiList);

        if (wifiListView != null) {
            wifiListView.setAdapter(null);
        }
        if (button != null) {
            if (!button.isEnabled()) {
                button.setEnabled(true);
            }
            wifiListEmptyView.setText("network_created");
            button.setText(String.format("%s (%s)", "back_to_idle", "owner_state"));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.tab3discover, container, false);
        rootViewForTab2 = inflater.inflate(R.layout.tab2neighbours, container, false);
        Button button = (Button) rootView.findViewById(R.id.scan_network);
        ListView wifiListView = (ListView) rootView.findViewById(R.id.list_of_networks);
        TextView wifiListEmptyView = (TextView) rootView.findViewById(R.id.emptyWifiList);
        NetworkService networkService = FCClient.getService(NetworkService.class);

        registerEventListeners();
        recieveMessages();
        switchToNetworkMode(networkService.getRole());

        button.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View view) {
                                          Toast toast = new Toast(getActivity().getApplicationContext());
                                          toast.makeText(getActivity().getApplicationContext(), "clicked on scan button" , Toast.LENGTH_SHORT).show();

                                          action();
                                      }
                                  });

//        wifiListView.setOnItemClickListener((parent, view, position, id) -> {
//            ConnectionState state = getDataItemByPosition(position);
//            mPresenter.joinNetwork(state);
//        });
        wifiListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                ConnectionState state = getDataItemByPosition(position);
                if (state == null)
                    return;
                NetworkService networkService = FCClient.getService(NetworkService.class);
                connectionState = state;
                networkService.joinNetwork(state.getNetworkPoint());

                Toast.makeText(getContext(), state.getState().toString() , Toast.LENGTH_SHORT).show();
                Toast.makeText(getActivity(), "Joined a mesh network", Toast.LENGTH_SHORT).show();
            }

        });

        wifiListAdapter = new WifiListAdapter(getContext(), null);
        wifiListView.setEmptyView(wifiListEmptyView);


//        scanNetworkBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                NetworkService networkService = FCClient.getService(NetworkService.class);
//                networkService.scan();
//                List<NetworkPoint> networkPointsList = networkService.getNetworkPoints();
//                Log.i("\nNetwork scanned\n","The network points are "+ networkService.getNetworkPoints());
//
//                ListView listview = (ListView) rootView.findViewById(R.id.list_of_networks);
//                ArrayList<String> list = new ArrayList<String>();
//                for (int i = 0; i < networkPointsList.size(); ++i) {
//                    list.add(networkPointsList.get(i).getNetworkPointId());
//                }
//                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity() , android.R.layout.simple_list_item_1 , list);
//                listview.setAdapter(adapter);
//
//                listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//                    @Override
//                    public void onItemClick(AdapterView<?> parent, final View view,
//                                            int position, long id) {
//                        final String item = (String) parent.getItemAtPosition(position);
//
//                    }
//
//                });
//            }
//        });
        return rootView;
    }


    private ConnectionState getDataItemByPosition(int position) {
        return (ConnectionState) wifiListAdapter.getItem(position);
    }

    public void onConnectionStateEvent(ConnectionState event) {
        ListView wifiListView = (ListView) rootView.findViewById(R.id.list_of_networks);

        if (wifiListView != null && wifiListAdapter != null) {
            wifiListAdapter.updateListItemStates(event.getNetworkPoint().getNetworkPointId(), event.getState());
        }
    }

    public void onNetworkPointsUpdated(List<NetworkPoint> networkPoints) {
        ListView wifiListView = (ListView) rootView.findViewById(R.id.list_of_networks);
        if (wifiListView != null && wifiListAdapter != null) {
            wifiListView.setAdapter(wifiListAdapter);
            wifiListAdapter.updateList(buildWifiList(networkPoints));
        }
    }

    private List<ConnectionState> buildWifiList(List<NetworkPoint> networkPoints) {
        List<ConnectionState> list = new ArrayList<>();
        for (NetworkPoint networkPoint : networkPoints) {
            ConnectionState state = ConnectionState.build(networkPoint);
            ConnectionState lastState = connectionState;
            if (lastState != null && lastState.getNetworkPoint() != null && lastState.getNetworkPoint().getNetworkPointId() != null && lastState.getNetworkPoint().getNetworkPointId().equals(networkPoint.getNetworkPointId())) {
                state.setState(lastState.getState());
            }
            list.add(state);
        }
        return list;
    }

    public class WifiListAdapter extends BaseAdapter {

        public WifiListAdapter(Context context, List<ConnectionState> list) {
            this.inflater = LayoutInflater.from(context);
            this.list = list;
        }

        final LayoutInflater inflater;

        List<ConnectionState> list;

        @Override
        public int getCount() {
            return list != null ? list.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            ConnectionState state = null;
            if (position < getCount()) {
                state = list.get(position);
            }
            return state;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = null;
            view = inflater.inflate(R.layout.item_wifi_list, null);
            ConnectionState state = list.get(position);
            NetworkPoint config = state.getNetworkPoint();
            TextView textView = (TextView) view.findViewById(R.id.textView);
            String groupName = config.getGroupName();
            UUID groupUuid = config.getGroupId();
            String groupIndex = null;
            if (groupUuid != null) {
                groupIndex = groupUuid.toString();
            }
            if (groupName == null) {
                groupName = "NA";
            }
            if (groupIndex == null) {
                groupIndex = "NA";
            }
            String role = config.getNetworkRole().name();
            String text = config.getNetworkPointId() + ":" + role + ":" + groupIndex.substring(0, Math.min(4, groupIndex.length())) + ":" + groupName + ":" + config.getFrequency();
            textView.setText(text);
            TextView signalStrenth = (TextView) view.findViewById(R.id.signal_strenth);
            signalStrenth.setText(String.valueOf(config.getRssi()));
            ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
            ProgressBar pbar = (ProgressBar) view.findViewById(R.id.progress);
            if (state.getState().equals(ConnectionState.State.CONNECTING)) {
                pbar.setVisibility(View.VISIBLE);
            } else if (state.getState().equals(ConnectionState.State.CONNECTED)) {
                pbar.setVisibility(View.GONE);

//                Tab2Neighbours.getInstance().loadContacts();

                String TabOfFragmentB = ((MainActivity)getActivity()).getTabFragmentB();

                Tab2Neighbours fragment2 = (Tab2Neighbours) getActivity()
                        .getSupportFragmentManager()
                        .findFragmentByTag(TabOfFragmentB);
                fragment2.loadContacts();

//                Toast.makeText(getContext(), "Got list", Toast.LENGTH_SHORT).show();

            } else {
                pbar.setVisibility(View.GONE);
            }
            return view;
        }

        public void updateList(List<ConnectionState> wifiConfigs) {
            this.list = wifiConfigs;
            Activity activity = getActivity();
            if (activity != null) {
                activity.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });
            }
        }

        public void updateListItemStates(String ssid, ConnectionState.State state) {
            if (list != null) {
                for (ConnectionState item : list) {
                    if (item.getNetworkPoint().getNetworkPointId().equals(ssid)) {
                        item.setState(state);
                    }
                }
                Activity activity = getActivity();
                if (activity != null) {
                    activity.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            notifyDataSetChanged();
                        }
                    });
                }
            }
        }
    }

}
