package com.akshayuprabhu.meshalert;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.BroadcastReceiver;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lqr.adapter.LQRAdapterForRecyclerView;
import com.lqr.adapter.LQRViewHolderForRecyclerView;
import com.lqr.recyclerview.LQRRecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class Tab2Neighbours extends Fragment {

    ListView mRvContacts;

    QuickIndexBar mQib;

    TextView mTvLetter;

    private View mHeaderView;

    private TextView mFooterView;

    View rootView;

    private ContactManager contactManager;

    public static Tab2Neighbours t2N;
    public static Tab2Neighbours getInstance(){
        if(t2N==null){
            return new Tab2Neighbours();
        }else
            return t2N;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.tab2neighbours, container, false);
        mRvContacts = (ListView) rootView.findViewById(R.id.rvContacts);
        mQib = (QuickIndexBar) rootView.findViewById(R.id.qib);
        mFooterView = (TextView) rootView.findViewById(R.id.tvLetter);

        registerBR();

        String myTag = getTag();
        ((MainActivity)getActivity()).setTabFragmentB(myTag);

        return rootView;
    }


    private void registerBR() {
        BroadcastManager.getInstance(getActivity()).register("UPDATE_RED_DOT", new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
            }
        });
        BroadcastManager.getInstance(getActivity()).register("UPDATE_FRIEND", new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
//                loadContacts();
            }
        });
    }

    private void unregisterBR() {
        BroadcastManager.getInstance(getActivity()).unregister("UPDATE_RED_DOT");
        BroadcastManager.getInstance(getActivity()).unregister("UPDATE_FRIEND");
    }

    private void showLetter(String letter) {
        mTvLetter.setVisibility(View.VISIBLE);
        mTvLetter.setText(letter);
    }

    private void hideLetter() {
        mTvLetter.setVisibility(View.GONE);
    }

    public void showQuickIndexBar(boolean show) {
        if (mQib != null) {
            mQib.setVisibility(show ? View.VISIBLE : View.GONE);
            mQib.invalidate();
        }
    }

//    protected ContactsFgPresenter createPresenter() {
//        return new ContactsFgPresenter((MainActivity) getActivity());
//    }

//    protected int provideContentViewId() {
//        return R.layout.fragment_contacts;
//    }

    public View getHeaderView() {
        return mHeaderView;
    }

//    public LQRRecyclerView getRvContacts() {
//        return mRvContacts;
//    }

    public TextView getFooterView() {
        return mFooterView;
    }


    public void loadContacts() {

//        Toast.makeText(getActivity(), "came to load contacts", Toast.LENGTH_SHORT).show();
        if (contactManager == null) {
            contactManager = ContactManager.getInstance();
        }
        contactManager.refreshContacts();
        List<MeshAlertContact> listOfNearbyContacts = contactManager.getContacts();
        List<String> namesOfListOfNearbyContacts = new ArrayList<String>();
        namesOfListOfNearbyContacts.add("sample contact");

        for(int i=0;i<listOfNearbyContacts.size();i++){
            namesOfListOfNearbyContacts.add(listOfNearbyContacts.get(i).getDisplayNameSpelling().toString());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>( getActivity().getApplicationContext() , android.R.layout.simple_list_item_1 , namesOfListOfNearbyContacts );
        mRvContacts.setAdapter(adapter);

    }
//
//    private void loadFreechat() {
//        Observable.just(ContactManager.getInstance().getContacts()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(contacts -> {
//            if (contacts != null && contacts.size() > 0) {
//                mData.clear();
//                mData.addAll(contacts);
//                getView().getFooterView().setText(UIUtils.getString(R.string.count_of_contacts, mData.size()));
//                SortUtils.sortContacts(mData);
//                if (mAdapter != null)
//                    mAdapter.notifyDataSetChanged();
//            }
//        }, this::loadError);
//    }
//
//    private void setAdapter() {
//        if (mAdapter == null) {
//            LQRAdapterForRecyclerView adapter = new LQRAdapterForRecyclerView<FreechatContact>(mContext, mData, R.layout.item_contact) {
//
//                @Override
//                public void convert(LQRViewHolderForRecyclerView helper, FreechatContact item, int position) {
//                    helper.setText(R.id.tvName, item.getContact().getNickName());
//                    ImageView ivHeader = helper.getView(R.id.ivHeader);
//                    Glide.with(mContext).load(AppUtils.getContactAvatarUri(item.getContact())).centerCrop().into(ivHeader);
//                    String str = "";
//                    String currentLetter = item.getDisplayNameSpelling().charAt(0) + "";
//                    if (position == 0) {
//                        str = currentLetter;
//                    } else {
//                        String preLetter = mData.get(position - 1).getDisplayNameSpelling().charAt(0) + "";
//                        if (!preLetter.equalsIgnoreCase(currentLetter)) {
//                            str = currentLetter;
//                        }
//                    }
//                    int nextIndex = position + 1;
//                    if (nextIndex < mData.size() - 1) {
//                        String nextLetter = mData.get(nextIndex).getDisplayNameSpelling().charAt(0) + "";
//                        if (!nextLetter.equalsIgnoreCase(currentLetter)) {
//                            helper.setViewVisibility(R.id.vLine, View.INVISIBLE);
//                        } else {
//                            helper.setViewVisibility(R.id.vLine, View.VISIBLE);
//                        }
//                    } else {
//                        helper.setViewVisibility(R.id.vLine, View.INVISIBLE);
//                    }
//                    if (position == mData.size() - 1) {
//                        helper.setViewVisibility(R.id.vLine, View.GONE);
//                    }
//                    if (TextUtils.isEmpty(str)) {
//                        helper.setViewVisibility(R.id.tvIndex, View.GONE);
//                    } else {
//                        helper.setViewVisibility(R.id.tvIndex, View.VISIBLE);
//                        helper.setText(R.id.tvIndex, str);
//                    }
//                }
//            };
//            adapter.addHeaderView(getView().getHeaderView());
//            adapter.addFooterView(getView().getFooterView());
//            mAdapter = adapter.getHeaderAndFooterAdapter();
//            getView().getRvContacts().setAdapter(mAdapter);
//        }
//        ((LQRAdapterForRecyclerView) mAdapter.getInnerAdapter()).setOnItemClickListener((lqrViewHolder, viewGroup, view, i) -> {
//            Intent intent = new Intent(mContext, UserInfoActivity.class);
//            UUID contactUuid = mData.get(i - 1).getContact().getContactUuid();
//            intent.putExtra("contactUUID", contactUuid);
//            mContext.jumpToActivity(intent);
//        });
//    }

    private void loadError(Throwable throwable) {
        Toast.makeText(getActivity(), "load_contacts_error", Toast.LENGTH_SHORT).show();
    }


}
