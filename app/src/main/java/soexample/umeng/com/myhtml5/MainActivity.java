package soexample.umeng.com.myhtml5;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.media.session.MediaController;
import android.net.Uri;

import android.os.Build;
import android.os.Bundle;


import android.support.v4.app.FragmentActivity;



import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;

import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;

import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;


import java.io.IOException;

public class MainActivity extends FragmentActivity implements TextureView.SurfaceTextureListener ,View.OnClickListener{


    private LinearLayout mLinearLayout;
    private Uri mVideoUri;
    private MediaPlayer player;
    private String[] permission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private float dX, dY;
    private Camera mCamera;
    private CameraPreview mCameraPreview;
    private int surfaceWidth;
    private int surfaceHeight;
    private String path;
    private TextureView surface;
    private VideoTextureRenderer renderer;
    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);


    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(permission, 100);
        }
        surface = (TextureView) findViewById(R.id.surface);
        surface.setSurfaceTextureListener(this);
        mButton = (Button) findViewById(R.id.video_button);
        mButton.setOnClickListener(this);
        mCamera = getCameraInstance();
        mCameraPreview = new CameraPreview(this, mCamera);
        RelativeLayout r = (RelativeLayout) findViewById(R.id.container);
        r.addView(mCameraPreview,0);
        mLinearLayout = (LinearLayout) findViewById(R.id.cam_container);
        mLinearLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.d("touch", "touch event:" + event.toString());
                        dX = v.getX() - event.getRawX();
                        dY = v.getY() - event.getRawY();

                        break;
                    case MotionEvent.ACTION_MOVE:
                        Log.d("touch", "touch event:" + event.toString());
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
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height)
    {
        surfaceWidth = width;
        surfaceHeight = height;
        startPlaying();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface)
    {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (player != null)
            player.release();
        if (renderer != null)
            renderer.onPause();

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.video_button:
                Intent intent = new Intent();
                intent.setType("video/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 1);
                break;
        }
    }

    private void startPlaying()
    {
        renderer = new VideoTextureRenderer(this, surface.getSurfaceTexture(), surfaceWidth, surfaceHeight);
        player = new MediaPlayer();

        try
        {
            if(!TextUtils.isEmpty(path)){
            player.setDataSource(path);
            player.setSurface(new Surface(renderer.getVideoTexture()));
            player.setLooping(true);
            player.prepare();
            renderer.setVideoSize(player.getVideoWidth(), player.getVideoHeight());
            player.start();
            }

        }
        catch (IOException e)
        {
            throw new RuntimeException("Could not open input video!");
        }
    }



    @Override
    public boolean onKeyDown(int keycode, KeyEvent e) {
        switch(keycode) {
            case KeyEvent.KEYCODE_MENU:
                Intent intent = new Intent();
                intent.setType("video/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 1);
                return true;
        }

        return super.onKeyDown(keycode, e);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            mVideoUri = data.getData();
            try {
                if (Build.VERSION.SDK_INT >= 19) {
                    path = DeviceUtil.getRealPathFromURI_API19(this, mVideoUri);
                } else if (Build.VERSION.SDK_INT >= 11) {
                    path = DeviceUtil.getRealPathFromURI_API11to18(this, mVideoUri);
                }
                if (!TextUtils.isEmpty(path)) {
                    //startPlaying();
                    Log.d("VideoPath",path);

                } else {
                    Toast.makeText(this, "Failed to Load Video", Toast.LENGTH_SHORT).show();
                }
            } catch (NullPointerException e) {
                Log.e("E", e.getMessage().toString());
            }
        }
    }





    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }
}
