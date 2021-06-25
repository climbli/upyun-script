package demo;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.IOException;

public class getBucketsConfig {

    public static void main(String[] args) throws Exception {

    getBuckets();
    }

    private  static  void  getBuckets() throws Exception {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url("http://api.upyun.com/buckets?business_type=file")
                .method("GET", null)
                .addHeader("Authorization", "Bearer 2ab0ca6c-feeb-4e70-b930-eebb9dd061ea")
                .build();
        Response response = client.newCall(request).execute();
        String string = response.body().string();
        JSONObject jsonObject = JSONObject.parseObject(string);
        JSONArray buckets = jsonObject.getJSONArray("buckets");
        String string1 = buckets.getString(1);

        System.out.println(string1);

    }

    private  static  String  getBucketConfig(){
        return "s";
    }
}
