package com.example.pm25.util.myComponent;

import java.util.List;

import com.example.pm25.po.BasePlace;
import com.example.pm25.po.Station;
import com.example.pm25.util.MyLog;

import android.R.bool;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * 自定义spinner，使其在文字后面有个下拉小三角的提示
 * 
 * @author Administrator
 * @param <T>
 */
public class StationAdapter<T> extends ArrayAdapter<BasePlace> {
	
	public StationAdapter(Context context, int resource, List<BasePlace> objects) {
		super(context, resource, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		BasePlace data = getItem(position);
		View selectedView = super.getView(position, convertView, parent);
		TextView textView = (TextView) selectedView.findViewById(android.R.id.text1);
		textView.setText(data.getName() + " ▼");
		return selectedView;
	}
	
	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		BasePlace data = getItem(position);
		View selectedView = super.getDropDownView(position, convertView, parent);
		TextView textView = (TextView) selectedView.findViewById(android.R.id.text1);  
		textView.setText(data.getName());
		return selectedView;
	}

}
