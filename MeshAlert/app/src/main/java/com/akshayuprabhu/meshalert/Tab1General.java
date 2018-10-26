package com.akshayuprabhu.meshalert;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class Tab1General extends Fragment{

    private ContactManager contactManager;

    ListView alertList;

    List<String> listOfAlertMessages;

    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.tab1general, container, false);


        String myTag = getTag();
        ((MainActivity)getActivity()).setTabFragmentA(myTag);

        listOfAlertMessages = new ArrayList<String>();
        return rootView;
    }


    public void loadAlertsAndReliefCamps(String message) {
        ListView alertList = (ListView) rootView.findViewById(R.id.alert_list);
        listOfAlertMessages.add(message);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>( getActivity().getApplicationContext() , android.R.layout.simple_list_item_1 , listOfAlertMessages );
        alertList.setAdapter(adapter);

    }

}
