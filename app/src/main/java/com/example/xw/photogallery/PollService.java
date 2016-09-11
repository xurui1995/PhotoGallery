package com.example.xw.photogallery;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

import java.util.List;


/**
 * Created by xw on 2016/9/11.
 */
public class PollService extends IntentService {
    private static final String TAG="PollService";
    public static Intent newIntent(Context context){
        return new Intent(context,PollService.class);
    }
    public PollService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (!isNetWorkAvailableAndConnected()){
            return;
        }
        String query=QueryPreferences.getStoredQuery(this);
        String lastResultId=QueryPreferences.getLastResultId(this);
        List<GalleryItem> items;
        if(query==null){
            items=new FlickrFetchr().fetchRecentPhotos();
        }else {
            items=new FlickrFetchr().searchPhotos(query);
        }
        if (items.size()==0){
            return;
        }
        String resultId=items.get(0).getId();
        if(resultId.equals(lastResultId)){
            Log.i(TAG,"Got an old result"+resultId);
        }else {
            Log.i(TAG,"Got a new result: "+resultId);
        }

        QueryPreferences.setPrefLastResultId(this,resultId);
    }

    private boolean isNetWorkAvailableAndConnected(){
        ConnectivityManager cm= (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        boolean isNetworkAvailable=cm.getActiveNetworkInfo()!=null;
        boolean isNetworkConnected=isNetworkAvailable&&cm.getActiveNetworkInfo().isConnected();
        return isNetworkConnected;
    }
}
