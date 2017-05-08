package cc.icen.vochat.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import cc.icen.vochat.R;
import cc.icen.vochat.activity.InCallActivity;
import cc.icen.vochat.adapter.ContactsAdapter;
import cc.icen.vochat.net.FriendSearcher;
import cc.icen.vochat.utils.Person;


public class ContactsFragment extends Fragment {

    private View view;
    private ListView lv;
    FriendSearcher mFriendSearcher;
    private Person[] fruits = {new Person("Apple", R.mipmap.ic_launcher), new Person("Banana", R.mipmap.ic_launcher),
            new Person("Cherry", R.mipmap.ic_launcher), new Person("Mango", R.mipmap.ic_launcher)};

    private List<Person> personList = new ArrayList<>();

    private ContactsAdapter adapter;

    private SwipeRefreshLayout swipeRefresh;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_contacts, null);
        initPerson();
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ContactsAdapter(personList);
        recyclerView.setAdapter(adapter);
        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshPerson();
            }
        });

        return view;
    }

    private void refreshPerson() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initPerson();
                        adapter.notifyDataSetChanged();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        }).start();
    }

    private void initPerson() {
        personList.clear();
        for (int i = 0; i < 50; i++) {
            Random random = new Random();
            int index = random.nextInt(fruits.length);
            personList.add(fruits[index]);
        }
    }

}
