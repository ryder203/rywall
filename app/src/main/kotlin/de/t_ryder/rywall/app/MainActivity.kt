package de.t_ryder.rywall.app

import com.github.javiersantos.piracychecker.PiracyChecker
import de.t_ryder.rywall.ui.activities.FramesActivity

class MainActivity : FramesActivity() {

    /**
     * These things here have the default values. You can delete the ones you don't want to change
     * and/or modify the ones you want to.
     */
    override val billingEnabled = false

    override fun amazonInstallsEnabled(): Boolean = false
    override fun checkLPF(): Boolean = true
    override fun checkStores(): Boolean = true

    /**
     * This is your app's license key. Get yours on Google Play Dev Console.
     * Default one isn't valid and could cause issues in your app.
     */
    override fun getLicKey(): String? = ""

    /**
     * This is the license checker code. Feel free to create your own implementation or
     * leave it as it is.
     * Anyways, keep the 'destroyChecker()' as the very first line of this code block
     * Return null to disable license check
     */
override fun getLicenseChecker():PiracyChecker? {
    destroyChecker()
    return null
}

    override fun defaultTheme(): Int = R.style.MyApp_Default
    override fun amoledTheme(): Int = R.style.MyApp_Default_Amoled
}