package com.habull.semo.mdm;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.WindowManager;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;

public class CameraService extends Service {
    protected static final int CAMERA_CALIBRATION_DELAY = 500;
    protected static long cameraCaptureStartTime;

    private CameraDevice cameraDevice;
    private CameraCaptureSession session;
    private ImageReader imageReader;

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    // so luong anh chup trong 1 phien, neu = 20 thi doi thanh camera con lai
    private int numOfImage = 0;
    public static int numConsImage = 10;

    // dung de phan biet luu anh camera truoc hay camera sau
    private boolean check = true;

    // tien to va hau to cua ten anh can luu
    private String prefix = "IMG_rear";
    private String postfix = ".jpg";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        CameraManager manager = (CameraManager) getSystemService(CAMERA_SERVICE);

        readyCamera(getBackCamera(manager));

        return super.onStartCommand(intent, flags, startId);
    }

    // h??m l???y id c???a camera c???n d??ng ????? l??m tham s??? cho h??m openCamera
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public String getFrontCamera(CameraManager manager) {
        try {
            for (String cameraId: manager.getCameraIdList()) {
                CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);

                // dung camera truoc
                if (characteristics.get(CameraCharacteristics.LENS_FACING)
                        == CameraCharacteristics.LENS_FACING_FRONT) {
                    return cameraId;
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // h??m l???y id c???a camera c???n d??ng ????? l??m tham s??? cho h??m openCamera
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public String getBackCamera(CameraManager manager) {
        try {
            for (String cameraId: manager.getCameraIdList()) {
                CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);

                // dung camera sau
                if (characteristics.get(CameraCharacteristics.LENS_FACING)
                        == CameraCharacteristics.LENS_FACING_BACK) {
                    return cameraId;
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // h??m m??? camera, n?? g???i ?????n callback CameraDevice.StateCallback
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void readyCamera(String pickedCamera) {
        CameraManager manager = (CameraManager) getSystemService(CAMERA_SERVICE);

        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED)
                return;

            manager.openCamera(pickedCamera, cameraStateCallback, null);
            imageReader = ImageReader.newInstance(1920, 1088, ImageFormat.JPEG, 2 /* images buffered */);
            imageReader.setOnImageAvailableListener(onImageAvailableListener, null);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // callback qu???n l?? tr???ng th??i camera
    protected CameraDevice.StateCallback cameraStateCallback = new CameraDevice.StateCallback() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onOpened(CameraDevice camera) {
            // n???u m??? th??nh c??ng th?? g??n bi???n cameraDevice ???? c?? = camera
            cameraDevice = camera;

            try {
                cameraDevice.createCaptureSession(Arrays.asList(imageReader.getSurface()),
                        sessionStateCallback, null);
            }
            catch (Exception e) {
                System.out.println(e);
            }
        }

        // n???u m??? kh??ng th??nh c??ng
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onDisconnected(CameraDevice camera) {
            camera.close();
            cameraDevice = null;
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onError(CameraDevice camera, int error) {
            camera.close();
            cameraDevice = null;
        }
    };

    // h??m t???o tham s??? th??? nh???t cho h??m setRepeatingRequest cho callback CameraCaptureSession
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected CaptureRequest createCaptureRequest() {
        try {
            CaptureRequest.Builder builder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);

            builder.addTarget(imageReader.getSurface());

            WindowManager windowManager = (WindowManager) getApplicationContext().getSystemService(WINDOW_SERVICE);
            int rotation = windowManager.getDefaultDisplay().getRotation();
            builder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));

            return builder.build();
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    // Callback ????? theo d??i ti???n tr??nh c???a camera
    protected CameraCaptureSession.StateCallback sessionStateCallback = new CameraCaptureSession.StateCallback() {

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onConfigured(CameraCaptureSession session) {
            CameraService.this.session = session;

            try {
                session.setRepeatingRequest(createCaptureRequest(), null, null);
                cameraCaptureStartTime = System.currentTimeMillis();
            } catch (Exception e) {
                System.out.println(e);
            }
        }

        @Override
        public void onConfigureFailed(CameraCaptureSession session) {
        }
    };

    // tr??nh x??? l?? khi ???nh ???????c ch???p
    protected ImageReader.OnImageAvailableListener onImageAvailableListener = new ImageReader.OnImageAvailableListener() {

        // h??m n??y ???????c g???i sau khi m???t ???nh ???????c ch???p
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onImageAvailable(ImageReader reader) {
            Image img = reader.acquireLatestImage();

            if (img != null) {
                if (System.currentTimeMillis() > cameraCaptureStartTime + CAMERA_CALIBRATION_DELAY)
                    processImage(img);

                // ????ng ???nh sau khi x??? l??, n???u kh??ng th?? kh??ng th??? ch???p ???????c ???nh kh??c
                img.close();
            }
        }
    };

    // h??m x??? l?? ???nh sau ch???p: l??u ???nh v??o th?? m???c "sdcard/Pictures"
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void processImage(Image image) {
        ByteBuffer buffer;
        byte[] bytes;

        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        String stringDate = df.format(Calendar.getInstance().getTime());
        String nameImage = prefix + "_" + stringDate + postfix;
        numOfImage++;

        if (numOfImage == numConsImage) {
            closeCamera();
            prefix = "IMG_front";

            CameraManager manager = (CameraManager) getSystemService(CAMERA_SERVICE);
            readyCamera(getFrontCamera(manager));
        }

        if (numOfImage == 2*numConsImage)
            closeCamera();

        File file = new File(Environment.getExternalStorageDirectory() + "/Pictures/" + nameImage);
        FileOutputStream output = null;

        if (image.getFormat() == ImageFormat.JPEG) {
            buffer = image.getPlanes()[0].getBuffer();
            bytes = new byte[buffer.remaining()]; // makes byte array large enough to hold image
            buffer.get(bytes); // copies image from buffer to byte array

            try {
                output = new FileOutputStream(file);
                output.write(bytes);    // write the byte array to file
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                image.close(); // close this to free up buffer for other images

                if (output != null) {
                    try {
                        output.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onDestroy() {
        try {
            session.abortCaptures();
        } catch (Exception e) {
            System.out.println(e);
        }

        session.close();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void closeCamera() {
        try {
            if (null != session) {
                session.close();
                session = null;
            }
            if (null != cameraDevice) {
                cameraDevice.close();
                cameraDevice = null;
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
