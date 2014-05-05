package com.waiki.daftarbelanja.common;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.waiki.daftarbelanja.R;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

public class CustomTitle extends LinearLayout {

	Button btnOption;
	TextView headerTitle;
	Button btnSearch;

	public CustomTitle(Context context, AttributeSet attrs) {
		super(context, attrs);

		LayoutInflater.from(context).inflate(R.layout.header, this, true);
		
		SearchView searchView = (SearchView) findViewById(R.id.header_search);
		
		ImageView searchButton = (ImageView) findViewById(searchView.getResources().getIdentifier("android:id/search_button", null, null));
		searchButton.setImageResource(R.drawable.search);
		
		ImageView searchMagButton = (ImageView) findViewById(searchView.getResources().getIdentifier("android:id/search_close_btn", null, null));
		searchMagButton.setImageResource(R.drawable.search_close);
		
		int queryTextViewId = getResources().getIdentifier("android:id/search_src_text", null, null);  
		View autoComplete = searchView.findViewById(queryTextViewId);
 
		try {
			Class<?> clazz = Class.forName("android.widget.SearchView$SearchAutoComplete");

			SpannableStringBuilder stopHint = new SpannableStringBuilder("   ");  
			stopHint.append("");

			Drawable searchIcon = getResources().getDrawable(R.drawable.search); 
			
			Method textSizeMethod = clazz.getMethod("getTextSize");
			Float rawTextSize = (Float)textSizeMethod.invoke(autoComplete);  
			int textSize = (int) (rawTextSize * 1.25);  
			searchIcon.setBounds(0, 0, textSize, textSize);  
			stopHint.setSpan(new ImageSpan(searchIcon), 1, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

			Method setHintMethod = clazz.getMethod("setHint", CharSequence.class);  
			setHintMethod.invoke(autoComplete, stopHint);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}  
		
	}

	public String getHeaderTitle() {
		return headerTitle.getText().toString();
	}

	public void setHeaderTitle(String headerTitle) {
		this.headerTitle.setText(headerTitle);
	}

}
