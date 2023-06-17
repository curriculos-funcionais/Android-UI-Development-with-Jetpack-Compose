package eu.thomaskuenneth.composebook.hello_view

import android.content.Intent
import com.stripe.android.Stripe;
import com.stripe.android.PaymentSession;
import com.stripe.android.PaymentSessionConfig
import com.stripe.android.model.PaymentMethod

import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.stripe.android.PaymentConfiguration
import com.stripe.android.PaymentSessionData
import com.stripe.android.googlepaylauncher.GooglePayEnvironment
import com.stripe.android.googlepaylauncher.GooglePayLauncher
import eu.thomaskuenneth.composebook.hello_view.databinding.MainBinding

class CheckoutActivity : AppCompatActivity() {

    private lateinit var googlePayButton: Button

    // fetch client_secret from backend
    private lateinit var clientSecret: String

    private lateinit var googlePayButton: Button

    private lateinit var binding: MainBinding

    private lateinit var stripe: Stripe
    private lateinit var paymentSession: PaymentSession

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try{

            setContentView(R.layout.main)

            val publishableKey = System.getenv("PUBLISHABLE_KEY")

            PaymentConfiguration.init(this, publishableKey)

            googlePayButton = findViewById<Button>(R.id.google_pay_button)

            val googlePayLauncher = GooglePayLauncher(
                activity = this,
                config = GooglePayLauncher.Config(
                    environment = GooglePayEnvironment.Test,
                    merchantCountryCode = "BR",
                    merchantName = "Luis Mendes @luismendes070"
                ),
                readyCallback = ::onGooglePayReady,
                resultCallback = ::onGooglePayResult
            )

            googlePayButton.setOnClickListener {
                // launch `GooglePayLauncher` to confirm a Payment Intent
                googlePayLauncher.presentForPaymentIntent(clientSecret)
            }

            PaymentConfiguration.init(
                applicationContext,
                publishableKey,
                "br"
            )

            binding = MainBinding.inflate(layoutInflater)
            setContentView(binding.root)
            binding.message.text = getString(R.string.welcome)
            binding.name.run {
                setOnEditorActionListener { _, _, _ ->
                    binding.googlePayButton.performClick()
                    true
                }
                doAfterTextChanged {
                    enableOrDisableButton()
                }
                visibility = VISIBLE
            }
            binding.googlePayButton.run {
                setOnClickListener {
                    val name = binding.name.text
                    if (name.isNotBlank()) {
                        binding.message.text = getString(R.string.hello, name)
                        binding.name.visibility = GONE
                        it.visibility = GONE
                    }
                }
                visibility = VISIBLE
            }
            enableOrDisableButton()

            setContentView(R.layout.main)

            // val publishableKey = "pk_test_..."
            stripe = Stripe(applicationContext, publishableKey)

            paymentSession = PaymentSession(
                this,
                createPaymentSessionConfig()
            )

            paymentSession.init(createPaymentSessionListener())

        }catch (e : Exception){

        }finally {

        }

    }

    private fun onGooglePayReady(isReady: Boolean) {
        googlePayButton.isEnabled = isReady
    }

    private fun onGooglePayReady(isReady: Boolean) {
        // implemented below
    }

    private fun onGooglePayResult(result: GooglePayLauncher.Result) {
        // implemented below
    }

    private fun enableOrDisableButton() {
        binding.googlePayButton.isEnabled = binding.name.text.isNotBlank()
    }

    private fun createPaymentSessionConfig(): PaymentSessionConfig {
        return PaymentSessionConfig.Builder()
            .setShippingInfoRequired(false)
            .setShippingMethodsRequired(false)
            .setPaymentMethodTypes(
                listOf(PaymentMethod.Type.Card)
            )
            .build()
    }

    private fun createPaymentSessionListener(): PaymentSession.PaymentSessionListener {
        return object : PaymentSession.PaymentSessionListener {
            override fun onCommunicatingStateChanged(isCommunicating: Boolean) {
                if (isCommunicating) {
                    // update UI to indicate that network communication is in progress
                } else {
                    // update UI to indicate that network communication has completed
                }
            }

            override fun onError(errorCode: Int, errorMessage: String) {
                // handle error
            }

            override fun onPaymentSessionDataChanged(data: PaymentSessionData) {
                // update UI with new data
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (paymentSession.handlePaymentData(requestCode, resultCode, data)) {
            // Payment was successful. Update UI as needed.
        }
    }
}

