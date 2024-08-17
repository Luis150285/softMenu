package com.example.softmenu.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.softmenu.R;
import com.example.softmenu.ui.dashboard.DashboardFragment.Comida;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private ComidaAdapter comidaAdapter;
    private List<Comida> comidaList;
    private DatabaseReference databaseReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        // Inicializa RecyclerView
        recyclerView = root.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        comidaList = new ArrayList<>();
        comidaAdapter = new ComidaAdapter(comidaList, getContext());  // Ahora se pasa el contexto junto con la lista
        recyclerView.setAdapter(comidaAdapter);

        // Inicializa la referencia a Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("comidas");

        // Carga los datos desde Firebase
        loadDataFromFirebase();

        return root;
    }

    private void loadDataFromFirebase() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                comidaList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Comida comida = dataSnapshot.getValue(Comida.class);
                    comidaList.add(comida);
                }
                comidaAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Manejar errores aquí si es necesario
            }
        });
    }
}
