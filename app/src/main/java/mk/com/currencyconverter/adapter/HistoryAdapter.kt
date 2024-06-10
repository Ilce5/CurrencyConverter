package mk.com.currencyconverter.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import mk.com.currencyconverter.R
import mk.com.currencyconverter.db.History

class HistoryAdapter(private val historyList: ArrayList<History>): RecyclerView.Adapter<HistoryAdapter.ViewHolderClass>()  {
    var onItemClick: ((History) -> Unit)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return ViewHolderClass(itemView)
    }
    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        val currentItem = historyList[position]
        val inputText = "${currentItem.input} USD"
        val resultText = "${currentItem.result} ${currentItem.currency}"
        holder.input.text = inputText
        holder.result.text = resultText
    }
    override fun getItemCount(): Int {
        return historyList.size
    }
    class ViewHolderClass(itemView: View): RecyclerView.ViewHolder(itemView) {
        val input: TextView = itemView.findViewById(R.id.history_text_input)
        val result: TextView = itemView.findViewById(R.id.history_text_result)
    }
}