package com.example.samah.victo.classes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "VICTO.DB";

    public static final String PRODUCTS = "PRODUCTS";
    public static final String proID = "proID";
    public static final String proName = "proName";
    public static final String proMaxPrice = "proMaxPrice";
    public static final String proWeight = "proWeight";

    public static final String RAW = "RAW";
    public static final String rawID = "rawID";
    public static final String rawName = "rawName";
    public static final String rawPrice = "rawPrice";
    public static final String rawUnit = "rawUnit";

    public static final String COMPOSITION = "COMPOSITION";
    public static final String quantity = "rawQuantity";


    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 2);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table '" + PRODUCTS + "'('proID' integer primary key autoincrement, 'proName' Text unique, 'proMaxPrice' float,'proWeight' float)");
        db.execSQL("create table '" + RAW + "'('rawID' integer primary key autoincrement, 'rawName' Text unique, 'rawUnit' Text, 'rawPrice' float)");
        db.execSQL("create table '" + COMPOSITION + "'('proID' integer , 'rawID' integer, 'rawQuantity' float, FOREIGN KEY ('proID') REFERENCES 'PRODUCTS'('proID') ON DELETE CASCADE, FOREIGN KEY ('rawID') REFERENCES 'RAW'('rawID)'), PRIMARY KEY ('proID', 'rawID'))");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            db.execSQL("ALTER TABLE PRODUCTS ADD COLUMN proMaxPrice float DEFAULT 10");
            db.execSQL("ALTER TABLE PRODUCTS ADD COLUMN proWeight float DEFAULT 0");
        }

    }


    public boolean insertProduct(String proName, float proMaxPrice, float proWeight) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(this.proName, proName);
        contentValues.put(this.proMaxPrice, proMaxPrice);
        contentValues.put(this.proWeight, proWeight);
        try {
            return db.insert(PRODUCTS, null, contentValues) != -1;
        } catch (SQLException e) {
            return false;
        } finally {
            db.close();
        }
    }

    public boolean insertRaw(String rawName, float rawPrice, String rawUnit) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(this.rawName, rawName);
        contentValues.put(this.rawUnit, rawUnit);
        contentValues.put(this.rawPrice, rawPrice);
        try {
            return db.insert(RAW, null, contentValues) != -1;
        } catch (SQLException e) {
            return false;
        } finally {
            db.close();
        }
    }

    public boolean insertComposition(int proID, int rawID, float quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(this.proID, proID);
        contentValues.put(this.rawID, rawID);
        contentValues.put(this.quantity, quantity);
        try {
            return db.insert(COMPOSITION, null, contentValues) != -1;
        } catch (SQLException e) {
            return false;
        } finally {
            db.close();
        }
    }

    public boolean updateComposition(int proID, int rawID, float quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(this.proID, proID);
        contentValues.put(this.rawID, rawID);
        contentValues.put(this.quantity, quantity);

        try {
            return db.update(COMPOSITION, contentValues, "rawID = ? AND proID =?", new String[]{rawID + "", proID + ""}) != 0;
        } catch (SQLException e) {
            return false;
        } finally {
            db.close();
        }
    }

    public Cursor getAllData(String table) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            Cursor cursor = db.rawQuery("select * from " + table, null);
            return cursor;
        } catch (SQLException e) {
            return null;
        }


    }


    public Cursor getComposition(int proID) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            Cursor cursor = db.rawQuery("select COMPOSITION.rawID,  rawQuantity, RAW.rawName, RAW.rawUnit, Raw.rawPrice from COMPOSITION" +
                    " LEFT JOIN RAW ON COMPOSITION.rawID = RAW.rawID  where  proID = ? ", new String[]{proID + ""});
            return cursor;
        } catch (SQLException E) {
            return null;
        }


    }


    public Cursor getDataFromString(String table, String argName, String arg) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            Cursor cursor = db.rawQuery("select * from " + table + " where " + argName + " = ?", new String[]{arg});
            return cursor;
        } catch (SQLException e) {
            return null;
        }
    }

    public boolean editRaw(int rawID, String rawName, float rawPrice, String rawUnit) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(this.rawID, rawID);
        contentValues.put(this.rawName, rawName);
        contentValues.put(this.rawUnit, rawUnit);
        contentValues.put(this.rawPrice, rawPrice);
        try {
            return db.update(RAW, contentValues, "rawID = ?", new String[]{rawID + ""}) != 0;
        } catch (SQLException e) {
            return false;
        } finally {
            db.close();
        }
    }

    public boolean editPriceRaw(int rawID, float rawPrice) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(this.rawID, rawID);
        contentValues.put(this.rawPrice, rawPrice);
        try {
            return db.update(RAW, contentValues, "rawID = ?", new String[]{rawID + ""}) != 0;
        } catch (SQLException e) {
            return false;
        } finally {
            db.close();
        }
    }


    public boolean editProduct(int proID, String proName, float proMaxPrice, float proWeight ) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(this.proID, proID);
        contentValues.put(this.proName, proName);
        contentValues.put(this.proMaxPrice, proMaxPrice);
        contentValues.put(this.proWeight, proWeight);
        try {
            return db.update(PRODUCTS, contentValues, "proID = ?", new String[]{proID + ""}) != -1;
        } catch (SQLException e) {
            return false;
        } finally {
            db.close();
        }
    }


    public boolean deleteRaw(int rawID) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.delete(COMPOSITION, "rawID = ?", new String[]{rawID + ""});
            return db.delete(RAW, "rawID = ?", new String[]{rawID + ""}) != -1;
        } catch (SQLException e) {
            return false;
        } finally {
            db.close();
        }
    }

    public boolean deleteComposition(int proID) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            return db.delete(COMPOSITION, "proID = ?", new String[]{proID + ""}) != -1;
        } catch (SQLException e) {
            return false;
        } finally {
            db.close();
        }
    }

    public boolean deleteProduct(int proID) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            return db.delete(PRODUCTS, "proID = ?", new String[]{proID + ""}) != -1;
        } catch (SQLException e) {
            return false;
        } finally {
            db.close();
        }
    }

}
