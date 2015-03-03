package com.night.contact.bean;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
/**
 * ��ϵ��ʵ��
 * 
 * @author NightHary
 *
 */
public class SortEntry implements Parcelable{
	public String mID; // �����ݿ��е�ID��
	public String mName; // ����
	public String mPY; // ����ƴ��
	public String mNum; // �绰����
	public String mFisrtSpell; // ����������ĸ ��:��ѩ��:zxb
	public int mchoose; // �Ƿ�ѡ�� 0--δѡ�� 1---ѡ��
	public int mOrder; // ��ԭCursor�е�λ��
	public String lookUpKey;
	public int photoId;
	public int groupId;
	public String groupName;
	public Bitmap contactPhoto;// ��Ƭ
	public String formattedNumber;
	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(mID);
		dest.writeString(mName);
		dest.writeString(mPY);
		dest.writeString(mNum);
		dest.writeString(mFisrtSpell);
		dest.writeInt(mchoose);
		dest.writeInt(mOrder);
		dest.writeString(lookUpKey);
		dest.writeInt(photoId);
		dest.writeInt(groupId);
		dest.writeString(groupName);
		dest.writeString(formattedNumber);
	}
	

	// ��дCreator
	public static final Parcelable.Creator<SortEntry> CREATOR = new Creator<SortEntry>() {

		@Override
		public SortEntry createFromParcel(Parcel source) {
			SortEntry contact = new SortEntry();
			contact.mID = source.readString();
			contact.mName = source.readString();
			contact.mPY = source.readString();
			contact.mNum = source.readString();
			contact.mFisrtSpell = source.readString();
			contact.mchoose = source.readInt();
			contact.mOrder = source.readInt();
			contact.lookUpKey = source.readString();
			contact.photoId = source.readInt();
			contact.groupId = source.readInt();
			contact.groupName = source.readString();
			contact.formattedNumber = source.readString();
			return contact;
		}

		@Override
		public SortEntry[] newArray(int size) {
			return new SortEntry[size];
		}

	};
}
