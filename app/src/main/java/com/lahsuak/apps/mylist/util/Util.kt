package com.lahsuak.apps.mylist.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.Toast
import com.lahsuak.apps.mylist.BuildConfig
import com.lahsuak.apps.mylist.R

object Util {
    //settings methods
    fun moreApp(context: Context) {
        try {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(context.getString(R.string.market_string))
                )
            )
        } catch (e: ActivityNotFoundException) {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(context.getString(R.string.market_developer_string))
                )
            )
        }
    }

    fun shareApp(context: Context) {
        try {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_SUBJECT, "Share this App")
            val shareMsg =
                context.getString(R.string.play_store_share) + BuildConfig.APPLICATION_ID + "\n\n"
            intent.putExtra(Intent.EXTRA_TEXT, shareMsg)
            context.startActivity(Intent.createChooser(intent, "Share by"))
        } catch (e: Exception) {
            notifyUser(
                context,
                "Some thing went wrong!!"
            )
        }
    }

    fun sendFeedbackMail(context: Context) {
        try {
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:") // only email apps should handle this
                putExtra(Intent.EXTRA_EMAIL, arrayOf(context.getString(R.string.feedback_email)))
                val info = Build.MODEL + "," + Build.MANUFACTURER
                putExtra(Intent.EXTRA_TEXT, "Please write your suggestions or issues")
                putExtra(Intent.EXTRA_SUBJECT, "Feedback from FlashLight, $info")
            }
            context.startActivity(emailIntent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    fun appRating(context: Context) {
        val uri = Uri.parse("market://details?id=" + context.packageName)
        val goToMarket = Intent(Intent.ACTION_VIEW, uri)
        try {
            context.startActivity(goToMarket)
        } catch (e: ActivityNotFoundException) {
            notifyUser(context, "Sorry for inconvenience")
        }
    }

    fun notifyUser(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}