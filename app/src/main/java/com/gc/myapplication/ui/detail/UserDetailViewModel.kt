package com.gc.myapplication.ui.detail

import com.gc.myapplication.base.BindingViewModel
import com.gc.myapplication.manager.UserManager

/**
 * DetailViewModel for [UserDetailActivity]
 * @author n.quoi
 * @date 10.19.2021
 */

class UserDetailViewModel(
    userManager: UserManager
) : BindingViewModel(userManager) {

    init {
    }

    fun onBackClicked() {
        finishActivity()
    }
}
