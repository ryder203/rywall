package de.t_ryder.rywall.ui.activities.base

import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import de.t_ryder.rywall.R
import de.t_ryder.rywall.data.Preferences
import de.t_ryder.rywall.extensions.context.isUpdate
import de.t_ryder.rywall.ui.fragments.buildChangelogDialog

abstract class BaseChangelogDialogActivity<out P : Preferences> : BaseSearchableActivity<P>() {

    private val changelogDialog: AlertDialog? by lazy { buildChangelogDialog() }

    fun showChangelog(force: Boolean = false) {
        if (isUpdate || force) changelogDialog?.show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.changelog) showChangelog(true)
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            changelogDialog?.dismiss()
        } catch (e: Exception) {
        }
    }
}