package ir.team_x.cloud_transport.driver.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import ir.team_x.cloud_transport.driver.app.MyApplication;


/**
 * Created by mohsen on 09/11/2016.
 */
public class WriteTextOnDrawable {

  public static Bitmap write(int drawableId, String text,int textSize,int ypos) {
    Context context= MyApplication.context;
    Bitmap bm = BitmapFactory.decodeResource(context.getResources(), drawableId).copy(Bitmap.Config.ARGB_8888, true);

    Paint paint = new Paint();
    paint.setStyle(Paint.Style.FILL);
//    if (MyApplication.prefManager.isDarkMode()){ //TODO uncomment this when add dark mode
//      paint.setColor(Color.BLACK);
//    }else {
//      paint.setColor(Color.WHITE);
//    }
    paint.setTypeface(MyApplication.Companion.getIranSansTF());
    paint.setTextAlign(Paint.Align.CENTER);
    paint.setTextSize(convertToPixels(context, textSize));

    Rect textRect = new Rect();
    paint.getTextBounds(text, 0, text.length(), textRect);

    Canvas canvas = new Canvas(bm);

    //If the text is bigger than the canvas , reduce the font size
    if(textRect.width() >= (canvas.getWidth() - 4))     //the padding on either sides is considered as 4, so as to appropriately fit in the text
      paint.setTextSize(convertToPixels(context, 7));        //Scaling needs to be used for different dpi's

    //Calculate the positions
    int xPos = (canvas.getWidth() / 2) - 2;     //-2 is for regulating the x position offset

    //"- ((paint.descent() + paint.ascent()) / 2)" is the distance from the baseline to the center.
    int yPos = (int) ((canvas.getHeight() / ypos) - ((paint.descent() + paint.ascent()) / 2)) ;

    canvas.drawText(text, xPos, yPos, paint);

    return  bm;
  }



  public static int convertToPixels(Context context, int nDP)
  {
    final float conversionScale = context.getResources().getDisplayMetrics().density;

    return (int) ((nDP * conversionScale) + 0.5f) ;

  }
}
