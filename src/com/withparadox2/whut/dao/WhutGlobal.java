package com.withparadox2.whut.dao;

import java.util.ArrayList;

public class WhutGlobal {
	public static String URL_HEADER_STR = "";
	public static String USER_ID = "";
	public static String USER_PASSWORD = "";
	public static String USER_NAME = "";
	
	/**
	 * WhichAction为一静态全局变量，当点击按钮连接网络时，线程（HttpOperateThread）可以用此值来判断执行哪个函数，规定如下：
	 * WhichAction = 1时，执行登陆。
	 * WhichAction = 2时，执行查询课表。
	 * WhichAction = 3时，执行查询个人课表。
	 * WhichAction = 4时，执行查询成绩。
	 * WhichAction = 5时，执行评教。
	 * WhichAction = 6时，执行保存评教
	 * WhichAction = 7时，执行提交评教
	 * WhichAction = 8时，执行请求个人课表Post
	 * WhichAction = 9时，执行登陆图书馆
	 * WhichAction = 10时，执行借阅查询
	 * WhichAction = 10时，执行进入续借
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
	public static boolean CANCEL_DOWNLOAD_FLAG = false;//提交续借后自动，若为false取消线程中更新数据，可以加快速度
	
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
