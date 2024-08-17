package com.example.softmenu.ui.dashboard;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.softmenu.R;
import com.example.softmenu.databinding.FragmentDashboardBinding;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DashboardFragment extends Fragment {

    private EditText editTextTitle, editTextDescription, editTextPrice;
    private Button buttonSave;
    private DatabaseReference databaseReference;

    private FragmentDashboardBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        // Inicializar los elementos de la vista
        editTextTitle = root.findViewById(R.id.editTextTitle);
        editTextDescription = root.findViewById(R.id.editTextDescription);
        editTextPrice = root.findViewById(R.id.editTextPrice);
        buttonSave = root.findViewById(R.id.buttonSave);

        // Inicializar la referencia a Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("comidas");

        // Configurar el listener para el botón Guardar
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDataToFirebase();
            }
        });

        return root;
    }

    private void saveDataToFirebase() {
        Log.d("Firebase", "Intentando guardar los datos...");

        String title = editTextTitle.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        String price = editTextPrice.getText().toString().trim();

        // Validar que los campos no estén vacíos
        if (title.isEmpty() || description.isEmpty() || price.isEmpty()) {
            Log.d("Firebase", "Campos vacíos, no se puede guardar.");
            Toast.makeText(getActivity(), "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear un ID único para el nuevo elemento
        String id = databaseReference.push().getKey();

        // Crear un objeto para la comida
        Comida comida = new Comida(id, title, description, price);

        // Guardar los datos en Firebase
        databaseReference.child(id).setValue(comida)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("Firebase", "Datos guardados correctamente.");
                        Toast.makeText(getActivity(), "Datos guardados correctamente", Toast.LENGTH_SHORT).show();
                        // Limpiar los campos después de guardar
                        editTextTitle.setText("");
                        editTextDescription.setText("");
                        editTextPrice.setText("");
                    } else {
                        Log.d("Firebase", "Error al guardar los datos.");
                        Toast.makeText(getActivity(), "Error al guardar los datos", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.d("Firebase", "Error: " + e.getMessage());
                    Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    public static class Comida {
        public String id;
        public String title;
        public String description;
        public String price;

        public Comida() {
            // Constructor vacío requerido por Firebase
        }

        public Comida(String id, String title, String description, String price) {
            this.id = id;
            this.title = title;
            this.description = description;
            this.price = price;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}