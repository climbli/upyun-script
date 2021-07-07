package demo;

import okhttp3.*;


/**
 * 创建TOKEN Demo
 * 参数详见又拍云API文档：https://api.upyun.com/doc#/api/guide/overview
 */
public class creatTokenDemo {
    public  static final String USERNAME = "";//账户名
    public  static final String PASSWORD = "";//账户密码
    public  static final String CODE = "";//20-32随机字符串，每次提交不可重复，只能包含数字，字母和中划线。
    public  static final String NAME = "";//备注
    public  static final String SCOPE = "";//参数详见文档


    public static void main(String[] args) throws Exception {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType,
                "username="+USERNAME+"&password="+PASSWORD+"&code="+CODE+"&name="+NAME+"&scope="+SCOPE);
        Request request = new Request.Builder()
                .url("https://api.upyun.com/oauth/tokens")
                .method("POST", body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();
        Response response = client.newCall(request).execute();
        String token = response.body().string();
        System.out.println(token);
    }
}
