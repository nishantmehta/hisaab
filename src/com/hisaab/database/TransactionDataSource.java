package com.hisaab.database;

import java.util.ArrayList;
import java.util.List;

import com.hisaab.unitclasses.Transaction;
import com.hisaab.unitclasses.TransactionPacket;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class TransactionDataSource {

	// Database fields
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	private String[] allColumns = { MySQLiteHelper.COLUMN_ID,
			MySQLiteHelper.COLUMN_AMOUNT, MySQLiteHelper.COLUMN_DESCRIPTION,
			MySQLiteHelper.COLUMN_FLAG, MySQLiteHelper.COLUMN_COM_ID,
			MySQLiteHelper.COLUMN_TOPAY, MySQLiteHelper.COLUMN_LASTUPDATED };

	public TransactionDataSource(Context context) {
		dbHelper = new MySQLiteHelper(context);
	}

	public void open() throws SQLException {
		Log.d("resulthisaab", "inside open");
		database = dbHelper.getWritableDatabase();

	}

	public void close() {
		dbHelper.close();
	}

	public void createTransaction(Transaction data) {

		try {
			ContentValues values = new ContentValues();
			Long tsLong;
			if (data.get_id() < 1) {
				tsLong = System.currentTimeMillis();
			} else {
				tsLong = data.get_id();
			}
			values.put(MySQLiteHelper.COLUMN_ID, tsLong);
			values.put(MySQLiteHelper.COLUMN_AMOUNT, data.getAmount());
			values.put(MySQLiteHelper.COLUMN_DESCRIPTION, data.getDecsription());
			values.put(MySQLiteHelper.COLUMN_COM_ID, data.getCom_id());
			values.put(MySQLiteHelper.COLUMN_FLAG, data.getFlag());
			values.put(MySQLiteHelper.COLUMN_TOPAY, data.getToPay());
			values.put(MySQLiteHelper.COLUMN_LASTUPDATED, tsLong);
			long insertId = database.insert(MySQLiteHelper.TABLE_TRANSACTION,
					null, values);

			Log.d("resulthisaab", "data base result upon insert is " + insertId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.d("resulthisaab", e.getMessage());
		}

	}

	public void updatePendingTransaction(Transaction data) {
		try {
			Long tsLong = System.currentTimeMillis();
			ContentValues values = new ContentValues();
			String whereClause = "_id=" + data.get_id();
			values.put(MySQLiteHelper.COLUMN_ID, data.get_id());
			values.put(MySQLiteHelper.COLUMN_AMOUNT, data.getAmount());
			values.put(MySQLiteHelper.COLUMN_DESCRIPTION, data.getDecsription());
			values.put(MySQLiteHelper.COLUMN_COM_ID, data.getCom_id());
			values.put(MySQLiteHelper.COLUMN_FLAG, data.getFlag());
			values.put(MySQLiteHelper.COLUMN_TOPAY, data.getToPay());
			values.put(MySQLiteHelper.COLUMN_LASTUPDATED, tsLong);
			long insertId = database.update(
					MySQLiteHelper.TABLE_PENDING_TRANSACTION, values,
					whereClause, null);
			Log.d("resulthisaab", "data base result upon insert is " + insertId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.d("resulthisaab", e.getMessage());
		}
	}

	public void createOrUpdatePendingTransaction(Transaction data) {
		Long tsLong = System.currentTimeMillis();
		try {
			String sqlString = "INSERT OR REPLACE INTO pendingtansactions (_id,com_id,flag,description,amount,toPay,lastUpdated) VALUES ("
					+ "coalesce((SELECT _id FROM pendingtansactions WHERE _id = '"
					+ data.get_id()
					+ "'),"
					+ data.get_id()
					+ "),"
					+ data.getCom_id()
					+ ",'"
					+ data.getFlag()
					+ "','"
					+ data.getDecsription()
					+ "',"
					+ data.getAmount()
					+ ","
					+ data.getToPay() + "," + data.getLastUpdated() + ")";
			database.execSQL(sqlString);
		} catch (SQLException e) {
			Log.d("data for view",e.getMessage());
			e.printStackTrace();
		}
	}

	public void updateTransaction(Transaction data) {
		try {
			Long tsLong = System.currentTimeMillis();
			ContentValues values = new ContentValues();
			String whereClause = "_id=" + data.get_id();
			values.put(MySQLiteHelper.COLUMN_ID, data.get_id());
			values.put(MySQLiteHelper.COLUMN_AMOUNT, data.getAmount());
			values.put(MySQLiteHelper.COLUMN_DESCRIPTION, data.getDecsription());
			values.put(MySQLiteHelper.COLUMN_COM_ID, data.getCom_id());
			values.put(MySQLiteHelper.COLUMN_FLAG, data.getFlag());
			values.put(MySQLiteHelper.COLUMN_TOPAY, data.getToPay());
			values.put(MySQLiteHelper.COLUMN_LASTUPDATED, tsLong);
			long insertId = database.update(MySQLiteHelper.TABLE_TRANSACTION,
					values, whereClause, null);
			Log.d("resulthisaab", "data base result upon insert is " + insertId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.d("resulthisaab", e.getMessage());
		}
	}

	public void createPendingTransaction(Transaction data) {
		try {
			Long tsLong = System.currentTimeMillis();
			ContentValues values = new ContentValues();
			String whereClause = "_id=" + data.get_id();
			values.put(MySQLiteHelper.COLUMN_ID, data.get_id());
			values.put(MySQLiteHelper.COLUMN_AMOUNT, data.getAmount());
			values.put(MySQLiteHelper.COLUMN_DESCRIPTION, data.getDecsription());
			values.put(MySQLiteHelper.COLUMN_COM_ID, data.getCom_id());
			values.put(MySQLiteHelper.COLUMN_FLAG, data.getFlag());
			values.put(MySQLiteHelper.COLUMN_TOPAY, data.getToPay());
			values.put(MySQLiteHelper.COLUMN_LASTUPDATED, tsLong);
			long insertId = database.insert(
					MySQLiteHelper.TABLE_PENDING_TRANSACTION, null, values);

			Log.d("resulthisaab", "data base result upon insert is " + insertId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.d("date", e.getMessage());
		}
	}

	public List<Transaction> getAllMessages() {
		List<Transaction> Transactions;
		Transactions = new ArrayList<Transaction>();
		try {

			Cursor cursor = database.query(MySQLiteHelper.TABLE_MESSAGE_LIST,
					allColumns, null, null, null, null,
					MySQLiteHelper.COLUMN_ID + " DESC");

			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				Transaction transaction = cursorToTransaction(cursor);
				Transactions.add(transaction);
				cursor.moveToNext();
			}
			// Make sure to close the cursor
			cursor.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.d("resulthisaab", e.getMessage());
		}
		return Transactions;
	}

	public void deleteTransaction(Transaction data) {
		long id = data.get_id();
		System.out.println("Comment deleted with id: " + id);
		database.delete(MySQLiteHelper.TABLE_TRANSACTION,
				MySQLiteHelper.COLUMN_ID + " = " + id, null);
	}

	public void deletePendingTransaction(Transaction data) {
		long id = data.get_id();
		System.out.println("Comment deleted with id: " + id);
		database.delete(MySQLiteHelper.TABLE_PENDING_TRANSACTION,
				MySQLiteHelper.COLUMN_ID + " = " + id, null);
	}

	public List<Transaction> getAllTransactions(long ID) {
		List<Transaction> Transactions;
		Transactions = new ArrayList<Transaction>();
		try {

			Cursor cursor = database.query(MySQLiteHelper.TABLE_TRANSACTION,
					allColumns, MySQLiteHelper.COLUMN_COM_ID + " = " + ID,
					null, null, null, MySQLiteHelper.COLUMN_LASTUPDATED
							);

			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				Transaction transaction = cursorToTransaction(cursor);
				Transactions.add(transaction);
				cursor.moveToNext();
			}
			// Make sure to close the cursor
			cursor.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.d("resulthisaab", e.getMessage());
		}
		return Transactions;
	}

	public List<Transaction> getAllPendingTransactions() {
		List<Transaction> Transactions;
		Transactions = new ArrayList<Transaction>();
		try {

			Cursor cursor = database.query(
					MySQLiteHelper.TABLE_PENDING_TRANSACTION, allColumns, null,
					null, null, null, MySQLiteHelper.COLUMN_LASTUPDATED
							+ " DESC");

			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				Transaction transaction = cursorToTransaction(cursor);
				Transactions.add(transaction);
				cursor.moveToNext();
			}
			// Make sure to close the cursor
			cursor.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.d("date", e.getMessage());
		}
		return Transactions;
	}

	private Transaction cursorToTransaction(Cursor cursor) {

		Log.d("date",
				cursor.getColumnName(0) + "``" + cursor.getColumnName(1) + "``"
						+ cursor.getColumnName(2) + "``"
						+ cursor.getColumnName(3) + "``"
						+ cursor.getColumnName(4) + "``"
						+ cursor.getColumnName(5) + "``"
						+ cursor.getColumnName(6));
		Transaction transaction = new Transaction();
		transaction.set_id(cursor.getLong(0));
		transaction.setCom_id(cursor.getLong(4));
		transaction.setFlag(cursor.getInt(3));
		transaction.setDecsription(cursor.getString(2));
		transaction.setAmount(cursor.getInt(1));
		transaction.setToPay(cursor.getInt(5));
		transaction.setLastUpdated(cursor.getLong(6));

		return transaction;
	}
}