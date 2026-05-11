package com.smd.penni.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.smd.penni.models.Transaction
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "penni_db"
        private const val DATABASE_VERSION = 2 // Incremented version to trigger onUpgrade for initial balance

        const val TABLE_CATEGORIES = "categories"
        const val TABLE_TRANSACTIONS = "transactions"

        const val KEY_ID = "id"
        const val KEY_CAT_NAME = "name"

        const val KEY_TRANS_TITLE = "title"
        const val KEY_TRANS_AMOUNT = "amount"
        const val KEY_TRANS_DATE = "date"
        const val KEY_TRANS_CAT_ID = "category_id"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createCategoryTable = ("CREATE TABLE $TABLE_CATEGORIES("
                + "$KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "$KEY_CAT_NAME TEXT)")

        val createTransactionTable = ("CREATE TABLE $TABLE_TRANSACTIONS("
                + "$KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "$KEY_TRANS_TITLE TEXT,"
                + "$KEY_TRANS_AMOUNT REAL,"
                + "$KEY_TRANS_DATE TEXT,"
                + "$KEY_TRANS_CAT_ID INTEGER,"
                + "FOREIGN KEY($KEY_TRANS_CAT_ID) REFERENCES $TABLE_CATEGORIES($KEY_ID))")

        db.execSQL(createCategoryTable)
        db.execSQL(createTransactionTable)

        // Seed some categories
        db.execSQL("INSERT INTO $TABLE_CATEGORIES ($KEY_CAT_NAME) VALUES ('Salary'), ('Food'), ('Transport'), ('Shopping'), ('Bills')")
        
        // F3: Seed Initial Balance of 10000
        val date = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date())
        val values = ContentValues().apply {
            put(KEY_TRANS_TITLE, "Initial Balance")
            put(KEY_TRANS_AMOUNT, 10000.0)
            put(KEY_TRANS_DATE, date)
            put(KEY_TRANS_CAT_ID, 1) // Salary category
        }
        db.insert(TABLE_TRANSACTIONS, null, values)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TRANSACTIONS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CATEGORIES")
        onCreate(db)
    }

    fun insertTransaction(title: String, amount: Double, date: String, catId: Int): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(KEY_TRANS_TITLE, title)
            put(KEY_TRANS_AMOUNT, amount)
            put(KEY_TRANS_DATE, date)
            put(KEY_TRANS_CAT_ID, catId)
        }
        return db.insert(TABLE_TRANSACTIONS, null, values)
    }

    fun getAllTransactions(): List<Transaction> {
        val list = mutableListOf<Transaction>()
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "SELECT t.*, c.name FROM $TABLE_TRANSACTIONS t " +
            "JOIN $TABLE_CATEGORIES c ON t.$KEY_TRANS_CAT_ID = c.$KEY_ID " +
            "ORDER BY t.$KEY_ID DESC", null
        )

        if (cursor.moveToFirst()) {
            do {
                val amount = cursor.getDouble(2)
                val sign = if (amount >= 0) "+" else "-"
                list.add(Transaction(
                    cursor.getInt(0).toString(),
                    cursor.getString(1),
                    "$sign$" + String.format("%.2f", Math.abs(amount)),
                    cursor.getString(3),
                    cursor.getString(5),
                    "Cleared",
                    ""
                ))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    fun searchTransactions(query: String): List<Transaction> {
        val list = mutableListOf<Transaction>()
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "SELECT t.*, c.name FROM $TABLE_TRANSACTIONS t " +
            "JOIN $TABLE_CATEGORIES c ON t.$KEY_TRANS_CAT_ID = c.$KEY_ID " +
            "WHERE t.$KEY_TRANS_TITLE LIKE ? " +
            "ORDER BY t.$KEY_ID DESC", arrayOf("%$query%")
        )

        if (cursor.moveToFirst()) {
            do {
                val amount = cursor.getDouble(2)
                val sign = if (amount >= 0) "+" else "-"
                list.add(Transaction(
                    cursor.getInt(0).toString(),
                    cursor.getString(1),
                    "$sign$" + String.format("%.2f", Math.abs(amount)),
                    cursor.getString(3),
                    cursor.getString(5),
                    "Cleared",
                    ""
                ))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    fun updateTransaction(id: Int, title: String, amount: Double, catId: Int): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(KEY_TRANS_TITLE, title)
            put(KEY_TRANS_AMOUNT, amount)
            put(KEY_TRANS_CAT_ID, catId)
        }
        return db.update(TABLE_TRANSACTIONS, values, "$KEY_ID=?", arrayOf(id.toString()))
    }

    fun deleteTransaction(id: Int): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_TRANSACTIONS, "$KEY_ID=?", arrayOf(id.toString()))
    }

    fun getTotalBalance(): Double {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT SUM($KEY_TRANS_AMOUNT) FROM $TABLE_TRANSACTIONS", null)
        var total = 0.0
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0)
        }
        cursor.close()
        return total
    }

    fun getCategories(): Map<Int, String> {
        val map = mutableMapOf<Int, String>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_CATEGORIES", null)
        if (cursor.moveToFirst()) {
            do {
                map[cursor.getInt(0)] = cursor.getString(1)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return map
    }
}
