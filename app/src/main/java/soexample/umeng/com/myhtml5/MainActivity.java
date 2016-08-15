package soexample.umeng.com.myhtml5;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.session.MediaController;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.CursorLoader;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.VideoView;

import com.umeng.comm.core.utils.DeviceUtils;

import java.io.IOException;

public class MainActivity extends FragmentActivity implements View.OnClickListener,SurfaceHolder.Callback{

    private GLSurfaceView mGlSurfaceView;
    private LinearLayout mLinearLayout;
    //private SurfaceView mSurfaceViewSmall;
    private Uri mVideoUri;
    private Button mVideoButton;
    //private SurfaceHolder mSurfaceHolder;
    private MediaPlayer mMediaPlayer;
    private VideoView mVideoView;
    private String [] permission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
    float dX, dY;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        mMediaPlayer = new MediaPlayer();
        mGlSurfaceView = (GLSurfaceView) findViewById(R.id.gl_surfaceview);
        mGlSurfaceView.setEGLContextClientVersion(1);
        mGlSurfaceView.setPreserveEGLContextOnPause(true);
        mGlSurfaceView.setRenderer(new GL2SurfaceRender());
        if(Build.VERSION.SDK_INT>=23){
            requestPermissions(permission,100);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mVideoView = (VideoView) findViewById(R.id.video_view);
        mVideoView.setZOrderOnTop(true);

        mLinearLayout = (LinearLayout) findViewById(R.id.cam_container);
        mVideoButton = (Button) findViewById(R.id.video_button);
        mVideoButton.setOnClickListener(this);

//        mSurfaceViewSmall = (SurfaceView) findViewById(R.id.gl_surfaceview_small);
//        mSurfaceViewSmall.setZOrderOnTop(true);
//
//        mSurfaceHolder = mSurfaceViewSmall.getHolder();
        //mSurfaceHolder.setFixedSize(DeviceUtils.dp2px(this,100),DeviceUtils.dp2px(this,200));
        //mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        mLinearLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        Log.d("touch","touch event:"+event.toString());
                        dX = v.getX() - event.getRawX();
                        dY = v.getY() - event.getRawY();

                        break;
                    case MotionEvent.ACTION_MOVE:
                        Log.d("touch","touch event:"+event.toString());
                        mVideoView.setMediaController(null);
                        v.animate()
                                .x(event.getRawX() + dX)
                                .y(event.getRawY() + dY)
                                .setDuration(0)
                                .start();
                        break;

                }
                return true;
            }
        });

    }



    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mMediaPlayer.setDisplay(holder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.video_button:
                Intent intent = new Intent();
                intent.setType("video/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 1);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        mSurfaceHolder.addCallback(this);
//        mSurfaceHolder.setFixedSize(mLinearLayout.getMeasuredWidth(),mLinearLayout.getMeasuredHeight());
        mGlSurfaceView.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
        mGlSurfaceView.onPause();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==RESULT_OK){
            mVideoUri = data.getData();
            String path="";
            try {
                if (Build.VERSION.SDK_INT >= 19) {
                    path = getRealPathFromURI_API19(this, mVideoUri);
                } else if (Build.VERSION.SDK_INT >= 11) {
                    path = getRealPathFromURI_API11to18(this, mVideoUri);
                }
                mVideoView.setVideoPath(path);
                mVideoView.start();

            }catch (NullPointerException e){
                Log.e("E",e.getMessage().toString());
            }
        }
    }

    @SuppressLint("NewApi")
    public static String getRealPathFromURI_API11to18(Context context, Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        String result = null;

        CursorLoader cursorLoader = new CursorLoader(
                context,
                contentUri, proj, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();

        if(cursor != null){
            int column_index =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            result = cursor.getString(column_index);
        }
        return result;
    }


    @SuppressLint("NewApi")
    public static String getRealPathFromURI_API19(Context context, Uri uri){
        String filePath = "";
        String wholeID = DocumentsContract.getDocumentId(uri);

        // Split at colon, use second item in the array
        String id = wholeID.split(":")[1];

        String[] column = { MediaStore.Video.Media.DATA };

        // where id is equal to
        String sel = MediaStore.Video.Media._ID + "=?";

        Cursor cursor = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                column, sel, new String[]{ id }, null);

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();
        Log.e("path",filePath);
        return filePath;
    }
}
