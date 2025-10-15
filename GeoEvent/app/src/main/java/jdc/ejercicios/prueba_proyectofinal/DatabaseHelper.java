package jdc.ejercicios.prueba_proyectofinal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public DatabaseHelper(Context context) {
        super(context, "EventsDB", null, 4); // Versión 4
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE events (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT, " +
                "description TEXT, " +
                "date TEXT, " +
                "image_uri TEXT, " +
                "latitude REAL, " +
                "longitude REAL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE events ADD COLUMN date TEXT");
        }
        if (oldVersion < 3) {
            db.execSQL("ALTER TABLE events ADD COLUMN image_uri TEXT");
        }
        if (oldVersion < 4) {
            db.execSQL("ALTER TABLE events ADD COLUMN latitude REAL");
            db.execSQL("ALTER TABLE events ADD COLUMN longitude REAL");
        }
    }

    public void insertEvent(String title, String description, String date, String imageUri, double latitude, double longitude) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("title", title);
        cv.put("description", description);
        cv.put("date", date);
        cv.put("image_uri", imageUri);
        cv.put("latitude", latitude);
        cv.put("longitude", longitude);
        db.insert("events", null, cv);
    }

    public Cursor getAllEvents() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM events", null);
    }

    public Cursor getEventById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM events WHERE id = ?", new String[]{String.valueOf(id)});
    }

    // Nuevo método para obtener un objeto Event completo
    public Event getEventByIdAsObject(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM events WHERE id = ?", new String[]{String.valueOf(id)});
        Event event = null;
        if (cursor.moveToFirst()) {
            int eventId = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
            String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
            String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
            String imageUri = cursor.getString(cursor.getColumnIndexOrThrow("image_uri"));
            double latitude = cursor.getDouble(cursor.getColumnIndexOrThrow("latitude"));
            double longitude = cursor.getDouble(cursor.getColumnIndexOrThrow("longitude"));

            event = new Event(eventId, title, description, date, imageUri, latitude, longitude);
        }
        cursor.close();
        return event;
    }

    public void updateEvent(int id, String title, String description, String date, String imageUri, double latitude, double longitude) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("title", title);
        cv.put("description", description);
        cv.put("date", date);
        cv.put("image_uri", imageUri);
        cv.put("latitude", latitude);
        cv.put("longitude", longitude);
        db.update("events", cv, "id = ?", new String[]{String.valueOf(id)});
    }

    public int deleteEvent(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("events", "id = ?", new String[]{String.valueOf(id)});
    }
}
