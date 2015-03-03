package com.night.contact.contact;

import java.util.ArrayList;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.RawContacts;
import android.provider.ContactsContract.RawContacts.Data;
import android.util.Log;

import com.night.contact.bean.SortEntry;

/**
 * database������
 * @deprecated
 * @author NightHary
 *
 */
public class Utils {
	public static Context m_context;
	//������ϵ�˵�����list
	public static ArrayList<SortEntry> mPersons = new ArrayList<SortEntry>();
	
	//��ʼ��������Activity��������
	public static void init(Context context)
	{
		m_context = context;
	}

	//�����ݿ���������ϵ��
	public static void addContact(String name, String number)
	{
		ContentValues values = new ContentValues(); 
        //������RawContacts.CONTENT_URIִ��һ����ֵ���룬Ŀ���ǻ�ȡϵͳ���ص�rawContactId  
        Uri rawContactUri = m_context.getContentResolver().insert(RawContacts.CONTENT_URI, values); 
        long rawContactId = ContentUris.parseId(rawContactUri); 
        //��data������������� 
        values.clear(); 
        values.put(Data.RAW_CONTACT_ID, rawContactId);  
        values.put(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE);//�������� 
        values.put(StructuredName.GIVEN_NAME, name); 
        m_context.getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);
       
        //��data�����绰���� 
        values.clear(); 
        values.put(Data.RAW_CONTACT_ID, rawContactId); 
        values.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE); 
        values.put(Phone.NUMBER, number); 
        values.put(Phone.TYPE, Phone.TYPE_MOBILE); 
        m_context.getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values); 
        
	}
	
	//�������ݿ�����ϵ��
	public static void changeContact(String name, String number, String ContactId)
	{
		Log.i("huahua", name);
		ContentValues values = new ContentValues();
		// ��������
        values.put(StructuredName.GIVEN_NAME, name);
        m_context.getContentResolver().update(ContactsContract.Data.CONTENT_URI,
                        values,
                        Data.RAW_CONTACT_ID + "=? and " + Data.MIMETYPE  + "=?",
                        new String[] { ContactId,StructuredName.CONTENT_ITEM_TYPE });
		
		//���µ绰
        values.clear();
        values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, number);
        m_context.getContentResolver().update(ContactsContract.Data.CONTENT_URI,
				values, 
				Data.RAW_CONTACT_ID + "=? and " + Data.MIMETYPE  + "=?",
				new String[] { ContactId,Phone.CONTENT_ITEM_TYPE});
	}
	
	//ɾ����ϵ��
	public static void deleteContact(String ContactId)
	{
		m_context.getContentResolver().delete(
				ContentUris.withAppendedId(RawContacts.CONTENT_URI,
						Integer.parseInt(ContactId)), null, null);
	}
}
