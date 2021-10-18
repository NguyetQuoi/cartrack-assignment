package com.gc.myapplication.global

import androidx.annotation.AnimRes
import androidx.annotation.AnimatorRes
import com.gc.myapplication.R

/**
 * Helper class for transaction animation
 * Define the enter-exit-popEnter-popExit of a view
 * @author n.quoi
 * @date 10.18.2021
 */

enum class TransactionAnimation(@param:AnimatorRes @param:AnimRes var enter: Int,
                                @param:AnimatorRes @param:AnimRes var exit: Int,
                                @param:AnimatorRes @param:AnimRes var popEnter: Int,
                                @param:AnimatorRes @param:AnimRes var popExit: Int) {
    NONE(0,0,0,0),
    SHOW(R.anim.slide_in_from_right, R.anim.slide_out_to_left, R.anim.slide_in_from_left, R.anim.slide_out_to_right),
    SHOW_DETAIL(R.anim.slide_in_from_right, R.anim.slide_out_to_left, R.anim.slide_in_from_left, R.anim.slide_out_to_right),
    PRESENT_MODAL(R.anim.jump_to_up, android.R.anim.fade_out, android.R.anim.fade_in, R.anim.jump_to_down),
    PRESENT_POPOVER(R.transition.explode, R.transition.explode, R.transition.explode, R.transition.explode),
    FROM_BOTTOM_TO_TOP(R.anim.slide_in_up, android.R.anim.fade_out, android.R.anim.fade_in, R.anim.slide_out_up),
    FROM_TOP_TO_BOTTOM(R.anim.slide_in_up, android.R.anim.fade_out, android.R.anim.fade_in, R.anim.slide_out_down)
}
