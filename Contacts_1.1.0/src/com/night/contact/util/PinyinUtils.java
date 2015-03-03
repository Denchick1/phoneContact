package com.night.contact.util;
import java.util.ArrayList;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import com.night.contact.util.HanziToPinyin.Token;
/**
 * ƴ�������� 
 */
public class PinyinUtils {
	/**
	 * ���ַ����е�����ת��Ϊƴ��,�����ַ�����
	 * @param inputString
	 * @return
	 */
	public static String getPingYin(String inputString) {
		HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
		format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		format.setVCharType(HanyuPinyinVCharType.WITH_V);

		char[] input = inputString.trim().toCharArray();
		String output = "";

		try {
			for (char curchar : input) {
				if (java.lang.Character.toString(curchar).matches(
						"[\\u4E00-\\u9FA5]+")) {
					String[] temp = PinyinHelper.toHanyuPinyinStringArray(
							curchar, format);
					output += temp[0];
				} else
					output += java.lang.Character.toString(curchar);
			}
		} catch (BadHanyuPinyinOutputFormatCombination e) {
			e.printStackTrace();
		}
		return output;
	}

	/**
	 * ����ת��Ϊ����ƴ������ĸ��Ӣ���ַ�����
	 * ��������->hhds
	 * @param chines
	 *            ����
	 * @return ƴ��
	 */
	public static String getFirstSpell(String chinese) {  
	            StringBuffer pybf = new StringBuffer();  
	            char[] arr = chinese.toCharArray();  
	            HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();  
	            defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);  
	            defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);  
	            for (char curchar : arr) {  
                    if (curchar > 128) {  
                            try {  
                                    String[] temp = PinyinHelper.toHanyuPinyinStringArray(curchar, defaultFormat);  
                                    if (temp != null) {  
                                            pybf.append(temp[0].charAt(0));  
                                    }  
                            } catch (BadHanyuPinyinOutputFormatCombination e) {  
                                    e.printStackTrace();  
                            }  
                    } else {  
                            pybf.append(curchar);  
                    }  
            }  
	            return pybf.toString().replaceAll("\\W", "").trim();  
	    } 
    
	public static String getPingYin1(String input){
		ArrayList<Token> tokens = HanziToPinyin.getInstance().get(input);
		StringBuilder sb = new StringBuilder();
		if(tokens != null && tokens.size() >0){
			for(Token token : tokens){
				if(Token.PINYIN == token.type){
					sb.append(token.target);
				}
				else{
					sb.append(token.source);
				}
			}
		}
		return sb.toString().toLowerCase();
	}
}
