package com.night.contact.ui;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.night.contact.DAO.GroupDAO;
import com.night.contact.adapter.GroupListAdapter;
import com.night.contact.bean.GroupBean;
import com.night.contact.util.Parameter;

/**
 * Ⱥ��������
 * 
 * @author NightHary
 * 
 */
@SuppressLint("InflateParams")
public class GroupManageActivity extends Activity {

	private ListView grouplist_name;
	private ArrayList<GroupBean> groups;
	private String[] groupClickItem = { "��ӳ�Ա", "�Ƴ���Ա", "�޸�����", "ɾ��", "ȡ��" };
	private Button btn_group_back;
	private Button btn_addgroup;
	private GroupListAdapter adapter;
	private GroupDAO groupDAO;
	private ProgressDialog m_dialogLoading;
	// ����Ⱥ��Ի���
	private static int groupFlag = 0;
	private LayoutInflater inflater;
	private AlertDialog.Builder builder;
	private AlertDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.group_manage_page);

		grouplist_name = (ListView) this.findViewById(R.id.grouplist_name);
		groupDAO = new GroupDAO(this);
		inflater = LayoutInflater.from(this);
		// groups = groupDAO.getGroups();

		btn_group_back = (Button) this.findViewById(R.id.btn_group_back);
		btn_addgroup = (Button) this.findViewById(R.id.btn_addgroup);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onResume() {
		super.onResume();
		Intent intent = getIntent();
		groups = (ArrayList<GroupBean>) intent.getExtras().getSerializable(
				Parameter.GROUP_LIST);
		groups.remove(0);
		groups.remove(groups.size() - 1);
		adapter = new GroupListAdapter(groups, this);
		grouplist_name.setAdapter(adapter);
	}

	@Override
	protected void onStart() {
		super.onStart();
		// Ⱥ�����¼�
		grouplist_name.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				GroupBean group = groups.get(position);
				showOperateDialog(group);
			}
		});
		// ���ذ�ť����¼�
		btn_group_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Intent intent = new Intent();
				// intent.setClass(GroupManageActivity.this,
				// MainActivity.class);
				// startActivity(intent);
				groupFlag = 1;
				finish();
			}
		});
		// ���Ⱥ��
		btn_addgroup.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showMyDialog(5, null);
			}
		});
	}

	// "��ӳ�Ա","�Ƴ���Ա","�޸�����","ɾ��","ȡ��"
	public void showOperateDialog(final GroupBean group) {
		Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(group.getName())
				.setItems(groupClickItem,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								if (which == 0) {
									Intent intent = new Intent();
									intent.setClass(GroupManageActivity.this,AddRemoveContactGroup.class);
									intent.putExtra("type","add");
									intent.putExtra("groupID",group.getId());
									startActivity(intent);
								} else if (which == 1) {
									Intent intent = new Intent();
									intent.setClass(GroupManageActivity.this,AddRemoveContactGroup.class);
									intent.putExtra("type","remove");
									intent.putExtra("groupID",group.getId());
									startActivity(intent);
								} else if (which == 2) {
									showMyDialog(2, group);
								} else if (which == 3) {
									showMyDialog(3, group);
								} else if (which == 4) {

								}
							}
						}).show();
	}


	/**
	 * ��ʾһ���Ի���
	 * 
	 * @param viewId
	 *            ����ĵ����
	 *            0--��ӳ�Ա
	 *            1--�Ƴ���Ա
	 *            2--�޸�Ⱥ������
	 *            3--ɾ��Ⱥ��
	 *            4--ȡ��
	 *            5--�½�Ⱥ��
	 * @author NightHary
	 */
	@SuppressLint("DefaultLocale")
	public void showMyDialog(int operateId, final GroupBean group) {

		View dialog_view = inflater.inflate(R.layout.dialog_add_group, null);
		final EditText edtNewGroupName = (EditText) dialog_view
				.findViewById(R.id.edtNewGroupName);
		TextView txtDialogTitleName = (TextView) dialog_view
				.findViewById(R.id.txtNewGroupTitle);
		TextView txtMsgAlert = (TextView) dialog_view
				.findViewById(R.id.txtMsgAlert);
		Button btnSure = (Button) dialog_view.findViewById(R.id.btnAddNewGroup);
		Button btnCancel = (Button) dialog_view
				.findViewById(R.id.btnGroupCancel);

		switch (operateId) {

		case 2:// ����������
			txtDialogTitleName.setText("�༭����");
			txtMsgAlert.setVisibility(View.GONE);
			edtNewGroupName.setVisibility(View.VISIBLE);
			edtNewGroupName.setHint("�������µķ������ƣ�");
			edtNewGroupName.setText(group.getName());
			builder = new AlertDialog.Builder(this);
			dialog = builder.setView(dialog_view).create();
			dialog.show();
			btnSure.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					new UpdateGroupTask(edtNewGroupName.getText().toString()
							.trim(), group.getId()).execute();
					dialog.dismiss();
				}
			});
			btnCancel.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
			break;
		case 3:// ɾ��һ������ʱ��ʾ�ĶԻ���
			txtDialogTitleName.setText("��ܰ��ʾ");
			txtMsgAlert.setVisibility(View.VISIBLE);
			txtMsgAlert.setText("ȷ�Ͻ�ɢ����'" + group.getName() + "'?");
			edtNewGroupName.setVisibility(View.GONE);
			builder = new AlertDialog.Builder(this);
			dialog = builder.setView(dialog_view).create();
			dialog.show();
			btnSure.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					new DeleteGroupTask(group.getId()).execute();
					dialog.dismiss();
				}
			});
			btnCancel.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
			break;
		case 4:
			break;
		case 5:// ���һ������ʱ��ʾ�ĶԻ���
			txtDialogTitleName.setText("�½�����");
			txtMsgAlert.setVisibility(View.GONE);
			edtNewGroupName.setVisibility(View.VISIBLE);
			edtNewGroupName.setHint("������������ƣ�");
			builder = new AlertDialog.Builder(this);
			dialog = builder.setView(dialog_view).create();
			dialog.show();
			btnSure.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					new AddGroupTask(edtNewGroupName.getText().toString()
							.trim()).execute();
					dialog.dismiss();
				}
			});
			btnCancel.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					dialog.dismiss();
				}
			});
			break;

		}
	}

	/**
	 * ����Ⱥ��
	 * 
	 * @author NightHary
	 * 
	 */
	private class AddGroupTask extends AsyncTask<Void, Integer, Void> {
		String groupName;

		public AddGroupTask(String groupName) {
			this.groupName = groupName;
		}

		@Override
		protected Void doInBackground(Void... params) {
			groupDAO.addGroup(groupName);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (m_dialogLoading != null) {
				updateGroups();
				m_dialogLoading.dismiss();
				Toast.makeText(GroupManageActivity.this, "��ӳɹ�",
						Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		protected void onPreExecute() {
			m_dialogLoading = new ProgressDialog(GroupManageActivity.this);
			m_dialogLoading.setProgressStyle(ProgressDialog.STYLE_SPINNER);// ���÷��ΪԲ�ν�����
			m_dialogLoading.setMessage("���ڱ���Ⱥ��...");
			m_dialogLoading.setCancelable(false);
			m_dialogLoading.show();
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
		}

	}

	/**
	 * ɾ��Ⱥ��
	 * 
	 * @author NightHary
	 * 
	 */
	private class DeleteGroupTask extends AsyncTask<Void, Integer, Void> {
		int groupId;

		public DeleteGroupTask(int groupId) {
			this.groupId = groupId;
		}

		@Override
		protected Void doInBackground(Void... params) {
			groupDAO.deleteGroup(groupId);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (m_dialogLoading != null) {
				updateGroups();
				m_dialogLoading.dismiss();
				Toast.makeText(GroupManageActivity.this, "ɾ���ɹ�",
						Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		protected void onPreExecute() {
			m_dialogLoading = new ProgressDialog(GroupManageActivity.this);
			m_dialogLoading.setProgressStyle(ProgressDialog.STYLE_SPINNER);// ���÷��ΪԲ�ν�����
			m_dialogLoading.setMessage("����ɾ��...");
			m_dialogLoading.setCancelable(false);
			m_dialogLoading.show();
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
		}
	}

	private class UpdateGroupTask extends AsyncTask<Void, Integer, Void> {
		String groupName;
		int groupId;

		public UpdateGroupTask(String groupName, int groupId) {
			this.groupName = groupName;
			this.groupId = groupId;
		}

		@Override
		protected Void doInBackground(Void... params) {
			groupDAO.updataGroup(groupId, groupName);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (m_dialogLoading != null) {
				updateGroups();
				m_dialogLoading.dismiss();
				Toast.makeText(GroupManageActivity.this, "�޸ĳɹ�",
						Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		protected void onPreExecute() {
			m_dialogLoading = new ProgressDialog(GroupManageActivity.this);
			m_dialogLoading.setProgressStyle(ProgressDialog.STYLE_SPINNER);// ���÷��ΪԲ�ν�����
			m_dialogLoading.setMessage("�����޸�Ⱥ��...");
			m_dialogLoading.setCancelable(false);
			m_dialogLoading.show();
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
		}
	}

	/**
	 * ����Ⱥ����Ϣ
	 * 
	 * @param handler
	 *            ʹ��Handler�������߳���Activity֮���ͨ�����⡣ ������Ҫ����ui��������Ҫʹ��handler
	 * @param runnable
	 *            ʹ��runnable������ʹ����߳��ܹ���μ���
	 */
	@SuppressLint("HandlerLeak")
	private void updateGroups() {
		groups = groupDAO.getGroups();
		adapter.refresh(groups);
	}

	public static int getGroupFlag() {
		return groupFlag;
	}

	public static void setGroupFlag(int groupFlag) {
		GroupManageActivity.groupFlag = groupFlag;
	}
}
