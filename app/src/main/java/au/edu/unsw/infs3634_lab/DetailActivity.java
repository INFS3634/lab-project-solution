package au.edu.unsw.infs3634_lab;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.concurrent.Executors;

import au.edu.unsw.infs3634_lab.adapters.CryptoAdapter;
import au.edu.unsw.infs3634_lab.api.Crypto;
import au.edu.unsw.infs3634_lab.api.Service;
import au.edu.unsw.infs3634_lab.db.CryptoDatabase;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DetailActivity extends AppCompatActivity {
    public static final String INTENT_MESSAGE = "intent_message";
    private static final String TAG= "Detail-Activity";
    private TextView mName;
    private TextView mSymbol;
    private TextView mRank;
    private TextView mValue;
    private TextView mChange1h;
    private TextView mChange24h;
    private TextView mChange7d;
    private TextView mMarketcap;
    private TextView mVolume;
    private ImageView mSearch, mArt;
    private CryptoDatabase database;
    private CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        // Set title for the activity
        setTitle("Detail Activity");

        // Get the intent that started this activity and extract the string
        Intent intent = getIntent();
        if (intent.hasExtra(INTENT_MESSAGE)) {
            String message = intent.getStringExtra(INTENT_MESSAGE);
            Log.d(TAG, "Intent Message = " + message);

            // Instantiate a CountryDatabase object for "country-database"
            database = Room.databaseBuilder(getApplicationContext(), CryptoDatabase.class, "crypto-database").build();

            // Create an asynchronous database call using Java Runnable to:
            // Select the crypto from the database by its id received from MainActivity
            // Update activity_detail with the coin details
            Executors.newSingleThreadExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    Crypto coin = database.cryptoDao().getCoin(message);
                    if(coin != null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Get handle for view elements
                                mName = findViewById(R.id.tvName);
                                mSymbol = findViewById(R.id.tvSymbol);
                                mRank = findViewById(R.id.tvRank);
                                mValue = findViewById(R.id.tvValue);
                                mChange1h = findViewById(R.id.tvChange1h);
                                mChange24h = findViewById(R.id.tvChange24h);
                                mChange7d = findViewById(R.id.tvChange7d);
                                mMarketcap = findViewById(R.id.tvMarketCap);
                                mVolume = findViewById(R.id.tvVolume24);
                                mSearch = findViewById(R.id.ivSearch);
                                mArt = findViewById(R.id.ivImage);
                                checkBox = findViewById(R.id.cbFavourite);

                                // Set value for the crypto attributes
                                NumberFormat formatter = NumberFormat.getCurrencyInstance();
                                setTitle(coin.getName());
                                Glide.with(DetailActivity.this)
                                        .load("https://www.coinlore.com/img/" + coin.getNameid() + ".png")
                                        .fitCenter()
                                        .into(mArt);
                                mName.setText(coin.getName());
                                mSymbol.setText(coin.getSymbol());
                                mRank.setText(coin.getRank().toString());
                                mValue.setText(formatter.format(Double.valueOf(coin.getPriceUsd())));
                                mChange1h.setText(coin.getPercentChange1h() + " %");
                                mChange24h.setText(coin.getPercentChange24h() + " %");
                                mChange7d.setText(coin.getPercentChange7d() + " %");
                                mMarketcap.setText(formatter.format(Double.valueOf(coin.getMarketCapUsd())));
                                mVolume.setText(formatter.format(coin.getVolume24()));
                                // Implement click listener for search icon
                                mSearch.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        searchCrypto(coin.getName());
                                    }
                                });

                                // Get the database instance from Firebase database
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                // Get the reference for the database
                                DatabaseReference reference = database.getReference(FirebaseAuth.getInstance().getUid());
                                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        String result = (String) snapshot.getValue();
                                        if (result != null && result.equals(coin.getSymbol())) {
                                            checkBox.setChecked(true);
                                        } else {
                                            checkBox.setChecked(false);
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                                // Implement checked change listener for the checkbox
                                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                        if (isChecked) {
                                            reference.setValue(coin.getSymbol());
                                        } else {
                                            reference.setValue("");
                                        }
                                    }
                                });
                            }
                        });
                    }

                }
            });
        }
    }

    // Called when user taps on search icon
    public void searchCrypto(String cryptoName){
        // Create an ACTION_VIEW intent
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=" + cryptoName));
        startActivity(intent);
    }

}