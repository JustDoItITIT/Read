package bean;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

public class BatteryView extends View{

	private int mPower = 100;
	
	public BatteryView(Context context) {
		super(context);
	}
	
	public BatteryView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		int battery_left = 0;
		int battery_top = 0;
		int battery_width = 30;
		int battery_height = 15;
		
		int battery_head_width = 3;
		int battery_head_height = 3;
		
		int battery_inside_margin = 2;

		final int paddingLeft = getPaddingLeft();
		final int paddingRight = getPaddingRight();
		final int paddingTop = getPaddingTop();
		final int paddingBottom = getPaddingBottom();

		battery_width = getWidth() - paddingLeft -paddingRight;
		battery_height = getHeight() - paddingTop - paddingBottom;



		//先画外框
		Paint paint = new Paint();
		paint.setColor(Color.BLACK);
		paint.setAntiAlias(true);
		paint.setStyle(Style.STROKE);
		
		Rect rect = new Rect(battery_left, battery_top, 
				battery_left + battery_width, battery_top + battery_height);
		canvas.drawRect(rect, paint);
		
		float power_percent = mPower / 100.0f;
		
		Paint paint2 = new Paint(paint);
		paint2.setStyle(Style.FILL);
		//画电量
		if(power_percent != 0) {
			int p_left = battery_left + battery_inside_margin;
			int p_top = battery_top + battery_inside_margin;
			int p_right = p_left - battery_inside_margin + (int)((battery_width - battery_inside_margin) * power_percent);
			int p_bottom = p_top + battery_height - battery_inside_margin * 2;
			Rect rect2 = new Rect(p_left, p_top, p_right , p_bottom);
			canvas.drawRect(rect2, paint2);
		}
		
		//画电池头
		int h_left = battery_left + battery_width;
		int h_top = battery_top + battery_height / 2 - battery_head_height / 2;
		int h_right = h_left + battery_head_width;
		int h_bottom = h_top + battery_head_height;
		Rect rect3 = new Rect(h_left, h_top, h_right, h_bottom);
		canvas.drawRect(rect3, paint2);
	}
	
	public void setPower(int power) {
		mPower = power;
		if(mPower < 0) {
			mPower = 0;
		}
		invalidate();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
		int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightSpecMode =  MeasureSpec.getMode(heightMeasureSpec);
		if(widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST){
			setMeasuredDimension(30,15);
		}else if(widthSpecMode ==  MeasureSpec.AT_MOST){
			setMeasuredDimension(30,heightSpecSize);
		}else if(heightMeasureSpec == MeasureSpec.AT_MOST){
			setMeasuredDimension(widthSpecSize,15);
		}

	}
//	public int measureDimension(int defaultSize, int measureSpec){
//		int result;
//
//		int specMode = MeasureSpec.getMode(measureSpec);
//		int specSize = MeasureSpec.getSize(measureSpec);
//
//		if(specMode == MeasureSpec.EXACTLY){
//			result = specSize;
//		}else{
//			result = defaultSize;   //UNSPECIFIED
//			if(specMode == MeasureSpec.AT_MOST){
//				result = Math.min(result, specSize);
//			}
//		}
//		return result;
//	}
}
