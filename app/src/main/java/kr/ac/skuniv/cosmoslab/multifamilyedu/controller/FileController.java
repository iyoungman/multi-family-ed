package kr.ac.skuniv.cosmoslab.multifamilyedu.controller;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import kr.ac.skuniv.cosmoslab.multifamilyedu.model.entity.UserModel;
import kr.ac.skuniv.cosmoslab.multifamilyedu.network.NetRetrofit;
import okhttp3.Headers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * MultiFamilyEdu_Android
 * Class: FileController
 * Created by youngjun on 2018-11-27.
 * <p>
 * Description:
 */
public class FileController {
    private static final String TAG = "FileController";
    private static final String FILE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/MultiFamily";
    private UserModel userModel;
    Context context;

    public FileController(Context context) {
        this.context = context;
    }

    public FileController(UserModel userModel, Context context) {
        this.userModel = userModel;
        this.context = context;
    }

    //Level로 랜덤으로 파일 다운로드하는 메소드
    public void downloadFileByLevel() {
        Call<ResponseBody> res = NetRetrofit.getInstance().getNetRetrofitInterface().downloadFileByLevel(userModel.getLevel());
        Log.i(TAG, "start");

        res.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String fileName = getFileName(response.headers());
                    boolean writtenToDisk = writeFileToDisk(response.body(), userModel.getLevel(), fileName);

                    if (writtenToDisk) {
                        Toast.makeText(context.getApplicationContext(), "파일 다운로드 성공", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(context.getApplicationContext(), "파일 다운로드 실패", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i(TAG, e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.i(TAG, t.getMessage());
                Toast.makeText(context.getApplicationContext(), "인터넷 연결 실패", Toast.LENGTH_LONG).show();
            }
        });
    }

    //파일이름으로 파일 다운로드하는 메소드
    public void downloadFileByFileName(final String fileName) {
        Call<ResponseBody> res = NetRetrofit.getInstance().getNetRetrofitInterface().downloadFileByFileName("1", fileName);
        Log.i(TAG, "start");

        res.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    boolean writtenToDisk = writeFileToDisk(response.body(), "1", fileName);

                    if (writtenToDisk) {
                        Toast.makeText(context.getApplicationContext(), "파일 다운로드 성공", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(context.getApplicationContext(), "파일 다운로드 실패", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(context.getApplicationContext(), "파일 다운로드 실패", Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.i(TAG + "실패", t.getMessage());
                Toast.makeText(context.getApplicationContext(), "인터넷 연결 실패", Toast.LENGTH_LONG).show();
            }
        });
    }

    //파일 디렉토리 만드는 메소드
    public void createFilePath() {
        File originalDir1 = new File(FILE_PATH + "/ORIGINAL" + "/1");
        File originalDir2 = new File(FILE_PATH + "/ORIGINAL" + "/2");
        File recodeDir1 = new File(FILE_PATH + "/RECORD" + "/1");
        File recodeDir2 = new File(FILE_PATH + "/RECORD" + "/2");
        File ImageDir = new File(FILE_PATH + "/IMAGE");

        if (!originalDir1.exists() || !recodeDir1.exists() || !ImageDir.exists()) {
            originalDir1.mkdirs();
            originalDir2.mkdirs();
            recodeDir1.mkdirs();
            recodeDir2.mkdirs();
            ImageDir.mkdir();
        }
    }

    //디렉토리에 파일있는지 확인하는 메소드
    public boolean confirmFile(String level, String fileName) {
        File file = new File(FILE_PATH + "/ORIGINAL/" + level + "/" + fileName);
        if (!file.exists()) {
            return false;
        } else {
            return true;
        }
    }

    //Response Header에서 파일이름 추출하는 메소드
    private String getFileName(Headers headers) {
        String fileName = headers.get("Content-Disposition");
        Log.i(TAG + "filename=", fileName);

        int index = fileName.indexOf("filename=");
        fileName = fileName.substring(index + 9, fileName.length());
        return fileName;
    }

    //파일 저장하는 메소드
    private boolean writeFileToDisk(ResponseBody responseBody, String level, String fileName) {
        try {
            // todo change the file location/name according to your needs
            File futureStudioIconFile = new File(FILE_PATH + "/ORIGINAL/" + level + File.separator + fileName);

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];
                long fileSize = responseBody.contentLength();
                long fileSizeDownloaded = 0;
                inputStream = responseBody.byteStream();
                outputStream = new FileOutputStream(futureStudioIconFile);

                while (true) {
                    int read = inputStream.read(fileReader);
                    if (read == -1) {
                        break;
                    }
                    outputStream.write(fileReader, 0, read);
                    fileSizeDownloaded += read;
                    Log.i(TAG, "file download: " + fileSizeDownloaded + " of " + fileSize);
                }
                outputStream.flush();
                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }

}
