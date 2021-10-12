package tech.bharatx.startup_sdk_flutter

import android.app.Activity
import android.content.Context
import androidx.annotation.NonNull
import androidx.fragment.app.FragmentActivity
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import tech.bharatx.common.*
import tech.bharatx.common.data_classes.CreditInfo
import tech.bharatx.common.data_classes.CreditInfoFull

/** BharatxFlutterStartupPlugin */
class BharatxFlutterStartupPlugin : FlutterPlugin, MethodCallHandler, ActivityAware {
    private val signature = "flutter.bharatx.tech/startup"
    private lateinit var channel: MethodChannel
    private lateinit var applicationContext: Context
    private lateinit var binaryMessenger: BinaryMessenger
    private var activity: FragmentActivity? = null

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        this.binaryMessenger = flutterPluginBinding.binaryMessenger
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, signature)
        channel.setMethodCallHandler(this)
        applicationContext = flutterPluginBinding.applicationContext
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull methodResult: Result) {
        when (call.method) {
            "registerUser" -> {
                val userManager = BharatXUserManager(applicationContext)
                    .id(call.argument<String>("id")!!)
                    .phoneNumber(call.argument<String>("phoneNumber")!!)
                    .name(call.argument("name"))
                    .gender(call.argument("gender"))
                    .age(call.argument("age"))
                    .address(call.argument("address"))
                if (call.hasArgument("dob") && call.hasArgument("dobFormat")) {
                    userManager.dob(
                        call.argument("dob"),
                        call.argument("dobFormat")
                    )
                }
                val namedKeys = arrayListOf(
                    "id",
                    "phoneNumber",
                    "name",
                    "gender",
                    "dob",
                    "age",
                    "address"
                )
                val extraKeys =
                    HashSet((call.arguments as HashMap<*, *>).keys)
                extraKeys.removeAll(namedKeys)

                for (key in extraKeys) {
                    if (key is String) {
                        userManager.prop(key, call.argument(key))
                    }
                }
                userManager.register()
                methodResult.success(null)
            }
            "displayBharatXProgressDialog" -> {
                BharatXUiManager.displayBharatXProgressDialog(activity!!)
                methodResult.success(null)
            }
            "closeBharatXProgressDialog" -> {
                BharatXUiManager.closeBharatXProgressDialog()
                methodResult.success(null)
            }
            "getUserCreditInfo" -> {
                CreditAccessManager.getUserCreditInfo(
                    activity!!,
                    object : CreditAccessManager.OnCompleteListener<CreditInfo> {
                        override fun onComplete(result: CreditInfo) {
                            methodResult.success(
                                hashMapOf(
                                    "creditTaken" to result.creditTaken,
                                    "creditLimit" to result.creditLimit
                                )
                            )
                        }
                    })
            }
            "getUserCreditInfoFull" -> {
                CreditAccessManager.getUserCreditInfoFull(
                    activity!!,
                    object : CreditAccessManager.OnCompleteListener<CreditInfoFull> {
                        override fun onComplete(result: CreditInfoFull) {
                            methodResult.success(
                                hashMapOf(
                                    "creditTaken" to result.creditTaken,
                                    "creditLimit" to result.creditLimit,
                                    "totalOutstandingAmount" to result.totalOutstandingAmount,
                                    "dueAmount" to result.dueAmount,
                                    "currentCycleDueDate" to result.currentCycleDueDate,
                                    "repaymentLink" to result.repaymentLink
                                )
                            )
                        }
                    })
            }
            "confirmTransactionWithUser" -> {
                val confirmTransactionWithUserChannel =
                    MethodChannel(binaryMessenger, "${signature}/confirmTransactionWithUser")
                BharatXTransactionManager.confirmTransactionWithUser(
                    activity ?: applicationContext,
                    call.argument("amountInPaise")!!,
                    call.argument("transactionId")!!,
                    object : BharatXTransactionManager.TransactionStatusListener() {
                        override fun onSuccess() {
                            confirmTransactionWithUserChannel.invokeMethod("onSuccess", null)
                        }

                        override fun onFailure(transactionFailureReason: BharatXTransactionManager.TransactionFailureReason) {
                            confirmTransactionWithUserChannel.invokeMethod(
                                "onFailure",
                                transactionFailureReason.getSerializedNameFromEnum()
                            )
                        }
                    }
                )
                methodResult.success(null)
            }
            else -> {
                methodResult.notImplemented()
            }
        }
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }

    private fun onActivityChange(activity: Activity) {
        this.activity = activity as FragmentActivity
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        onActivityChange(binding.activity)
    }

    override fun onDetachedFromActivity() {
        activity = null
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        onActivityChange(binding.activity)
    }

    override fun onDetachedFromActivityForConfigChanges() {
        activity = null
    }
}
