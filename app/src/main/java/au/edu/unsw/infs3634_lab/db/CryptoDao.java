package au.edu.unsw.infs3634_lab.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import au.edu.unsw.infs3634_lab.api.Crypto;

@Dao
public interface CryptoDao {
    @Query("SELECT * FROM crypto")
    List<Crypto> getCryptos();

    @Query("SELECT * FROM crypto WHERE id == :cryptoId")
    Crypto getCoin(String cryptoId);

    @Insert
    void insertAll(Crypto... cryptos);

    @Delete
    void deleteAll(Crypto... cryptos);
}
