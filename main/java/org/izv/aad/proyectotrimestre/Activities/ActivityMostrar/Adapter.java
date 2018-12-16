package org.izv.aad.proyectotrimestre.Activities.ActivityMostrar;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.StorageReference;
import org.izv.aad.proyectotrimestre.POJO.Readings;
import org.izv.aad.proyectotrimestre.R;
import java.util.List;
import static org.izv.aad.proyectotrimestre.Activities.MainMenu.fbc;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    public interface OnItemClickListener {
        void onItemClick(Readings item);
    }

    private List<Readings> items;
    private final OnItemClickListener listener;
    private List<String> nombreAutor;

    public Adapter(List<Readings> items, OnItemClickListener listener,List<String> nombreAutor) {
        this.items = items;
        this.listener = listener;
        this.nombreAutor = nombreAutor;
    }

    @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.mostrar_item, parent, false);
        return new ViewHolder(v,nombreAutor);
    }

    @Override public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(items.get(position), listener,position);
    }

    @Override public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView titulo,autor,comienzo,fin;
        private ImageView portada;
        private RatingBar rating;
        private ProgressBar progress;
        private List<String> nAutor;

        public ViewHolder(View itemView, List<String> nombreAutor) {
            super(itemView);
            this.nAutor = nombreAutor;
            titulo = itemView.findViewById(R.id.tv_mostrar_titulo);
            autor = itemView.findViewById(R.id.tv_mostrar_autor);
            comienzo = itemView.findViewById(R.id.tv_mostrar_comienzo);
            fin = itemView.findViewById(R.id.tv_mostrar_fin);
            portada = itemView.findViewById(R.id.iv_mostrar_portada);
            rating = itemView.findViewById(R.id.rating_mostrar);
            progress = itemView.findViewById(R.id.pb_mostrar);

        }

        public void bind(final Readings item, final OnItemClickListener listener, int position) {
            titulo.setText(item.getTitulo());
            autor.setText(nAutor.get(position));
            if (item.getFecha_comienzo() == null){
                comienzo.setText("Aún no empezado");
            }else comienzo.setText("Inicio: "+item.getFecha_comienzo());
            if (item.getFecha_fin() == null){
                fin.setText("Aún no terminado");
            }else fin.setText("Fin: "+item.getFecha_fin());
            //portada.setImageURI(DBManager.getImageURI(item));
            //progress.setVisibility(View.GONE);
            //Log.v(TAG, DBManager.getImageURI(item) +"");
            StorageReference imageRef = fbc.storageRef.child(item.getDrawable_portada());
            final long ONE_MEGABYTE = 2048 * 2048;
            imageRef.getBytes(ONE_MEGABYTE).addOnCompleteListener(new OnCompleteListener<byte[]>() {
                @Override
                public void onComplete(@NonNull Task<byte[]> task) {
                    if(task.isSuccessful()) {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inMutable = true;
                        Bitmap bmp = BitmapFactory.decodeByteArray(task.getResult(), 0, task.getResult().length, options);
                        portada.setImageBitmap(bmp);
                        progress.setVisibility(View.GONE);
                    }else {
                        portada.setImageResource(R.drawable.ic_no_internet);
                        progress.setVisibility(View.GONE);
                    }
                }
            });
            rating.setRating(item.getValoracion());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }
}