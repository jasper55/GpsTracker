package wagner.jasper.gpstracker.extensions

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import wagner.jasper.gpstracker.R

fun Toast.show(context: Context?, message:String, gravity:Int, duration:Int){
    val inflater: LayoutInflater = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    /*first parameter is the layout you made
    second parameter is the root view in that xml
     */
    val layout = inflater.inflate(R.layout.custom_toast_layout, (context as Activity).findViewById<ViewGroup>(R.id.custom_toast_container))

    layout.findViewById<TextView>(R.id.text).text = message
    setGravity(gravity, xOffset, yOffset)
    setDuration(duration)
    view = layout
    show()
}