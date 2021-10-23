package com.carthik.whats_app_share

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.PackageManager.NameNotFoundException
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import androidx.annotation.NonNull
import androidx.core.content.FileProvider

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import java.io.File
import java.util.*

/** WhatsAppSharePlugin */
class WhatsAppSharePlugin : FlutterPlugin, MethodCallHandler {
    var context: Context? = null
    var methodChannel: MethodChannel? = null

    fun WhatsappShare() {}

    /** Plugin registration.  */
    fun registerWith(registrar: io.flutter.plugin.common.PluginRegistry.Registrar) {
        val instance: com.carthik.whats_app_share.WhatsAppSharePlugin = com.carthik.whats_app_share.WhatsAppSharePlugin()
        instance.onAttachedToEngine(registrar.context(), registrar.messenger())
    }

    override fun onAttachedToEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        onAttachedToEngine(binding.applicationContext, binding.binaryMessenger)
    }

    open fun onAttachedToEngine(applicationContext: Context, messenger: BinaryMessenger) {
        this.context = applicationContext
        methodChannel = MethodChannel(messenger, "whats_app_share")
        methodChannel?.setMethodCallHandler(this)
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        context = null
        methodChannel?.setMethodCallHandler(null)
        methodChannel = null
    }

    override fun onMethodCall(call: MethodCall, result: Result) {
        when {
            call.method == "shareFile" -> {
                shareFile(call, result)
            }
            call.method == "share" -> {
                share(call, result)
            }
            call.method == "isInstalled" -> {
                isInstalled(call, result)
            }
            else -> {
                result.notImplemented()
            }
        }
    }

    open fun isPackageInstalled(packageName: String, packageManager: PackageManager): Boolean {
        return try {
            packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: NameNotFoundException) {
            false
        }
    }

    open fun isInstalled(call: MethodCall, result: Result) {
        try {
            val packageName: String? = call.argument("package")
            if (packageName == null || packageName.isEmpty()) {
                Log.println(Log.ERROR, "", "FlutterShare Error: Package name null or empty")
                result.error("FlutterShare:Package name cannot be null or empty", null, null)
                return
            }
            val pm = context!!.packageManager
            val isInstalled = isPackageInstalled(packageName, pm)
            result.success(isInstalled)
        } catch (ex: Exception) {
            Log.println(Log.ERROR, "", "FlutterShare: Error")
            result.error(ex.message, null, null)
        }
    }

    open fun share(call: MethodCall, result: Result) {
        try {
            val title: String? = call.argument("title")
            val text: String? = call.argument("text")
            val linkUrl: String? = call.argument("linkUrl")
            val chooserTitle: String? = call.argument("chooserTitle")
            val phone: String? = call.argument("phone")
            val packageName: String? = call.argument("package")
            if (title == null || title.isEmpty()) {
                Log.println(Log.ERROR, "", "FlutterShare Error: Title null or empty")
                result.error("FlutterShare: Title cannot be null or empty", null, null)
                return
            } else if (phone == null || phone.isEmpty()) {
                Log.println(Log.ERROR, "", "FlutterShare Error: phone null or empty")
                result.error("FlutterShare: phone cannot be null or empty", null, null)
                return
            } else if (packageName == null || packageName.isEmpty()) {
                Log.println(Log.ERROR, "", "FlutterShare Error: Package name null or empty")
                result.error("FlutterShare:Package name cannot be null or empty", null, null)
                return
            }
            val extraTextList = ArrayList<String?>()
            if (text != null && !text.isEmpty()) {
                extraTextList.add(text)
            }
            if (linkUrl != null && !linkUrl.isEmpty()) {
                extraTextList.add(linkUrl)
            }
            var extraText: String? = ""
            if (!extraTextList.isEmpty()) {
                extraText = TextUtils.join("\n\n", extraTextList)
            }
            val intent = Intent()
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.action = Intent.ACTION_SEND
            intent.type = "text/plain"
            intent.setPackage(packageName)
            intent.putExtra("jid", "$phone@s.whatsapp.net")
            intent.putExtra(Intent.EXTRA_SUBJECT, title)
            intent.putExtra(Intent.EXTRA_TEXT, extraText)

            //Intent chooserIntent = Intent.createChooser(intent, chooserTitle);
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context!!.startActivity(intent)
            result.success(true)
        } catch (ex: Exception) {
            Log.println(Log.ERROR, "", "FlutterShare: Error")
            result.error(ex.message, null, null)
        }
    }

    open fun shareFile(call: MethodCall, result: Result) {
        var filePaths = ArrayList<String?>()
        val files = ArrayList<Uri>()
        try {
            val title: String? = call.argument("title")
            val text: String? = call.argument("text")
            filePaths = call.argument("filePath")!!
            val chooserTitle: String? = call.argument("chooserTitle")
            val phone: String? = call.argument("phone")
            val packageName: String? = call.argument("package")
            if (filePaths.isEmpty()) {
                Log.println(Log.ERROR, "", "FlutterShare: ShareLocalFile Error: filePath null or empty")
                result.error("FlutterShare: FilePath cannot be null or empty", null, null)
                return
            } else if (phone == null || phone.isEmpty()) {
                Log.println(Log.ERROR, "", "FlutterShare Error: phone null or empty")
                result.error("FlutterShare: phone cannot be null or empty", null, null)
                return
            } else if (packageName == null || packageName.isEmpty()) {
                Log.println(Log.ERROR, "", "FlutterShare Error: Package name null or empty")
                result.error("FlutterShare:Package name cannot be null or empty", null, null)
                return
            }
            for (i in filePaths.indices) {
                val file = File(filePaths[i]!!)
                val fileUri: Uri? = context?.let { FileProvider.getUriForFile(it, context!!.applicationContext.packageName + ".provider", file) }
                if (fileUri != null) {
                    files.add(fileUri)
                }
            }
            val intent = Intent()
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.action = Intent.ACTION_SEND_MULTIPLE
            intent.type = "*/*"
            intent.setPackage(packageName)
            intent.putExtra("jid", "$phone@s.whatsapp.net")
            intent.putExtra(Intent.EXTRA_SUBJECT, title)
            intent.putExtra(Intent.EXTRA_TEXT, text)
            intent.putExtra(Intent.EXTRA_STREAM, files)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

            //Intent chooserIntent = Intent.createChooser(intent, chooserTitle);
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context!!.startActivity(intent)
            result.success(true)
        } catch (ex: Exception) {
            result.error(ex.message, null, null)
            Log.println(Log.ERROR, "", "FlutterShare: Error")
        }
    }
}