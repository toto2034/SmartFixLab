package it.unisa.smartfixlab.ui.transform;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Locale;

import it.unisa.smartfixlab.R;
import it.unisa.smartfixlab.ui.bean.Accessories;
import it.unisa.smartfixlab.ui.bean.ComputerDevice;
import it.unisa.smartfixlab.ui.bean.Device;
import it.unisa.smartfixlab.ui.bean.PhoneDevice;
import it.unisa.smartfixlab.ui.bean.Stato;
import it.unisa.smartfixlab.ui.bean.TabletDevice;

/**
 * Adapter per la RecyclerView che mostra la lista dei dispositivi
 * con calcolo del profitto (prezzoRivendita - prezzoAcquisto).
 */
public class DeviceAdapter extends ListAdapter<Device, DeviceAdapter.DeviceViewHolder> {

    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onEditClick(Device device);
        void onDeleteClick(Device device);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public DeviceAdapter() {
        super(new DiffUtil.ItemCallback<Device>() {
            @Override
            public boolean areItemsTheSame(@NonNull Device oldItem, @NonNull Device newItem) {
                return oldItem.getId() == newItem.getId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull Device oldItem, @NonNull Device newItem) {
                return oldItem.getId() == newItem.getId()
                        && oldItem.getPrezzoAcquisto() == newItem.getPrezzoAcquisto()
                        && oldItem.getPrezzoRivendita() == newItem.getPrezzoRivendita()
                        && (oldItem.getModello() != null && oldItem.getModello().equals(newItem.getModello()));
            }
        });
    }

    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_device, parent, false);
        return new DeviceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder holder, int position) {
        Device device = getItem(position);

        // Modello
        holder.tvModello.setText(device.getModello() != null ? device.getModello() : "—");

        // Marca + Colore + Descrizione (per Accessori)
        String marcaColore = "";
        if (device.getMarca() != null) marcaColore += device.getMarca();
        if (device.getColore() != null && !device.getColore().isEmpty()) {
            marcaColore += " · " + device.getColore();
        }
        if (device instanceof Accessories) {
            Accessories acc = (Accessories) device;
            if (acc.getDescrizione() != null && !acc.getDescrizione().isEmpty()) {
                if (!marcaColore.isEmpty()) marcaColore += " - ";
                marcaColore += acc.getDescrizione();
            }
        }
        holder.tvMarca.setText(marcaColore);

        // Stato con colore e Gradient
        if (device.getStato() != null) {
            holder.tvStato.setText(device.getStato().name().replace("_", " "));
            holder.tvStato.setVisibility(View.VISIBLE);
            
            // Text Color White for better contrast on gradients
            holder.tvStato.setTextColor(Color.WHITE); 
            // Reset badge tint because we use gradient on the main card now, 
            // or maybe we keep badge as semi-transparent white?
            // Actually user asked for "gradient colors for device cards", which implies the WHOLE card.
            // So we set the background of the containerLayout.
            
            // Badge background can be just transparent or semi-transparent
            holder.tvStato.setBackgroundTintList(null);
            holder.tvStato.setBackgroundResource(R.drawable.bg_status_badge); // Ensure this exists or use transparent
            holder.tvStato.setBackgroundColor(Color.parseColor("#40000000")); // Semi-transparent black for badge

            switch (device.getStato()) {
                case DISPONIBILE:
                    holder.clDeviceBackground.setBackgroundResource(R.drawable.gradient_device_available);
                    break;
                case DA_RIPARARE:
                    holder.tvStato.setText(device.getStato().name().replace("_", " "));
                    holder.clDeviceBackground.setBackgroundResource(R.drawable.gradient_device_torepair);
                    break;
                case VENDUTO:
                    holder.tvStato.setText(device.getStato().name().replace("_", " "));
                    holder.clDeviceBackground.setBackgroundResource(R.drawable.gradient_device_sold);
                    break;
                case RIPARAZIONE_CONCLUSA:
                     holder.tvStato.setText("DA VENDERE");
                     holder.clDeviceBackground.setBackgroundResource(R.drawable.gradient_device_available); // Treat as available
                     break;
            }

        } else {
            holder.tvStato.setVisibility(View.GONE);
             // Default background if no status
             holder.clDeviceBackground.setBackgroundColor(Color.WHITE);
        }

        // Prezzi
        if (device.isRiparazione()) {
            holder.tvLabelAcquisto.setVisibility(View.GONE);
            holder.tvPrezzoAcquisto.setVisibility(View.GONE);
            holder.tvLabelVendita.setText("Riparazione");
            holder.tvPrezzoVendita.setText(String.format(Locale.ITALY, "€ %.2f", device.getPrezzoRiparazione()));
        } else {
            holder.tvLabelAcquisto.setVisibility(View.VISIBLE);
            holder.tvPrezzoAcquisto.setVisibility(View.VISIBLE);
            holder.tvLabelAcquisto.setText("Acquisto");
            holder.tvLabelVendita.setText("Vendita");
            holder.tvPrezzoAcquisto.setText(String.format(Locale.ITALY, "€ %.2f", device.getPrezzoAcquisto()));
            holder.tvPrezzoVendita.setText(String.format(Locale.ITALY, "€ %.2f", device.getPrezzoRivendita()));
        }

        // Profitto con colore (verde se positivo, rosso se negativo)
        double profitto = device.getProfitto();
        holder.tvProfitto.setText(String.format(Locale.ITALY, "€ %.2f", profitto));
        if (profitto >= 0) {
            holder.tvProfitto.setTextColor(Color.parseColor("#2E7D32")); // verde
        } else {
            holder.tvProfitto.setTextColor(Color.parseColor("#C62828")); // rosso
        }

        // Memoria
        if (device.getMemoriaGb() > 0) {
            holder.tvMemoria.setText(device.getMemoriaGb() + " GB");
            holder.tvMemoria.setVisibility(View.VISIBLE);
        } else {
            holder.tvMemoria.setVisibility(View.GONE);
        }

        // Icona diversa per tipo di device
        if (device instanceof PhoneDevice) {
            holder.ivIcon.setImageResource(R.drawable.ic_camera_black_24dp);
        } else if (device instanceof TabletDevice) {
            holder.ivIcon.setImageResource(R.drawable.ic_gallery_black_24dp);
        } else if (device instanceof ComputerDevice) {
            holder.ivIcon.setImageResource(R.drawable.ic_settings_black_24dp);
        } else if (device instanceof Accessories) {
            holder.ivIcon.setImageResource(R.drawable.ic_slideshow_black_24dp);
        } else {
            holder.ivIcon.setImageResource(R.drawable.ic_camera_black_24dp);
        }
        
        // Edit Click
        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null && position != RecyclerView.NO_POSITION) {
                listener.onEditClick(getItem(position));
            }
        });

        // Delete Click
        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null && position != RecyclerView.NO_POSITION) {
                listener.onDeleteClick(getItem(position));
            }
        });
    }

    static class DeviceViewHolder extends RecyclerView.ViewHolder {
        final com.google.android.material.card.MaterialCardView cardView;
        final androidx.constraintlayout.widget.ConstraintLayout clDeviceBackground;
        final ImageView ivIcon;
        final TextView tvModello;
        final TextView tvMarca;
        final TextView tvStato;
        final TextView tvLabelAcquisto, tvPrezzoAcquisto;
        final TextView tvLabelVendita, tvPrezzoVendita;
        final TextView tvProfitto;
        final TextView tvMemoria;
        final com.google.android.material.button.MaterialButton btnEdit;
        final com.google.android.material.button.MaterialButton btnDelete;


        
        DeviceViewHolder(@NonNull View itemView) {
            super(itemView);
             if (itemView instanceof com.google.android.material.card.MaterialCardView) {
                cardView = (com.google.android.material.card.MaterialCardView) itemView;
            } else {
                cardView = null; // Fallback se layout cambia
            }
            clDeviceBackground = itemView.findViewById(R.id.cl_device_background);
            ivIcon = itemView.findViewById(R.id.iv_device_icon);

            tvModello = itemView.findViewById(R.id.tv_modello);
            tvMarca = itemView.findViewById(R.id.tv_marca);
            tvStato = itemView.findViewById(R.id.tv_stato);
            tvLabelAcquisto = itemView.findViewById(R.id.tv_label_acquisto);
            tvPrezzoAcquisto = itemView.findViewById(R.id.tv_prezzo_acquisto);
            tvLabelVendita = itemView.findViewById(R.id.tv_label_vendita);
            tvPrezzoVendita = itemView.findViewById(R.id.tv_prezzo_vendita);
            tvProfitto = itemView.findViewById(R.id.tv_profitto);
            tvMemoria = itemView.findViewById(R.id.tv_memoria);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}

