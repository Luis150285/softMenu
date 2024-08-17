package com.example.softmenu.ui.home;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.softmenu.R;
import com.example.softmenu.ui.dashboard.DashboardFragment.Comida;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class ComidaAdapter extends RecyclerView.Adapter<ComidaAdapter.ComidaViewHolder> {

    private List<Comida> comidaList;
    private Context context;

    public ComidaAdapter(List<Comida> comidaList, Context context) {
        this.comidaList = comidaList;
        this.context = context;
    }

    @NonNull
    @Override
    public ComidaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comida, parent, false);
        return new ComidaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ComidaViewHolder holder, int position) {
        Comida comida = comidaList.get(position);
        holder.textViewTitle.setText(comida.title);
        holder.textViewDescription.setText(comida.description);
        holder.textViewPrice.setText(comida.price);

        holder.buttonDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Eliminar Comida")
                    .setMessage("¿Estás seguro de que quieres eliminar esta comida?")
                    .setPositiveButton("Sí", (dialog, which) -> deleteItem(comida))
                    .setNegativeButton("No", null)
                    .show();
        });

        holder.buttonEdit.setOnClickListener(v -> {
            showEditDialog(comida);
        });
    }

    @Override
    public int getItemCount() {
        return comidaList.size();
    }

    public static class ComidaViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle, textViewDescription, textViewPrice;
        Button buttonDelete, buttonEdit;

        public ComidaViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
            textViewPrice = itemView.findViewById(R.id.textViewPrice);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
            buttonEdit = itemView.findViewById(R.id.buttonEdit);
        }
    }

    private void deleteItem(Comida comida) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("comidas").child(comida.id);
        databaseReference.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(context, "Comida eliminada", Toast.LENGTH_SHORT).show();
                comidaList.remove(comida);
                notifyDataSetChanged();
            } else {
                Toast.makeText(context, "Error al eliminar la comida", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showEditDialog(Comida comida) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Modificar Comida");

        // Layout para el diálogo
        View viewInflated = LayoutInflater.from(context).inflate(R.layout.dialog_edit_comida, (ViewGroup) null, false);
        final EditText inputTitle = viewInflated.findViewById(R.id.editTextTitle);
        final EditText inputDescription = viewInflated.findViewById(R.id.editTextDescription);
        final EditText inputPrice = viewInflated.findViewById(R.id.editTextPrice);

        // Pre-popular campos con datos actuales
        inputTitle.setText(comida.title);
        inputDescription.setText(comida.description);
        inputPrice.setText(comida.price);

        builder.setView(viewInflated);

        // Configurar botones del diálogo
        builder.setPositiveButton("Guardar", (dialog, which) -> {
            comida.title = inputTitle.getText().toString();
            comida.description = inputDescription.getText().toString();
            comida.price = inputPrice.getText().toString();

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("comidas").child(comida.id);
            databaseReference.setValue(comida).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(context, "Comida actualizada", Toast.LENGTH_SHORT).show();
                    notifyDataSetChanged();
                } else {
                    Toast.makeText(context, "Error al actualizar la comida", Toast.LENGTH_SHORT).show();
                }
            });
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

        builder.show();
    }
}
