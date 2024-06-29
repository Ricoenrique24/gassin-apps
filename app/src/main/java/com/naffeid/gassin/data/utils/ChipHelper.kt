package com.naffeid.gassin.data.utils

import android.content.Context
import androidx.core.content.ContextCompat
import com.google.android.material.chip.Chip
import com.naffeid.gassin.R

class ChipHelper(private val context: Context) {

    fun setupStatusChips(
        chips: List<Chip>,
        initialStatus: String = "all",
        onStatusSelected: (String) -> Unit
    ) {
        for (chip in chips) {
            chip.setOnClickListener {
                selectChip(chip, chips)
                chip.tag?.let { tag ->
                    if (tag is String) {
                        onStatusSelected(tag)
                    } else {
                        // Handle unexpected tag type if needed
                    }
                }
            }
        }
        // Set initial selected chip
        selectInitialChip(initialStatus, chips)
    }

    private fun selectInitialChip(initialStatus: String, chips: List<Chip>) {
        for (chip in chips) {
            if (chip.tag == initialStatus) {
                selectChip(chip, chips)
                break
            }
        }
    }

    private fun selectChip(selectedChip: Chip, chips: List<Chip>) {
        for (chip in chips) {
            chip.isChecked = chip == selectedChip
            if (chip.isChecked) {
                chip.setChipBackgroundColorResource(R.color.darkGreen)
                chip.setTextColor(ContextCompat.getColorStateList(context, R.color.white))
            } else {
                chip.setChipBackgroundColorResource(R.color.greyLight)
                chip.setTextColor(ContextCompat.getColorStateList(context, R.color.black))
            }
        }
    }
}
