package com.example.grafica.lab3;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;

import java.util.Arrays;

/**
 * Created by Sex_predator on 04.03.2016.
 */
public abstract class MyDrawView extends View {

    private int mWidth, mHeight;
    private Bitmap mBitmap;
    private int[]  pixels;

    public MyDrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mWidth = w;
        mHeight = h;

        if (mBitmap != null)
            mBitmap.recycle();
        mBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        pixels = new int[mWidth * mHeight];
    }

    @Override
    protected final void onDraw(Canvas canvas) {
        if (mBitmap != null) {
            draw();
            mBitmap.setPixels(pixels, 0, mWidth, 0, 0, mWidth, mHeight);
            canvas.drawBitmap(mBitmap, 0, 0, null);
        }
    }

    public void erase() {
        if (pixels != null)
            Arrays.fill(pixels, 0);
    }

    protected abstract void draw();

    public void putPixel(int x, int y, int color) {
        if (mBitmap == null)
            return;

        if (x < 0 || x > mWidth - 1 || y < 0 || y > mHeight - 1)
            return;

//        mBitmap.setPixel(x, y, color);
        pixels[x + y * mWidth] = color;
    }

    public void drawLine(int x1, int y1, int x2, int y2, int color) {
        if (mBitmap == null)
            return;

        boolean steep = Math.abs(y2 - y1) > Math.abs(x2 - x1);

        if (steep) {
            int tmp = x1;
            x1 = y1;
            y1 = tmp;

            tmp = x2;
            x2 = y2;
            y2 = tmp;
        }

        if (x1 > x2) {
            int tmp = x1;
            x1 = x2;
            x2 = tmp;

            tmp = y1;
            y1 = y2;
            y2 = tmp;
        }

        int dx = x2 - x1;
        int dy = Math.abs(y2 - y1);
        int error = dx / 2;
        int yStep = (y1 < y2) ? 1 : -1;

        int y = y1;
        for (int x = x1; x <= x2; x++) {
            putPixel(steep ? y : x, steep ? x : y, color);
            error -= dy;

            if (error < 0) {
                y += yStep;
                error += dx;
            }
        }
    }

    public void drawCircle(int x0, int y0, int radius, int color) {
        if (radius <= 0)
            return;

        int x = radius;
        int y = 0;
        int radiusError = 1 - x;

        while (x >= y) {
            putPixel(x + x0, y + y0, color);
            putPixel(y + x0, x + y0, color);
            putPixel(-x + x0, y + y0, color);
            putPixel(-y + x0, x + y0, color);
            putPixel(-x + x0, -y + y0, color);
            putPixel(-y + x0, -x + y0, color);
            putPixel(x + x0, -y + y0, color);
            putPixel(y + x0, -x + y0, color);

            y++;
            if (radiusError < 0)
                radiusError += 2 * y + 1;
            else {
                x--;
                radiusError += 2 * (y - x + 1);
            }
        }
    }

    public void fillCircle(int x0, int y0, int radius, int color) {
        if (radius <= 0)
            return;

        int x = radius;
        int y = 0;
        int radiusError = 1 - x;

        int sq = (int) (radius / Math.sqrt(2)) - 1;

        while (x >= y) {
            for (int a = -x; a <= x; a++) {
                putPixel(x0 + a, y + y0, color);
                putPixel(x0 + a, -y + y0, color);
            }

            for (int a = x; a >= sq; a--) {
                putPixel(x0 + y, a + y0, color);
                putPixel(x0 - y, a + y0, color);
                putPixel(x0 + y, -a + y0, color);
                putPixel(x0 - y, -a + y0, color);
            }

            y++;
            if (radiusError < 0)
                radiusError += 2 * y + 1;
            else {
                x--;
                radiusError += 2 * (y - x + 1);
            }
        }
    }

    public void drawRectangle(int x1, int y1, int x2, int y2, int color) {
        if (x1 > x2) {
            int tmp = x1;
            x1 = x2;
            x2 = tmp;
        }

        if (y1 > y2) {
            int tmp = y1;
            y1 = y2;
            y2 = tmp;
        }

        for (int x = x1; x <= x2; x++) {
            putPixel(x, y1, color);
            putPixel(x, y2, color);
        }

        for (int y = y1; y <= y2; y++) {
            putPixel(x1, y, color);
            putPixel(x2, y, color);
        }
    }

    public void fillRectangle(int x1, int y1, int x2, int y2, int color) {
        if (x1 > x2) {
            int tmp = x1;
            x1 = x2;
            x2 = tmp;
        }

        if (y1 > y2) {
            int tmp = y1;
            y1 = y2;
            y2 = tmp;
        }

        for (int x = x1; x <= x2; x++)
            for (int y = y1; y <= y2; y++)
                putPixel(x, y, color);
    }

    public void drawTriangle(int x1, int y1, int x2, int y2, int x3, int y3, int color) {
        drawLine(x1, y1, x2, y2, color);
        drawLine(x1, y1, x3, y3, color);
        drawLine(x2, y2, x3, y3, color);
    }

    private void fillBottomTriangle(int x1, int y1, int x2, int y2, int x3, int y3, int color) {
        float invslope1 = (float) (x2 - x1) / (y2 - y1);
        float invslope2 = (float) (x3 - x1) / (y3 - y1);

        float curX1 = x1, curX2 = x1;

        for (int curY = y1; curY <= y2; curY++) {
            drawLine((int) curX1, curY, (int) curX2, curY, color);
            curX1 += invslope1;
            curX2 += invslope2;
        }
    }

    private void fillTopTriangle(int x1, int y1, int x2, int y2, int x3, int y3, int color) {
        float invslope1 = (float) (x3 - x1) / (y3 - y1);
        float invslope2 = (float) (x3 - x2) / (y3 - y2);

        float curX1 = x3, curX2 = x3;

        for (int curY = y3; curY > y1; curY--) {
            curX1 -= invslope1;
            curX2 -= invslope2;
            drawLine((int) curX1, curY, (int) curX2, curY, color);
        }
    }

    public void fillTriangle(int x1, int y1, int x2, int y2, int x3, int y3, int color) {
        //sort vertices y1 <= y2 <= y3
        if (y1 > y2) {
            int tmp = y1;
            y1 = y2;
            y2 = tmp;

            tmp = x1;
            x1 = x2;
            x2 = tmp;
        }

        if (y1 > y3) {
            int tmp = y1;
            y1 = y3;
            y3 = tmp;

            tmp = x1;
            x1 = x3;
            x3 = tmp;
        }

        if (y2 > y3) {
            int tmp = y2;
            y2 = y3;
            y3 = tmp;

            tmp = x2;
            x2 = x3;
            x3 = tmp;
        }

        //fill
        if (y2 == y3)
            fillBottomTriangle(x1, y1, x2, y2, x3, y3, color);
        else if (y1 == y2)
            fillTopTriangle(x1, y1, x2, y2, x3, y3, color);
        else {
            int x4 = (int) (x1 + ((float) (y2 - y1) / (float) (y3 - y1)) * (x3 - x1));
            int y4 = y2;
            fillBottomTriangle(x1, y1, x2, y2, x4, y4, color);
            fillTopTriangle(x2, y2, x4, y4, x3, y3, color);
        }
    }

    private float countSplineCoefficient(int index, int x1, int x2, int x3, int x4) {
        switch (index) {
            case 0:
                return (x1 + 4 * x2 + x3) / 6.0f;
            case 1:
                return (-x1 + x3) / 2.0f;
            case 2:
                return (x1 - 2 * x2 + x3) / 2.0f;
            case 3:
                return (-x1 + 3 * x2 - 3 * x3 + x4) / 6.0f;
        }

        return 0;
    }

    private void drawSpline(int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4,
                            int color) {
        float a0 = countSplineCoefficient(0, x1, x2, x3, x4);
        float a1 = countSplineCoefficient(1, x1, x2, x3, x4);
        float a2 = countSplineCoefficient(2, x1, x2, x3, x4);
        float a3 = countSplineCoefficient(3, x1, x2, x3, x4);

        float b0 = countSplineCoefficient(0, y1, y2, y3, y4);
        float b1 = countSplineCoefficient(1, y1, y2, y3, y4);
        float b2 = countSplineCoefficient(2, y1, y2, y3, y4);
        float b3 = countSplineCoefficient(3, y1, y2, y3, y4);

        float xPrev = a0, yPrev = b0;

        for (int i = 1; i <= 100; i++) {
            float t = i / 100.0f;
            float x = ((a3 * t + a2) * t + a1) * t + a0;
            float y = ((b3 * t + b2) * t + b1) * t + b0;

            drawLine((int) xPrev, (int) yPrev, (int) x, (int) y, color);
            xPrev = x;
            yPrev = y;
        }
    }

    public void drawSpline(int[] points, int color) {
        if (points.length % 2 == 1 || points.length < 8)
            return;

        drawSpline(points[0], points[1], points[0], points[1], points[2], points[3], points[4],
                   points[5], color);

        for (int i = 8; i <= points.length; i += 2)
            drawSpline(points[i - 8], points[i - 7], points[i - 6], points[i - 5], points[i - 4],
                       points[i - 3], points[i - 2], points[i - 1], color);

        int i = points.length - 6;
        drawSpline(points[i], points[i + 1], points[i + 2], points[i + 3], points[i + 4],
                   points[i + 5], points[i + 4], points[i + 5], color);
    }

}
