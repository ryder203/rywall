package de.t_ryder.rywall.ui.fragments.base

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Build
import android.view.View
import android.view.WindowManager
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.FragmentActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import de.t_ryder.rywall.R
import de.t_ryder.rywall.extensions.context.getRightNavigationBarColor
import de.t_ryder.rywall.extensions.context.navigationBarLight
import de.t_ryder.rywall.extensions.resources.isDark
import de.t_ryder.rywall.extensions.utils.postDelayed

open class BaseBottomSheet : BottomSheetDialogFragment() {

    private var behavior: BottomSheetBehavior<*>? = null
    private val sheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            var correctAlpha = slideOffset + 1
            if (correctAlpha < 0) correctAlpha = 0.0F
            if (correctAlpha > 1) correctAlpha = 1.0F
            bottomSheet.alpha = correctAlpha
        }

        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) dismiss()
        }
    }

    @SuppressLint("RestrictedApi", "VisibleForTests")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)

        val content = getContentView()
        content?.let { dialog.setContentView(it) }

        val params =
            (content?.parent as? View)?.layoutParams as? CoordinatorLayout.LayoutParams

        (params?.behavior as? BottomSheetBehavior<*>)?.let {
            it.saveFlags = BottomSheetBehavior.SAVE_ALL
            // Important to keep the shape style
            it.disableShapeAnimations()
            it.addBottomSheetCallback(sheetCallback)
        }

        dialog.setOnShowListener { dialogInterface ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                (dialogInterface as? BottomSheetDialog)?.apply {
                    window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                    val navigationBarColor = context.getRightNavigationBarColor()
                    window?.navigationBarColor = navigationBarColor
                    window?.navigationBarLight = !navigationBarColor.isDark
                }
            }
            expand()
        }
    }

    fun show(context: FragmentActivity, tag: String) {
        show(context.supportFragmentManager, tag)
    }

    fun expand() {
        behavior?.state = BottomSheetBehavior.STATE_EXPANDED
        (dialog as? BottomSheetDialog)?.let {
            try {
                val sheet = it.findViewById<View?>(R.id.design_bottom_sheet)
                sheet ?: return@let
                BottomSheetBehavior.from(sheet).state = BottomSheetBehavior.STATE_EXPANDED
            } catch (e: Exception) {
            }
        }
    }

    fun hide() {
        behavior?.state = BottomSheetBehavior.STATE_COLLAPSED
        postDelayed(10) { behavior?.state = BottomSheetBehavior.STATE_HIDDEN }
    }

    open fun getContentView(): View? = null
}