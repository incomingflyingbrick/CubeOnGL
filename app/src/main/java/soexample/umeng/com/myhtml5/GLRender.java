package soexample.umeng.com.myhtml5;


import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.media.MediaMetadataRetriever;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.os.Handler;
import android.os.Message;
import android.renderscript.RenderScript;
import android.util.Log;


import com.umeng.comm.core.utils.ThreadPoolUtils;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by umeng on 8/11/16.
 */
public class GLRender implements GLSurfaceView.Renderer ,Runnable{
    private boolean mSurfaceCreated;
    private int mWidth = -1;
    private int mHeight = -1;
    private long mLastTime;
    private int mFPS;
    private boolean mFirstDraw;
    private static String TAG;
    protected Cube mCube = new Cube();
    protected float mCubeRotation;
    private MediaMetadataRetriever mediaMetadataRetriever;
    protected String mMediaPath;
    private long counter=0;
    private Bitmap mBitmapImage;
    private ExecutorService executorService;
    private Handler mHandler;
    private Thread thread;
    private SurfaceTexture mSurfaceTexture;


    public GLRender() {
        mFirstDraw = true;
        mLastTime = System.currentTimeMillis();
        TAG = getClass().getSimpleName();
        mediaMetadataRetriever = new MediaMetadataRetriever();
        executorService = Executors.newFixedThreadPool(2);
        mHandler = new Handler();

    }

    public void startThread(){
        if(thread!=null){
            thread.start();
        }
    }

    @Override
    public void run() {
        while(counter<Long.MAX_VALUE){
            Bitmap frameImage = mediaMetadataRetriever.getFrameAtTime(counter,MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
            if(frameImage!=null){
                mBitmapImage = frameImage;
            }
            counter = counter+1000000;
            Log.d(TAG,"counter:"+counter);

            if(counter>=Long.MAX_VALUE){
                counter=0;
            }

        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {



        Log.d(TAG,Thread.currentThread().getName());
        gl.glClearColor(0f,0f,0f,0f);
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();

        gl.glTranslatef(0.0f, 0.0f, -6.0f);
        gl.glRotatef(mCubeRotation, 1.0f, 1.0f, 1.0f);

        mCube.loadGLTexture(gl,mBitmapImage);
        mCube.draw(gl);

        gl.glLoadIdentity();
        mCubeRotation -= 2f;

    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);
        gl.glEnable(GL10.GL_TEXTURE_2D);
        gl.glClearDepthf(1.0f);
        gl.glEnable(GL10.GL_DEPTH_TEST);
        gl.glDepthFunc(GL10.GL_LEQUAL);

        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT,
                GL10.GL_NICEST);
        Log.d(TAG,"surface created");
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        GLU.gluPerspective(gl, 45.0f, (float)width / (float)height, 0.1f, 100.0f);
        gl.glViewport(0, 0, width, height);

        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
        Log.d(TAG,"surface changed");
    }


}
