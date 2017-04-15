package com.example.kyle.joulieapp.presenter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.example.kyle.joulieapp.FilterListAdapter;
import com.example.kyle.joulieapp.Models.Device;
import com.example.kyle.joulieapp.Models.DummyContent;
import com.example.kyle.joulieapp.Models.Usage;
import com.example.kyle.joulieapp.R;
import com.example.kyle.joulieapp.api.ApiClient;
import com.example.kyle.joulieapp.api.ApiService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Kyle on 2017-04-05.
 */

public class UsagePresenter {

    private final Context context;
    private final UsagePresenterListener mListener;
    private final ApiService apiService;
    private FilterListAdapter adapter;


    public interface UsagePresenterListener{
        void UsagesReady(List<Usage> Usages);
    }

    public UsagePresenter(UsagePresenterListener listener, Context context){
        this.mListener = listener;
        this.context = context;
        this.apiService = ApiClient.getInstance(this.context).getApiService();
        this.adapter = new FilterListAdapter(context, R.layout.filter_list_item , DummyContent.MY_DEVICES);
    }

    public void getUsages(){
        apiService
                .getUsages()
                .enqueue(new Callback<List<Usage>>() {
                    @Override
                    public void onResponse(Call<List<Usage>> call, Response<List<Usage>> response) {
                        mListener.UsagesReady(response.body());
                    }

                    @Override
                    public void onFailure(Call<List<Usage>> call, Throwable t) {
                        //// TODO: 2017-04-05 throw error, display message to user
                    }
                });
    }


    public void openFilterDialog(View view){

        ListView lv = (ListView) view.findViewById(R.id.lv_filter);
        lv.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        lv.setItemsCanFocus(true);
      //  lv.setDividerHeight(20);
        lv.setMinimumHeight(80);
        lv.setAdapter(adapter);

        for ( int i=0; i < lv.getChildCount(); i++) {
            lv.setItemChecked(i, true);
        }




        final AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("Shown Devices")
                .setView(view)
                //.setAdapter(adapter, null)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //  Your code when user clicked on OK
                        //  You can write the code  to save the selected item here
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //  Your code when user clicked on Cancel
                    }
                }).create();

      //  dialog.getListView().setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
      //  dialog.getListView().setItemsCanFocus(true);
      //  dialog.getListView().setDividerHeight(20);
      //  dialog.getListView().setMinimumHeight(80);
//        dialog.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                dialog.getListView().setItemChecked(i, !dialog.getListView().isItemChecked(i));
//            }
//        });

        dialog.show();
    }

}
