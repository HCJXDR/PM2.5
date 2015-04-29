package com.example.pm25.exception;

import com.example.pm25.R;
import com.example.pm25.MyApplication;
import com.example.pm25.util.MyLog;

public class FetchTimesException extends Exception {
	
	public static final int CODE = 1;
	/**
	 * 
	 */
	private static final long serialVersionUID = -2041801605234433795L;

	private static final String MSG_STRING = MyApplication.getContext().getResources().getStringArray(R.array.errors)[2];
	
	public FetchTimesException() {
		super(MSG_STRING);
		MyLog.e("", MSG_STRING);
	}
}
