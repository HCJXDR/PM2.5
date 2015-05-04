package com.example.pm25;

import java.util.ArrayList;
import java.util.List;

import com.example.pm25.model.ModelCallBackListener;
import com.example.pm25.model.ModelService;
import com.example.pm25.po.City;
import com.example.pm25.po.Station;
import com.example.pm25.po.StationAirQuality;
import com.example.pm25.util.Constants;
import com.example.pm25.util.MyLog;
import com.example.pm25.util.PM25Constants;
import com.example.pm25.util.myComponent.StationAdapter;

import android.R.layout;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

public class DetailActivity extends BaseActivity {

	public static final int CIRCLES_NUM = 8;
	// 下方细节的按钮
	private LinearLayout[] circles = new LinearLayout[CIRCLES_NUM];
	
	private View aqiAskBtn;
	private ViewFlipper aqiAns;

	private LinearLayout elseAskBtn1;
	private LinearLayout elseAskBtn2;
	private ViewFlipper elseAns;

	private Spinner titleSpinner;
	private StationAdapter<String> stationAdapter;
	
	private City selectedCity;
	
	// 所选择的位置数据
	private List<String> stationList = new ArrayList<>();

	public static void actionStart(Context context, City selectedCity) {
		Intent intent = new Intent(context, DetailActivity.class);
		intent.putExtra("city", selectedCity);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_detail);

		Intent intent = getIntent();
		selectedCity = intent.getParcelableExtra("city");

		setHelperButtons();

		// 初始化只有当前区域的spinner
		titleSpinner = (Spinner) findViewById(R.id.title);
		stationList.add(selectedCity.getCityName());
		
		stationAdapter = new StationAdapter<String>(this, 
				R.layout.spinner_checked_text, stationList);
		titleSpinner.setAdapter(stationAdapter);
//		titleSpinner.setOnItemClickListener(new OnItemClickListener() {
//			@Override
//			public void onItemClick(AdapterView<?> parent,
//					View view, int position, long id) {
//				// TODO 2、title的点击
//				String selected = stationAdapter.getItem(position);
//				Toast.makeText(DetailActivity.this, selected, Toast.LENGTH_SHORT).show();
//			}
//		});

		setDialogHint("正在更新数据……");

		// 获取station列表
		ModelService.getStations(selectedCity, new ModelCallBackListener<Station>() {
			@Override
			public void onFinish(final List<Station> stations) {
				if (stations==null || stations.size()==0) {
					closeProgressDialog();
				} else {
					updateStation(stations);
				}
			}
			private void updateStation(final List<Station> stations) {
				// 刷新界面
				runOnUiThread(new Runnable() {
					public void run() {
						for (Station station : stations) {
							MyLog.e("DetailActivity", station.toString());
							stationList.add(station.getStationName());
						}
						stationAdapter.notifyDataSetChanged();
					}
				});
			}
			@Override
			public void onError(final Exception e) {
				e.printStackTrace();
				runOnUiThread(new Runnable() {
					public void run() {
						Toast.makeText(DetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
					}
				});
			}
		});

		// TODO 1、获取数据
//		getCityDetails();
	}

	/**
	 * 这里获取城市所有的数据
	 * 包括观测点Station列表、每个Station的值
	 */
	private void getCityDetails() {
		showProgressDialog();
		
		// 获取数据
		ModelService.getDetails(selectedCity, new ModelCallBackListener<StationAirQuality>() {
			@Override
			public void onFinish(final List<StationAirQuality> cityDetails) {
				if (cityDetails==null || cityDetails.size()==0) {
					closeProgressDialog();
				} else {
					updateDetails(cityDetails);
				}
			}
			private void updateDetails(final List<StationAirQuality> cityDetails) {
				// 刷新界面
				runOnUiThread(new Runnable() {
					public void run() {
						StationAirQuality quality = cityDetails.get(0);
						putDataOnViews(quality);
						closeProgressDialog();
					}
				});
			}
			@Override
			public void onError(final Exception e) {
				e.printStackTrace();
				runOnUiThread(new Runnable() {
					public void run() {
						closeProgressDialog();
						Toast.makeText(DetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}

	/**
	 * 这里获取观测点的数据
	 * 包括观测点Station列表、每个Station的值
	 */
	private void getStationDetails(City city, String station) {
		showProgressDialog();
		
		// 获取数据
		ModelService.getDetails(city, new ModelCallBackListener<StationAirQuality>() {
			@Override
			public void onFinish(final List<StationAirQuality> stations) {
				if (stations==null || stations.size()==0) {
					closeProgressDialog();
				} else {
					updateDetails();
				}
			}
			private void updateDetails() {
//				// 刷新界面
//				runOnUiThread(new Runnable() {
//					public void run() {
//						cityList.clear();
//						for (City city : cities) {
//							cityList.add(city);
//						}
//						adapter.notifyDataSetChanged();
//						listView.setSelection(0);
//						closeProgressDialog();
//						MyLog.d("test", cities.toString());
//					}
//				});
			}
			@Override
			public void onError(final Exception e) {
				e.printStackTrace();
				runOnUiThread(new Runnable() {
					public void run() {
						closeProgressDialog();
						Toast.makeText(DetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}

	/**
	 * 将数据放入view中
	 * @param quality
	 */
	private void putDataOnViews(StationAirQuality quality) {
		((TextView) findViewById(R.id.pm)).setText(quality.getAqiDetail().getAqi());
	
		String aqiDesc = getResources().getString(R.string.describe);
		aqiDesc = String.format(aqiDesc, quality.getAqiDetail().getLevel(), quality.getAqiDetail().getQuality()); 
		((TextView) findViewById(R.id.pm_describe)).setText(aqiDesc);
		
		((TextView) findViewById(R.id.time)).setText(quality.getTime_point());

		String primaryPoll = getResources().getString(R.string.primary_pollutant_detail);
		aqiDesc = String.format(primaryPoll, quality.getPrimaryPollutant()); 
		((TextView) findViewById(R.id.primary_pollutant)).setText(primaryPoll);
		
		((TextView) findViewById(R.id.aqi_effect)).setText(quality.getAqiDetail().getEffect());
		((TextView) findViewById(R.id.aqi_suggestion)).setText(quality.getAqiDetail().getSuggestion());
		
		((TextView) circles[0].findViewById(R.id.num)).setText(quality.getPm25());
		((TextView) circles[1].findViewById(R.id.num)).setText(quality.getPm10());
		((TextView) circles[2].findViewById(R.id.num)).setText(quality.getCo());
		((TextView) circles[3].findViewById(R.id.num)).setText(quality.getNo2());
		((TextView) circles[4].findViewById(R.id.num)).setText(quality.getO3_1h());
		((TextView) circles[5].findViewById(R.id.num)).setText(quality.getO3_8h());
		((TextView) circles[6].findViewById(R.id.num)).setText(quality.getSo2());

	}
	/**
	 * 初始化所有的提示按钮
	 */
	private void setHelperButtons() {
		// 初始化下方按钮
		setCircles();

		// 初始化AQI按钮及其监听
		aqiAns = (ViewFlipper) findViewById(R.id.what_is_aqi);
		aqiAskBtn = (View) findViewById(R.id.aqi_btn);
		aqiAskBtn.setOnTouchListener(new OnAQITouchListener(aqiAns,
				this));

		// 初始化下方按钮监听
		elseAns = (ViewFlipper) findViewById(R.id.what_is_else);
		elseAskBtn1 = (LinearLayout) findViewById(R.id.c1);
		elseAskBtn1.setOnTouchListener(new OnCirclesTouchListener(
				elseAns, OnCirclesTouchListener.Part.UP, this));
		elseAskBtn2 = (LinearLayout) findViewById(R.id.c2);
		elseAskBtn2.setOnTouchListener(new OnCirclesTouchListener(
				elseAns, OnCirclesTouchListener.Part.DOWN, this));
	}

	/**
	 * 初始化下方8个提示按钮
	 */
	private void setCircles() {
		circles[0] = (LinearLayout) findViewById(R.id.circle1);
		circles[1] = (LinearLayout) findViewById(R.id.circle2);
		circles[2] = (LinearLayout) findViewById(R.id.circle3);
		circles[3] = (LinearLayout) findViewById(R.id.circle4);
		circles[4] = (LinearLayout) findViewById(R.id.circle5);
		circles[5] = (LinearLayout) findViewById(R.id.circle6);
		circles[6] = (LinearLayout) findViewById(R.id.circle7);
		circles[7] = (LinearLayout) findViewById(R.id.circle8);
		
		String[] nameArr = PM25Constants.getNameArray();
		
		// 因为nameArr中的第一位是AQI，这里需要被忽略
		for (int i = 0; i < CIRCLES_NUM - 1; i++) {
			((TextView) circles[i].findViewById(R.id.des)).setText(nameArr[i+1]);
		}
		//最后一个，是空的
		((TextView) circles[CIRCLES_NUM-1].findViewById(R.id.num)).setText(
				getResources().getString(R.string.moeEye));
		((TextView) circles[CIRCLES_NUM-1].findViewById(R.id.des)).setText(
				Html.fromHtml(getResources().getString(R.string.moeMouse)));
	}

}

/**
 * AQI按钮的点击效果
 * @author Administrator
 */
final class OnAQITouchListener implements OnTouchListener {

	private ViewFlipper aqiAns;
	private Context context;

	public OnAQITouchListener(ViewFlipper aqiAns, Context context) {
		this.aqiAns = aqiAns;
		this.context = context;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		// 根据圆形判断给text赋值
		case MotionEvent.ACTION_DOWN:
			aqiAns.setVisibility(View.VISIBLE);
			aqiAns.startAnimation(AnimationUtils.loadAnimation(
					context, R.anim.push_in));
			break;
		case MotionEvent.ACTION_UP:
			v.performClick();
			aqiAns.setVisibility(View.INVISIBLE);
			aqiAns.startAnimation(AnimationUtils.loadAnimation(
					context, R.anim.push_out));
			break;
		default:
			break;
		}
		return true;
	}

}

/**
 * 污染物细节的点击效果
 * @author Administrator
 */
final class OnCirclesTouchListener implements OnTouchListener {

	// 该点击块所在位置的enum
	public enum Part {
		UP, DOWN;
	}

	private ViewFlipper elseAns;
	private Context context;
	private TextView elseTextArea;
	private String toShow;
	private Part part;
	
	public OnCirclesTouchListener(ViewFlipper elseAns, Part part, Context context) {
		this.elseAns = elseAns;
		this.context = context;
		this.part = part;
		elseTextArea = (TextView) elseAns.findViewById(R.id.elseText);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (isInCircleArea(v, event)) {
					elseTextArea.setText(toShow);
					elseAns.setVisibility(View.VISIBLE);
					elseAns.startAnimation(AnimationUtils
							.loadAnimation(context,
									R.anim.push_in));
					break;
				} else {
					return false;
				}
			case MotionEvent.ACTION_UP:
				v.performClick();
				elseAns.setVisibility(View.INVISIBLE);
				elseAns.startAnimation(AnimationUtils
						.loadAnimation(context,
								R.anim.push_out));
				break;
			default:
				break;
			}
			return true;

	}

	// 圆形区域的矩形范围，直接通过坐标x在该行的四分之多少进行定位
	// ! WARN 具有幻数4
	private boolean isInCircleArea(View v, MotionEvent event) {
		int lineIndex = Constants.NO_VALUE;// 定位，从0开始
		int xTouch = (int) event.getX();
		int halfWidth = v.getWidth() >> 1;
		
		if (xTouch < halfWidth) {// 0or1
			lineIndex=0;
		} else {		// 2or3
			lineIndex=2;
		}
		// 再一半（四分之一）
		int quarterWidth = halfWidth >> 1;
		
		// 是否在右侧
		boolean isInRhs = (lineIndex == 0) ? (xTouch > quarterWidth) : (xTouch > quarterWidth + halfWidth);
		if (isInRhs) {
			lineIndex += 1;
		}
		
		int circleIndex = part.ordinal() * 4 + lineIndex;
		MyLog.e("circleIndex", ""+circleIndex);

		if (circleIndex == DetailActivity.CIRCLES_NUM - 1) {
			return false;
		} else {
			// 因为第一个是AQI，在圆中没有AQI
			toShow = PM25Constants.getDescribeArray()[circleIndex + 1];
			return true;
		}
	}

}

