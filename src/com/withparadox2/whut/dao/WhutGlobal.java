package com.withparadox2.whut.dao;

import java.util.ArrayList;

public class WhutGlobal {
	public static String URL_HEADER_STR = "";
	public static String USER_ID = "";
	public static String USER_PASSWORD = "";
	public static String USER_NAME = "";
	
	/**
	 * WhichActionΪһ��̬ȫ�ֱ������������ť��������ʱ���̣߳�HttpOperateThread�������ô�ֵ���ж�ִ���ĸ��������涨���£�
	 * WhichAction = 1ʱ��ִ�е�½��
	 * WhichAction = 2ʱ��ִ�в�ѯ�α�
	 * WhichAction = 3ʱ��ִ�в�ѯ���˿α�
	 * WhichAction = 4ʱ��ִ�в�ѯ�ɼ���
	 * WhichAction = 5ʱ��ִ�����̡�
	 * WhichAction = 6ʱ��ִ�б�������
	 * WhichAction = 7ʱ��ִ���ύ����
	 * WhichAction = 8ʱ��ִ��������˿α�Post
	 * WhichAction = 9ʱ��ִ�е�½ͼ���
	 * WhichAction = 10ʱ��ִ�н��Ĳ�ѯ
	 * WhichAction = 10ʱ��ִ�н�������
	 */
	public static int WhichAction;
	
	public static String[][] htmlData ;
	public static int PINGJIAO_URL_POSITION;
	public static String[][] PINGJIAO_URLS;
	public static String PINGJIAO_URL;
	public static String VIEW_STATE;
	public static String DROPDOWN_LIST_STR;
	public static String TIJIAO;
	public static String PART_URL;
	public static boolean JUMP_OR_NOT;
	public static boolean TIJIAO_SUCCESS;
	
	
	public static String[] DATE_LIST;
	public static String SELECTED_DATE;
	public static String SELECTED_TERM;
	
	public static String JSESSIONID;
	public static boolean XUJIE_SUCCESS_FLAG;
	public static boolean CANCEL_DOWNLOAD_FLAG = false;//�ύ������Զ�����Ϊfalseȡ���߳��и������ݣ����Լӿ��ٶ�
	
	public static final String ShARE_LIXIAN_KEBIAO_NAME = "liXianKeBiao";
	public static final String SHARE_LIXIAN_JIEYUE_NAME = "liXianJieYue";
	
	public static ArrayList<String[]> BOOKLIST = new ArrayList<String[]>(); 
	public static ArrayList<ArrayList<String[]>> CHILDLIST = new ArrayList<ArrayList<String[]>>();
	public static String BOOK_CODE;
	public static String SEARCH_TITLE;
	public static int BOOK_CODE_POS;
	public static ArrayList<Boolean> CLICK_GROUP_FLAG = new ArrayList<Boolean>();
	public static int PAGE;
	
}
