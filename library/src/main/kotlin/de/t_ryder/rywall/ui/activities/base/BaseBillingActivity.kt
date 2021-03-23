package de.t_ryder.rywall.ui.activities.base

import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AlertDialog
import com.android.billingclient.api.SkuDetails
import de.t_ryder.rywall.R
import de.t_ryder.rywall.data.Preferences
import de.t_ryder.rywall.data.listeners.BillingProcessesListener
import de.t_ryder.rywall.data.models.CleanSkuDetails
import de.t_ryder.rywall.data.models.DetailedPurchaseRecord
import de.t_ryder.rywall.data.viewmodels.BillingViewModel
import de.t_ryder.rywall.extensions.context.firstInstallTime
import de.t_ryder.rywall.extensions.context.getAppName
import de.t_ryder.rywall.extensions.context.string
import de.t_ryder.rywall.extensions.context.stringArray
import de.t_ryder.rywall.extensions.fragments.mdDialog
import de.t_ryder.rywall.extensions.fragments.message
import de.t_ryder.rywall.extensions.fragments.negativeButton
import de.t_ryder.rywall.extensions.fragments.positiveButton
import de.t_ryder.rywall.extensions.fragments.singleChoiceItems
import de.t_ryder.rywall.extensions.fragments.title
import de.t_ryder.rywall.extensions.resources.hasContent
import de.t_ryder.rywall.extensions.utils.lazyViewModel
import de.t_ryder.rywall.ui.fragments.viewer.IndeterminateProgressDialog

@Suppress("MemberVisibilityCanBePrivate")
abstract class BaseBillingActivity<out P : Preferences> : BaseLicenseCheckerActivity<P>(),
    BillingProcessesListener {

    val billingViewModel: BillingViewModel by lazyViewModel()
    val isBillingClientReady: Boolean
        get() = billingEnabled && billingViewModel.isBillingClientReady

    private val billingLoadingDialog: IndeterminateProgressDialog by lazy { IndeterminateProgressDialog.create() }
    private var purchasesDialog: AlertDialog? = null

    open val billingEnabled: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (billingEnabled) {
            billingViewModel.billingProcessesListener = this
            billingViewModel.observe(this)
            billingViewModel.initialize()
        }
    }

    override fun onResume() {
        super.onResume()
        if (preferences.isFirstRun && firstInstallTime > 10000) {
            preferences.isFirstRun = false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val created = super.onCreateOptionsMenu(menu)
        menu?.findItem(R.id.donate)?.isVisible =
            isBillingClientReady && getDonationItemsIds().isNotEmpty()
        return created
    }

    private fun dismissDialogs() {
        try {
            billingLoadingDialog.dismiss()
        } catch (e: Exception) {
        }
        try {
            purchasesDialog?.dismiss()
        } catch (e: Exception) {
        }
        purchasesDialog = null
    }

    override fun onDestroy() {
        super.onDestroy()
        dismissDialogs()
        billingViewModel.destroy(this)
    }

    fun showDonationsDialog() {
        if (!isBillingClientReady) {
            onSkuPurchaseError()
            return
        }
        val skuDetailsList = billingViewModel.inAppSkuDetails.map { CleanSkuDetails(it) }
            .filter { getDonationItemsIds().contains(it.originalDetails.sku) }
        if (skuDetailsList.isEmpty()) {
            onSkuPurchaseError()
            return
        }
        dismissDialogs()
        purchasesDialog = mdDialog {
            title(R.string.donate)
            singleChoiceItems(skuDetailsList, 0)
            negativeButton(android.R.string.cancel)
            positiveButton(R.string.donate) { dialog ->
                val listView = (dialog as? AlertDialog)?.listView
                if ((listView?.checkedItemCount ?: 0) > 0) {
                    val checkedItemPosition = listView?.checkedItemPosition ?: -1
                    billingViewModel.launchBillingFlow(
                        this@BaseBillingActivity,
                        skuDetailsList[checkedItemPosition].originalDetails
                    )
                }
                dialog.dismiss()
            }
        }
        purchasesDialog?.show()
    }

    override fun onSkuPurchaseSuccess(purchase: DetailedPurchaseRecord?) {
        dismissDialogs()
        purchasesDialog = mdDialog {
            title(R.string.donate_success_title)
            message(string(R.string.donate_success_content, getAppName()))
            positiveButton(android.R.string.ok)
        }
        purchasesDialog?.show()
    }

    override fun onSkuPurchaseError(purchase: DetailedPurchaseRecord?) {
        dismissDialogs()
        purchasesDialog = mdDialog {
            title(R.string.error)
            message(string(R.string.unexpected_error_occurred))
        }
        purchasesDialog?.show()
    }

    override fun onBillingClientReady() {
        super.onBillingClientReady()
        invalidateOptionsMenu()
        val inAppItems =
            ArrayList(getDonationItemsIds()).apply { addAll(getInAppPurchasesItemsIds()) }
        billingViewModel.queryInAppSkuDetailsList(inAppItems)
        billingViewModel.querySubscriptionsSkuDetailsList(getSubscriptionsItemsIds())
    }

    override fun onInAppSkuDetailsListUpdated(skuDetailsList: List<SkuDetails>) {
        super.onInAppSkuDetailsListUpdated(skuDetailsList)
        invalidateOptionsMenu()
    }

    override fun onSubscriptionsSkuDetailsListUpdated(skuDetailsList: List<SkuDetails>) {
        super.onSubscriptionsSkuDetailsListUpdated(skuDetailsList)
        invalidateOptionsMenu()
    }

    override fun onBillingClientDisconnected() {
        super.onBillingClientDisconnected()
        invalidateOptionsMenu()
    }

    open fun getDonationItemsIds(): List<String> = try {
        stringArray(R.array.donation_items).filter { it.hasContent() }
    } catch (e: Exception) {
        listOf()
    }

    open fun getInAppPurchasesItemsIds(): List<String> = listOf()
    open fun getSubscriptionsItemsIds(): List<String> = listOf()
}
