package demo;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import okhttp3.*;

import java.io.*;
import java.util.concurrent.TimeUnit;

/**
 * 批量预热
 * 已刷新的列表txt文件位置在outputFile/purge下
 */
public class preheatDemo {

    //public  static final String FILE_PATH = "/Users/panzi/Documents/develop/upyun/purge/test.txt";//刷新文件系统位置

    public  static final String FILE_PATH = System.getProperty("user.dir") + "/outputFile/purge/20220413.txt";//需要刷新的txt文件
    public  static final String TOKEN = "9b005079-c4ef-4477-8ddb-84cf27f8ab2b";//token


    public static void main(String[] args) throws IOException {

        getData();
    }


    private static String requestPurge(String token,String url) throws Exception {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(50000, TimeUnit.MILLISECONDS)
                .readTimeout(50000, TimeUnit.MILLISECONDS)
                .build();
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, "url="+url+"&range_bytes=0-104857600");
        Request request = new Request.Builder()
                .url("https://api.upyun.com/preheat")
                .method("POST", body)
                .addHeader("Authorization", "Bearer "+token)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();
        Response response = client.newCall(request).execute();
        String string = response.body().string();
        Thread.sleep(30000);
        return string;
    }
    private static void getData() throws IOException {
        FileWriter success=new FileWriter(new File(System.getProperty("user.dir")+"/outputFile/purge/success.txt"));
        FileWriter fild=new FileWriter(new File(System.getProperty("user.dir")+"/outputFile/purge/failed.txt"));


        BufferedReader bufferedReader = null;
        try {
            String str = "";
            bufferedReader = new BufferedReader(new FileReader(FILE_PATH));
            while ((str = bufferedReader.readLine()) != null) {

                String message = requestPurge(TOKEN, str);
                JSONObject jsonObject = JSONObject.parseObject(message);
                JSONArray result = jsonObject.getJSONArray("result");
                if (result!=null){
                    JSONObject jsonObject1 = result.getJSONObject(0);
                    System.out.println(jsonObject1);
                    String code = result.getJSONObject(0).getString("code");

                    int i = Integer.parseInt(code);
                    if (i!=1){
                        fild.write(message+"\n");

                    }else{
                        success.write(message+"\n");
                    }
                }else{
                    Thread.currentThread().sleep(500);//刷新次数限制则等待0.5s
                }
                }



        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            bufferedReader.close();
            success.close();
            fild.close();
        }
        }
    }
