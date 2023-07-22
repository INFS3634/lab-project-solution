package au.edu.unsw.infs3634_lab;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

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
            // Select the coin from the database by coin symbol received from MainActivity
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

                                // Set value for the crypto attributes
                                NumberFormat formatter = NumberFormat.getCurrencyInstance();
                                setTitle(coin.getName());
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
                            }
                        });
                    }

                }
            });

            // Implement Retrofit to make API call
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://api.coinlore.net")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            // Create object to make API call
            Service service = retrofit.create(Service.class);
            Call<ArrayList<Crypto>> responseCall = service.getCoin(Integer.valueOf(message));
            responseCall.enqueue(new Callback<ArrayList<Crypto>>() {
                @Override
                public void onResponse(Call<ArrayList<Crypto>> call, Response<ArrayList<Crypto>> response) {
                    Log.d(TAG, "API Call Successful!" + " URL="+call.request().url().toString());

                    Crypto coin = response.body().get(0);
                    if(coin != null) {
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

                        // Set value for the crypto attributes
                        NumberFormat formatter = NumberFormat.getCurrencyInstance();
                        setTitle(coin.getName());
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
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<Crypto>> call, Throwable t) {
                    Log.d(TAG, "API Call Failure." + " URL="+call.request().url().toString());
                    t.printStackTrace();
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