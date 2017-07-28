package com.malviya.demoofflinelocfinder.network_manager;

/**
 * Created by 23508 on 5/31/2017.
 */

public interface IWebService {
    public void onResponse(String response);
    public void onErrorResponse(String error);
}
