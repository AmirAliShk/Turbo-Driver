package ir.team_x.ariana.operator.utils

import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import ir.team_x.ariana.driver.app.MyApplication

class TypeFaceUtil {

    companion object {

        fun overrideFont(v: View) {
            try {
                if (v is ViewGroup) {
                    for (i in 0..v.childCount) {
                        val child = v.getChildAt(i)
                        overrideFont(child)
                    }
                } else if (v is TextView) {
                    v.typeface = MyApplication.iranSansTF
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun overrideFont(v: View, typeFace:Typeface) {
            try {
                if (v is ViewGroup) {
                    for (i in 0..v.childCount) {
                        val child = v.getChildAt(i)
                        overrideFont(child)
                    }
                } else if (v is TextView) {
                    v.typeface = typeFace
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }

}