package com.night.contact.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Ⱥ��ʵ��
 * 
 * @author NightHary
 *
 */
public class GroupBean implements Parcelable{

	private int id;
	private String name;
	private int count;
	
	public GroupBean() {
		super();
	}
	public GroupBean(int id, String name) {
		super();
		this.id = id;
		this.name = name;
	}
	public GroupBean(int id, String name, int count) {
		super();
		this.id = id;
		this.name = name;
		this.count = count;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	/**
	 * ���л�
	 */
	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(getId());
		dest.writeString(getName());
		dest.writeInt(getCount());
	}
	//��дCreator
	 public static final Parcelable.Creator<GroupBean> CREATOR = new Creator<GroupBean>(){

		@Override
		public GroupBean createFromParcel(Parcel source) {
			GroupBean group = new GroupBean();
			group.id = source.readInt();
			group.name = source.readString();
			group.count = source.readInt();
			return group;
		}

		@Override
		public GroupBean[] newArray(int size) {
			return new GroupBean[size];
		}
		 
	 };
}
