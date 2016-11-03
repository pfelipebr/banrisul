package banrisul.ibm.com.banrisulmobileapp.database;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by renatosilva on 22/10/16.
 */

public class DbBarinsul {


    /**
     * To save datas in Andorid
     * @param context
     * @param fileName
     * @param mJsonResponse
     */
    public static void mCreateAndSaveFile(Context context, String fileName, String mJsonResponse) {
        try {

            String filePath = context.getApplicationContext().getFilesDir().getAbsolutePath() + "/" + fileName;
            FileWriter file = new FileWriter(filePath);
            file.write(mJsonResponse);
            file.flush();
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * to get datas in Android
     * @param context
     * @param fileName
     * @return
     */
    public static String mReadJsonData(Context context, String fileName) {

        String mResponse = null;
        File mFile = null;
        try {
                // verifica se o arquivo existe
                mFile = new File(context.getApplicationContext().getFilesDir().getAbsolutePath() + "/" + fileName);

                if (mFile.exists()) {

                    String filePath = context.getApplicationContext().getFilesDir().getAbsolutePath() + "/" + fileName;

                    File f = new File(filePath);
                    FileInputStream is = new FileInputStream(f);
                    int size = is.available();
                    byte[] buffer = new byte[size];
                    is.read(buffer);
                    is.close();
                    mResponse = new String(buffer);
                }
        } catch (IOException e) {

            e.printStackTrace();
        }

        return mResponse;
    }
}
