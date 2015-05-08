package com.example.pm25;

import com.example.pm25.util.MyLog;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;

public class BaseActivity extends Activity {

	public BaseActivity() {
		MyLog.v("Which Activity is ?", getClass().getSimpleName());
	}

	private ProgressDialog progressDialog;
	public String dialogHint = "正在加载……";
	
	public void setDialogHint(String dialogHint) {
		this.dialogHint = dialogHint;
	}
	
	public void showProgressDialog() {
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage(dialogHint);
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}

	public void closeProgressDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}
	
}
