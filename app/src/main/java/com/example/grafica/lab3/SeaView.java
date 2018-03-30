package com.example.grafica.lab3;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;

import java.util.Random;

/**
 * Created by Sex_predator on 06.03.2016.
 */
public class SeaView extends MyDrawView {

    private int mWidth, mHeight;
    private int mSkyColor, mSeaColor, mSunColor, mCloudColor, mMountainColor, mWaveColor;

    private int[]  mSpline;
    private Random mRandom;

    public SeaView(Context context, AttributeSet attrs) {
        super(context, attrs);

        Resources res = getResources();
        mSkyColor = res.getColor(R.color.sky);
        mSeaColor = res.getColor(R.color.sea);
        mSunColor = res.getColor(R.color.sun);
        mCloudColor = res.getColor(R.color.cloud);
        mMountainColor = res.getColor(R.color.mountain);
        mWaveColor = res.getColor(R.color.wave);

        mSpline = new int[28];
        mRandom = new Random();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }

    private long getTime() {
        return System.nanoTime() / 1_000_000;
    }

    @Override
    protected void draw() {
        int seaHeight = mHeight / 3;
        int skyHeight = mHeight - seaHeight;

        //draw landscape
        fillRectangle(0, 0, mWidth, skyHeight, mSkyColor);
        fillRectangle(0, skyHeight, mWidth, mHeight, mSeaColor);

        //draw mountain
        int mountHeight = skyHeight / 3;
        int mountWidth = mountHeight;
        int mountX = (mWidth - mountWidth) / 2;
        int mountY = skyHeight;

        fillTriangle(mountX, mountY, mountX + mountWidth / 2, mountY - mountHeight,
                     mountX + mountWidth, mountY, mMountainColor);

        int colorReflect = combineColors(mMountainColor, mSeaColor);
        fillTriangle(mountX, mountY, mountX + mountWidth / 2, mountY + mountHeight / 2,
                     mountX + mountWidth, mountY, colorReflect);

        //draw sun
        int sunRadius = Math.min(skyHeight, mWidth) / 8;
        int sunFromX = sunRadius * 3;
        int sunToX = mWidth - sunRadius * 3;
        int sunFromY = sunRadius * 3;
        int sunToY = skyHeight / 2;

        drawSun(sunFromX, sunToX, sunFromY, sunToY, sunRadius);

        //draw cloud
        int cloudSmallRadius = Math.min(skyHeight, mWidth) / 11;
        int cloudBigRadius = Math.min(skyHeight, mWidth) / 7;

        int cloudFromX = cloudSmallRadius + cloudBigRadius + 20;
        int cloudToX = mWidth - cloudSmallRadius - cloudBigRadius - 20;
        int cloudFromY = skyHeight / 2;
        int cloudToY = skyHeight * 2 / 3;

        if (mWidth - 20 <= mHeight)
            drawCloud(cloudFromX, cloudToX, cloudFromY, cloudToY, cloudSmallRadius, cloudBigRadius);
        else {
            int cloudWidth = 2 * (cloudBigRadius + cloudSmallRadius);
            int x = drawCloud(cloudFromX, cloudToX - cloudWidth, cloudFromY, cloudToY,
                              cloudSmallRadius, cloudBigRadius);
            drawCloud(x + cloudWidth + 20, cloudToX, cloudFromY, cloudToY, cloudSmallRadius,
                      cloudBigRadius);
        }

        //draw waves
        int waveWidth = Math.min(mWidth / 2, seaHeight);
        int wave = waveWidth / (mSpline.length / 2);

        for (int i = 0; i < mSpline.length; i += 2)
            mSpline[i] = i * wave / 2;

        int waveH = 1;
        for (int i = 1; i < mSpline.length; i += 2) {
            mSpline[i] = wave * waveH;
            waveH *= -1;
        }

        int waveFromX = 20;
        int waveToX = mWidth - waveWidth - 20;

        drawWave(mSpline, waveFromX, waveToX, skyHeight + seaHeight / 4);
        drawWave(mSpline, waveFromX, waveToX, skyHeight + seaHeight / 2);
        drawWave(mSpline, waveFromX, waveToX, skyHeight + seaHeight * 3 / 4);
    }

    private int combineColors(int color1, int color2) {
        return Color.rgb((Color.red(color1) + Color.red(color2)) / 2,
                         (Color.green(color1) + Color.green(color2)) / 2,
                         (Color.blue(color1) + Color.blue(color2)) / 2);
    }

    private void drawSun(int fromX, int toX, int fromY, int toY, int radius) {
        int x = mRandom.nextInt(Math.abs(toX - fromX) + 1) + Math.min(fromX, toX);
        int y = mRandom.nextInt(Math.abs(toY - fromY) + 1) + Math.min(fromY, toY);
        fillCircle(x, y, radius, mSunColor);
    }

    private int drawCloud(int fromX, int toX, int fromY, int toY, int smallRadius, int bigRadius) {
        int x = mRandom.nextInt(Math.abs(toX - fromX) + 1) + Math.min(fromX, toX);
        int y = mRandom.nextInt(Math.abs(toY - fromY) + 1) + Math.min(fromY, toY);

        fillCircle(x, y, bigRadius, mCloudColor);
        fillCircle(x - bigRadius, y, smallRadius, mCloudColor);
        fillCircle(x + bigRadius, y, smallRadius, mCloudColor);
        return x;
    }

    private void drawWave(int[] wave, int fromX, int toX, int y) {
        int x = mRandom.nextInt(Math.abs(toX - fromX) + 1) + Math.min(fromX, toX);

        for (int i = 0; i < wave.length; i += 2)
            wave[i] += x;
        for (int i = 1; i < wave.length; i += 2)
            wave[i] += y;

        for (int i = 0; i < 5; i++) {
            drawSpline(wave, mWaveColor);
            for (int k = 1; k < wave.length; k += 2)
                wave[k]++;
        }

        for (int i = 0; i < wave.length; i += 2)
            wave[i] -= x;
        for (int i = 1; i < wave.length; i += 2)
            wave[i] -= (y + 5);
    }

}
