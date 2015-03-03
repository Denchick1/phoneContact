package com.night.contact.fragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import a_vcard.android.provider.Contacts;
import a_vcard.android.syncml.pim.vcard.ContactStruct;
import a_vcard.android.syncml.pim.vcard.VCardComposer;
import a_vcard.android.syncml.pim.vcard.VCardException;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.night.contact.DAO.ContactDAO;
import com.night.contact.bean.SortEntry;
import com.night.contact.contact.SortCursor;
import com.night.contact.ui.MergeContactActivity;
import com.night.contact.ui.R;
import com.night.contact.util.NameCompartor;
import com.night.contact.util.NumCompartor;
import com.night.contact.util.Tools;

@SuppressLint({ "InflateParams", "HandlerLeak" })
public class SettingFragment extends Fragment implements
		OnFocusChangeListener {
	private View concertView;

	private LinearLayout ll_backup_contact;
	private LinearLayout ll_restore_contact;
	private LinearLayout ll_merge_contact;
	private ContactDAO contactDAO;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LayoutInflater inflater = getActivity().getLayoutInflater();
		concertView = inflater.inflate(R.layout.activity_tab_setting, null);

		ll_backup_contact = (LinearLayout) concertView
				.findViewById(R.id.ll_backup_contact);

		ll_restore_contact = (LinearLayout) concertView
				.findViewById(R.id.ll_restore_contact);
		
		ll_merge_contact = (LinearLayout) concertView
				.findViewById(R.id.ll_merge_contact);

		contactDAO = new ContactDAO(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		ViewGroup p = (ViewGroup) concertView.getParent();
		if (p != null) {
			p.removeAllViewsInLayout();
		}
		return concertView;
	}

	public void onResume() {
		super.onResume();
		//����
		ll_backup_contact.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(getActivity())
						.setTitle("������ϵ��")
						.setMessage(
								"��ϵ�˽����ݵ�"
										+ Environment
												.getExternalStorageDirectory()
										+ "/example.vcf")
						.setPositiveButton("ȷ��",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										backup_dialog = ProgressDialog.show(
												getActivity(), "������ϵ��",
												"���ڱ���...", true);
										backupThread();
									}
								})
						.setNegativeButton("ȡ��",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
									}
								}).show();

			}
		});
		ll_backup_contact.setOnFocusChangeListener(this);
		
		//��ԭ
		ll_restore_contact.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				restore_dialog = ProgressDialog.show(getActivity(), "��ԭ��ϵ��",
						"���ڻ�ԭ...", true);
				restoreThread();
			}
		});
		ll_restore_contact.setOnFocusChangeListener(this);
		
		//ȥ��
		ll_merge_contact.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(getActivity(),MergeContactActivity.class);
				entrys = contactDAO.getContacts1();
				Bundle bundle = new Bundle();
				bundle.putParcelableArrayList("entrys",(ArrayList<? extends Parcelable>) entrys);
				
				//���绰��������Ȼ����в�����ͬ����
				Collections.sort(entrys,new NumCompartor());
				List<SortEntry> numList= Tools.duplicateNum(entrys);
				
//				for(SortEntry entry : numList){
//					System.out.println(entry.mName+"  "+entry.mNum);
//				}
				//����������Ȼ����в�����ͬ����
				Collections.sort(entrys,new NameCompartor());
				List<SortEntry> nameList = Tools.duplicateName(entrys);
				
//				for(SortEntry entry : nameList){
//					System.out.println(entry.mName+"  "+entry.mNum);
//				}
//				numList.addAll(nameList);
				
			//	Collections.sort(numList,new NumCompartor());
				
				if(numList.isEmpty() && nameList.isEmpty()){
					Tools.Toast(getActivity(),"�����ظ���ϵ��");
				}else{
					bundle.putParcelableArrayList("numList",(ArrayList<? extends Parcelable>) numList);
					bundle.putParcelableArrayList("nameList",(ArrayList<? extends Parcelable>) nameList);
					intent.putExtras(bundle);
					startActivity(intent);
				}
			}
		});
		ll_merge_contact.setOnFocusChangeListener(this);
		
	}

	ProgressDialog backup_dialog;
	ProgressDialog restore_dialog;
	SortCursor contactCursor;
	List<SortEntry> entrys;

	/**
	 * ������ϵ��
	 * 
	 * @param entrys
	 *            ��Ҫ���ݵ���ϵ������
	 */
	public void backupContact(List<SortEntry> entrys) {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) // �жϴ洢���Ƿ����
		{
			OutputStreamWriter writer;
			File file = new File(Environment.getExternalStorageDirectory(),
					"example.vcf");
			// �õ��洢���ĸ�·������example.vcfд�뵽��Ŀ¼��
			try {
				writer = new OutputStreamWriter(new FileOutputStream(file),
						"UTF-8");

				VCardComposer composer = new VCardComposer();
				if(entrys != null){
					for (SortEntry entry : entrys) {
						ContactStruct contact1 = new ContactStruct();
						contact1.name = entry.mName;
						contact1.addPhone(Contacts.Phones.TYPE_MOBILE, entry.mNum,
								null, true);
						String vcardString;
						vcardString = composer.createVCard(contact1,
								VCardComposer.VERSION_VCARD30_INT);
						writer.write(vcardString);
						writer.write("\n");

						writer.flush();
					}
				}
				writer.close();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (VCardException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			try {
				throw new Exception("д��ʧ�ܣ�SD��������");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * ������ϵ�����̴߳���
	 */
	private void backupThread() {
		final Handler handler = new Handler() {

			public void handleMessage(Message msg) {
				backup_dialog.dismiss();
				Toast.makeText(getActivity().getApplicationContext(),
						"�ѳɹ�����SD���У�", Toast.LENGTH_SHORT).show();
			}
		};

		Thread myThread = new Thread() {

			@Override
			public void run() {
				super.run();
				entrys = contactDAO.getContacts();
				backupContact(entrys);
				handler.sendEmptyMessage(0);
			}
		};
		myThread.start();
	}

	/**
	 * ��ԭ��ϵ�����߳�
	 */
	private void restoreThread() {
		final Handler handler = new Handler() {

			public void handleMessage(Message msg) {
				restore_dialog.dismiss();
				Toast.makeText(getActivity(), "������ϵ����Ϣ�ɹ�!", Toast.LENGTH_LONG)
						.show();
			}
		};

		Thread restoreThread = new Thread() {
			@Override
			public void run() {
				super.run();
				restoreContact();
				handler.sendEmptyMessage(0);
			}
		};
		restoreThread.start();
	}

	/**
	 * ��ԭ��ϵ��
	 */
	private void restoreContact() {
		try {
			// ��ȡҪ�ָ�����ϵ����Ϣ
			List<SortEntry> infoList = contactDAO.restoreContacts();
			System.out.println(infoList.size());
			if(infoList != null){
				for (SortEntry entry : infoList) {
					// �ָ���ϵ��
					contactDAO.addContact1(entry, 0);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		switch (v.getId()) {
		case R.id.ll_backup_contact:
			ll_backup_contact
					.setBackgroundResource(R.drawable.btn_save_pressed);
			break;
		case R.id.ll_restore_contact:
			ll_restore_contact
					.setBackgroundResource(R.drawable.btn_save_pressed);
			break;
		}
	}

}
