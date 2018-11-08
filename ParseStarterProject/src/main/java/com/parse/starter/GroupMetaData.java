package com.parse.starter;

import android.net.Uri;

import java.util.ArrayList;

public class GroupMetaData {
    public String grpName;
    public String grpStatus;
    public String imageUri;
    public ArrayList<String> members;

    public void groupMetadata() {

    }

    public ArrayList<String> getMembers() {
        return members;
    }

    public String getGrpName() {
        return grpName;
    }

    public String getGrpStatus() {
        return grpStatus;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setGrpName(String grpName) {
        this.grpName = grpName;
    }

    public void setGrpStatus(String grpStatus) {
        this.grpStatus = grpStatus;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public void setMembers(ArrayList<String> members) {
        this.members = members;
    }
}
