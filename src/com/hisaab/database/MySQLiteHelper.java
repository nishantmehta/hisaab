package com.hisaab.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {

	public static final String TABLE_FRIENDS = "friends";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_PHONENUMBER = "phoneNumber";
	public static final String TABLE_TRANSACTION = "transactions";
	public static final String TABLE_PENDING_TRANSACTION ="pendingtansactions";
	public static final String TABLE_MESSAGE_LIST ="messagelist";
	public static final String COLUMN_COM_ID = "com_id";
	public static final String COLUMN_DESCRIPTION = "description";
	public static final String COLUMN_AMOUNT = "amount";
	public static final String COLUMN_FLAG = "flag";
	public static final String COLUMN_TOPAY = "toPay";
	public static final String COLUMN_LASTUPDATED = "lastUpdated";
	public static final String COLUMN_LASTTRANSACTION = "lastTransaction";

	private static final String DATABASE_NAME = "hisaab.db";
	private static final int DATABASE_VERSION = 2;
	Context ctx;
	// Database creation sql statement
	private static final String DATABASE_CREATE_FRIEND = "create table "
			+ TABLE_FRIENDS + "(" + COLUMN_ID + " integer primary key , "
			+ COLUMN_PHONENUMBER + " text not null, " + COLUMN_NAME
			+ " text not null, "+COLUMN_LASTTRANSACTION+" integer );";
	
	private static final String DATABASE_CREATE_TRANSACTION = "create table "
			+ TABLE_TRANSACTION + "(" + COLUMN_ID + " integer primary key , "
			+ COLUMN_COM_ID + " integer ," + COLUMN_FLAG + " integer ,"
			+ COLUMN_DESCRIPTION + " text ," + COLUMN_AMOUNT + " integer ,"+ COLUMN_TOPAY +" integer,"+COLUMN_LASTUPDATED+" integer);";
 
	private static final String DATABASE_CREATE_PENDING_TRANSACTION = "create table " 
			+ TABLE_PENDING_TRANSACTION + "(" + COLUMN_ID + " integer primary key , "
			+ COLUMN_COM_ID + " integer ," + COLUMN_FLAG + " integer ,"
			+ COLUMN_DESCRIPTION + " text ," + COLUMN_AMOUNT + " integer ,"+ COLUMN_TOPAY +" integer,"+COLUMN_LASTUPDATED+" integer);";
	
	private static final String DATABASE_CREATE_MESSAGE_lIST = "create table " 
			+ TABLE_MESSAGE_LIST + "(" + COLUMN_ID + " integer primary key , "
			+ COLUMN_COM_ID + " integer ," + COLUMN_FLAG + " integer ,"
			+ COLUMN_DESCRIPTION + " text ," + COLUMN_AMOUNT + " integer ,"+ COLUMN_TOPAY +" integer);";
	
	public MySQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		
		ctx=context;
		
        
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		Log.d("database",DATABASE_CREATE_FRIEND);
		Log.d("database",DATABASE_CREATE_TRANSACTION);
		database.execSQL(DATABASE_CREATE_FRIEND);
		database.execSQL(DATABASE_CREATE_TRANSACTION);
		database.execSQL(DATABASE_CREATE_PENDING_TRANSACTION);
		SharedPreferences myPrefs = ctx.getSharedPreferences("UserInfoHisaab",android.content.Context.MODE_WORLD_READABLE);
        SharedPreferences.Editor prefsEditor = myPrefs.edit();
        prefsEditor.putString("UserName", null);
        prefsEditor.putString("phoneNumber", null);
        prefsEditor.putString("registered","0");
        prefsEditor.commit();
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(MySQLiteHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_FRIENDS);
		onCreate(db);
	}

}