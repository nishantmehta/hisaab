package com.hisaab.database;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.hisaab.unitclasses.Friend;

public class FriendsDataSource {

	// Database fields.
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	private String[] allColumns = { MySQLiteHelper.COLUMN_ID,
			MySQLiteHelper.COLUMN_NAME, MySQLiteHelper.COLUMN_PHONENUMBER,
			MySQLiteHelper.COLUMN_LASTTRANSACTION };

	public FriendsDataSource(Context context) {
		dbHelper = new MySQLiteHelper(context);
	}

	public void open() throws SQLException {
		Log.d("resulthisaab", "inside open");
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public void updateTimeStamp(Friend data) {


		// test code
		try {
			String number = data.getPhoneNumber();
			number = number.replaceAll("[^0-9]", "");
			number = number.substring(number.length() - 10, number.length());
			Long tsLong = System.currentTimeMillis() / 1000;
			String sqlString = "INSERT OR REPLACE INTO friends (_id,name,phoneNumber,lastTransaction) VALUES ("
					+ "coalesce((SELECT _id FROM friends WHERE phoneNumber ='"
					+ data.getPhoneNumber()
					+ "'),"
					+ number
					+ "),"
					+ "'"
					+ data.getName()
					+ "',"
					+ "coalesce((SELECT phoneNumber FROM friends WHERE phoneNumber ='"
					+ data.getPhoneNumber()
					+ "'),'"
					+ data.getPhoneNumber()
					+ "'),"
					+ tsLong + ")";
			database.execSQL(sqlString);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			Log.d("resulthisaab", e.getMessage());
		}
	}

	public void createOrUpdateFriend(Friend data) {

		// test code
		try {
			String number = data.getPhoneNumber();
			number = number.replaceAll("[^0-9]", "");
			number = number.substring(number.length() - 10, number.length());
			Long tsLong = System.currentTimeMillis() / 1000;
			String sqlString = "INSERT OR REPLACE INTO friends (_id,name,phoneNumber,lastTransaction) VALUES ("
					+ "coalesce((SELECT _id FROM friends WHERE phoneNumber ='"
					+ data.getPhoneNumber()
					+ "'),"
					+ number
					+ "),"
					+ "'"
					+ data.getName()
					+ "',"
					+ "coalesce((SELECT phoneNumber FROM friends WHERE phoneNumber ='"
					+ data.getPhoneNumber()
					+ "'),'"
					+ data.getPhoneNumber()
					+ "'),"
					+ "coalesce((SELECT lastTransaction FROM friends WHERE phoneNumber = '"
					+ data.getPhoneNumber() + "'),'" + tsLong + "'))";
			database.execSQL(sqlString);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			Log.d("resulthisaab", e.getMessage());
		}

	}

	public void deleteFriend(Friend data) {
		long id = data.getId();
		System.out.println("Comment deleted with id: " + id);
		database.delete(MySQLiteHelper.TABLE_FRIENDS, MySQLiteHelper.COLUMN_ID
				+ " = " + id, null);
	}

	public List<Friend> getAllFriends() {
		List<Friend> Friends = new ArrayList<Friend>();

		Cursor cursor = database.query(MySQLiteHelper.TABLE_FRIENDS,
				allColumns, null, null, null, null,
				MySQLiteHelper.COLUMN_LASTTRANSACTION + " DESC");

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Friend friend = cursorToFriend(cursor);
			Friends.add(friend);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return Friends;
	}

	public Friend getFriend(long id) {
		Friend data = new Friend();
		try {
			Cursor cursor = database.query(MySQLiteHelper.TABLE_FRIENDS,
					allColumns, MySQLiteHelper.COLUMN_ID + " = " + id, null,
					null, null, null);

			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				data = cursorToFriend(cursor);
				cursor.moveToNext();
			}
			// Make sure to close the cursor
			cursor.close();
			return data;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.d("resulthisaab", e.getMessage());
		}

		return data;
	}

	private Friend cursorToFriend(Cursor cursor) {
		Log.d("database", cursor.getString(2));
		Friend friend = new Friend();
		friend.setId(cursor.getLong(0));
		friend.setName(cursor.getString(1));
		friend.setPhoneNumber(cursor.getString(2));
		friend.setLastTransaction(cursor.getLong(cursor
				.getColumnIndex(MySQLiteHelper.COLUMN_LASTTRANSACTION)));
		return friend;
	}
}