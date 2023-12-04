package yuanren.tvsamrtwatch.smartwatchinteractions.views.search;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class DrawingView extends View {
    private Paint paint = new Paint();
    private Path path = new Path();

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);

        paint.setAntiAlias(true);  // smoother line
        paint.setColor(Color.WHITE);
        paint.setStrokeJoin(Paint.Join.ROUND);  // make the turning points of strokes more rounded
        paint.setStrokeWidth(5f);
        paint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPath(path, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                path.moveTo(x, y);
                return true;
            case MotionEvent.ACTION_MOVE:
                path.lineTo(x, y);
                break;
            case MotionEvent.ACTION_UP:
                break;
            default:
                return false;
        }

        //schedule a repaint
        invalidate();
        return true;
    }

    protected void clear() {
        invalidate();
        path.reset();
    }
}
