package mk.com.currencyconverter.ui.home

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mk.com.currencyconverter.api.model.response.Currencies
import mk.com.currencyconverter.databinding.FragmentHomeBinding
import mk.com.currencyconverter.db.History
import mk.com.currencyconverter.db.HistoryDatabase
import mk.com.currencyconverter.db.HistoryRepository
import mk.com.sette_clipping.api.service.ApiManager

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        homeViewModel.text.observe(viewLifecycleOwner) {
            // textView.text = it
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)
        val spinner = _binding!!.selectedCurrencySpinner
        val apiManager = ApiManager()

        var currencies: Currencies? = null
        var currencyKeys: List<String>
        var adapter: ArrayAdapter<String>
        var selectedCurrency = "USD"
        var resultValue: Double

        val database by lazy { HistoryDatabase.getDatabase(requireContext()) }
        val repository by lazy { HistoryRepository(database.historyDao()) }

        CoroutineScope(Dispatchers.Main).launch {
            currencies = apiManager.getLatestCurrencies().body()
            currencyKeys = currencies!!.conversion_rates.keys.toList()
            adapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, currencyKeys)
            adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        spinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    selectedCurrency = parent.getItemAtPosition(position).toString()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                }
            }

        _binding!!.exchangeButton.setOnClickListener {
            val input = _binding!!.inputCurrencyInputEditText.text.toString()
            resultValue = input.toDouble() * currencies!!.conversion_rates[selectedCurrency]!!
            _binding!!.resultCurrencyEditText.setText(resultValue.toString())
            CoroutineScope(Dispatchers.IO).launch {
                repository.insertHistory(History(0, input, resultValue.toString(), selectedCurrency))
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
