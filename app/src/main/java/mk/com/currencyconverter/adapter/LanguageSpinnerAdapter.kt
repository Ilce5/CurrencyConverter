package mk.com.currencyconverter.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import mk.com.currencyconverter.R
import mk.com.currencyconverter.db.LanguageItem

class LanguageSpinnerAdapter(
    context: Context,
    private val languages: List<LanguageItem>
) : ArrayAdapter<LanguageItem>(context, 0, languages) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createView(position, convertView, parent)
    }

    private fun createView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(
            R.layout.item_language_spinner, parent, false
        )

        val languageItem = getItem(position)
        view.findViewById<ImageView>(R.id.language_image).setImageResource(languageItem!!.imageResId)
        view.findViewById<TextView>(R.id.language_text).text = languageItem.language

        return view
    }
}