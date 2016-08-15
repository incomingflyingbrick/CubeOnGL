package soexample.umeng.com.myhtml5;

import android.content.SyncAdapterType;
import android.media.MediaMetadataRetriever;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.provider.Settings;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by umeng on 8/11/16.
 */
public abstract class GLRender implements GLSurfaceView.Renderer {
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

    public MediaMetadataRetriever getMediaMetadataRetriever() {
        return mediaMetadataRetriever;
    }

    public void setMediaMetadataRetriever(MediaMetadataRetriever mediaMetadataRetriever) {
        this.mediaMetadataRetriever = mediaMetadataRetriever;
    }

    public GLRender() {
        mFirstDraw = true;
        mLastTime = System.currentTimeMillis();
        TAG = getClass().getSimpleName();
        mediaMetadataRetriever = new MediaMetadataRetriever();
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();

        gl.glTranslatef(0.0f, 0.0f, -10.0f);
        gl.glRotatef(mCubeRotation, 1.0f, 1.0f, 1.0f);
        mCube.draw(gl);

        gl.glLoadIdentity();
        mCubeRotation -= 2f;

    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);

        gl.glClearDepthf(1.0f);
        gl.glEnable(GL10.GL_DEPTH_TEST);
        gl.glDepthFunc(GL10.GL_LEQUAL);

        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT,
                GL10.GL_NICEST);

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

    }


}
