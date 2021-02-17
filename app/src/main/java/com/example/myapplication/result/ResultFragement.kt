package com.example.myapplication.result

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.data.datasource.remote.api.RecipeDTO
import com.example.myapplication.data.repository.Repository


class ResultFragement : Fragment() {

    private lateinit var v: View

    internal lateinit var rvResults: RecyclerView
    private lateinit var resultAdapter: ResultAdapter
    private lateinit var autoTextview: AutoCompleteTextView

    private var data1 = mutableListOf<String>("스피너1-1", "스피너1-2", "스피너1-3", "스피너1-4", "스피너1-5", "스피너1-6")

    private var inputTextFromSearchFragment: String? = ""

    private val repository = Repository()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_result, container, false)

        setAutoCompleteTextView()
        setResultRecyclerView()
        setSpinner()
        setButtonSearch()

        return v
    }


    private fun setAutoCompleteTextView() {
        inputTextFromSearchFragment = arguments?.getString("input_search")// arguments로 Bundle 받아옴
        autoTextview = v.findViewById<AutoCompleteTextView>(R.id.actv_recipe_in_result)

        autoTextview.setText(inputTextFromSearchFragment, TextView.BufferType.EDITABLE);
        autoTextview.setTextColor(Color.parseColor("#77000000"))

    }

    private fun setResultRecyclerView() {
        resultAdapter = ResultAdapter()
        resultAdapter.addSampleResult(RecipeDTO.tempResultRecipes(1,"R.drawable.ic_home","간단한 햄버거 만들기",null,null,null,4.3,22))
        resultAdapter.addSampleResult(RecipeDTO.tempResultRecipes(2,"R.drawable.ic_home","간단한 햄버거 만들기",null,null,null,4.3,22))
        resultAdapter.addSampleResult(RecipeDTO.tempResultRecipes(3,"R.drawable.ic_home","간단한 햄버거 만들기",null,null,null,4.3,22))
        resultAdapter.addSampleResult(RecipeDTO.tempResultRecipes(4,"R.drawable.ic_home","간단한 햄버거 만들기",null,null,null,4.3,22))

        rvResults = v.findViewById<RecyclerView>(R.id.rv_result_recipe)
        rvResults.layoutManager =
            LinearLayoutManager(v.context, LinearLayoutManager.VERTICAL, false)
        rvResults.setHasFixedSize(true)
        rvResults.adapter = resultAdapter
    }


    private fun setSpinner() {
        data1.add(0, "제목입니다.")
        var adapter1 = ArrayAdapter(v.context, android.R.layout.simple_spinner_dropdown_item, data1)

        val spinner1 = v.findViewById<Spinner>(R.id.spinner_filter1)
        spinner1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when(position) {
                    0 -> {
                        view?.setBackgroundColor(Color.LTGRAY)
                    }
                    1 -> {

                    }
                    else -> {

                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                spinner1.prompt = "Select your favorite Planet!"
            }
        }
        spinner1.adapter = adapter1
    }

    private fun setButtonSearch() {
        val btnSearch = v.findViewById<Button>(R.id.btn_search)
        btnSearch.setOnClickListener {

            repository.saveSearch(autoTextview.text.toString()) // 검색어 저장

            val bundle = Bundle()
            bundle.putString("input_search",autoTextview.text.toString())

            val activity = v.context as AppCompatActivity
            val transaction = activity.supportFragmentManager.beginTransaction()
            val resultFragment: Fragment = ResultFragement()
            resultFragment.arguments = bundle

            transaction.replace(R.id.fl_container, resultFragment)
            transaction.addToBackStack(null)
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            transaction.commit()
        }
    }

}