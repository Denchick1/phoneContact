package com.night.contact.observer;

import android.database.ContentObserver;
import android.os.Handler;
import android.util.Log;

/**
 * ��ϵ�����ݿ�۲���
 * 
 * @author NightHary
 *
 */
public class ContactsContentObserver extends ContentObserver{

	public ContactsContentObserver(Handler handler) {
		super(handler);
	}
	@Override
	public void onChange(boolean selfChange) {
		
		Log.i("datachanged", "��ϵ�����ݿⷢ���˱仯");
	}

}
