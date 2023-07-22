package au.edu.unsw.infs3634_lab.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import au.edu.unsw.infs3634_lab.api.Crypto;

@Database(entities = {Crypto.class}, version = 1)
public abstract class CryptoDatabase extends RoomDatabase {
    public abstract CryptoDao cryptoDao();
}
