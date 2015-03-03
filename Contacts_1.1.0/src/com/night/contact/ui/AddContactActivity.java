package com.night.contact.ui;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.night.contact.DAO.ContactDAO;
import com.night.contact.DAO.GroupDAO;
import com.night.contact.bean.GroupBean;
import com.night.contact.bean.SortEntry;
import com.night.contact.util.ImageConvert;
import com.night.contact.util.Parameter;
import com.night.contact.util.Tools;

public class AddContactActivity extends Activity {

	private Button btn_contact_back;
	private TextView btn_contact_title;
	private Button btn_save_contact;

	public EditText et_name;
	private ImageButton btn_img;
	private Bitmap contactPhotos;
	public String photoUri;
	public EditText et_phone;
	public EditText et_email;
	public EditText et_address;
	private Spinner sp_group;

	private ProgressDialog m_dialogLoading;

	private List<GroupBean> groupList;
	private String groupName;
	private ContactDAO cat;

	// ��Ӱ�ť
	private ImageButton add_phone_row_btn;

	public EditText et_phone_2;
	private ImageButton add_phone_row_btn_2;

	public EditText et_phone_3;
	private ImageButton add_phone_row_btn_3;

	/* ͷ������ */
	private static final String IMAGE_FILE_NAME = "faceImage.jpg";

	/* ������ */
	private static final int IMAGE_REQUEST_CODE = 0;
	private static final int CAMERA_REQUEST_CODE = 1;
	private static final int RESULT_REQUEST_CODE = 2;
	// ���հ�ť
//	private ImageButton imagebtn_camera;
	// ��ͼ��ѡ����Ƭ��ť
//	private ImageButton imagebtn_glary;

	private SortEntry entry;
	private int flag = 0;
	private int loadlag = 0;
	private int groupFlag = 0;// ��ϵ���Ƿ��Ѵ��ڷ��� 0--none 1--have
	private int photoFlag = 0;// ��ϵ���Ƿ��Ѵ���ͷ��

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.contact_addnew);
		findId();
		String type = getIntent().getStringExtra("type");
		if (type != null && type.equals(Parameter.CONTACT_EDIT_TYPE)) {
			entry = getIntent().getParcelableExtra(Parameter.CONTACT_EDIT_KEY);
			groupName = entry.groupName;
			flag = 1;
			if (entry.groupId != 0) {
				groupFlag = 1;
			}
			if (entry.photoId != 0) {
				photoFlag = 1;
			}
			btn_contact_title.setText(Parameter.EDIT_CONTACT_TITLE);

			et_name.setText(entry.mName);
			String[] numbers = Tools.getPhoneNumber(entry.mNum);
			if (numbers.length == 1) {
				et_phone.setText(numbers[0]);
			} else if (numbers.length == 2) {// 2������
				et_phone.setText(numbers[0]);
				et_phone_2 = (EditText) this.findViewById(R.id.et_phone_2);
				View v = (View) et_phone_2.getParent();
				v.setVisibility(View.VISIBLE);
				et_phone_2.setText(numbers[1]);
			} else if (numbers.length == 3) {// 3������
				et_phone.setText(numbers[0]);
				et_phone_2 = (EditText) this.findViewById(R.id.et_phone_2);
				View v = (View) et_phone_2.getParent();
				v.setVisibility(View.VISIBLE);
				et_phone_2.setText(numbers[1]);

				et_phone_3 = (EditText) this.findViewById(R.id.et_phone_3);
				View v1 = (View) et_phone_3.getParent();
				v1.setVisibility(View.VISIBLE);
				et_phone_3.setText(numbers[2]);
			}

			// et_address.setText(entry.m);
			// et_email.setText(contact.getEmail());

			if (0 == entry.photoId) {
				btn_img.setImageResource(R.drawable.default_contact_photo);
			} else {
				Uri uri = ContentUris.withAppendedId(
						ContactsContract.Contacts.CONTENT_URI,
						Integer.parseInt(entry.mID));
				InputStream input = ContactsContract.Contacts
						.openContactPhotoInputStream(this.getContentResolver(),
								uri);
				Bitmap contactPhoto = BitmapFactory.decodeStream(input);
				contactPhotos = contactPhoto;
				btn_img.setImageBitmap(contactPhoto);
			}
		}
	}

	public void findId() {
		btn_contact_back = (Button) this.findViewById(R.id.btn_contact_back);
		btn_contact_title = (TextView) this
				.findViewById(R.id.btn_contact_title);
		btn_save_contact = (Button) this.findViewById(R.id.btn_save_contact);

		et_name = (EditText) this.findViewById(R.id.et_name);
		btn_img = (ImageButton) this.findViewById(R.id.btn_img);
		et_phone = (EditText) this.findViewById(R.id.et_phone);
		et_email = (EditText) this.findViewById(R.id.et_email);
		et_address = (EditText) this.findViewById(R.id.et_address);
		sp_group = (Spinner) this.findViewById(R.id.sp_group);

		add_phone_row_btn = (ImageButton) this.findViewById(R.id.add_phone_row);
	}

	int count = 0;

	@Override
	protected void onResume() {
		super.onResume();

		new Thread(runnable).start();

		// ��ӵ绰��
		add_phone_row_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				count++;
				if (count == 1) {// ��һ�ε��ʱ
					add_phone_row_btn_2 = (ImageButton) AddContactActivity.this
							.findViewById(R.id.add_phone_row_2);
					et_phone_2 = (EditText) AddContactActivity.this
							.findViewById(R.id.et_phone_2);
					View v1 = (View) et_phone_2.getParent();
					v1.setVisibility(View.VISIBLE);
					// ���ɾ���¼�����
					add_phone_row_btn_2
							.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									View v1 = (View) add_phone_row_btn_2
											.getParent();
									v1.setVisibility(View.GONE);
									count--;
								}
							});
				}
				if (count == 2) {// ���ڶ��ε�������ʱ
					add_phone_row_btn_3 = (ImageButton) AddContactActivity.this
							.findViewById(R.id.add_phone_row_3);
					et_phone_3 = (EditText) AddContactActivity.this
							.findViewById(R.id.et_phone_3);
					View v2 = (View) et_phone_3.getParent();
					v2.setVisibility(View.VISIBLE);
					// ���ɾ���¼�����
					add_phone_row_btn_3
							.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									View v1 = (View) add_phone_row_btn_3
											.getParent();
									v1.setVisibility(View.GONE);
									count--;
								}
							});
				}
			}
		});

		// ͷ��ѡ��
		btn_img.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				showChooseDialog();
			}
		});

		// ����
		btn_save_contact.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				if (loadlag != 1) {
					Toast.makeText(getApplicationContext(), "Ⱥ�鲻��Ϊ��",
							Toast.LENGTH_SHORT).show();
					return;
				}
				String groupName = sp_group.getSelectedItem().toString();
				if (TextUtils.isEmpty(groupName)) {// ��Ⱥ��û�м��س���ʱ�û�����޸ĵ�������ʾ
					Toast.makeText(getApplicationContext(), "��������Ϊ��",
							Toast.LENGTH_SHORT).show();
					return;
				}
				int groupId = new GroupDAO(AddContactActivity.this)
						.getIdByGroupName(groupName);
				SortEntry contact = new SortEntry();
				// ��ȡ����
				String name = et_name.getText().toString();
				// String address = et_address.getText().toString();
				// String email = et_email.getText().toString();
				if (TextUtils.isEmpty(name)) {
					Toast.makeText(AddContactActivity.this, "��������Ϊ��",
							Toast.LENGTH_SHORT).show();
					return;
				}

				contact.mName = name;
				// ��ȡ�ֻ�����
				String mobilePhone = et_phone.getText().toString();
				// ���ֻ�����ĸ��������жϣ�
				// ��������һ�У��ͻ�ȡһ��
				// �����������У��ͻ�ȡ����
				// ����ֻ���������
				if (et_phone_2 != null) {
					String mobilePhone_2 = et_phone_2.getText().toString()
							.trim();
					mobilePhone += "#" + mobilePhone_2;
				} else if (et_phone_3 != null) {
					String mobilePhone_2 = et_phone_2.getText().toString()
							.trim();
					String mobilePhone_3 = et_phone_3.getText().toString()
							.trim();
					mobilePhone += "#" + mobilePhone_2 + "#" + mobilePhone_3;
				}
				contact.mNum = mobilePhone;
				if (contactPhotos != null) {
					contact.contactPhoto = contactPhotos;
				}

				if (groupName == "��") {
					groupId = 0;
				}
				contact.groupId = groupId;
				if (flag == 0) {
					new AddContactTask(contact, groupId).execute();
				} else {
					contact.mID = entry.mID;
					new UpdateContactTask(contact, entry.groupId).execute();
				}
			}
		});

		// ����
		btn_contact_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	// ���Զ������,����Ⱥ����Ϣ
	private Runnable runnable = new Runnable() {
		@Override
		public void run() {
			GroupDAO gp = new GroupDAO(AddContactActivity.this);
			groupList = gp.getGroups();
			handler.sendEmptyMessage(0);
		}
	};
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				ArrayAdapter<String> adapter3 = setAdapterGroup(groupList);// ������Ϣ֪ͨListView����
				sp_group.setAdapter(adapter3); // ��������ListView������������
				if (flag == 1) {
					Tools.setSpinnerItemSelectedByValue(sp_group, groupName);
				}
				loadlag = 1;// Ⱥ����Ϣ����״̬ 1����������
				break;
			default:
				break;
			}
		}
	};

	public ArrayAdapter<String> setAdapterGroup(List<GroupBean> list) {
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		adapter.add("��");
		for (GroupBean bean : list) {
			adapter.add(bean.getName());
		}
		return adapter;
	}

	private AlertDialog dialog;

	/**
	 * ��ʾѡ��Ի��� �Զ���Ի�����
	 */
	String[] items = {"����","��ͼ��ѡ��"};
	@SuppressLint("InflateParams")
	private void showChooseDialog() {

		dialog = new AlertDialog.Builder(this)
				.setTitle("����ͷ��")
				.setItems(items, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 1:
							Intent intentFromGallery = new Intent();
							intentFromGallery.setType("image/*"); // �����ļ�����
							intentFromGallery
									.setAction(Intent.ACTION_GET_CONTENT);
							startActivityForResult(intentFromGallery,
									IMAGE_REQUEST_CODE);
							break;
						case 0:
							Intent intentFromCapture = new Intent(
									MediaStore.ACTION_IMAGE_CAPTURE);
							// �жϴ洢���Ƿ�����ã����ý��д洢
							if (Tools.hasSdcard()) {
								intentFromCapture.putExtra(
										MediaStore.EXTRA_OUTPUT,
										Uri.fromFile(new File(Environment
												.getExternalStorageDirectory(),
												IMAGE_FILE_NAME)));
							}

							startActivityForResult(intentFromCapture,
									CAMERA_REQUEST_CODE);
							break;
						}
					}
				})
				.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).create();
		dialog.show();
		// Window window = dialog.getWindow();
		// // �Զ���alertdialog����ʽ
		// LayoutInflater inflater = this.getLayoutInflater();
		// View view = inflater.inflate(R.layout.show_choose_photo_dialog,
		// null);
		// window.setContentView(view);
		// // �Զ���alertdialog��λ��
		// LayoutParams lp = window.getAttributes();
		// lp.y = -50;
		// lp.width = 300;
		// lp.height = 230;
		// window.setAttributes(lp);
		// dialog.setCanceledOnTouchOutside(true);
		// // ���ð�ť����¼�
		// TextView take_photo_tv = (TextView)
		// view.findViewById(R.id.take_photo);
		// TextView choose_glary_tv = (TextView) view
		// .findViewById(R.id.choose_glary);
		//
		// imagebtn_camera = (ImageButton)
		// view.findViewById(R.id.imagebtn_camera);
		// imagebtn_glary = (ImageButton)
		// view.findViewById(R.id.imagebtn_glary);
		//
		// // ���ð�ť�Լ����ֵĵ���¼����ж�������ѡ��ͷ���Ǵ�ͼ��ѡ��
		// imagebtn_camera.setOnClickListener(new PhotoListener());
		// take_photo_tv.setOnClickListener(new PhotoListener());
		// imagebtn_glary.setOnClickListener(new PhotoListener());
		// choose_glary_tv.setOnClickListener(new PhotoListener());
	}

	// ѡ��ͷ�����¼�
//	private class PhotoListener implements OnClickListener {
//
//		@Override
//		public void onClick(View v) {
//			switch (v.getId()) {
//			case R.id.take_photo:
//			case R.id.imagebtn_camera:
//				dialog.dismiss();
//				Intent intentFromCapture = new Intent(
//						MediaStore.ACTION_IMAGE_CAPTURE);
//				// �жϴ洢���Ƿ�����ã����ý��д洢
//				if (Tools.hasSdcard()) {
//					intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT, Uri
//							.fromFile(new File(Environment
//									.getExternalStorageDirectory(),
//									IMAGE_FILE_NAME)));
//				}
//				startActivityForResult(intentFromCapture, CAMERA_REQUEST_CODE);
//				break;
//			case R.id.choose_glary:
//			case R.id.imagebtn_glary:
//				dialog.dismiss();
//				Intent intentFromGallery = new Intent();
//				intentFromGallery.setType("image/*"); // �����ļ�����
//				intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
//				startActivityForResult(intentFromGallery, IMAGE_REQUEST_CODE);
//				break;
//			}
//		}
//	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// ����벻����ȡ��ʱ��
		if (resultCode != RESULT_CANCELED) {

			switch (requestCode) {
			case IMAGE_REQUEST_CODE:// ͼ��
				startPhotoZoom(data.getData());
				break;
			case CAMERA_REQUEST_CODE:// ���
				if (Tools.hasSdcard()) {
					File tempFile = new File(
							Environment.getExternalStorageDirectory(),
							IMAGE_FILE_NAME);
					startPhotoZoom(Uri.fromFile(tempFile));
				} else {
					Toast.makeText(AddContactActivity.this, "δ�ҵ��洢�����޷��洢��Ƭ��",
							Toast.LENGTH_LONG).show();
				}

				break;
			case RESULT_REQUEST_CODE:// ����ֵ
				if (data != null) {
					contactPhotos = new ImageConvert().getImageToView(
							AddContactActivity.this, data, btn_img);
				}
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * �ü�ͼƬ����ʵ��
	 * 
	 * @param uri
	 */
	public void startPhotoZoom(Uri uri) {

		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// ���òü�
		intent.putExtra("crop", "true");
		// aspectX aspectY �ǿ�ߵı���
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY �ǲü�ͼƬ���
		intent.putExtra("outputX", 320);
		intent.putExtra("outputY", 320);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, 2);
	}

	// ������ϵ��
	private class AddContactTask extends AsyncTask<Void, Integer, Void> {

		private SortEntry be;
		private int groupId;

		public AddContactTask(SortEntry be, int groupId) {
			this.be = be;
			this.groupId = groupId;
		}

		@Override
		protected Void doInBackground(Void... params) {
			if (!TextUtils.isEmpty(be.mName)) {
				System.out.println(be.mNum);
				cat = new ContactDAO(getApplicationContext());
				cat.addContact1(be, groupId);
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (m_dialogLoading != null) {
				finish();
				// contactFlag = 2;//��ϵ�˷����˱仯
				m_dialogLoading.dismiss();
			}
		}

		@Override
		protected void onPreExecute() {
			m_dialogLoading = new ProgressDialog(AddContactActivity.this);
			m_dialogLoading.setProgressStyle(ProgressDialog.STYLE_SPINNER);// ���÷��ΪԲ�ν�����
			m_dialogLoading.setMessage("���ڱ���...");
			m_dialogLoading.setCancelable(false);
			m_dialogLoading.show();
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
		}
	}

	private class UpdateContactTask extends AsyncTask<Void, Integer, Void> {

		private SortEntry be;
		private int old_groupID;

		public UpdateContactTask(SortEntry be, int old_groupID) {
			this.be = be;
			this.old_groupID = old_groupID;
		}

		@Override
		protected Void doInBackground(Void... params) {
			ContactDAO contactDAO = new ContactDAO(AddContactActivity.this);
			int raw_contact_id = Integer.parseInt(be.mID);
			System.out.println(be.mID + "  " + be.mName + " " + be.mNum + " "
					+ be.contactPhoto);
			if (groupFlag == 1 && photoFlag == 1) {// ��ϵ���Ѿ�����ͷ���ѷ���-
				System.out.println("1");
				contactDAO.updataCotact(raw_contact_id, be, old_groupID);
			} else if (groupFlag == 0 && photoFlag == 1) {// ��ϵ��δ���顢ͷ�����-
				System.out.println("2");
				contactDAO.updataCotactNoGroup(raw_contact_id, be);
			} else if (groupFlag == 1 && photoFlag == 0) {// ��ϵ���ѷ��顢ͷ�񲻴���
				System.out.println("3");
				contactDAO.updataCotactNoPhoto(raw_contact_id, be, old_groupID);
			} else {// ��ϵ��ͷ�񲻴��ڡ�δ����-
				System.out.println("4");
				contactDAO.updataCotactNoG_Photo(raw_contact_id, be);
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (m_dialogLoading != null) {
				finish();
				Intent intent = new Intent();
				intent.setClass(AddContactActivity.this,
						ContactDetialActivity.class);
				Bundle bundle = new Bundle();
				bundle.putParcelable(Parameter.CONTACT_DETIAL_KEY, be);
				intent.putExtra("type", Parameter.CONTACT_DETIAL_TYPE);
				intent.putExtras(bundle);
				startActivity(intent);
				m_dialogLoading.dismiss();
			}
		}

		@Override
		protected void onPreExecute() {
			m_dialogLoading = new ProgressDialog(AddContactActivity.this);
			m_dialogLoading.setProgressStyle(ProgressDialog.STYLE_SPINNER);// ���÷��ΪԲ�ν�����
			m_dialogLoading.setMessage("���ڱ���...");
			m_dialogLoading.setCancelable(false);
			m_dialogLoading.show();
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
		}
	}

}
