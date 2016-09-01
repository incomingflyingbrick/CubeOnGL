package soexample.umeng.com.myhtml5;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.Date;

/**
 * Created by umeng on 8/28/16.
 */
public class MycustomView extends View{

    Paint paint = new Paint();
    Path path = new Path();
    private String time;

    public void setDate(String time) {
        this.time = time;
        requestLayout();
        invalidate();
    }

    public MycustomView(Context context) {
        super(context);
        init();
    }

    public MycustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MycustomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        paint.setColor(getResources().getColor(R.color.colorPrimary));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(getResources().getDimension(R.dimen.circle_stroke_size));
        paint.setTextSize(getResources().getDimension(R.dimen.large_text_size));
        Date date = new Date(1000*60*10);
        time = date.getMinutes()+":"+date.getSeconds();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(400,400);
        Log.d("Size","width:"+MeasureSpec.getSize(widthMeasureSpec)+" height:"+MeasureSpec.getSize(heightMeasureSpec));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(path,paint);
        canvas.drawCircle(Math.min(getMeasuredHeight(),getMeasuredWidth())/2,Math.min(getMeasuredHeight(),getMeasuredWidth())/2,Math.min(getMeasuredHeight(),getMeasuredWidth())/2,paint);
        int xPos = (canvas.getWidth() / 2);
        int yPos = (int) ((canvas.getHeight() / 2) - ((paint.descent() + paint.ascent()) / 2)) ;
        canvas.drawText(time,xPos,yPos,paint);

    }
}
