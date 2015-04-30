package com.example.pm25;

import java.util.ArrayList;
import java.util.List;

import com.example.pm25.R;
import com.example.pm25.model.City;
import com.example.pm25.model.ModelCallBackListener;
import com.example.pm25.model.ModelService;
import com.example.pm25.util.MyLog;
import com.example.pm25.util.myComponent.CityAdapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private ProgressDialog progressDialog;
	private ListView listView;
	private CityAdapter adapter;
	private List<City> cityList = new ArrayList<>();
	private City selectedCity;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
	
		listView = (ListView) findViewById(R.id.list_view);
		adapter = new CityAdapter(this, R.layout.city_item, cityList);

		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				selectedCity = cityList.get(position);
				Toast.makeText(MainActivity.this, selectedCity.getCityName(), Toast.LENGTH_SHORT).show();
			}
		});
		getCities();
	}
	
	private void getCities() {
		showProgressDialog();
		ModelService.getCities(new ModelCallBackListener<City>() {
			@Override
			public void onFinish(final List<City> cities) {
				if (cities==null || cities.size()==0) {
					closeProgressDialog();
				} else {
					// 刷新界面
					runOnUiThread(new Runnable() {
						public void run() {
							cityList.clear();
							for (City city : cities) {
								cityList.add(city);
							}
							adapter.notifyDataSetChanged();
							listView.setSelection(0);
							closeProgressDialog();
							MyLog.d("test", cities.toString());
						}
					});
				}
			}
			@Override
			public void onError(final Exception e) {
				e.printStackTrace();
				runOnUiThread(new Runnable() {
					public void run() {
						closeProgressDialog();
						Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}
	
	private void showProgressDialog() {
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("正在加载。。。");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}
	
	private void closeProgressDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}
}
