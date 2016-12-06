package com.example.kyle.joulieapp;

import android.os.AsyncTask;
import android.util.Log;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBQueryExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedQueryList;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedScanList;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.PutItemResult;
import com.example.kyle.joulieapp.Models.Device;
import com.example.kyle.joulieapp.Models.DummyContent;
import com.example.kyle.joulieapp.Models.PiElectricity;
import com.example.kyle.joulieapp.Models.UserDevice;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Kyle on 2016-12-05.
 */

public class DynamoDBManager {

    private AmazonDynamoDBClient ddbClient;
    private DynamoDBMapper mapper;

    private static DynamoDBManager ourInstance = new DynamoDBManager();

    public static DynamoDBManager getInstance() {
        return ourInstance;
    }

    private DynamoDBManager() {
        ddbClient = new AmazonDynamoDBClient(LoginActivity.credentialsProvider);
        ddbClient.setRegion(Region.getRegion(Regions.US_WEST_2));
        mapper = new DynamoDBMapper(ddbClient);
    }

    public PaginatedScanList<PiElectricity> getUsageData(){
        PaginatedScanList<PiElectricity> result = null;

        try {
            DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
            result = mapper.scan(PiElectricity.class, scanExpression);
        }catch (Exception e){
            Log.i("DynamoDBManager", "Error getting usage data");
        }

        return result;
    }

    private PaginatedQueryList<UserDevice> getUserDevices(){

        UserDevice UserToFind = new UserDevice();
        UserToFind.setUserID(LoginActivity.credentialsProvider.getIdentityId());

        DynamoDBQueryExpression queryExpression = new DynamoDBQueryExpression()
                .withHashKeyValues(UserToFind)
                .withConsistentRead(false);

        PaginatedQueryList<UserDevice> result = mapper.query(UserDevice.class, queryExpression);

        return result;
    }

    private boolean insertUserDevice(Device device){

        UserDevice existingBook = mapper.load(UserDevice.class, device.id);
        if(existingBook != null) return false;
        DummyContent.addDevice(device);
        UserDevice userDevice = new UserDevice();
        userDevice.setUserID(LoginActivity.credentialsProvider.getIdentityId());
        userDevice.setDeviceID(device.id);
        userDevice.setName(device.deviceName);
        mapper.save(userDevice);
        return true;

    }

    public static class DynamoDBManagerTask extends
            AsyncTask<Object, Void, Object> {

        private  DynamoDBTaskListener mListener;
        private  DynamoDBManagerType type;
        private Object returnData;

        public DynamoDBManagerTask(DynamoDBTaskListener listener){
            mListener = listener;
        }

        protected Object doInBackground(
                Object...objects) {

            type = (DynamoDBManagerType)objects[0];
            //String tableStatus = DynamoDBManager.getTestTableStatus();

            //DynamoDBManagerTaskResult result = new DynamoDBManagerTaskResult();
            //result.setTableStatus(tableStatus);
            //result.setTaskType((DynamoDBManagerType)objects[0]);

            if (type == DynamoDBManagerType.GET_USAGE_DATA) {
                returnData = DynamoDBManager.getInstance().getUsageData();
            } else if (type == DynamoDBManagerType.GET_USER_DEVICES) {
                returnData = DynamoDBManager.getInstance().getUserDevices();
            } else if (type == DynamoDBManagerType.INSERT_USER_DEVICE) {
                DynamoDBManager.getInstance().insertUserDevice((Device)objects[1]);
            }

            return returnData;
        }

        protected void onPostExecute(Object result) {

            if (type == DynamoDBManagerType.GET_USAGE_DATA) {
//                    Toast.makeText(
//                            UserPreferenceDemoActivity.this,
//                            "The test table already exists.\nTable Status: "
//                                    + result.getTableStatus(),
//                            Toast.LENGTH_LONG).show();
                mListener.onResult(result);


            } else if (type == DynamoDBManagerType.GET_USER_DEVICES) {

                // startActivity(new Intent(UserPreferenceDemoActivity.this,
                //       UserListActivity.class));
                mListener.onResult(result);

            }  else if (type == DynamoDBManagerType.INSERT_USER_DEVICE) {
//                Toast.makeText(UserPreferenceDemoActivity.this,
//                        "Users inserted successfully!", Toast.LENGTH_SHORT).show();
                mListener.onResult(result);
            }
        }
    }

    public enum DynamoDBManagerType {
        GET_USAGE_DATA, GET_USER_DEVICES, INSERT_USER_DEVICE
    }

    private class DynamoDBManagerTaskResult {
        private DynamoDBManagerType taskType;
        private String tableStatus;

        public DynamoDBManagerType getTaskType() {
            return taskType;
        }

        public void setTaskType(DynamoDBManagerType taskType) {
            this.taskType = taskType;
        }

        public String getTableStatus() {
            return tableStatus;
        }

//        public void setTableStatus(String tableStatus) {
//            this.tableStatus = tableStatus;
//        }
    }

    public interface DynamoDBTaskListener{
        void onResult(Object res);
    }




}
