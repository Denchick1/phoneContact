package com.night.contact.fragment;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.night.contact.DAO.ContactDAO;
import com.night.contact.DAO.GroupDAO;
import com.night.contact.adapter.FilterAdapter;
import com.night.contact.adapter.GroupAdapter;
import com.night.contact.bean.GroupBean;
import com.night.contact.bean.SortEntry;
import com.night.contact.contact.ClearEditText;
import com.night.contact.contact.ContactsCursorAdapter;
import com.night.contact.contact.SortCursor;
import com.night.contact.contact.SortCursorLoader;
import com.night.contact.observer.ContactsContentObserver;
import com.night.contact.ui.AddContactActivity;
import com.night.contact.ui.AddRemoveContactGroup;
import com.night.contact.ui.AlphabetScrollBar;
import com.night.contact.ui.ContactDetialActivity;
import com.night.contact.ui.GroupManageActivity;
import com.night.contact.ui.R;
import com.night.contact.util.Parameter;

/**
 * ��ϵ��������--Fragment
 * 
 * @author NightHary
 * 
 */
@SuppressLint("InflateParams")
public class ContactsFragment extends Fragment {

	private View contactsView;

	// �������layout
	private RelativeLayout acbuwa_topbar;

	// ��ϵ�����ݹ۲���
	private ContactsContentObserver contactsObserver;

	// ��ĸ������
	private AlphabetScrollBar alphaBar;
	// ��ʾѡ�е���ĸ
	private TextView letterNotice;
	// ��ϵ�˵��б�
	private ListView contactslist;
	// ��ϵ���б��������
	private ContactsCursorAdapter contactsAdapter;
	// ������������
	private ContactsLoaderListener m_ContactsCallback = new ContactsLoaderListener();

	// ɸѡ��ѯ�������list
	private ArrayList<SortEntry> filterList = new ArrayList<SortEntry>();
	// ����������ϵ��EditText
	private EditText filterEditText;
	// ɸѡ���������
	private FilterAdapter fAdapter;
	// ������ϵ��
	private Button btn_addContact;
	// ѡ����ϵ��ID
	private String chooseContactID;
	// ѡ�е���ϵ������
	private String chooseContactName;
	// ѡ�е���ϵ�˺���
	private String chooseContactNumber;
	// ѡ����ϵ��lookupkey
	private String chooseContactLoopUpKey;
	// ѡ����ϵ��ͷ��ID
	private int chooseContactPhotoId;
	// ���ȶԻ���
	ProgressDialog dialogLoading;
	ProgressDialog dialog;

	// Ⱥ��
	private TextView topbar_title_tv;
	// �Ƿ�չ��ͼ��
	private ImageView topbar_group;
	// �Ƿ�չ��
	private boolean isOpen = false;
	// Ⱥ�鵯����
	private PopupWindow popupwindow;
	private GroupDAO groupDAO;
	// Ⱥ��
	private ArrayList<GroupBean> groupList;
	// ������view������ͼ
	private ListView groupView;
	// ѡ��Ⱥ��ʱ��ʾ��list
	// ����Ⱥ�鰴ť
	private Button group_manage;
	private boolean loadFlag = false;
	private ProgressBar process_init_data;
	private TextView tv_loading;
	private SortCursor contactsCursor;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@SuppressLint("InflateParams")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LayoutInflater inflater = getActivity().getLayoutInflater();
		contactsView = inflater.inflate(R.layout.home_contact_page, null);

		contactsObserver = new ContactsContentObserver(new Handler());
		getActivity().getContentResolver().registerContentObserver(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI, false,
				contactsObserver);

		acbuwa_topbar = (RelativeLayout) contactsView
				.findViewById(R.id.acbuwa_topbar);
		process_init_data = (ProgressBar) contactsView
				.findViewById(R.id.process_init_data);
		process_init_data.setVisibility(View.VISIBLE);
		tv_loading = (TextView) contactsView.findViewById(R.id.tv_loading);
		tv_loading.setVisibility(View.VISIBLE);
		// �Ҳ���ĸ������
		alphaBar = (AlphabetScrollBar) contactsView
				.findViewById(R.id.fast_scroller);
		alphaBar.setOnTouchBarListener(new ScrollBarListener());
		letterNotice = (TextView) contactsView.findViewById(R.id.fast_position);
		alphaBar.setTextView(letterNotice);

		// �õ���ϵ���б�,������������
		getActivity().getLoaderManager()
				.initLoader(0, null, m_ContactsCallback);

		contactslist = (ListView) contactsView.findViewById(R.id.pb_listvew);
		contactsAdapter = new ContactsCursorAdapter(getActivity(), null);
		contactslist.setAdapter(contactsAdapter);

		// ������ϵ��
		btn_addContact = (Button) contactsView
				.findViewById(R.id.btn_add_contact);

		// Ⱥ��˵�
		topbar_title_tv = (TextView) contactsView
				.findViewById(R.id.topbar_title);
		topbar_group = (ImageView) this.contactsView
				.findViewById(R.id.topbar_group);

		filterEditText = (ClearEditText) contactsView
				.findViewById(R.id.pb_search_edit);
	}

	// ����Ⱥ����Ϣ
	private Runnable groupRunnable = new Runnable() {
		@Override
		public void run() {
			groupDAO = new GroupDAO(getActivity());
			groupList = groupDAO.getGroups();
			groupList.add(0, new GroupBean(0, "ȫ��"));
			groupList.add(groupList.size(), new GroupBean(0, "δ����"));
		}
	};

	@Override
	public void onResume() {
		super.onResume();

		new Thread(groupRunnable).start();

		while (loadFlag) {
			process_init_data.setVisibility(View.GONE);
			tv_loading.setVisibility(View.GONE);
		}
		// Ⱥ��˵������
		topbar_title_tv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (!isOpen) {
					topbar_group.setImageResource(R.drawable.btn_group_click);
					isOpen = true;
				} else {
					topbar_group.setImageResource(R.drawable.btn_group_normal);
					isOpen = false;
				}
				if (popupwindow != null && popupwindow.isShowing()) {
					popupwindow.dismiss();
					return;
				} else {
					if (groupList != null) {
						initmPopupWindowView();
						popupwindow.showAsDropDown(v, 0, 5);
					}
				}
			}
		});

		// ������ϵ��
		btn_addContact.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(getActivity().getApplicationContext(),
						AddContactActivity.class);
				startActivity(intent);
			}
		});

		// �鿴��ϵ��
		contactslist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// ���������жϣ�����������ڵ�ֵΪ�գ�����ʾȫ����ϵ���б�
				// �����Ϊ�գ�����ʾ����list�е�����
				SortCursor contactsCursor = (SortCursor) contactsAdapter
						.getCursor();
				if (TextUtils.isEmpty(filterEditText.getText().toString()
						.trim())
						&& (topbar_title_tv.getText().toString().trim()
								.equals("ȫ��") || topbar_title_tv.getText()
								.toString().trim().equals("��ϵ��"))) {
					chooseContactName = contactsCursor.getName(position);
					chooseContactNumber = contactsCursor.getNumber(position);
					chooseContactID = contactsCursor.getID(position);
					chooseContactLoopUpKey = contactsCursor
							.getLookUpKey(position);
					chooseContactPhotoId = contactsCursor.getPhotoId(position);

				} else {
					chooseContactName = filterList.get(position).mName;
					chooseContactNumber = filterList.get(position).mNum;
					chooseContactID = filterList.get(position).mID;
					chooseContactLoopUpKey = filterList.get(position).lookUpKey;
					chooseContactPhotoId = filterList.get(position).photoId;
				}
				Toast.makeText(getActivity(),
						chooseContactName + chooseContactID, Toast.LENGTH_SHORT)
						.show();
				SortEntry entry = new SortEntry();
				entry.mID = chooseContactID;
				entry.mName = chooseContactName;
				entry.mNum = chooseContactNumber;
				entry.lookUpKey = chooseContactLoopUpKey;
				entry.photoId = chooseContactPhotoId;
				Intent intent = new Intent();
				// ���ݵ������ϵ����Ϣ����ϵ���������
				Bundle bundle = new Bundle();
				// ���ò�������
				bundle.putString("type", Parameter.CONTACT_DETIAL_TYPE);
				// ���þ������
				bundle.putParcelable(Parameter.CONTACT_DETIAL_KEY, entry);
				intent.putExtras(bundle);
				intent.setClass(getActivity().getApplicationContext(),
						ContactDetialActivity.class);
				startActivity(intent);
			}
		});

		// ������ϵ��
		filterEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

				if (!"".equals(s.toString().trim())) {
					// ���ݱ༭��ֵ������ϵ�˲�������ϵ�б�
					SortCursor contactsCursor = (SortCursor) contactsAdapter
							.getCursor();
					filterList = contactsCursor.filterSearch(s.toString()
							.trim());

					fAdapter = new FilterAdapter(getActivity(), filterList);
					contactslist.setAdapter(fAdapter);
				} else {
					contactsAdapter.notifyDataSetChanged();
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

		// ��ϵ���б�������
		contactslist.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {

				// ��
				Vibrator vib = (Vibrator) getActivity().getSystemService(
						Service.VIBRATOR_SERVICE);
				vib.vibrate(50);

				// ���������жϣ�����������ڵ�ֵΪ�գ�����ʾȫ����ϵ���б�
				// �����Ϊ�գ�����ʾ����list�е�����
				if (TextUtils.isEmpty(filterEditText.getText().toString()
						.trim())
						&& (topbar_title_tv.getText().toString().trim()
								.equals("ȫ��") || topbar_title_tv.getText()
								.toString().trim().equals("��ϵ��"))) {
					SortCursor contactsCursor = (SortCursor) contactsAdapter
							.getCursor();
					chooseContactName = contactsCursor.getName(arg2);
					chooseContactNumber = contactsCursor.getNumber(arg2);
					chooseContactID = contactsCursor.getID(arg2);
				} else {
					chooseContactName = filterList.get(arg2).mName;
					chooseContactNumber = filterList.get(arg2).mNum;
					chooseContactID = filterList.get(arg2).mID;
				}
				showLongClickDialog();
				return false;
			}
		});
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onPause() {
		super.onPause();

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	// ��ʼ��Ⱥ��˵�
	@SuppressWarnings("deprecation")
	public void initmPopupWindowView() {
		// // ��ȡ�Զ��岼���ļ�pop.xml����ͼ
		View customView = getActivity().getLayoutInflater().inflate(
				R.layout.popview_page, null, false);
		groupView = (ListView) customView.findViewById(R.id.lv_group);
		// ����PopupWindowʵ��,200,150�ֱ��ǿ�Ⱥ͸߶�
		popupwindow = new PopupWindow(customView, 150, 270);
		// ���ö���Ч�� [R.style.AnimationFade ���Լ����ȶ���õ�]
		popupwindow.setAnimationStyle(R.style.AnimationFade);
		// �������ĸ�view��Ӧ���Ǹ������parent��ȷ������λ�ã��������parent��x��ƫ�� �������parent��y��ƫ��
		popupwindow.showAsDropDown(customView, 150, 100);

		// ʹ��ۼ�
		popupwindow.setFocusable(true);
		// ����������������ʧ
		popupwindow.setOutsideTouchable(true);
		// ˢ��״̬������ˢ�·�����Ч��
		popupwindow.update();

		// �����Ϊ�˵��������Back��Ҳ��ʹ����ʧ�����Ҳ�����Ӱ����ı�����������ģ�
		popupwindow.setBackgroundDrawable(new BitmapDrawable());

		// �Զ���view��Ӵ����¼�
		customView.setOnTouchListener(new OnTouchListener() {

			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (popupwindow != null && popupwindow.isShowing()) {
					popupwindow.dismiss();
					popupwindow = null;
				}
				return false;
			}
		});
		groupView.setDividerHeight(1);
		// Ⱥ�����ť
		group_manage = (Button) customView.findViewById(R.id.group_manage);
		group_manage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				popupwindow.dismiss();
				Intent intent = new Intent();
				intent.setClass(getActivity(), GroupManageActivity.class);
				Bundle bundle = new Bundle();
				bundle.putParcelableArrayList(Parameter.GROUP_LIST,
						(ArrayList<? extends Parcelable>) groupList);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
		groupView.setAdapter(new GroupAdapter(getActivity(), groupList));
		groupView.setOnItemClickListener(new GroupListener());
	}

	// Ⱥ��˵����
	private class GroupListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			int groupID = groupList.get(position).getId();
			String groupName = groupList.get(position).getName();
			SortCursor data = (SortCursor) contactsAdapter.getCursor();
			topbar_title_tv.setText(groupName);
			filterList.clear();
			if (groupName == "ȫ��") {
				contactsAdapter.notifyDataSetChanged();
				contactslist.setAdapter(contactsAdapter);
			} else if (groupName == "δ����") {
				filterList = data.filterGroup(0);
				fAdapter.notifyDataSetChanged();
				
				fAdapter = new FilterAdapter(getActivity(), filterList);
				contactslist.setAdapter(fAdapter);
			} else {
				filterList = data.filterGroup(groupID);
				fAdapter = new FilterAdapter(getActivity(), filterList);
				fAdapter.notifyDataSetChanged();
				contactslist.setAdapter(fAdapter);
			}
			// ���ز˵���
			if (popupwindow != null && popupwindow.isShowing()) {
				popupwindow.dismiss();
				popupwindow = null;
			}
		}
	}

	String[] items = { "����", "ɾ����ϵ��" };

	// ������ʾ�ĶԻ���
	private void showLongClickDialog() {
		new AlertDialog.Builder(getActivity())
				.setTitle(chooseContactName)
				.setItems(items, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0:
							Intent intent = new Intent(Intent.ACTION_CALL, Uri
									.parse("tel://" + chooseContactNumber));
							startActivity(intent);
							break;
						case 1:
							new AlertDialog.Builder(getActivity())
									.setTitle("")
									.setIcon(R.drawable.top_bar_bg)
									.setMessage(
											"ɾ����ϵ��" + chooseContactName + "?")
									.setPositiveButton(
											"ȷ��",
											new DialogInterface.OnClickListener() {

												@Override
												public void onClick(
														DialogInterface dialog,
														int which) {
													// ɾ����ϵ�˲���,�����߳��д���
													new DeleteContactTask()
															.execute();
												}
											})
									.setNegativeButton(
											"ȡ��",
											new DialogInterface.OnClickListener() {
												@Override
												public void onClick(
														DialogInterface dialog,
														int which) {
												}
											}).show();
							break;
						}
					}
				})
				.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).show();
		// Window window = longDialog.getWindow();
		// // �Զ���alertdialog����ʽ
		// LayoutInflater inflater = getActivity().getLayoutInflater();
		// View view = inflater.inflate(R.layout.show_longclick_contact_dialog,
		// null);
		// window.setContentView(view);
		// LayoutParams lp = window.getAttributes();
		// lp.y = -50;
		// lp.width = 300;
		// lp.height = 230;
		// window.setAttributes(lp);
		// longDialog.setCanceledOnTouchOutside(true);
		//
		// // ��ȡ�����Ի�������id
		// TextView dialog_title_tv = (TextView) view
		// .findViewById(R.id.dialog_title_tv);
		// dialog_title_tv.setText(chooseContactName);
		//
		// TextView dial_tv = (TextView) view.findViewById(R.id.dial_tv);
		// TextView delete_contact_tv = (TextView) view
		// .findViewById(R.id.delete_contact_tv);
		//
		// btn_dial = (Button) view.findViewById(R.id.imagebtn_dial);
		// btn_delete_contact = (Button) view
		// .findViewById(R.id.imagebtn_delete_contact);
		// // ���õ��������
		// dial_tv.setOnClickListener(new LongClickListener());
		// delete_contact_tv.setOnClickListener(new LongClickListener());
		// btn_dial.setOnClickListener(new LongClickListener());
		// btn_delete_contact.setOnClickListener(new LongClickListener());
	}

	// // ������ϵ�˵��¼�����
	// private class LongClickListener implements OnClickListener {
	//
	// @Override
	// public void onClick(View v) {
	// switch (v.getId()) {
	// case R.id.dial_tv:
	// case R.id.imagebtn_dial:
	// longDialog.dismiss();
	// Intent intent = new Intent(Intent.ACTION_CALL,
	// Uri.parse("tel://" + chooseContactNumber));
	// startActivity(intent);
	// break;
	// case R.id.delete_contact_tv:
	// case R.id.imagebtn_delete_contact:
	// longDialog.dismiss();
	// new AlertDialog.Builder(getActivity())
	// .setTitle("")
	// .setIcon(R.drawable.top_bar_bg)
	// .setMessage("ɾ����ϵ��" + chooseContactName + "?")
	// .setPositiveButton("ȷ��",
	// new DialogInterface.OnClickListener() {
	//
	// @Override
	// public void onClick(DialogInterface dialog,
	// int which) {
	// // ɾ����ϵ�˲���,�����߳��д���
	// new DeleteContactTask().execute();
	// }
	// })
	// .setNegativeButton("ȡ��",
	// new DialogInterface.OnClickListener() {
	// @Override
	// public void onClick(DialogInterface dialog,
	// int which) {
	// }
	// }).show();
	// break;
	// }
	// }
	//
	// }

	// ɾ����ϵ��
	private class DeleteContactTask extends AsyncTask<Void, Integer, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			new ContactDAO(getActivity()).deleteContact(Integer
					.parseInt(chooseContactID));
			// Utils.deleteContact(chooseContactID);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (dialogLoading != null) {
				contactsAdapter.notifyDataSetChanged();
				dialogLoading.dismiss();
			}
		}

		@Override
		protected void onPreExecute() {
			dialogLoading = new ProgressDialog(getActivity());
			dialogLoading.setProgressStyle(ProgressDialog.STYLE_SPINNER);// ���÷��ΪԲ�ν�����
			dialogLoading.setMessage("����ɾ��");
			dialogLoading.setCancelable(false);
			dialogLoading.show();
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			Log.i("huahua", "onProgressUpdate");
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		ViewGroup p = (ViewGroup) contactsView.getParent();
		if (p != null) {
			p.removeAllViewsInLayout();
		}
		return contactsView;
	}

	// �������ļ�����
	private class ContactsLoaderListener implements
			LoaderManager.LoaderCallbacks<Cursor> {

		@Override
		public Loader<Cursor> onCreateLoader(int id, Bundle args) {
			System.out.println("onCreateLoader...");
			return new SortCursorLoader(getActivity(),
					ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
					null, null, null);
		}

		@Override
		public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
			contactsAdapter.swapCursor(arg1);
			System.out.println("onLoadFinished...");
			process_init_data.setVisibility(View.GONE);
			tv_loading.setVisibility(View.GONE);
			contactsCursor = (SortCursor) contactsAdapter.getCursor();
			if (acbuwa_topbar.getVisibility() == View.VISIBLE) {
				// Utils.mPersons = data.GetContactsArray();
			} else {
				filterList = contactsCursor.filterSearch(filterEditText
						.getText().toString().trim());
				// fAdapter.notifyDataSetChanged();
				fAdapter = new FilterAdapter(getActivity(), filterList);
				fAdapter.notifyDataSetChanged();
				contactslist.setAdapter(fAdapter);
			}
		}

		@Override
		public void onLoaderReset(Loader<Cursor> arg0) {
			contactsAdapter.swapCursor(null);

		}

	}

	// ��ĸ�д����ļ�����
	private class ScrollBarListener implements
			AlphabetScrollBar.OnTouchBarListener {

		@Override
		public void onTouch(String letter) {

			// ������ĸ��ʱ,����ϵ���б���µ�����ĸ���ֵ�λ��
			SortCursor contactsCursor = (SortCursor) contactsAdapter
					.getCursor();
			if (contactsCursor != null) {
				int idx = contactsCursor.binarySearch(letter);
				if (idx != -1) {
					contactslist.setSelection(idx);
				}
			}
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		menu.add(0, 1, 1, "����ɾ��");
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == 1) {
			Intent intent = new Intent(getActivity(),
					AddRemoveContactGroup.class);
			intent.putExtra("type", "bathdelete");
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}
}
