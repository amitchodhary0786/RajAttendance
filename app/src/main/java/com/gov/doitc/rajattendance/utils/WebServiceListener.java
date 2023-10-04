package com.gov.doitc.rajattendance.utils;

import org.json.JSONException;

/**
 * Author       :  Inderjeet kaur
 * Designation  :   Android Developer
 * E-mail       :   inderjeet.gs@gmail.com
 * Date         :   1/30/2016
 * Company      :   Parasme Softwares & Technology
 * Purpose      :   This class
 * Description  :   Description.
 */
public interface WebServiceListener {
    public void onResponse(String response) throws JSONException;
    public void onFailer(String failure) throws JSONException;
}
