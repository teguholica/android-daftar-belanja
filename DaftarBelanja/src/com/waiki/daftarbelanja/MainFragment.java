package com.waiki.daftarbelanja;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

import com.squareup.timessquare.CalendarPickerView;
import com.squareup.timessquare.CalendarPickerView.SelectionMode;
import com.waiki.daftarbelanja.common.CustomTitle;
import com.waiki.daftarbelanja.common.GlobalClass;
import com.waiki.daftarbelanja.common.Helper;
import com.waiki.daftarbelanja.obj.ItemObj;
import com.waiki.daftarbelanja.storage.Database;

public class MainFragment extends Fragment implements OnClickListener, OnItemLongClickListener, OnItemClickListener {
	
	private ArrayList<ItemObj> listData;
	private ListItemAdapter listItemAdapter;
	private Database database;
	private CustomTitle header;
	private Boolean isBatchDelete = false;
	private TextView txtTotal;
	private TextView headerDateFrom;
	private TextView headerDateSeparator;
	private TextView headerDateTo;
	private ArrayList<Date> dateRange;
	private TextView txtNoData;
	private ListView list;
	
	GlobalClass globalVariable;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		database = new Database(getActivity());
		globalVariable = (GlobalClass) getActivity().getApplicationContext();
		View rootView = inflater.inflate(R.layout.fragment_main, container,
				false);
		
		txtTotal = (TextView) rootView.findViewById(R.id.fragment_main_total);
		
		dateRange = new ArrayList<Date>();
		headerDateFrom = (TextView) rootView.findViewById(R.id.fragment_main_date_from);
		headerDateSeparator = (TextView) rootView.findViewById(R.id.fragment_main_date_separator);
		headerDateTo = (TextView) rootView.findViewById(R.id.fragment_main_date_to);
		Calendar currentDate = Calendar.getInstance();
		dateRange.add(currentDate.getTime());
		setHeaderDate(currentDate.getTime(), currentDate.getTime());
		
		header = (CustomTitle) rootView.findViewById(R.id.fragment_main_header);
		ImageButton btnCalendar = (ImageButton) header.findViewById(R.id.header_date);
		btnCalendar.setOnClickListener(this);
		toggleAddDelete("add");
		
		txtNoData = (TextView) rootView.findViewById(R.id.fragment_main_no_data);
		
		list = (ListView) rootView.findViewById(R.id.fragment_main_list);
		listData = new ArrayList<ItemObj>();
		database.open();
		txtTotal.setText(Helper.toCurrencyFormat(database.getTotalPrice(), "ind" ,"IDN"));
		listData = database.getAllItems();
		database.close();
		listItemAdapter = new ListItemAdapter();
		list.setAdapter(listItemAdapter);
		noData();
		list.setOnItemLongClickListener(this);
		list.setOnItemClickListener(this);
		
		return rootView;
	}
	
	private void noData(){
		if(listData.size() == 0){
			list.setVisibility(View.GONE);
			txtNoData.setVisibility(View.VISIBLE);
		}else{
			list.setVisibility(View.VISIBLE);
			txtNoData.setVisibility(View.GONE);
		}
	}
	
	private void setHeaderDate(Date dateFrom, Date dateTo){
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
		String dateFromStr = dateFormat.format(dateFrom);
		String dateToStr = dateFormat.format(dateTo);
		if(dateFrom.compareTo(dateTo) == 0){
			headerDateFrom.setText(dateFromStr);
			headerDateSeparator.setVisibility(View.GONE);
			headerDateTo.setVisibility(View.GONE);
		}else{
			headerDateFrom.setText(dateFromStr);
			headerDateTo.setText(dateToStr);
			headerDateSeparator.setVisibility(View.VISIBLE);
			headerDateTo.setVisibility(View.VISIBLE);
		}
	}
	
	public Boolean backPress(){
		if(isBatchDelete){
			isBatchDelete = false;
			listItemAdapter.notifyDataSetChanged();
			noData();
			toggleAddDelete("add");
			return false;
		}else{
			return true;
		}
	}
	
	class ListItemAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return listData.size();
		}

		@Override
		public Object getItem(int position) {
			return listData.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView == null){
				LayoutInflater mInflater = (LayoutInflater) getActivity().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
				convertView = mInflater.inflate(R.layout.fragment_main_list_item, null);
			}
			
			TextView txtName = (TextView) convertView.findViewById(R.id.fragment_main_list_item_name);
			txtName.setText(listData.get(position).getName());
			
			TextView txtDate = (TextView) convertView.findViewById(R.id.fragment_main_list_item_date);
			txtDate.setText(Helper.dateToString(listData.get(position).getItemDate(), "dd-MMM-yyyy"));
			
			TextView txtQty = (TextView) convertView.findViewById(R.id.fragment_main_list_item_qty);
			txtQty.setText(listData.get(position).getQty()+" x "+Helper.toCurrencyFormat(listData.get(position).getPrice(), "ind" ,"IDN"));
			
			TextView txtPrice = (TextView) convertView.findViewById(R.id.fragment_main_list_item_price);
			txtPrice.setText(Helper.toCurrencyFormat((listData.get(position).getPrice()*listData.get(position).getQty()), "ind" ,"IDN"));
			
			if(!isBatchDelete){
				convertView.setBackgroundColor(Color.TRANSPARENT);
			}
			
			return convertView;
		}
		
	}
	
	private void showAddItemDialog(String title, String msg){
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View customView = inflater.inflate(R.layout.dialog_add, null);
		
		builder.setView(customView);
		
		final EditText txtName = (EditText) customView.findViewById(R.id.dialog_add_name);
		final EditText txtQty = (EditText) customView.findViewById(R.id.dialog_add_qty);
		final EditText txtPrice = (EditText) customView.findViewById(R.id.dialog_add_price);
		final Spinner spChooseDate = (Spinner) customView.findViewById(R.id.dialog_add_choose_date);
		spChooseDate.setAdapter(new BaseAdapter() {
			
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				if (convertView == null) {
					LayoutInflater mInflatermInflater = LayoutInflater
							.from(getActivity().getApplicationContext());
					convertView = mInflatermInflater.inflate(
							R.layout.common_combo_box, null);
				}
				
				SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
				String currentDateStr = dateFormat.format(dateRange.get(position));

				TextView value = (TextView) convertView
						.findViewById(R.id.common_combo_box_item_value);
				value.setText(currentDateStr);

				return convertView;
			}
			
			@Override
			public long getItemId(int position) {
				return position;
			}
			
			@Override
			public Date getItem(int position) {
				return dateRange.get(position);
			}
			
			@Override
			public int getCount() {
				return dateRange.size();
			}
		});
		
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
           public void onClick(DialogInterface dialog, int id) {
        	   ItemObj item = new ItemObj(txtName.getText().toString(), Integer.parseInt(txtQty.getText().toString()), Integer.parseInt(txtPrice.getText().toString()), (Date) spChooseDate.getSelectedItem());
        	   database.open();
        	   int lastInsertId = database.insertItem(item);
        	   item.setId(lastInsertId);
        	   txtTotal.setText(Helper.toCurrencyFormat(database.getTotalPrice(), "ind" ,"IDN"));
        	   database.close();
               listData.add(item);
               listItemAdapter.notifyDataSetChanged();
               noData();
               dialog.dismiss();
           }
		});
		
		builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
           public void onClick(DialogInterface dialog, int id) {
               dialog.dismiss();
           }
		});
		
		AlertDialog dialog = builder.create();
		dialog.show();
	}
	
	private void showChooseDateDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View customView = inflater.inflate(R.layout.dialog_date_picker, null);
		
		Calendar beforeYear = Calendar.getInstance();
		beforeYear.add(Calendar.YEAR, -1);
		Calendar currentYear = Calendar.getInstance();
		currentYear.set(Calendar.DATE, currentYear.getActualMaximum(Calendar.DATE)+1);

		final CalendarPickerView calendar = (CalendarPickerView) customView.findViewById(R.id.calendar_view);
		Date selectedToday = new Date();
		calendar.init(beforeYear.getTime(), currentYear.getTime(), new Locale("ind", "IDN"))
		    .withSelectedDate(selectedToday).inMode(SelectionMode.RANGE);
		
		builder.setView(customView);
		
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
           public void onClick(DialogInterface dialog, int id) {
        	   Iterator<Date> itr = calendar.getSelectedDates().iterator();
        	   Date dateFrom = itr.next();
        	   Date dateTo = dateFrom;
        	   dateRange.clear();
        	   dateRange.add(dateFrom);
        	   while (itr.hasNext()) {
        		   dateTo = itr.next();
        		   dateRange.add(dateTo);
        	   }
        	   setHeaderDate(dateFrom, dateTo);
               dialog.dismiss();
           }
		});
		
		builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
           public void onClick(DialogInterface dialog, int id) {
               dialog.dismiss();
           }
		});
		
		AlertDialog dialog = builder.create();
		dialog.show();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.header_add:
			showAddItemDialog("test", "test");
			break;
			
		case R.id.header_delete:
			database.open();
			for (ItemObj item : globalVariable.getBatchDeleteItem()) {
	            database.deleteItem(item.getId());
	        }
			listData = database.getAllItems();
			listItemAdapter.notifyDataSetChanged();
			noData();
			txtTotal.setText(Helper.toCurrencyFormat(database.getTotalPrice(), "ind" ,"IDN"));
			database.close();
			globalVariable.resetBatchDeleteItem();
			toggleAddDelete("add");
			isBatchDelete = false;
			break;
			
		case R.id.header_date:
			showChooseDateDialog();
			break;

		default:
			break;
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		isBatchDelete = true;
		view.setBackgroundColor(getResources().getColor(R.color.asm_dark_blue));
		toggleAddDelete("delete");
		globalVariable.setBatchDeleteItem(listData.get(position));
		
		return false;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if(isBatchDelete){
			view.setBackgroundColor(getResources().getColor(R.color.asm_dark_blue));
			globalVariable.setBatchDeleteItem(listData.get(position));
		}
	}
	
	private void toggleAddDelete(String action){
		ImageButton btnHeaderAdd = (ImageButton) header.findViewById(R.id.header_add);
		btnHeaderAdd.setOnClickListener(this);
		ImageButton btnHeaderDelete = (ImageButton) header.findViewById(R.id.header_delete);
		btnHeaderDelete.setOnClickListener(this);
		if(action.equalsIgnoreCase("add")){
			btnHeaderAdd.setVisibility(View.VISIBLE);
			btnHeaderDelete.setVisibility(View.GONE);
		}else if(action.equalsIgnoreCase("delete")){
			btnHeaderAdd.setVisibility(View.GONE);
			btnHeaderDelete.setVisibility(View.VISIBLE);
		}
	}
	
}
