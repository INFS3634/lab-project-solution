package au.edu.unsw.infs3634_lab.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import au.edu.unsw.infs3634_lab.R;
import au.edu.unsw.infs3634_lab.api.Crypto;
import kotlin.reflect.KParameter;

public class CryptoAdapter extends RecyclerView.Adapter<CryptoAdapter.MyViewHolder> implements Filterable {
    private List<Crypto> localDataSet, localDataSetFiltered;
    private RecyclerViewClickListener localListener;
    public static final int SORT_BY_NAME = 1;
    public static final int SORT_BY_VALUE = 2;

    // Set the supplied data to the adapter
    public void setData(List<Crypto> data) {
        localDataSet.clear();
        localDataSet.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        // Implement the “Filter()” method to filter the dataset based on search queries
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                ArrayList<Crypto> filteredList = new ArrayList<>();
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    filteredList = (ArrayList<Crypto>) localDataSet;
                } else {
                    for (Crypto crypto : localDataSet) {
                        if (crypto.getName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(crypto);
                        }
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                localDataSetFiltered = (ArrayList<Crypto>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    // Use the Java Collections.sort() and Comparator methods to sort the list
    public void sortList(final int sortMethod) {
        if (localDataSetFiltered.size() > 0) {
            Collections.sort(localDataSetFiltered, new Comparator<Crypto>() {
                @Override
                public int compare(Crypto c1, Crypto c2) {
                    if (sortMethod == SORT_BY_NAME) {
                        // Sort by name in ascending order
                        return c1.getName().compareTo(c2.getName());
                    } else if (sortMethod == SORT_BY_VALUE) {
                        // sort by value in USD in descending order
                        return Double.valueOf(c2.getPriceUsd()).compareTo(Double.valueOf(c1.getPriceUsd()));
                    }
                    // By default sort the list by crypto name
                    return c1.getName().compareTo(c2.getName());
                }
            });
        }
        // Inform the adapter that the dataset has changed
        notifyDataSetChanged();
    }



    /**
     * Implement custom ViewHolder
     */
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;
        private TextView name, value, change;

        public MyViewHolder(@NonNull View itemView, RecyclerViewClickListener listener) {
            super(itemView);
            image = itemView.findViewById(R.id.ivArt);
            name = itemView.findViewById(R.id.tvCryptoName);
            value = itemView.findViewById(R.id.tvCryptoValue);
            change = itemView.findViewById(R.id.tvCryptoChange);
            // Implement click listener event for the row
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onRowClick((String) itemView.getTag());
                    }
                }
            });
        }
    }

    /**
     * Initialize the dataset of the Adapter
     *
     * @param dataSet containing the data to populate views to be used
     * by RecyclerView
     */
    public CryptoAdapter(List<Crypto> dataSet, RecyclerViewClickListener listener) {
        localDataSet = dataSet;
        localDataSetFiltered = dataSet;
        localListener = listener;

    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public CryptoAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row, parent, false);
        return new MyViewHolder(view, localListener);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull CryptoAdapter.MyViewHolder holder, int position) {
        // Get element from the dataset at this position and replace the
        // contents of the view with that element
        Crypto crypto = localDataSetFiltered.get(position);
        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        holder.name.setText(crypto.getName());
        holder.value.setText(formatter.format(Double.valueOf(crypto.getPriceUsd())));
        holder.change.setText(crypto.getPercentChange1h() + " %");
        Glide.with(holder.itemView)
                .load("https://www.coinlore.com/img/" + crypto.getNameid() + ".png")
                .fitCenter()
                .into(holder.image);
        // Set crypto id as a tag to the view holder
        holder.itemView.setTag(crypto.getId());
    }

    // Return the size of the dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSetFiltered.size();
    }



}
