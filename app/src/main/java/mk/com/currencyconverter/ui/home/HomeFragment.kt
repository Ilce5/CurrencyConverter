package mk.com.currencyconverter.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mk.com.currencyconverter.api.model.response.Currencies
import mk.com.currencyconverter.databinding.FragmentHomeBinding
import mk.com.currencyconverter.db.History
import mk.com.currencyconverter.db.HistoryDatabase
import mk.com.currencyconverter.db.HistoryRepository
import mk.com.sette_clipping.api.service.ApiManager
import java.math.BigDecimal
import java.math.RoundingMode

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val database by lazy { HistoryDatabase.getDatabase(requireContext()) }
    private val repository by lazy { HistoryRepository(database.historyDao()) }
    val firestore = Firebase.firestore

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
        val spinner = binding.selectedCurrencySpinner
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
            adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, currencyKeys)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
            binding.selectedCurrencyInputEditText.hint = "1"
        }

        spinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    selectedCurrency = parent.getItemAtPosition(position).toString()
                    binding.selectedCurrencyInputEditText.hint =
                        currencies!!.conversion_rates[selectedCurrency].toString()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                }
            }

        binding.exchangeButton.setOnClickListener {
            val input = binding.inputCurrencyInputEditText.text.toString()
            val value = input.toDouble() * currencies!!.conversion_rates[selectedCurrency]!!
            resultValue = BigDecimal(value).setScale(2, RoundingMode.HALF_EVEN).toDouble()
            binding.resultCurrencyEditText.setText(resultValue.toString())
            CoroutineScope(Dispatchers.IO).launch {
                repository.insertHistory(History(0, input, resultValue.toString(), selectedCurrency))
            }
            val userHistory = hashMapOf(
                "input" to input,
                "result" to resultValue,
                "currency" to selectedCurrency
            )
            val user = FirebaseAuth.getInstance().currentUser
            if(user != null && user.email != null) {
                firestore.collection("History").document(user.email!!).set(userHistory)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Added to history", Toast.LENGTH_LONG).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(requireContext(), "Error adding document: ${e.message}", Toast.LENGTH_LONG).show()
                    }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
