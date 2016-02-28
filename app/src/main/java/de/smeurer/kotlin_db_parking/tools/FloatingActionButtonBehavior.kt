package de.smeurer.kotlin_db_parking.tools

import android.content.Context
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.FloatingActionButton
import android.util.AttributeSet
import android.view.View
import de.smeurer.kotlin_db_parking.BottomSheetState
import de.smeurer.kotlin_db_parking.MapsActivity
import com.britneykilla.parkingtest.R

/**
 * Created by Simon Meurer on 28.02.16.
 */

class FloatingActionButtonBehavior(var context: Context, attrs: AttributeSet?) : CoordinatorLayout.Behavior<FloatingActionButton>(context, attrs) {


    override fun layoutDependsOn(parent: CoordinatorLayout?, child: FloatingActionButton?, dependency: View?): Boolean {
        return dependency?.id == R.id.bottom_sheet
    }


    override fun onDependentViewChanged(parent: CoordinatorLayout?, child: FloatingActionButton?, dependency: View?): Boolean {
        if (dependency != null ) {
            val mapsActivity = context as MapsActivity

            when (mapsActivity.bottomSheetState) {
                BottomSheetState.hidden -> {
                    child?.hide()
                }
                BottomSheetState.visible -> {
                    child?.show()
                }
            }

        }
        return true;
    }
}