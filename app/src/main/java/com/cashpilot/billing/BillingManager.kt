package com.cashpilot.billing

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.cashpilot.data.preferences.UserPreferencesRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import javax.inject.Inject
import javax.inject.Singleton

/** ID del producto "Quitar anuncios" en Play Console. Debe coincidir con el creado en tu app. */
private const val PRODUCT_REMOVE_ADS = "remove_ads"

@Singleton
class BillingManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userPreferences: UserPreferencesRepository
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val _purchaseResult = MutableSharedFlow<PurchaseResult>(replay = 0)
    val purchaseResult: SharedFlow<PurchaseResult> = _purchaseResult

    private val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
        if (billingResult.responseCode != BillingClient.BillingResponseCode.OK) {
            scope.launch {
                _purchaseResult.emit(
                    PurchaseResult.Error(billingResult.debugMessage ?: "Error de facturación")
                )
            }
            return@PurchasesUpdatedListener
        }
        purchases?.forEach { purchase ->
            if (purchase.products.contains(PRODUCT_REMOVE_ADS)) {
                scope.launch {
                    handlePurchase(purchase)
                }
            }
        }
    }

    private val billingClient: BillingClient = BillingClient.newBuilder(context)
        .setListener(purchasesUpdatedListener)
        .enablePendingPurchases()
        .build()

    init {
        connectAndQueryPurchases()
    }

    private fun connectAndQueryPurchases() {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    queryPurchases()
                }
            }

            override fun onBillingServiceDisconnected() {
                // Reconexión opcional; por ahora no hacemos nada
            }
        })
    }

    private fun queryPurchases() {
        billingClient.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        ) { billingResult, purchases ->
            if (billingResult.responseCode != BillingClient.BillingResponseCode.OK) return@queryPurchasesAsync
            purchases.find { it.products.contains(PRODUCT_REMOVE_ADS) }?.let { purchase ->
                scope.launch {
                    handlePurchase(purchase)
                }
            }
        }
    }

    private suspend fun handlePurchase(purchase: Purchase) {
        withContext(Dispatchers.IO) {
            userPreferences.setAdsRemoved(true)
        }
        if (!purchase.isAcknowledged) {
            val params = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()
            billingClient.acknowledgePurchase(params) { _ -> }
        }
        _purchaseResult.emit(PurchaseResult.Success)
    }

    /**
     * Lanza el flujo de compra "Quitar anuncios". Debe llamarse desde un Activity (p. ej. desde Settings).
     */
    fun launchRemoveAdsPurchase(activity: Activity) {
        if (!billingClient.isReady) {
            scope.launch {
                _purchaseResult.emit(PurchaseResult.Error("Billing no está listo. Intenta de nuevo."))
            }
            return
        }
        scope.launch {
            val productDetails = queryProductDetails() ?: run {
                _purchaseResult.emit(PurchaseResult.Error("No se pudo cargar el producto."))
                return@launch
            }
            val productDetailsParamsList = listOf(
                BillingFlowParams.ProductDetailsParams.newBuilder()
                    .setProductDetails(productDetails)
                    .build()
            )
            val params = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(productDetailsParamsList)
                .build()
            billingClient.launchBillingFlow(activity, params)
        }
    }

    private suspend fun queryProductDetails(): ProductDetails? = withContext(Dispatchers.IO) {
        val productList = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(PRODUCT_REMOVE_ADS)
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        )
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()
        suspendCancellableCoroutine { cont ->
            billingClient.queryProductDetailsAsync(params) { billingResult, productDetailsList ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && productDetailsList.isNotEmpty()) {
                    cont.resume(productDetailsList.first())
                } else {
                    cont.resume(null)
                }
            }
        }
    }

    sealed class PurchaseResult {
        data object Success : PurchaseResult()
        data class Error(val message: String) : PurchaseResult()
    }
}
