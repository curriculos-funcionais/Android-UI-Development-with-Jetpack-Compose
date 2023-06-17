package eu.thomaskuenneth.composebook.hello_view

import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import eu.thomaskuenneth.composebook.hello_view.databinding.MainBinding

class CheckoutActivity : AppCompatActivity() {

    private lateinit var binding: MainBinding

    private lateinit var stripe: Stripe
    private lateinit var paymentSession: PaymentSession

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = MainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.message.text = getString(R.string.welcome)
        binding.name.run {
            setOnEditorActionListener { _, _, _ ->
                binding.done.performClick()
                true
            }
            doAfterTextChanged {
                enableOrDisableButton()
            }
            visibility = VISIBLE
        }
        binding.done.run {
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

        setContentView(R.layout.activity_checkout)

        val publishableKey = "pk_test_..."
        stripe = Stripe(applicationContext, publishableKey)

        paymentSession = PaymentSession(
            this,
            createPaymentSessionConfig()
        )

        paymentSession.init(createPaymentSessionListener())
    }

    private fun enableOrDisableButton() {
        binding.done.isEnabled = binding.name.text.isNotBlank()
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

