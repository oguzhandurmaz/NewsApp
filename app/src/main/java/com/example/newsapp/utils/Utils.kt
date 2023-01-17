package com.example.newsapp.utils

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import com.example.newsapp.R

object Utils {

    fun getLoadingDialog(context: Context): Dialog{
        return Dialog(context).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(R.layout.layout_loading)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window?.setDimAmount(0f)
            setCancelable(false)
        }
    }

    fun getSuccessDialog(context: Context): AlertDialog{
        return AlertDialog.Builder(context)
            .setIcon(R.drawable.ic_info)
            .setTitle(R.string.favorite)
            .setMessage(R.string.success_add_favorite)
            .setNeutralButton(android.R.string.ok){dialog,_ ->
                dialog.dismiss()
            }
            .create()
    }
    fun getIdFromTitle(title: String) = title.replace("[^A-Za-z0-9]".toRegex(), "")
}