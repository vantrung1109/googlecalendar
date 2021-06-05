package com.example.GoogleCalendar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.widget.AutoScrollHelper;

public class JCalendarMonthView extends View {
    float eachcellheight, eachcellwidth;
    long lastsec;
    int selectedcell;
    private Paint paint, mHeaderTextPaint, jDateTextPaint, jeventRectPaint, jeventtextpaint, jselectrectpaint, jtodaypaint;
    private Typeface dayfont;
    private int dayHeight, daytextsize, datemargintop, linecolor, linewidth, daytextcolor, datetextsize, datetextcolor, eventtextsize;
    private Context mContext;
    private float downx, downy;
    private String dayname[] = {"S", "M", "T", "W", "T", "F", "S"};
    private Rect selectedrect;
    private boolean isup = false;
    private ArrayList<DayModel> dayModels;
    private Rect mHeaderTextPaintRect;
    private Rect jDateTextPaintRect,jeventtextpaintRect;
    private int currentdaynameindex;

    public JCalendarMonthView(Context context) {
        this(context, null);
    }

    public JCalendarMonthView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public void setDayModels(ArrayList<DayModel> dayModels,int currentdaynameindex) {
        this.dayModels = dayModels;
        this.currentdaynameindex = currentdaynameindex;
        invalidate();
    }



    public JCalendarMonthView(final Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // Hold references.
        mContext = context;
        if (attrs == null) {
            return;
        }
        // Get the attribute values (if any).
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.JCalendarMonthView, 0, 0);
        try {
            dayHeight = a.getDimensionPixelSize(R.styleable.JCalendarMonthView_dayHeight, 200);
            daytextsize = a.getDimensionPixelSize(R.styleable.JCalendarMonthView_daytextsize, 12);
            datetextsize = a.getDimensionPixelSize(R.styleable.JCalendarMonthView_datetextsize, 14);
            eventtextsize = a.getDimensionPixelSize(R.styleable.JCalendarMonthView_eventtextsize, 11);

            daytextcolor = a.getColor(R.styleable.JCalendarMonthView_daytextcolor, Color.GRAY);
            datetextcolor = a.getColor(R.styleable.JCalendarMonthView_datetextcolor, Color.GRAY);


            datemargintop = a.getDimensionPixelSize(R.styleable.JCalendarMonthView_datemargintop, 25);
            linecolor = a.getColor(R.styleable.JCalendarMonthView_linecolor, Color.GRAY);
            linewidth = a.getDimensionPixelSize(R.styleable.JCalendarMonthView_linewidth, 2);







        } finally {
            a.recycle();
        }

    }

//    @Override
//    public boolean dispatchTouchEvent(MotionEvent event) {
//        if (event.getAction()==MotionEvent.ACTION_UP)return true;
//
//        if (event.getAction()==MotionEvent.ACTION_MOVE)return false;
//        return super.dispatchTouchEvent(event);
//    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        final int xtouch = (int) motionEvent.getX();
        final int ytouch = (int) motionEvent.getY();
        if (ytouch < dayHeight) return true;



        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {

            isup = false;
            downx = xtouch;
            downy = ytouch;
            lastsec = System.currentTimeMillis();
            return true;

        } else if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {


            Log.e("actionmove",motionEvent.getX()-downx+"");
            Log.e("actionmovey",motionEvent.getY()-downy+"");


                        if (xtouch == downx && ytouch == downy && System.currentTimeMillis() - lastsec >= 80) {
                            int column = (int) (xtouch / eachcellwidth);
                            int row = (int) ((ytouch - dayHeight) / eachcellheight);
                            int cell = (row * 7) + column;
                            if (selectedcell != cell) {
                                selectedcell = cell;
                                Log.e("select" + row + "," + column, selectedcell + "");
                                int reachxend = (int) (eachcellwidth * (column + 1));
                                int reachxstart = (int) (eachcellwidth * (column));
                                int reachyend = (int) (eachcellheight * (row + 1) + dayHeight);
                                int reachystart = (int) (eachcellheight * (row) + dayHeight);

                                final int left = (int) (xtouch - reachxstart);
                                final int right = (int) (reachxend - xtouch);
                                final int top = (int) (ytouch - reachystart);
                                final int bottom = (int) (reachyend - ytouch);
                                ValueAnimator widthAnimator = ValueAnimator.ofInt(0, 100);
                                widthAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                    @Override
                                    public void onAnimationUpdate(ValueAnimator animation) {

                                        int progress = (int) animation.getAnimatedValue();
                                        int start = xtouch - ((left * progress) / 100);
                                        int endside = xtouch + ((right * progress) / 100);
                                        int topside = ytouch - ((top * progress) / 100);
                                        int bottomside = ytouch + ((bottom * progress) / 100);
                                        selectedrect = new Rect(start, topside, endside, bottomside);
                                        Log.e("selecty", selectedrect.toString());
                                        invalidate();
                                    }
                                });
                                widthAnimator.addListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        if (isup) {
                                            selectedrect = null;
                                            selectedcell = -1;
                                            downx = -1;
                                            downy = -1;
                                            invalidate();
                                        }
                                    }
                                });
                                widthAnimator.setDuration(220);
                                widthAnimator.start();
                            }

                        } else {
                            selectedrect = null;
                            selectedcell = -1;
                            invalidate();
                        }
                         return super.onTouchEvent(motionEvent);
        } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {


            if (xtouch == downx && ytouch == downy) {
                int column = (int) (xtouch / eachcellwidth);
                int row = (int) ((ytouch - dayHeight) / eachcellheight);
                int cell = (row * 7) + column;

                    selectedcell = cell;
                    int reachxend = (int) (eachcellwidth * (column + 1));
                    int reachxstart = (int) (eachcellwidth * (column));
                    int reachyend = (int) (eachcellheight * (row + 1) + dayHeight);
                    int reachystart = (int) (eachcellheight * (row) + dayHeight);


                    final int left = (int) (xtouch - reachxstart);
                    final int right = (int) (reachxend - xtouch);
                    final int top = (int) (ytouch - reachystart);
                    final int bottom = (int) (reachyend - ytouch);
                    ValueAnimator widthAnimator = ValueAnimator.ofInt(0, 100);
                    widthAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {

                            int progress = (int) animation.getAnimatedValue();
                            int start = xtouch - ((left * progress) / 100);
                            int endside = xtouch + ((right * progress) / 100);
                            int topside = ytouch - ((top * progress) / 100);
                            int bottomside = ytouch + ((bottom * progress) / 100);
                            selectedrect = new Rect(start, topside, endside, bottomside);
                            Log.e("selecty", selectedrect.toString());
                            invalidate();
                            if (progress==100){
                                MainActivity mainActivity = (MainActivity) mContext;
                                if (mainActivity != null&&selectedcell!=-1) {
                                    DayModel dayModel = dayModels.get(selectedcell);
                                    mainActivity.selectdateFromMonthPager(dayModel.getYear(), dayModel.getMonth(), dayModel.getDay());
                                }
                                selectedrect = null;
                                selectedcell = -1;
                                downx = -1;
                                downy = -1;
                                invalidate();
                            }
                        }
                    });

                    widthAnimator.setDuration(150);
                    widthAnimator.start();



            } else {
                selectedrect = null;
                selectedcell = -1;
                downx = -1;
                downy = -1;
                invalidate();
            }

            isup = true;
            return super.onTouchEvent(motionEvent);
        }
        selectedrect = null;
        selectedcell = -1;
        downx = -1;
        downy = -1;
        invalidate();

        return super.onTouchEvent(motionEvent);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        selectedrect = null;
        selectedcell = -1;
        downx = -1;
        downy = -1;

        mHeaderTextPaintRect = new Rect();
        jDateTextPaintRect = new Rect();
        jeventtextpaintRect=new Rect();
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(linewidth);
        paint.setColor(linecolor);

        mHeaderTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextAlign(Paint.Align.CENTER);
        mHeaderTextPaint.setColor(daytextcolor);
        mHeaderTextPaint.setTypeface(ResourcesCompat.getFont(mContext, R.font.googlesansmed));
        mHeaderTextPaint.setTextSize(daytextsize);
        mHeaderTextPaint.getTextBounds("S", 0, "S".length(), mHeaderTextPaintRect);


        jDateTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        jDateTextPaint.setTextAlign(Paint.Align.CENTER);
        jDateTextPaint.setColor(datetextcolor);
        jDateTextPaint.setTypeface(ResourcesCompat.getFont(mContext, R.font.latoregular));
        jDateTextPaint.setTextSize(datetextsize);


        jeventtextpaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        jeventtextpaint.setTextAlign(Paint.Align.LEFT);
        jeventtextpaint.setColor(Color.WHITE);
        jeventtextpaint.setTypeface(ResourcesCompat.getFont(mContext, R.font.googlesansmed));
        jeventtextpaint.setTextSize(eventtextsize);
        jeventtextpaint.getTextBounds("a", 0, "a".length(), jeventtextpaintRect);

        jeventRectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        jeventRectPaint.setStyle(Paint.Style.FILL);
        jeventRectPaint.setColor(Color.parseColor("#009688"));

        jselectrectpaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        jselectrectpaint.setStyle(Paint.Style.FILL);
        jselectrectpaint.setColor(Color.parseColor("#F0F0F0"));


        jtodaypaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        jtodaypaint.setStyle(Paint.Style.FILL);
        jtodaypaint.setColor(getResources().getColor(R.color.selectday));


//        Log.e("height",rect.toString());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        eachcellheight = (getHeight() - dayHeight) / 6;
        eachcellwidth = getWidth() / 7;
        if (selectedrect != null) {
            canvas.drawRect(selectedrect, jselectrectpaint);
        }
        float[] point = new float[4];

        float begining = dayHeight;


        for (int i = 0; i < 7; i++) {

            if (i < 6) {
                point[0] = 0;
                point[1] = begining;
                point[2] = getWidth();
                point[3] = begining;
                canvas.drawLines(point, paint);
            }
            point[0] = eachcellwidth + eachcellwidth * i;
            point[1] = dayHeight / 1.5f;
            point[2] = eachcellwidth + eachcellwidth * i;
            point[3] = getHeight();
            canvas.drawLines(point, paint);
            begining = begining + eachcellheight;

        }
        for (int i = 0; i < 7&&dayModels!=null&&dayModels.size()==42; i++) {
            for (int j = 0; j < 7 && i < 6; j++) {

                if (i == 0) {
                    if (j == currentdaynameindex) mHeaderTextPaint.setColor(getResources().getColor(R.color.selectday));//todaycolor
                    else mHeaderTextPaint.setColor(daytextcolor);
                    canvas.drawText(dayname[j], (eachcellwidth * j + eachcellwidth / 2.0f) - mHeaderTextPaintRect.right / 2.0f, 5 + mHeaderTextPaintRect.height(), mHeaderTextPaint);
                }

                DayModel mydayModel=dayModels.get((i * 7) + j);
                String ss =mydayModel.getDay()+"";
                jDateTextPaint.getTextBounds(ss, 0, ss.length(), jDateTextPaintRect);
                if (mydayModel.isToday()) {//istoday
                    float centerx = ((eachcellwidth * j) + eachcellwidth / 2.0f);
                    float centery = datemargintop + dayHeight + (i * eachcellheight) + jDateTextPaintRect.height() / 2.0f;
                    float max = Math.max(jDateTextPaintRect.width(), jDateTextPaintRect.height());
                    jDateTextPaint.setColor(Color.WHITE);
                    canvas.drawRoundRect(centerx - max, centery - max, centerx + max, centery + max, max, max, jtodaypaint);
                } else {
                    if (!mydayModel.isenable()) jDateTextPaint.setColor(daytextcolor);//date disable color
                    else jDateTextPaint.setColor(datetextcolor);//date enable color
                }

                canvas.drawText(ss, ((eachcellwidth * j) + eachcellwidth / 2.0f), datemargintop + dayHeight + (i * eachcellheight) + jDateTextPaintRect.height(), jDateTextPaint);
                if (mydayModel.getEvents()!=null) {
                    RectF rect1 = new RectF();
                    rect1.left = (eachcellwidth * j) - linewidth;
                    rect1.right = (eachcellwidth * (j + 1));
                    rect1.top = dayHeight + (i * eachcellheight);//(2 * datemargintop + dayHeight + (i * eachcellheight) + rect.height());
                    rect1.bottom = dayHeight + ((i + 1) * eachcellheight);//(2 * datemargintop + dayHeight + (i * eachcellheight) + rect.height() + 50);
                    canvas.save();
                    canvas.clipRect(rect1);
                    float constant = (2 * datemargintop) + dayHeight + (i * eachcellheight) + jDateTextPaintRect.height();
                    for (int k = 0; k < mydayModel.getEvents().length; k++) {
                        RectF colorrect = new RectF();
                        if (j > 0) colorrect.left = rect1.left;
                        else colorrect.left = rect1.left + 8;//0th column left padding
                        colorrect.right = rect1.right - 12;
                        colorrect.top = constant + (42 * k) + (3 * k);

                        colorrect.bottom = colorrect.top + 42;
                        canvas.drawRoundRect(colorrect, 6, 6, jeventRectPaint);

                        canvas.drawText(mydayModel.getEvents()[k], colorrect.left + 5, colorrect.centerY() + (jeventtextpaintRect.height() / 2.0f), jeventtextpaint);

                    }
                    canvas.restore();
                }
            }

        }
    }
}
