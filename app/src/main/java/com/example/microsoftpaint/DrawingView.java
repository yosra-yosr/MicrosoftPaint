package com.example.microsoftpaint;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

public class DrawingView extends View {

    private Path mPath;
    private Paint mPaint;
    private Paint mCanvasPaint;
    private ArrayList<DrawPath> paths = new ArrayList<>();
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private float mStartX, mStartY;
    private RectF mRect = new RectF();

    private int backgroundColor = Color.WHITE;
    private int strokeColor = Color.RED;
    private int strokeWidth = 10;
    private String currentTool = "BRUSH"; // BRUSH, PENCIL, FORK, RECTANGLE
    private int previousStrokeColor;
    // Classe interne pour stocker les chemins de dessin avec leurs propriétés
    private static class DrawPath {
        public Path path;
        public Paint paint;
        public String tool;
        public float startX, startY, endX, endY;

        public DrawPath(Path path, Paint paint, String tool, float startX, float startY, float endX, float endY) {
            this.path = path;
            this.paint = paint;
            this.tool = tool;
            this.startX = startX;
            this.startY = startY;
            this.endX = endX;
            this.endY = endY;
        }
    }

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
        mPaint.setColor(strokeColor);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(strokeWidth);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);

        mCanvasPaint = new Paint(Paint.DITHER_FLAG);
        previousStrokeColor = strokeColor;

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
        mCanvas.drawColor(backgroundColor);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(mBitmap, 0, 0, mCanvasPaint);

        // Dessiner tous les chemins déjà enregistrés
        for (DrawPath dp : paths) {
            if (dp.tool.equals("RECTANGLE")) {
                mRect.set(dp.startX, dp.startY, dp.endX, dp.endY);
                canvas.drawRect(mRect, dp.paint);
            } else {
                canvas.drawPath(dp.path, dp.paint);
            }
        }

        // Dessiner le chemin actuel
        if (currentTool.equals("RECTANGLE")) {
            mRect.set(mStartX, mStartY, mStartX, mStartY);
            canvas.drawRect(mRect, mPaint);
        } else {
            canvas.drawPath(mPath, mPaint);
        }
    }

    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;

    private void touchStart(float x, float y) {
        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
        mStartX = x;
        mStartY = y;
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
                case "RECTANGLE":
                    // Mise à jour pour le rectangle - ne fait rien ici, géré dans onDraw
                    break;
                case "ERASER":
                    // La gomme est similaire au pinceau mais avec la couleur du fond
                    mPath.lineTo(x, y);
                    break;
                case "Discard":
                    // La gomme est similaire au pinceau mais avec la couleur du fond
                    mPath.lineTo(x, y);
                    break;
            }

            mX = x;
            mY = y;
        }
    }

    private void touchUp(float x, float y) {
        if (currentTool.equals("RECTANGLE")) {
            Paint newPaint = new Paint(mPaint);
            DrawPath dp = new DrawPath(new Path(), newPaint, currentTool, mStartX, mStartY, x, y);
            paths.add(dp);

            // Dessiner définitivement le rectangle sur le canvas
            mRect.set(mStartX, mStartY, x, y);
            mCanvas.drawRect(mRect, mPaint);
        } else {
            mPath.lineTo(mX, mY);

            // Dessiner le chemin sur le canevas
            mCanvas.drawPath(mPath, mPaint);

            // Stocker le chemin et la peinture
            Paint newPaint = new Paint(mPaint);
            DrawPath dp = new DrawPath(new Path(mPath), newPaint, currentTool, mStartX, mStartY, x, y);
            paths.add(dp);

            // Réinitialiser le chemin
            mPath = new Path();
        }
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
                touchUp(x, y);
                invalidate();
                break;
        }
        return true;
    }

    public void clearCanvas() {
        paths.clear();
        mPath.reset();
        mCanvas.drawColor(backgroundColor);
        invalidate();
    }

    public void setStrokeColor(int color) {
        strokeColor = color;
        mPaint.setColor(color);
    }

    public void setBackgroundColor(int color) {
        backgroundColor = color;
        // Redessiner le fond
        mCanvas.drawColor(backgroundColor);

        // Redessiner tous les chemins
        for (DrawPath dp : paths) {
            if (dp.tool.equals("RECTANGLE")) {
                mRect.set(dp.startX, dp.startY, dp.endX, dp.endY);
                mCanvas.drawRect(mRect, dp.paint);
            } else {
                mCanvas.drawPath(dp.path, dp.paint);
            }
        }

        invalidate();
    }

    public void setStrokeWidth(int width) {
        strokeWidth = width;
        mPaint.setStrokeWidth(width);
    }

    public void setTool(String tool) {
        currentTool = tool;
        if (tool.equals("ERASER")) {
            previousStrokeColor = mPaint.getColor();
            mPaint.setColor(backgroundColor);
            mPaint.setStrokeWidth(strokeWidth * 2); // Option: rendre la gomme plus large
        }
        // Si nous quittons l'outil gomme, restaurons la couleur précédente
        else if (mPaint.getColor() == backgroundColor && !tool.equals("ERASER")) {
            mPaint.setColor(previousStrokeColor);
            mPaint.setStrokeWidth(strokeWidth); // Restaurer la largeur normale
        }
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public int getStrokeColor() {
        return strokeColor;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }
}