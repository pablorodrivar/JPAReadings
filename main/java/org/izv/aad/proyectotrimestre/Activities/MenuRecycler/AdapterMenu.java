package org.izv.aad.proyectotrimestre.Activities.MenuRecycler;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.izv.aad.proyectotrimestre.R;

import java.util.List;

public class AdapterMenu extends RecyclerView.Adapter <AdapterMenu.MyViewHolder> implements View.OnClickListener{

    private View.OnClickListener listener;
    private List<String> listAuthors;

    public AdapterMenu(List<String> myArray) { this.listAuthors = myArray; }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView autor;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            autor = itemView.findViewById(R.id.tvautor);
        }
    }

    @NonNull
    @Override
    public AdapterMenu.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i){
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.menu_item, viewGroup, false);
        itemView.setOnClickListener(this);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AdapterMenu.MyViewHolder myViewHolder, int i){
        myViewHolder.autor.setText(listAuthors.get(i));
    }

    @Override
    public int getItemCount() {
        Log.d("longitud count", String.valueOf(listAuthors.size()));
        return listAuthors.size();
    }

    public void setOnClickListener(View.OnClickListener listener){
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        if(listener!=null){
            listener.onClick(view);
        }
    }
}
