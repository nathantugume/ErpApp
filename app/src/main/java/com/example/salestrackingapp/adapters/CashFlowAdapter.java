package com.example.salestrackingapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.salestrackingapp.Classes.CashFlowItem;
import com.example.salestrackingapp.R;
import java.util.List;

public class CashFlowAdapter extends RecyclerView.Adapter<CashFlowAdapter.CashFlowViewHolder> {

    private List<CashFlowItem> cashFlowItemList;

    public CashFlowAdapter(List<CashFlowItem> cashFlowItemList) {
        this.cashFlowItemList = cashFlowItemList;
    }

    @NonNull
    @Override
    public CashFlowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cash_flow, parent, false);
        return new CashFlowViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CashFlowViewHolder holder, int position) {
        CashFlowItem cashFlowItem = cashFlowItemList.get(position);

        holder.dateTextView.setText(cashFlowItem.getDate());
        holder.cashFlowAmountTextView.setText(String.valueOf(cashFlowItem.getCashFlowAmount()));
        holder.descTextView.setText(cashFlowItem.getPaymentDec());
    }

    @Override
    public int getItemCount() {
        return cashFlowItemList.size();
    }

    public void setData(List<CashFlowItem> newData) {
        cashFlowItemList.clear();
        cashFlowItemList.addAll(newData);
    }

    public class CashFlowViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView;
        TextView cashFlowAmountTextView;
        TextView descTextView;

        public CashFlowViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            cashFlowAmountTextView = itemView.findViewById(R.id.cashFlowAmountTextView);
            descTextView = itemView.findViewById(R.id.descAmountTextView);
        }
    }
}
