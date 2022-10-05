package com.example.myapplication.Fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;

import java.util.ArrayList;

public class ChatsFragment2 extends Fragment {

    ArrayAdapter<String> arrayAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ArrayList<String> arrayList = new ArrayList<>();

        arrayList.add("Or Bukai");
        arrayList.add("Haim Bukai");
        arrayList.add("Oger Shrem");
        arrayList.add("Shuki Bukai");
        arrayList.add("Amit Aboudi");
        arrayList.add("Dolev Haziza");
        arrayList.add("Shuk Hashmal");
        arrayList.add("Muki");
        arrayList.add("Sima Shrem");
        arrayList.add("Roni Shrem");
        arrayList.add("Haim Fiora");
        arrayList.add("Haim Kayle");
        arrayList.add("Haim Sett");
        arrayList.add("Haimon");
        arrayList.add("Hidan Haminai");
        arrayList.add("Idan");
        arrayList.add("Ramesses");
        arrayList.add("Hezion");

        View view = inflater.inflate(R.layout.fragment_chats_2, container, false);

        SearchView searchView = view.findViewById(R.id.search_view);
        ListView listView = view.findViewById(R.id.list_view);

        arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getContext(), "" + parent.getItemIdAtPosition(position), Toast.LENGTH_SHORT).show();
            }
        });

        listView.setEmptyView(view.findViewById(R.id.no_results_tv));

        listView.setVisibility(View.GONE);

        searchView.setQueryHint("Search");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                ChatsFragment2.this.arrayAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String text = newText;
                arrayAdapter.getFilter().filter(text);
                if (TextUtils.isEmpty(text)) {
                    listView.setVisibility(View.GONE);
                } else {
                    listView.setVisibility(View.VISIBLE);
                }
                return true;
            }
        });

        // TODO: hide completely listview upon deleting previous search result
//
//        searchView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                searchView.onActionViewExpanded();
//                Toast.makeText(getContext(), "HEZI", Toast.LENGTH_SHORT).show();
//
//            }
//        });
        return view;
    }

//    @Override
//    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
//        inflater.inflate(R.menu.search_menu_item, menu);
//        MenuItem searchItem = menu.findItem(R.id.search_item);
//        SearchView searchView = (SearchView) searchItem.getActionView();
//        searchView.setQueryHint("Search something!");
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                arrayAdapter.getFilter().filter(newText);
//                return true;
//            }
//        });
//        super.onCreateOptionsMenu(menu, inflater);
//    }
}
