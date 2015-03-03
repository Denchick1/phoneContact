package com.night.contact.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.content.Context;
import android.os.Environment;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.night.contact.bean.SortEntry;

/**
 * ���SD���Ƿ����
 * 
 * @author NightHary
 * 
 */
public class Tools {
	/**
	 * ����Ƿ����SDCard
	 * 
	 * @return
	 */
	public static boolean hasSdcard() {
		String state = Environment.getExternalStorageState();
		if (state.equals(Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * ����ֵ, ����spinnerĬ��ѡ��:
	 * 
	 * @param spinner
	 * @param value
	 */
	public static void setSpinnerItemSelectedByValue(Spinner spinner,
			String value) {
		SpinnerAdapter apsAdapter = spinner.getAdapter(); // �õ�SpinnerAdapter����
		int k = apsAdapter.getCount();
		for (int i = 0; i < k; i++) {
			if (value.equals(apsAdapter.getItem(i).toString())) {
				spinner.setSelection(i, true);// Ĭ��ѡ����
				break;
			}
		}
	}

	/**
	 * ��ֵ绰����ȥ��#�Ų����ж��м�������
	 * 
	 * 
	 */
	public static String[] getPhoneNumber(String phoneNumber) {
		if (phoneNumber == null) {
			throw new NullPointerException("�绰����Ϊ��");
		}
		String[] numbers;
		numbers = phoneNumber.split("#");
		return numbers;
	}

	/**
	 * ��ӡ��Ϣ
	 * 
	 * @param context
	 *            ������
	 * @param message
	 *            ��ӡ����Ϣ����
	 */
	public static void Toast(Context context, String message) {
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}

	public static String pinYinToHanZi(String pinYin) {
		if (pinYin.equals("add")) {
			return "���";
		} else {
			return "ɾ��";
		}
	}

	/**
	 * �ж��ظ��绰����
	 * 
	 * @return
	 */
	public static List<SortEntry> duplicateNum(List<SortEntry> entrys) {
		List<SortEntry> entryList = new ArrayList<>();
		Map<String, SortEntry> numMap = new LinkedHashMap<>();
		for (int i = 0; i < entrys.size() - 1; i++) {
			if (entrys.get(i).mNum.equals(entrys.get(i + 1).mNum)
					&& i <= entrys.size() - 1) {
//				if (!numMap.containsKey(entrys.get(i).mID)) {
					numMap.put(entrys.get(i).mID, entrys.get(i));
					numMap.put(entrys.get(i + 1).mID, entrys.get(i + 1));
//				}
			}
		}
		entryList = Tools.mapTransitionList(numMap);
		return entryList;
	}

	/**
	 *�ж��ظ�����
	 * 
	 * @return
	 */
	public static List<SortEntry> duplicateName(List<SortEntry> entrys) {
		List<SortEntry> entryList = new ArrayList<>();
		Map<String, SortEntry> nameMap = new LinkedHashMap<>();
		for (int i = 0; i < entrys.size() - 1; i++) {
			if (entrys.get(i).mName.equals(entrys.get(i + 1).mName)
					&& i < entrys.size() - 2) {
//				if (!nameMap.containsKey(entrys.get(i).mID)) {
					nameMap.put(entrys.get(i).mID, entrys.get(i));
					nameMap.put(entrys.get(i + 1).mID, entrys.get(i + 1));
//				}
			}
		}
		entryList = Tools.mapTransitionList(nameMap);
		return entryList;
	}

	
	/**
	 * ��mapת����list
	 * @param map
	 * @return
	 */
	public static List<SortEntry> mapTransitionList(Map<String,SortEntry> map) {
		List<SortEntry> list = new ArrayList<>();
		Iterator<Entry<String, SortEntry>> iter = map.entrySet().iterator();
		// ���map��Iterator
		while (iter.hasNext()) {
			Entry<String,SortEntry> entry = (Entry<String,SortEntry>) iter.next();
			list.add((SortEntry) entry.getValue());
		}
		return list;
	}
	
	public static List<List<SortEntry>> getMergeListNum(List<SortEntry> entrys){
		List<List<SortEntry>> mergeList = new ArrayList<>();
		int i = 0;
		SortEntry entry = entrys.get(0);
		List<SortEntry> entryList;
//		for(int i = 0;i<entrys.size()-1;i++){
		while(i < entrys.size()){
			entryList = new ArrayList<>();
//			System.out.println(i);
			while(entry.mNum.equals(entrys.get(i).mNum) && i<entrys.size()){
				
				if(i == entrys.size()-1){
					entryList.add(entrys.get(i));
					break;
				}
//				System.out.println(entrys.get(i).mNum+"  "+entry.mNum);
				entryList.add(entrys.get(i));
				i++;
			}
			if(i == entrys.size()-1){
				mergeList.add(entryList);
				break;
			}
			mergeList.add(entryList);
//			System.out.println("a"+i);
			entry = entrys.get(i);
		}
		return mergeList;
	}
	
	public static List<List<SortEntry>> getMergeListName(List<SortEntry> entrys){
		List<List<SortEntry>> mergeList = new ArrayList<>();
		int i = 0;
		SortEntry entry = entrys.get(0);
		List<SortEntry> entryList;
//		for(int i = 0;i<entrys.size()-1;i++){
		while(i < entrys.size()){
			entryList = new ArrayList<>();
//			System.out.println(i);
			while(entry.mName.equals(entrys.get(i).mName) && i<entrys.size()){
				
				if(i == entrys.size()-1){
					entryList.add(entrys.get(i));
					break;
				}
//				System.out.println(entrys.get(i).mNum+"  "+entry.mNum);
				entryList.add(entrys.get(i));
				i++;
			}
			if(i == entrys.size()-1){
				mergeList.add(entryList);
				break;
			}
			mergeList.add(entryList);
//			System.out.println("a"+i);
			entry = entrys.get(i);
		}
		return mergeList;
	}
}
