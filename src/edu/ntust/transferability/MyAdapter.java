package edu.ntust.transferability;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class MyAdapter extends BaseAdapter
{
	private List<ApplicationInfo> list;
	private SparseBooleanArray checkedList;
	private LayoutInflater inflater;
	private Context context;
//	private String filePath;

	public MyAdapter(Context context, List<ApplicationInfo> list, SparseBooleanArray checkedArray)
	{
		inflater = LayoutInflater.from(context);
		this.list = list;
		this.checkedList = checkedArray;
		this.context = context;
	}

	@Override
	public int getCount()
	{
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	public int getCheckedCount()
	{
		int count = 0;
		for (int i = 0; i < checkedList.size(); i++)
		{
			if (checkedList.valueAt(i))
			{
				count++;
			}
		}
		return count;
	}
	
	public List<ApplicationInfo> getList()
	{
		return list;
	}
	
	class Holder
	{
		public CheckBox cb;
		public ImageView ivInstallAppIcon;
		public TextView tvInstallAppName;
		public TextView tvPackageName;
//		public String filePath;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		Holder holder;
		if (convertView == null)
		{
			convertView = inflater.inflate(R.layout.item_listview_main, null);
			holder = new Holder();
			convertView.setTag(holder);
		}
		else
		{
			holder = (Holder) convertView.getTag();
		}
		
		holder.cb = (CheckBox) convertView.findViewById(R.id.cb);
		holder.cb.setChecked(checkedList.get(position, false));	

		holder.ivInstallAppIcon = (ImageView) convertView.findViewById(R.id.ivInstallAppIcon);
		holder.ivInstallAppIcon.setImageDrawable(list.get(position).loadIcon(context.getPackageManager()));

		holder.tvInstallAppName = (TextView) convertView.findViewById(R.id.tvInstallAppName);
		holder.tvInstallAppName.setText(list.get(position).loadLabel(context.getPackageManager()));

		holder.tvPackageName = (TextView) convertView.findViewById(R.id.tvPackageName);
		File f = new File(list.get(position).sourceDir);
		holder.tvPackageName.setText(list.get(position).packageName + " " + String.format("%.2f", ((float)f.length() / 1024 / 1024)) + "MB");

		return convertView;
	}
	
	public SparseBooleanArray getCheckedList()
	{
		return checkedList;
	}

	public void setCheckedList(SparseBooleanArray checkedList)
	{
		this.checkedList = checkedList;
	}

}
