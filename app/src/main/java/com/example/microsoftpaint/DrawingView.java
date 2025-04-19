package com.example.microsoftpaint;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

public class DrawingView extends View {

    private Path mPath;
    private Paint mPaint;
    private ArrayList<Path> paths = new ArrayList<>();
    private ArrayList<Paint> paints = new ArrayList<>();
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private int currentColor = Color.BLACK;
    private int currentWidth = 10;
    private String currentTool = "PENCIL"; // PENCIL, BRUSH, FORK

    public DrawingView(Context context) {
        this(context, null);
    }

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupDrawing();
    }

    private void setupDrawing() {
        mPath = new Path();
        mPaint = new Paint();
        mPaint.setColor(currentColor);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(currentWidth);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(mBitmap, 0, 0, null);

        for (int i = 0; i < paths.size(); i++) {
            canvas.drawPath(paths.get(i), paints.get(i));
        }

        canvas.drawPath(mPath, mPaint);
    }

    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;

    private void touchStart(float x, float y) {
        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    private void touchMove(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);

        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            switch (currentTool) {
                case "PENCIL":
                    mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
                    break;
                case "BRUSH":
                    mPath.lineTo(x, y);
                    break;
                case "FORK":
                    // Dessin d'une forme de fourchette simplifiée
                    mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
                    float dX = x - mX;
                    float dY = y - mY;
                    // Ajouter des petites lignes perpendiculaires
                    mPath.moveTo((x + mX) / 2, (y + mY) / 2);
                    mPath.lineTo((x + mX) / 2 - dY/4, (y + mY) / 2 + dX/4);
                    mPath.moveTo((x + mX) / 2, (y + mY) / 2);
                    mPath.lineTo((x + mX) / 2 + dY/4, (y + mY) / 2 - dX/4);
                    break;
            }

            mX = x;
            mY = y;
        }
    }

    private void touchUp() {
        mPath.lineTo(mX, mY);

        // Dessiner le chemin sur le canevas
        mCanvas.drawPath(mPath, mPaint);

        // Stocker le chemin et la peinture
        paths.add(mPath);
        Paint newPaint = new Paint(mPaint);
        paints.add(newPaint);

        // Réinitialiser le chemin
        mPath = new Path();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchStart(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touchMove(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touchUp();
                invalidate();
                break;
        }
        return true;
    }

    public void clearCanvas() {
        paths.clear();
        paints.clear();
        mPath.reset();
        mCanvas.drawColor(Color.WHITE);
        invalidate();
    }

    public void setColor(int color) {
        currentColor = color;
        mPaint.setColor(color);
    }

    public void setStrokeWidth(int width) {
        currentWidth = width;
        mPaint.setStrokeWidth(width);
    }

    public void setTool(String tool) {
        currentTool = tool;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }
}