package com.naffeid.gassin.data.utils

import android.content.Context
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.radiobutton.MaterialRadioButton
import com.naffeid.gassin.R

class TimeFilterHelper(private val context: Context) {

    var filterBy: String = "all" // Default filter

    fun showFilterBottomSheet(onFilterSelected: (String) -> Unit) {
        val bottomSheet = BottomSheetDialog(context)
        val view = View.inflate(context, R.layout.time_filter_bottom_sheet, null)
        bottomSheet.setContentView(view)

        view.findViewById<MaterialRadioButton>(R.id.radioAll).setOnClickListener {
            filterBy = "all"
            onFilterSelected(filterBy)
            bottomSheet.dismiss()
        }

        view.findViewById<MaterialRadioButton>(R.id.radioDay).setOnClickListener {
            filterBy = "day"
            onFilterSelected(filterBy)
            bottomSheet.dismiss()
        }

        view.findViewById<MaterialRadioButton>(R.id.radioWeek).setOnClickListener {
            filterBy = "week"
            onFilterSelected(filterBy)
            bottomSheet.dismiss()
        }

        view.findViewById<MaterialRadioButton>(R.id.radioMonth).setOnClickListener {
            filterBy = "month"
            onFilterSelected(filterBy)
            bottomSheet.dismiss()
        }

        bottomSheet.show()
    }
}
