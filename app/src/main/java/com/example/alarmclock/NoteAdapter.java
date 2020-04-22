package com.example.alarmclock;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {
    private Context context;
    private List<Note> notes;
    private OnItemSelected onItemSelected;

    public NoteAdapter(Context context, OnItemSelected onItemSelected) {
        this.context = context;
        this.onItemSelected = onItemSelected;
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false);
        return new ViewHolder(view, onItemSelected);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Note note = notes.get(position);
        holder.tvTime.setText(note.getTime());
        holder.tvTitle.setText(note.getMessage());
        holder.scState.setChecked(note.isState() ? true : false);
    }

    @Override
    public int getItemCount() {
        return notes == null ? 0 : notes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView tvTime, tvTitle;
        SwitchCompat scState;
        OnItemSelected onItemSelected;
        public ViewHolder(@NonNull View itemView, OnItemSelected onItemSelected) {
            super(itemView);
            this.onItemSelected = onItemSelected;
            tvTime = itemView.findViewById(R.id.tvTime);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            scState = itemView.findViewById(R.id.scState);
            scState.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Note note = notes.get(getAdapterPosition());
                    note.setState(isChecked);
                    if (isChecked){
                        AlarmHelper.setAlarm(context, note);
                    }
                    else{
                        AlarmHelper.cancelAlarm(context, note);
                    }
                    DatabaseHelper.getInstance(context).updateNote(note);
                }
            });
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            this.onItemSelected.onItemClickListener(notes.get(getAdapterPosition()));
        }

        @Override
        public boolean onLongClick(View v) {
            this.onItemSelected.onItemLongClickListener(notes.get(getAdapterPosition()));
            return true;
        }
    }

    public interface OnItemSelected{
        void onItemClickListener(Note note);
        void onItemLongClickListener(Note note);
    }
}
