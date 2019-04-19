package com.example.final_bitm;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by saran on 1/2/2018.
 */

public class InstructionAdapter extends RecyclerView.Adapter<InstructionAdapter.InstructionViewHolder> {
    private Context MyContext;
    private ArrayList<Instruction> instructions;


    public InstructionAdapter(Context myContext, ArrayList<Instruction> instructions) {
        MyContext = myContext;
        this.instructions=instructions;
    }


    @Override
    public InstructionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(MyContext).inflate(R.layout.instruction_single_row,parent,false);
        return new InstructionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(InstructionViewHolder holder, int position) {
        Instruction instruction=instructions.get(position);
        String ins=(position+1)+". "+instruction.getInstruction();
        holder.instructionTV.setText(Html.fromHtml(ins));
    }

    @Override
    public int getItemCount() {
        return instructions.size();
    }

    public class InstructionViewHolder extends RecyclerView.ViewHolder {
        TextView instructionTV;
        public InstructionViewHolder(View itemView) {
            super(itemView);
            instructionTV=itemView.findViewById(R.id.instructionTV);
        }
    }

    public void Update(ArrayList<Instruction> instructions){
        this.instructions=instructions;
        notifyDataSetChanged();
    }
}
