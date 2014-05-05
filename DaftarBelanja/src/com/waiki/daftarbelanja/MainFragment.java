package com.waiki.daftarbelanja;

import java.util.ArrayList;

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
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

import com.waiki.daftarbelanja.common.CustomTitle;
import com.waiki.daftarbelanja.common.GlobalClass;
import com.waiki.daftarbelanja.obj.ItemObj;
import com.waiki.daftarbelanja.storage.Database;

public class MainFragment extends Fragment implements OnClickListener, OnItemLongClickListener, OnItemClickListener {
	
	private ArrayList<ItemObj> listData;
	private ListItemAdapter listItemAdapter;
	private Database database;
	private CustomTitle header;
	private Boolean isBatchDelete = false;
	
	GlobalClass globalVariable;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		database = new Database(getActivity());
		globalVariable = (GlobalClass) getActivity().getApplicationContext();
		View rootView = inflater.inflate(R.layout.fragment_main, container,
				false);
		
		header = (CustomTitle) rootView.findViewById(R.id.fragment_main_header);
		toggleAddDelete("add");
		
		ListView list = (ListView) rootView.findViewById(R.id.fragment_main_list);
		listData = new ArrayList<ItemObj>();
		database.open();
		listData = database.getAllItems();
		database.close();
		listItemAdapter = new ListItemAdapter();
		list.setAdapter(listItemAdapter);
		list.setOnItemLongClickListener(this);
		list.setOnItemClickListener(this);
		
		return rootView;
	}
	
	public Boolean backPress(){
		if(isBatchDelete){
			isBatchDelete = false;
			listItemAdapter.notifyDataSetChanged();
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
			TextView txtPrice = (TextView) convertView.findViewById(R.id.fragment_main_list_item_price);
			txtPrice.setText("Rp "+listData.get(position).getPrice()+"");
			
			if(!isBatchDelete){
				convertView.setBackgroundColor(Color.TRANSPARENT);
			}
			
			return convertView;
		}
		
	}
	
	private void dialogBox(String title, String msg){
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View customView = inflater.inflate(R.layout.dialog_add, null);
		
		builder.setView(customView);
		
		final EditText txtName = (EditText) customView.findViewById(R.id.dialog_add_name);
		final EditText txtQty = (EditText) customView.findViewById(R.id.dialog_add_qty);
		final EditText txtPrice = (EditText) customView.findViewById(R.id.dialog_add_price);
		
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
           public void onClick(DialogInterface dialog, int id) {
        	   ItemObj item = new ItemObj(txtName.getText().toString(), Integer.parseInt(txtQty.getText().toString()), Integer.parseInt(txtPrice.getText().toString()));
        	   database.open();
        	   int lastInsertId = database.insertItem(item);
        	   item.setId(lastInsertId);
        	   database.close();
               listData.add(item);
               listItemAdapter.notifyDataSetChanged();
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
			dialogBox("test", "test");
			break;
			
		case R.id.header_delete:
			database.open();
			for (ItemObj item : globalVariable.getBatchDeleteItem()) {
	            database.deleteItem(item.getId());
	        }
			listData = database.getAllItems();
			listItemAdapter.notifyDataSetChanged();
			database.close();
			globalVariable.resetBatchDeleteItem();
			toggleAddDelete("add");
			isBatchDelete = false;
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
