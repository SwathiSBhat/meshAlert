package com.akshayuprabhu.meshalert;

import android.support.annotation.NonNull;

import cn.meshee.fclib.api.contact.model.Contact;

public class MeshAlertContact {

    private Contact contact;

    private String nameSpelling;

    private CharSequence displayNameSpelling;

    public MeshAlertContact(Contact contact) {
        this.contact = contact;
        this.nameSpelling = "Akshay U Prabhu" ;//contact.getNickName();
        this.displayNameSpelling = nameSpelling;
    }

    public Contact getContact() {
        return contact;
    }

    public static MeshAlertContact newMeshAlertContact(Contact contact) {
        return new MeshAlertContact(contact);
    }

    public String getNameSpelling() {
        return nameSpelling;
    }

//    public int compareTo(@NonNull FreechatContact friend) {
//        return this.getContact().getNickName().compareTo(friend.getContact().getNickName());
//    }

    public CharSequence getDisplayNameSpelling() {
        return displayNameSpelling;
    }
}
