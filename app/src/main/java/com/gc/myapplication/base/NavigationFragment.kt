package com.gc.myapplication.base

/**
 * Fragments representing main navigate destinations implement this
 * interface.
 */
interface NavigationFragment {
    /**
     * Called by the hosting activity when the Back button is pressed.
     * @return True if the fragment handled the back press, false otherwise.
     */
    fun onBackPressed(): Boolean

    /** Called by the hosting activity when the user interacts with it.  */
    fun onUserInteraction()
}
