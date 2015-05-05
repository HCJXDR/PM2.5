package com.example.pm25.exception;

import com.example.pm25.R;
import com.example.pm25.util.MyLog;
import com.example.pm25.util.myComponent.MyApplication;

public class NoCityException extends Exception {
	
	public static final int CODE = 1;
	/**
	 * 
	 */
	private static final long serialVersionUID = -2041801605234433795L;

	private static final String MSG_STRING = MyApplication.getContext().getResources().getStringArray(R.array.errors)[1];
	
	public NoCityException() {
		super(MSG_STRING);
		MyLog.e("NoCityException", MSG_STRING);
	}
}
