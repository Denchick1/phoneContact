package com.night.contact.ui;


import java.io.InputStream;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.night.contact.DAO.GroupDAO;
import com.night.contact.adapter.FilterAdapter;
import com.night.contact.bean.SortEntry;
import com.night.contact.contact.SortCursor;
import com.night.contact.contact.SortCursorLoader;
import com.night.contact.util.Tools;

@SuppressLint("HandlerLeak")
public class AddRemoveContactGroup extends Activity{
	
	// �������layout
	private RelativeLayout acbuwa_topbar;
	//��ĸ����ͼView
	private AlphabetScrollBar asb;
	//��ʾѡ�е���ĸ
	private TextView letterNotice;
	//��ϵ�˵��б�
	private ListView contactslist;
	//��ϵ���б��������
	private ContactsCursorAdapter contactsAdapter;
	//������������
	private ContactsLoaderListener m_loaderCallback = new ContactsLoaderListener();
	private String type;//��������
	private int groupID;
	//������ϵ�˵�����list
	private ArrayList<SortEntry> sortList = new ArrayList<SortEntry>();
	//ɸѡ��ѯ�������list
	private ArrayList<SortEntry> filterList = new ArrayList<SortEntry>();
	// ����������ϵ��EditText
	private EditText filterEditText;
	// ɸѡ���������
	private FilterAdapter fAdapter;
	//ѡ�ж��ٸ���Ҫɾ������ϵ��
	private int choosenum=0;
	//����ɾ����ť
	private Button sureNumBtn;
	private TextView txtAddContactToGroup_Title;
	//û��ƥ����ϵ��ʱ��ʾ��TextView
	private TextView listEmptyText;
	//ѡ��ȫ����ť
	private Button selectAllBtn;
	//ѡ��������ϵ�˵ı�־
	private boolean selectAll = false;
	//id������
	private ArrayList<String> chooseContactsID = new ArrayList<String>();
	private ProgressDialog m_dialogLoading;
	private SortCursor contactsCursor;
	
	private ImageButton imbAddContactToGroup_Back;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_add_contact_to_group);
		
		txtAddContactToGroup_Title = (TextView) this.findViewById(R.id.txtAddContactToGroup_Title);
		type = getIntent().getExtras().getString("type");
		groupID = getIntent().getExtras().getInt("groupID");
		
		//�õ���ϵ���б�,������������
		getLoaderManager().initLoader(0,null,m_loaderCallback);
		contactslist = (ListView)findViewById(R.id.pb_listvew);
		contactsAdapter = new ContactsCursorAdapter(this, null);
		contactsAdapter.setData(sortList);
		contactslist.setAdapter(contactsAdapter);
		
		//�õ���ĸ�еĶ���,�����ô�����Ӧ������
		asb = (AlphabetScrollBar)findViewById(R.id.fast_scroller);
		asb.setOnTouchBarListener(new ScrollBarListener());
		letterNotice = (TextView)findViewById(R.id.fast_position);
		asb.setTextView(letterNotice);
				
		listEmptyText = (TextView)findViewById(R.id.nocontacts_notice);
		acbuwa_topbar = (RelativeLayout)findViewById(R.id.acbuwa_topbar);
		sureNumBtn = (Button)findViewById(R.id.sure_num);
		selectAllBtn = (Button)findViewById(R.id.select_all);
		
		//��ʼ�������༭��,�����ı��ı�ʱ�ļ�����
		filterEditText = (EditText)findViewById(R.id.pb_search_edit);
		
		imbAddContactToGroup_Back = (ImageButton) this.findViewById(R.id.imbAddContactToGroup_Back);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if(type.equals("add")){
			Tools.Toast(AddRemoveContactGroup.this,type);
			txtAddContactToGroup_Title.setText("��Ⱥ����ӳ�Ա");
		}else{
			Tools.Toast(AddRemoveContactGroup.this,type);
			txtAddContactToGroup_Title.setText("��Ⱥ���Ƴ���Ա");
		}
		//����
		imbAddContactToGroup_Back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		// ������ϵ��
		filterEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

				if (!"".equals(s.toString().trim())) {
					// ���ݱ༭��ֵ������ϵ�˲�������ϵ�б�
//					SortCursor contactsCursor = (SortCursor) contactsAdapter.getCursor();
//					getContactCursor();
					filterList = contactsCursor.filterSearch1(s.toString().trim(),sortList);

					fAdapter = new FilterAdapter(AddRemoveContactGroup.this, filterList);
					contactslist.setAdapter(fAdapter);
				} else {
					contactslist.setAdapter(contactsAdapter);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
				
		//��ϵ�˵���¼������������ѡ�л��߷�ѡ
		contactslist.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if(sortList.get(arg2).mchoose == 0)
				{
					sortList.get(arg2).mchoose = 1;
					choosenum++;
					chooseContactsID.add(sortList.get(arg2).mID);
				}
				else
				{
					sortList.get(arg2).mchoose = 0;
					for(int i=0;i<choosenum;i++)
					{
						if(chooseContactsID.get(i).equals(sortList.get(arg2).mID))
						{
							chooseContactsID.remove(i);
							break;
						}
					}
					choosenum--;
				}
				
				contactsAdapter.notifyDataSetChanged();
				sureNumBtn.setText("ȷ��("+ choosenum +")");
			}
		});
		
		//ȷ����ť��ѡ��ȫ����ť
		sureNumBtn.setOnClickListener(new BtnClick());
		selectAllBtn.setOnClickListener(new BtnClick());
	}
	
	private class BtnClick implements View.OnClickListener{

		@Override
		public void onClick(View v) {
			if(v.getId() == R.id.sure_num)
			{
				if(choosenum>0)
				{
					if(type.equals("add")){
						new AddContactToGTask(groupID).execute();
					}else if(type.equals("remove")){
						new RemoveContactFromGTask(groupID).execute();
					}
				}
				else
				{
					Tools.Toast(AddRemoveContactGroup.this,"��ѡ��Ҫ"+Tools.pinYinToHanZi(type)+"����ϵ��");
				}
			}
			else if(v.getId() == R.id.select_all)
			{
				chooseContactsID.clear();
				if(!selectAll)
				{
					for(int i=0;i<sortList.size();i++)
					{
						sortList.get(i).mchoose = 1;
						chooseContactsID.add(sortList.get(i).mID);
					}
					choosenum = sortList.size();
					sureNumBtn.setText("ȷ��("+ sortList.size() +")");
					selectAllBtn.setText("ȡ��ȫ��");
					contactsAdapter.notifyDataSetChanged();
					selectAll = !selectAll;
				}
				else
				{
					for(int i=0;i<sortList.size();i++)
					{
						sortList.get(i).mchoose = 0;
					}
					choosenum = 0;
					sureNumBtn.setText("ȷ��(0)");
					selectAllBtn.setText("ѡ��ȫ��");
					contactsAdapter.notifyDataSetChanged();
					selectAll = !selectAll;
				}
			}
		}
	}
	
	// �������ļ�����
	private class ContactsLoaderListener implements
			LoaderManager.LoaderCallbacks<Cursor> {

		@Override
		public Loader<Cursor> onCreateLoader(int id, Bundle args) {
			return new SortCursorLoader(AddRemoveContactGroup.this,
					ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
					null, null, null);
		}

		@Override
		public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
			contactsAdapter.swapCursor(arg1);
//			new LoadAnysTack().execute();
			contactsCursor = (SortCursor) contactsAdapter.getCursor();
			if (acbuwa_topbar.getVisibility() == View.VISIBLE) {
				if(type.equals("add")){
					sortList  = contactsCursor.getAddContactsArray(groupID);
				}else{
					sortList  = contactsCursor.getRemoveContactsArray(groupID);
				}
				contactsAdapter.setData(sortList);
				contactslist.setAdapter(contactsAdapter);
			}else {
				filterList = contactsCursor.filterSearch(filterEditText.getText()
						.toString().trim());
				fAdapter = new FilterAdapter(AddRemoveContactGroup.this, filterList);
				contactslist.setAdapter(fAdapter);
			}
		}
		@Override
		public void onLoaderReset(Loader<Cursor> arg0) {
			contactsAdapter.swapCursor(null);
		}
	}
	
	//��ĸ�д����ļ�����
	private class ScrollBarListener implements AlphabetScrollBar.OnTouchBarListener {

		@Override
		public void onTouch(String letter) {
			
			//������ĸ��ʱ,����ϵ���б���µ�����ĸ���ֵ�λ��
//			contactsCursor = (SortCursor)contactsAdapter.getCursor();
//			getContactCursor();
			if(contactsCursor != null) 
			{
				int idx = contactsCursor.binarySearch(letter);
				if(idx != -1)
				{
					contactslist.setSelection(idx);
				}
			}
		}
	}
	
	private class ContactsCursorAdapter extends CursorAdapter{
		private Context context;
		private ArrayList<SortEntry> lists;
		@SuppressWarnings("deprecation")
		public ContactsCursorAdapter(Context context, Cursor c) {
			super(context, c);
			this.context = context;
		}
		public void setData(ArrayList<SortEntry> entryLists){
			this.lists = entryLists;
		}
		
		@Override
		public SortEntry getItem(int position) {
			return sortList.get(position);
		}

		@Override
		public int getCount() {
			return sortList.size();
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView == null)
			{
				convertView = LayoutInflater.from(context).inflate(R.layout.list_item_add_remove_contact, parent, false);
			}
			
			ImageView photo = (ImageView) convertView.findViewById(R.id.contact_photo);
	        
	        int photoId = lists.get(position).photoId;
	        String contactID = lists.get(position).mID;
	        if(0 == photoId){
	        	photo.setImageResource(R.drawable.default_contact_photo);
	        }else{
	        	Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI,Integer.parseInt(contactID));
				InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(context.getContentResolver(), uri); 
				Bitmap contactPhoto = BitmapFactory.decodeStream(input);
				photo.setImageBitmap(contactPhoto);
	        }
	        
            TextView name = (TextView) convertView.findViewById(R.id.contacts_name);
            name.setText(lists.get(position).mName);
			if(lists.get(position).mchoose == 1)
			{
				ImageView choosecontact = (ImageView)convertView.findViewById(R.id.choose_contact);
				choosecontact.setImageResource(R.drawable.cb_checked);
			}
			else
			{
				ImageView choosecontact = (ImageView)convertView.findViewById(R.id.choose_contact);
				choosecontact.setImageResource(R.drawable.cb_unchecked);
			}

			return convertView;
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			if(cursor == null)
			{
				return;
			}
			
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			return LayoutInflater.from(context).inflate(R.layout.list_item_home_contacts, parent, false);
		}
		
	}
	
	
	/**
	 * ��ӳ�Ա��Ⱥ��
	 * 
	 * @author NightHary
	 * 
	 */
	private class AddContactToGTask extends AsyncTask<Void, Void, ArrayList<SortEntry>> {
		int groupID;

		public AddContactToGTask(int groupID) {
			this.groupID = groupID;
		}

		@Override
		protected ArrayList<SortEntry> doInBackground(Void... params) {
			ArrayList<SortEntry> entrys;
			for(String contactId : chooseContactsID){
				new GroupDAO(AddRemoveContactGroup.this).addMemberToGroup(Integer.parseInt(contactId),groupID);
			}
		//	contactsCursor = (SortCursor) contactsAdapter.getCursor();
			if(type.equals("add")){
				entrys  = contactsCursor.getAddContactsArray(groupID);
			}else{
				entrys  = contactsCursor.getRemoveContactsArray(groupID);
			}
			return entrys;
		}

		@Override
		protected void onPostExecute(ArrayList<SortEntry> result) {
			super.onPostExecute(result);
			if (m_dialogLoading != null) {
				if(result.isEmpty()){
					listEmptyText.setVisibility(View.VISIBLE);
				}
				contactsAdapter.setData(result);
				contactslist.setAdapter(contactsAdapter);
				contactsAdapter.notifyDataSetChanged();
				sureNumBtn.setText("ȷ��(0)");
				choosenum = 0;
				try {
					Thread.sleep(8000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				m_dialogLoading.dismiss();
			}
		}

		@Override
		protected void onPreExecute() {
			m_dialogLoading = new ProgressDialog(AddRemoveContactGroup.this);
			m_dialogLoading.setProgressStyle(ProgressDialog.STYLE_SPINNER);// ���÷��ΪԲ�ν�����
			m_dialogLoading.setMessage("�������...");
			m_dialogLoading.setCancelable(false);
			m_dialogLoading.show();
		}
	}

	/**
	 * ��Ⱥ���Ƴ���Ա
	 * 
	 * @author NightHary
	 * 
	 */
	private class RemoveContactFromGTask extends AsyncTask<Void, Void, ArrayList<SortEntry>> {
		int groupID;
		
		public RemoveContactFromGTask(int groupID) {
			this.groupID = groupID;
		}

		@Override
		protected ArrayList<SortEntry> doInBackground(Void... params) {
			ArrayList<SortEntry> entrys;
			for(String contactId : chooseContactsID){
				new GroupDAO(AddRemoveContactGroup.this).deleteMemberFromGroup(Integer.parseInt(contactId),groupID);
			}
			contactsCursor = (SortCursor) contactsAdapter.getCursor();
			if(type.equals("add")){
				entrys  = contactsCursor.getAddContactsArray(groupID);
			}else{
				entrys  = contactsCursor.getRemoveContactsArray(groupID);
			}
			return entrys;
		}

		@Override
		protected void onPostExecute(ArrayList<SortEntry> result) {
			super.onPostExecute(result);
			if (m_dialogLoading != null) {
				if(result.isEmpty()){
					listEmptyText.setVisibility(View.VISIBLE);
				}
				contactsAdapter.setData(result);
				contactslist.setAdapter(contactsAdapter);
				contactsAdapter.notifyDataSetChanged();
				sureNumBtn.setText("ȷ��(0)");
				choosenum = 0;
				try {
					Thread.sleep(8000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				m_dialogLoading.dismiss();
			}
		}

		@Override
		protected void onPreExecute() {
			m_dialogLoading = new ProgressDialog(AddRemoveContactGroup.this);
			m_dialogLoading.setProgressStyle(ProgressDialog.STYLE_SPINNER);// ���÷��ΪԲ�ν�����
			m_dialogLoading.setMessage("�����Ƴ�...");
			m_dialogLoading.setCancelable(false);
			m_dialogLoading.show();
		}
	}
}
