package mk.com.currencyconverter.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.room.InvalidationTracker
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import mk.com.currencyconverter.MainActivity
import mk.com.currencyconverter.R
import mk.com.currencyconverter.adapter.LanguageSpinnerAdapter
import mk.com.currencyconverter.databinding.FragmentProfileBinding
import mk.com.currencyconverter.db.DataManager
import mk.com.currencyconverter.db.Language
import mk.com.currencyconverter.db.LanguageItem
import mk.com.currencyconverter.util.IntentKeys

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by activityViewModels()
    lateinit var dataManager: DataManager
    val dbLanguage = MutableLiveData<Language>()
    var selectedLanguage = ""
    val languageItems = listOf(
        LanguageItem(R.drawable.flag_of_north_macedonia, IntentKeys.MACEDONIAN_LANGUAGE),
        LanguageItem(R.drawable.flag_of_united_kingdom, IntentKeys.ENGLISH_LANGUAGE),
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dataManager = DataManager(requireContext())
        val mainActivity = requireActivity() as MainActivity
        val adapter = LanguageSpinnerAdapter(requireContext(), languageItems)
        binding.profileSpinner.adapter = adapter

        CoroutineScope(Dispatchers.IO).launch {
            dataManager.getLanguage().catch { e ->
                e.printStackTrace()
            }.collect {
                dbLanguage.postValue(it)
            }
        }

        dbLanguage.observe(viewLifecycleOwner, Observer {
            if (viewModel.isInitialLanguageAdded.value != true) {
                viewModel.addInitialLanguage(it.language)
            }
            selectedLanguage = it.language
            binding.profileSpinner.setSelection(languageItems.indexOfFirst { item -> item.language == selectedLanguage })
        })
        selectLanguage(mainActivity)

        val user = FirebaseAuth.getInstance().currentUser

        if (user != null && user.displayName != null) {
            binding.userName.text = user.displayName
        }

        if (user != null && user.email != null) {
            binding.email.text = user.email
        }
    }

    private fun selectLanguage(yourActivity: MainActivity) {
        binding.profileSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, position: Int, id: Long
                ) {
                    CoroutineScope(Dispatchers.IO).launch {
                        dataManager.saveLanguage(
                            Language(
                                language = languageItems[position].language
                            )
                        )
                    }
                    if (viewModel.initialLanguage.value != languageItems[position].language) {
                        yourActivity.resetActivity()
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
