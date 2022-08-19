package com.upv.pm_2022.iti_27849_u3_equipo_01;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GLRender implements GLSurfaceView.Renderer {

    private int zoom;
    private int programHandle;
    private float[] mModelMatrix = new float[16];
    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private final float[] mFinalMVPMatrix = new float[16];
    private final float[] mRotationMatrix = new float[16];
    public float[] vertices;


    // floats per trapezoid = 7 floats per vertex * 3 vertices per triangle * 2 triangles per trapezoid
    private final int floatsPerTrap = 42;

    /** How many bytes per float. */
    private final int mBytesPerFloat = 4;

    /** How many elements per vertex. */
    private final int mStrideBytes = 7 * mBytesPerFloat;

    /** Offset of the position data. */
    private final int mPositionOffset = 0;

    /** Size of the position data in elements. */
    private final int mPositionDataSize = 3;

    /** Offset of the color data. */
    private final int mColorOffset = 3;

    /** Size of the color data in elements. */
    private final int mColorDataSize = 4;

    /** This will be used to pass in the transformation matrix. */
    private int mMVPMatrixHandle;

    /** This will be used to pass in model position information. */
    private int mPositionHandle;

    /** This will be used to pass in model color information. */
    private int mColorHandle;

    /** This is a handle to our per-vertex cube shading program. */
    private int mPerVertexProgramHandle;

    /** This will be used to pass in the modelview matrix. */
    private int mMVMatrixHandle;

    /** This will be used to pass in the light position. */
    private int mLightPosHandle;

    /** This will be used to pass in model normal information. */
    private int mNormalHandle;

    public Cube mCube,
    left_line,left_line_behind,
    right_line,right_line_behind,
    top_line,top_line_behind,
    top_left_line,top_right_line,
    bottom_line,bottom_line_behind,
    bottom_left_line,bottom_right_line;

    private float mCubeRotation;
    private long mLastUpdateMillis;

    //ambient light
    private final float[] mat_ambient = {0.2f, 0.3f, 0.4f, 1.0f };
    private FloatBuffer mat_ambient_buf;
    //parallel incident light
    private final float[] mat_diffuse = {0.4f, 0.6f, 0.8f, 1.0f };
    private FloatBuffer mat_diffuse_buf;
    //highlight area
    private final float[] mat_specular = {0.2f * 0.4f, 0.2f * 0.6f, 0.2f * 0.8f, 1.0f };
    private FloatBuffer mat_specular_buf;
    private Sphere mSphere,left_top_sphere;
    public volatile float mLightX = 10f;
    public volatile float mLightZ = 10f;

    /**
     * These values are used to rotate the image by a certain value
     */
    private float xRot;
    private float yRot;

    public void setxRot(float xRot) {
        this.xRot += xRot;
    }

    public void setyRot(float yRot) {
        this.yRot += yRot;
    }


    private Cube getHorizontalLine(float width){
        width = width / 2;
        return new Cube(new float[]{
                -width, -0.01f, -0.01f,
                width, -0.01f, -0.01f,
                width, 0.01f, -0.01f,
                -width, 0.01f, -0.01f,
                -width, -0.01f, 0.01f,
                width, -0.01f, 0.01f,
                width, 0.01f, 0.01f,
                -width, 0.01f, 0.01f
        });
    }

    private Cube getVerticalLine(float width){
        width = width / 2;
        return new Cube(new float[]{
                -0.01f, -width, -0.01f,
                0.01f, -width, -0.01f,
                0.01f, width, -0.01f,
                -0.01f, width, -0.01f,
                -0.01f, -width, 0.01f,
                0.01f, -width, 0.01f,
                0.01f, width, 0.01f,
                -0.01f, width, 0.01f
        });
    }

    private Cube getInclinedVerticalLine(float height,float width){//linea inclinada po weon
        height = height / 2;
        return new Cube(new float[]{
                -0.01f, -height, -0.01f,
                0.01f, -height, -0.01f,
                width, height, -0.01f,
                width-0.01f, height, -0.01f,
                -0.01f, -height, 0.01f,
                0.01f, -height, 0.01f,
                width, height, 0.01f,
                width-0.01f, height, 0.01f
        });
    }

    private Cube getDepthLine(float width){
        width = width / 2;
        return new Cube(new float[]{
                -0.01f, -0.01f, -width,
                0.01f, -0.01f, -width,
                0.01f, 0.01f, -width,
                -0.01f, 0.01f, -width,
                -0.01f, -0.01f, width,
                0.01f, -0.01f, width,
                0.01f, 0.01f, width,
                -0.01f, 0.01f, width
        });
    }

    int OPTIONS = 0;

    //private FloatBuffer vertexBuffer;

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glClearDepthf(1.0f);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glDepthFunc(GLES20.GL_LEQUAL);
        //mCube = new Cube(vertices);
        mSphere = new Sphere(1,1,1,1);

    }

    public GLRender(int options){
        Matrix.setLookAtM(mViewMatrix, 0, 0.0f, 0.0f, -4.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);
        this.OPTIONS = options;
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        float ratio = (float) width / height;
        GLES20.glViewport(0, 0, width, height);
        // This projection matrix is applied to object coordinates in the onDrawFrame() method.
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1.0f, 1.0f, 3.0f, 7.0f);
        // modelView = projection x view
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
        //initBuffers();
        //mCube = new Cube(vertices);
        //mSphere = new Sphere(1,1,1,1);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        // Create a rotation transformation for the triangle
        long time = SystemClock.uptimeMillis() % 4000L;
        float angle = 0.09f * ((int) time);
        Matrix.setRotateM(mRotationMatrix, 0, angle, 0, .5f, 0);

        // Apply the rotation.
//        Matrix.setRotateM(mRotationMatrix, 0, mCubeRotation, 1.0f, 1.0f, 1.0f);
        // Combine the rotation matrix with the projection and camera view
        Matrix.multiplyMM(mFinalMVPMatrix, 0, mMVPMatrix, 0, mRotationMatrix, 0);

        //options
        if (OPTIONS == 1){//cúbico
            //Vertical lines
            Matrix.translateM(mFinalMVPMatrix, 0, 0.0f, 0.0f, 0.0f);
            left_line = getVerticalLine(0.5f);
            left_line.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, 0.0f, 0.0f, 0.5f);
            left_line_behind = getVerticalLine(0.5f);
            left_line_behind.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, 0.5f, 0.0f, 0.0f);
            right_line = getVerticalLine(0.5f);
            right_line.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, 0.0f, 0.0f, -0.5f);
            right_line_behind = getVerticalLine(0.5f);
            right_line_behind.draw(mFinalMVPMatrix);

            //Horizontal lines
            Matrix.translateM(mFinalMVPMatrix, 0, -0.25f, -0.25f, 0.0f);
            bottom_line = getHorizontalLine(0.5f);
            bottom_line.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, 0.0f, 0.0f, 0.5f);
            bottom_line_behind = getHorizontalLine(0.5f);
            bottom_line_behind.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, 0.0f, 0.5f, 0.0f);
            top_line = getHorizontalLine(0.5f);
            top_line.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, 0.0f, 0.0f, -0.5f);
            top_line_behind = getHorizontalLine(0.5f);
            top_line_behind.draw(mFinalMVPMatrix);

            //Depth Lines
            Matrix.translateM(mFinalMVPMatrix, 0, -0.25f, 0.0f, 0.25f);
            bottom_left_line = getDepthLine(0.5f);
            bottom_left_line.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, 0.5f, 0.0f, 0.0f);
            bottom_right_line = getDepthLine(0.5f);
            bottom_right_line.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, 0.0f, -0.5f, 0.0f);
            top_left_line = getDepthLine(0.5f);
            top_left_line.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, -0.5f, 0.0f, 0.0f);
            top_right_line = getDepthLine(0.5f);
            top_right_line.draw(mFinalMVPMatrix);

            //arriba
            Matrix.translateM(mFinalMVPMatrix, 0, 0.0f, 0.0f, 0.25f);
            mSphere = new Sphere(10,10,0.05f,1.0f);
            mSphere.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, 0.0f, 0.0f, -0.5f);
            mSphere = new Sphere(10,10,0.05f,1.0f);
            mSphere.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, 0.5f, 0.0f, 0.0f);
            mSphere = new Sphere(10,10,0.05f,1.0f);
            mSphere.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, 0.0f, 0.0f, 0.5f);
            mSphere = new Sphere(10,10,0.05f,1.0f);
            mSphere.draw(mFinalMVPMatrix);

            //abajo
            Matrix.translateM(mFinalMVPMatrix, 0, 0.0f, 0.5f, 0.0f);
            mSphere = new Sphere(10,10,0.05f,1.0f);
            mSphere.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, 0.0f, 0.0f, -0.5f);
            mSphere = new Sphere(10,10,0.05f,1.0f);
            mSphere.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, -0.5f, 0.0f, 0f);
            mSphere = new Sphere(10,10,0.05f,1.0f);
            mSphere.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, 0.0f, 0.0f, 0.5f);
            mSphere = new Sphere(10,10,0.05f,1.0f);
            mSphere.draw(mFinalMVPMatrix);
        }if (OPTIONS == 11){//cúbico centrado en el cuerpo
            //Vertical lines
            Matrix.translateM(mFinalMVPMatrix, 0, 0.0f, 0.0f, 0.0f);
            left_line = getVerticalLine(0.5f);
            left_line.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, 0.0f, 0.0f, 0.5f);
            left_line_behind = getVerticalLine(0.5f);
            left_line_behind.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, 0.5f, 0.0f, 0.0f);
            right_line = getVerticalLine(0.5f);
            right_line.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, 0.0f, 0.0f, -0.5f);
            right_line_behind = getVerticalLine(0.5f);
            right_line_behind.draw(mFinalMVPMatrix);

            //Horizontal lines
            Matrix.translateM(mFinalMVPMatrix, 0, -0.25f, -0.25f, 0.0f);
            bottom_line = getHorizontalLine(0.5f);
            bottom_line.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, 0.0f, 0.0f, 0.5f);
            bottom_line_behind = getHorizontalLine(0.5f);
            bottom_line_behind.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, 0.0f, 0.5f, 0.0f);
            top_line = getHorizontalLine(0.5f);
            top_line.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, 0.0f, 0.0f, -0.5f);
            top_line_behind = getHorizontalLine(0.5f);
            top_line_behind.draw(mFinalMVPMatrix);

            //Depth Lines
            Matrix.translateM(mFinalMVPMatrix, 0, -0.25f, 0.0f, 0.25f);
            bottom_left_line = getDepthLine(0.5f);
            bottom_left_line.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, 0.5f, 0.0f, 0.0f);
            bottom_right_line = getDepthLine(0.5f);
            bottom_right_line.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, 0.0f, -0.5f, 0.0f);
            top_left_line = getDepthLine(0.5f);
            top_left_line.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, -0.5f, 0.0f, 0.0f);
            top_right_line = getDepthLine(0.5f);
            top_right_line.draw(mFinalMVPMatrix);

            //arriba
            Matrix.translateM(mFinalMVPMatrix, 0, 0.0f, 0.0f, 0.25f);
            mSphere = new Sphere(10,10,0.05f,1.0f);
            mSphere.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, 0.0f, 0.0f, -0.5f);
            mSphere = new Sphere(10,10,0.05f,1.0f);
            mSphere.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, 0.5f, 0.0f, 0.0f);
            mSphere = new Sphere(10,10,0.05f,1.0f);
            mSphere.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, 0.0f, 0.0f, 0.5f);
            mSphere = new Sphere(10,10,0.05f,1.0f);
            mSphere.draw(mFinalMVPMatrix);

            //abajo
            Matrix.translateM(mFinalMVPMatrix, 0, 0.0f, 0.5f, 0.0f);
            mSphere = new Sphere(10,10,0.05f,1.0f);
            mSphere.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, 0.0f, 0.0f, -0.5f);
            mSphere = new Sphere(10,10,0.05f,1.0f);
            mSphere.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, -0.5f, 0.0f, 0f);
            mSphere = new Sphere(10,10,0.05f,1.0f);
            mSphere.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, 0.0f, 0.0f, 0.5f);
            mSphere = new Sphere(10,10,0.05f,1.0f);
            mSphere.draw(mFinalMVPMatrix);
        } else if (OPTIONS == 2){//monoclínico
            //Vertical lines
            Matrix.translateM(mFinalMVPMatrix, 0, 0.0f, 0.0f, 0.0f);
            left_line = getInclinedVerticalLine(0.6f,0.5f);
            left_line.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, 0.0f, 0.0f, 0.5f);
            left_line_behind = getInclinedVerticalLine(0.6f,0.5f);
            left_line_behind.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, 0.5f, 0.0f, 0.0f);
            right_line = getInclinedVerticalLine(0.6f,0.5f);
            right_line.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, 0.0f, 0.0f, -0.5f);
            right_line_behind = getInclinedVerticalLine(0.6f,0.5f);
            right_line_behind.draw(mFinalMVPMatrix);

            //Horizontal lines
            Matrix.translateM(mFinalMVPMatrix, 0, -0.25f, -0.3f, 0.0f);
            bottom_line = getHorizontalLine(0.5f);
            bottom_line.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, 0.0f, 0.0f, 0.5f);
            bottom_line_behind = getHorizontalLine(0.5f);
            bottom_line_behind.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, 0.5f, 0.6f, 0.0f);
            top_line = getHorizontalLine(0.5f);
            top_line.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, 0.0f, 0.0f, -0.5f);
            top_line_behind = getHorizontalLine(0.5f);
            top_line_behind.draw(mFinalMVPMatrix);

            //Depth Lines
            Matrix.translateM(mFinalMVPMatrix, 0, -0.25f, 0.0f, 0.25f);
            bottom_left_line = getDepthLine(0.5f);
            bottom_left_line.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, 0.5f, 0.0f, 0.0f);
            bottom_right_line = getDepthLine(0.5f);
            bottom_right_line.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, -0.5f, -0.6f, 0.0f);
            top_left_line = getDepthLine(0.5f);
            top_left_line.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, -0.5f, 0.0f, 0.0f);
            top_right_line = getDepthLine(0.5f);
            top_right_line.draw(mFinalMVPMatrix);

            //arriba
            Matrix.translateM(mFinalMVPMatrix, 0, 0.0f, 0.0f, 0.25f);
            mSphere = new Sphere(10,10,0.05f,1.0f);
            mSphere.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, 0.0f, 0.0f, -0.5f);
            mSphere = new Sphere(10,10,0.05f,1.0f);
            mSphere.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, 0.5f, 0.0f, 0.0f);
            mSphere = new Sphere(10,10,0.05f,1.0f);
            mSphere.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, 0.0f, 0.0f, 0.5f);
            mSphere = new Sphere(10,10,0.05f,1.0f);
            mSphere.draw(mFinalMVPMatrix);

            //abajo
            Matrix.translateM(mFinalMVPMatrix, 0, 0.0f, 0.6f, 0.0f);
            mSphere = new Sphere(10,10,0.05f,1.0f);
            mSphere.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, 0.0f, 0.0f, -0.5f);
            mSphere = new Sphere(10,10,0.05f,1.0f);
            mSphere.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, 0.5f, 0.0f, 0f);
            mSphere = new Sphere(10,10,0.05f,1.0f);
            mSphere.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, 0.0f, 0.0f, 0.5f);
            mSphere = new Sphere(10,10,0.05f,1.0f);
            mSphere.draw(mFinalMVPMatrix);

        }else if (OPTIONS == 3){//Hexagonal
            //Vertical lines
            Matrix.translateM(mFinalMVPMatrix, 0, 0.0f, 0.0f, 0.0f);
            left_line = getInclinedVerticalLine(0.6f,0.5f);
            left_line.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, 0.0f, 0.0f, 0.5f);
            left_line_behind = getInclinedVerticalLine(0.6f,0.5f);
            left_line_behind.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, 0.4f, 0.0f, 0.0f);
            right_line = getInclinedVerticalLine(0.6f,0.5f);
            right_line.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, 0.0f, 0.0f, -0.5f);
            right_line_behind = getInclinedVerticalLine(0.6f,0.5f);
            right_line_behind.draw(mFinalMVPMatrix);

            //Horizontal lines
            Matrix.translateM(mFinalMVPMatrix, 0, -0.2f, -0.3f, 0.0f);
            bottom_line = getHorizontalLine(0.4f);
            bottom_line.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, 0.0f, 0.0f, 0.5f);
            bottom_line_behind = getHorizontalLine(0.4f);
            bottom_line_behind.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, 0.4f, 0.6f, 0.0f);
            top_line = getHorizontalLine(0.4f);
            top_line.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, 0.0f, 0.0f, -0.5f);
            top_line_behind = getHorizontalLine(0.4f);
            top_line_behind.draw(mFinalMVPMatrix);

            //Depth Lines
            Matrix.translateM(mFinalMVPMatrix, 0, -0.2f, 0.0f, 0.25f);
            bottom_left_line = getDepthLine(0.5f);
            bottom_left_line.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, 0.4f, 0.0f, 0.0f);
            bottom_right_line = getDepthLine(0.5f);
            bottom_right_line.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, -0.4f, -0.6f, 0.0f);
            top_left_line = getDepthLine(0.5f);
            top_left_line.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, -0.4f, 0.0f, 0.0f);
            top_right_line = getDepthLine(0.5f);
            top_right_line.draw(mFinalMVPMatrix);
        }else if (OPTIONS == 4){//ortorrombica
            //Vertical lines
            Matrix.translateM(mFinalMVPMatrix, 0, 0.0f, 0.0f, 0.0f);
            left_line = getVerticalLine(0.8f);
            left_line.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, 0.0f, 0.0f, 0.5f);
            left_line_behind = getVerticalLine(0.8f);
            left_line_behind.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, 0.6f, 0.0f, 0.0f);
            right_line = getVerticalLine(0.8f);
            right_line.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, 0.0f, 0.0f, -0.5f);
            right_line_behind = getVerticalLine(0.8f);
            right_line_behind.draw(mFinalMVPMatrix);

            //Horizontal lines
            Matrix.translateM(mFinalMVPMatrix, 0, -0.3f, -0.4f, 0.0f);
            bottom_line = getHorizontalLine(0.6f);
            bottom_line.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, 0.0f, 0.0f, 0.5f);
            bottom_line_behind = getHorizontalLine(0.6f);
            bottom_line_behind.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, 0.0f, 0.8f, 0.0f);
            top_line = getHorizontalLine(0.6f);
            top_line.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, 0.0f, 0.0f, -0.5f);
            top_line_behind = getHorizontalLine(0.6f);
            top_line_behind.draw(mFinalMVPMatrix);

            //Depth Lines
            Matrix.translateM(mFinalMVPMatrix, 0, -0.3f, 0.0f, 0.25f);
            bottom_left_line = getDepthLine(0.5f);
            bottom_left_line.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, 0.6f, 0.0f, 0.0f);
            bottom_right_line = getDepthLine(0.5f);
            bottom_right_line.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, 0.0f, -0.8f, 0.0f);
            top_left_line = getDepthLine(0.5f);
            top_left_line.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, -0.6f, 0.0f, 0.0f);
            top_right_line = getDepthLine(0.5f);
            top_right_line.draw(mFinalMVPMatrix);

            //arriba
            Matrix.translateM(mFinalMVPMatrix, 0, 0.0f, 0.0f, 0.25f);
            mSphere = new Sphere(10,10,0.05f,1.0f);
            mSphere.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, 0.0f, 0.0f, -0.5f);
            mSphere = new Sphere(10,10,0.05f,1.0f);
            mSphere.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, 0.6f, 0.0f, 0.0f);
            mSphere = new Sphere(10,10,0.05f,1.0f);
            mSphere.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, 0.0f, 0.0f, 0.5f);
            mSphere = new Sphere(10,10,0.05f,1.0f);
            mSphere.draw(mFinalMVPMatrix);

            //abajo
            Matrix.translateM(mFinalMVPMatrix, 0, 0.0f, 0.8f, 0.0f);
            mSphere = new Sphere(10,10,0.05f,1.0f);
            mSphere.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, 0.0f, 0.0f, -0.5f);
            mSphere = new Sphere(10,10,0.05f,1.0f);
            mSphere.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, -0.6f, 0.0f, 0f);
            mSphere = new Sphere(10,10,0.05f,1.0f);
            mSphere.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, 0.0f, 0.0f, 0.5f);
            mSphere = new Sphere(10,10,0.05f,1.0f);
            mSphere.draw(mFinalMVPMatrix);
        }else if (OPTIONS == 5){//Romboedro
            Matrix.translateM(mFinalMVPMatrix, 0, 0.0f, 0.0f, 0.0f);
            left_line = getInclinedVerticalLine(0.5f,0.5f);
            left_line.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, 0.0f, 0.0f, 0.5f);
            left_line_behind = getInclinedVerticalLine(0.5f,0.5f);
            left_line_behind.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, 0.5f, 0.0f, 0.0f);
            right_line = getInclinedVerticalLine(0.5f,0.5f);
            right_line.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, 0.0f, 0.0f, -0.5f);
            right_line_behind = getInclinedVerticalLine(0.5f,0.5f);
            right_line_behind.draw(mFinalMVPMatrix);

            //Horizontal lines
            Matrix.translateM(mFinalMVPMatrix, 0, -0.25f, -0.25f, 0.0f);
            bottom_line = getHorizontalLine(0.5f);
            bottom_line.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, 0.0f, 0.0f, 0.5f);
            bottom_line_behind = getHorizontalLine(0.5f);
            bottom_line_behind.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, 0.5f, 0.5f, 0.0f);
            top_line = getHorizontalLine(0.5f);
            top_line.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, 0.0f, 0.0f, -0.5f);
            top_line_behind = getHorizontalLine(0.5f);
            top_line_behind.draw(mFinalMVPMatrix);

            //Depth Lines
            Matrix.translateM(mFinalMVPMatrix, 0, -0.25f, 0.0f, 0.25f);
            bottom_left_line = getDepthLine(0.5f);
            bottom_left_line.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, 0.5f, 0.0f, 0.0f);
            bottom_right_line = getDepthLine(0.5f);
            bottom_right_line.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, -0.5f, -0.5f, 0.0f);
            top_left_line = getDepthLine(0.5f);
            top_left_line.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, -0.5f, 0.0f, 0.0f);
            top_right_line = getDepthLine(0.5f);
            top_right_line.draw(mFinalMVPMatrix);

            //arriba
            Matrix.translateM(mFinalMVPMatrix, 0, 0.0f, 0.0f, 0.25f);
            mSphere = new Sphere(10,10,0.05f,1.0f);
            mSphere.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, 0.0f, 0.0f, -0.5f);
            mSphere = new Sphere(10,10,0.05f,1.0f);
            mSphere.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, 0.5f, 0.0f, 0.0f);
            mSphere = new Sphere(10,10,0.05f,1.0f);
            mSphere.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, 0.0f, 0.0f, 0.5f);
            mSphere = new Sphere(10,10,0.05f,1.0f);
            mSphere.draw(mFinalMVPMatrix);

            //abajo
            Matrix.translateM(mFinalMVPMatrix, 0, 0.0f, 0.5f, 0.0f);
            mSphere = new Sphere(10,10,0.05f,1.0f);
            mSphere.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, 0.0f, 0.0f, -0.5f);
            mSphere = new Sphere(10,10,0.05f,1.0f);
            mSphere.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, 0.5f, 0.0f, 0f);
            mSphere = new Sphere(10,10,0.05f,1.0f);
            mSphere.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, 0.0f, 0.0f, 0.5f);
            mSphere = new Sphere(10,10,0.05f,1.0f);
            mSphere.draw(mFinalMVPMatrix);
        }
        else if (OPTIONS == 6){//tetragonal
            //Vertical lines
            Matrix.translateM(mFinalMVPMatrix, 0, 0.0f, 0.0f, 0.0f);
            left_line = getVerticalLine(0.8f);
            left_line.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, 0.0f, 0.0f, 0.5f);
            left_line_behind = getVerticalLine(0.8f);
            left_line_behind.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, 0.5f, 0.0f, 0.0f);
            right_line = getVerticalLine(0.8f);
            right_line.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, 0.0f, 0.0f, -0.5f);
            right_line_behind = getVerticalLine(0.8f);
            right_line_behind.draw(mFinalMVPMatrix);

            //Horizontal lines
            Matrix.translateM(mFinalMVPMatrix, 0, -0.25f, -0.4f, 0.0f);
            bottom_line = getHorizontalLine(0.5f);
            bottom_line.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, 0.0f, 0.0f, 0.5f);
            bottom_line_behind = getHorizontalLine(0.5f);
            bottom_line_behind.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, 0.0f, 0.8f, 0.0f);
            top_line = getHorizontalLine(0.5f);
            top_line.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, 0.0f, 0.0f, -0.5f);
            top_line_behind = getHorizontalLine(0.5f);
            top_line_behind.draw(mFinalMVPMatrix);

            //Depth Lines
            Matrix.translateM(mFinalMVPMatrix, 0, -0.25f, 0.0f, 0.25f);
            bottom_left_line = getDepthLine(0.5f);
            bottom_left_line.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, 0.5f, 0.0f, 0.0f);
            bottom_right_line = getDepthLine(0.5f);
            bottom_right_line.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, 0.0f, -0.8f, 0.0f);
            top_left_line = getDepthLine(0.5f);
            top_left_line.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, -0.5f, 0.0f, 0.0f);
            top_right_line = getDepthLine(0.5f);
            top_right_line.draw(mFinalMVPMatrix);

            //arriba
            Matrix.translateM(mFinalMVPMatrix, 0, 0.0f, 0.0f, 0.25f);
            mSphere = new Sphere(10,10,0.05f,1.0f);
            mSphere.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, 0.0f, 0.0f, -0.5f);
            mSphere = new Sphere(10,10,0.05f,1.0f);
            mSphere.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, 0.5f, 0.0f, 0.0f);
            mSphere = new Sphere(10,10,0.05f,1.0f);
            mSphere.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, 0.0f, 0.0f, 0.5f);
            mSphere = new Sphere(10,10,0.05f,1.0f);
            mSphere.draw(mFinalMVPMatrix);

            //abajo
            Matrix.translateM(mFinalMVPMatrix, 0, 0.0f, 0.8f, 0.0f);
            mSphere = new Sphere(10,10,0.05f,1.0f);
            mSphere.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, 0.0f, 0.0f, -0.5f);
            mSphere = new Sphere(10,10,0.05f,1.0f);
            mSphere.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, -0.5f, 0.0f, 0f);
            mSphere = new Sphere(10,10,0.05f,1.0f);
            mSphere.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, 0.0f, 0.0f, 0.5f);
            mSphere = new Sphere(10,10,0.05f,1.0f);
            mSphere.draw(mFinalMVPMatrix);
        } else if (OPTIONS == 7){//triclínico
            //Vertical lines
            Matrix.translateM(mFinalMVPMatrix, 0, 0.0f, 0.0f, 0.0f);
            left_line = getInclinedVerticalLine(0.6f,0.5f);
            left_line.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, 0.0f, 0.0f, 0.5f);
            left_line_behind = getInclinedVerticalLine(0.6f,0.5f);
            left_line_behind.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, 0.4f, 0.0f, 0.0f);
            right_line = getInclinedVerticalLine(0.6f,0.5f);
            right_line.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, 0.0f, 0.0f, -0.5f);
            right_line_behind = getInclinedVerticalLine(0.6f,0.5f);
            right_line_behind.draw(mFinalMVPMatrix);

            //Horizontal lines
            Matrix.translateM(mFinalMVPMatrix, 0, -0.2f, -0.3f, 0.0f);
            bottom_line = getHorizontalLine(0.4f);
            bottom_line.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, 0.0f, 0.0f, 0.5f);
            bottom_line_behind = getHorizontalLine(0.4f);
            bottom_line_behind.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, 0.5f, 0.6f, 0.0f);
            top_line = getHorizontalLine(0.4f);
            top_line.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, 0.0f, 0.0f, -0.5f);
            top_line_behind = getHorizontalLine(0.4f);
            top_line_behind.draw(mFinalMVPMatrix);

            //Depth Lines
            Matrix.translateM(mFinalMVPMatrix, 0, -0.2f, 0.0f, 0.25f);
            bottom_left_line = getDepthLine(0.5f);
            bottom_left_line.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, 0.4f, 0.0f, 0.0f);
            bottom_right_line = getDepthLine(0.5f);
            bottom_right_line.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, -0.5f, -0.6f, 0.0f);
            top_left_line = getDepthLine(0.5f);
            top_left_line.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, -0.4f, 0.0f, 0.0f);
            top_right_line = getDepthLine(0.5f);
            top_right_line.draw(mFinalMVPMatrix);

            //arriba
            Matrix.translateM(mFinalMVPMatrix, 0, 0.0f, 0.0f, 0.25f);
            mSphere = new Sphere(10,10,0.05f,1.0f);
            mSphere.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, 0.0f, 0.0f, -0.5f);
            mSphere = new Sphere(10,10,0.05f,1.0f);
            mSphere.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, 0.4f, 0.0f, 0.0f);
            mSphere = new Sphere(10,10,0.05f,1.0f);
            mSphere.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, 0.0f, 0.0f, 0.5f);
            mSphere = new Sphere(10,10,0.05f,1.0f);
            mSphere.draw(mFinalMVPMatrix);

            //abajo
            Matrix.translateM(mFinalMVPMatrix, 0, 0.1f, 0.6f, 0.0f);
            mSphere = new Sphere(10,10,0.05f,1.0f);
            mSphere.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, 0.0f, 0.0f, -0.5f);
            mSphere = new Sphere(10,10,0.05f,1.0f);
            mSphere.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, 0.4f, 0.0f, 0f);
            mSphere = new Sphere(10,10,0.05f,1.0f);
            mSphere.draw(mFinalMVPMatrix);
            Matrix.translateM(mFinalMVPMatrix, 0, 0.0f, 0.0f, 0.5f);
            mSphere = new Sphere(10,10,0.05f,1.0f);
            mSphere.draw(mFinalMVPMatrix);
        }

        mSphere.draw(mViewMatrix);
//        gl.glLoadIdentity();
//        gl.glTranslatef(0.0f, -1.2f, -1);
//        gl.glRotatef(xRot, 1.0f, 0.0f, 0.0f);   //X
//        gl.glRotatef(yRot, 0.0f, 1.0f, 0.0f);   //Y
        xRot += 1;
        xRot += 1;

    }

    public static int loadShader(int type, String shaderCode){

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

}
