package com.fanfan.robot.local.utils.bitmap;

import android.graphics.Bitmap;
import android.util.Log;

import com.fanfan.robot.local.app.common.Constants;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class BitmapUtils {

    /**
     * 将Bitmap转换成文件
     * 保存文件
     *
     * @param bm
     * @param fileName
     * @throws IOException
     */
    public static boolean saveBitmapToFile(Bitmap bm, String dirName, String fileName) {
        try {
            File dirFile = new File(Constants.PROJECT_PATH + dirName);
            if (!dirFile.exists()) {
                dirFile.mkdirs();
            }
            File myCaptureFile = new File(dirFile, fileName);
            if (!myCaptureFile.exists()) {
                myCaptureFile.createNewFile();
            }
            Log.e("myCaptureFile", myCaptureFile.getAbsolutePath());

            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));

            bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);

            bos.flush();
            bos.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
