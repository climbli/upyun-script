package demo;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.util.ArrayList;
import java.util.List;

/**
 * 获取控制台指定日期的日志
 */
public class getLogsDemo {
    public  static final String TOKEN = "";//token
    public  static final String BUCKET_NAME = "";//服务名
    public  static final String DOMAIN = "";//域名

    public  static final String START_DATE = "";//开始日期  日期格式"2021-07-01"
    public  static final String END_DATE = "";//结束日期

    public  static final String DATE = "";//日期
    //查询一天的日志需填写DATE，查询一段时间需填写开始日期-结束日期，二者选一个即可

    public static void main(String[] args) throws Exception {
        getLogs();
    }
    private static void getLogs() throws Exception {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        String url =getUrl();
        Request request = new Request.Builder()
                .url(url)
                .method("GET", null)
                .addHeader("Authorization", "Bearer "+TOKEN)
                .build();
        Response response = client.newCall(request).execute();
        String responseMes = response.body().string();
        JSONObject jsonObject = JSONObject.parseObject(responseMes);
        JSONArray data = jsonObject.getJSONArray("data");
        List<String> list =new ArrayList<String>();
        for (int i = 0; i < data.size(); i++) {
            String logsUrl = data.getJSONObject(i).getString("url");
            list.add(logsUrl);
        }
        for (int i = 0; i < list.size(); i++) {
            String s = list.get(i);
            //System.out.print(s+"|");//digger日志分析格式 ｜ 间隔
            System.out.println(s);//迅雷下载格式
        }
    }
    private static String getUrl(){
        StringBuilder sb=new StringBuilder();
        sb.append("https://api.upyun.com/analysis/archives?bucket_name=");
        sb.append(BUCKET_NAME);
        sb.append("&domain=");
        sb.append(DOMAIN);
        try {
            if (START_DATE.equals("")&&END_DATE.equals("")){
                sb.append("&date=");
                sb.append(DATE);
            }else if (DATE.equals("")){
                sb.append("&start_date=");
                sb.append(START_DATE);
                sb.append("&end_date=");
                sb.append(END_DATE);
            }
        } catch (NullPointerException e) {
            System.out.println("请检查参数是否填写");
        }
        String s = sb.toString();
        return  s;


    }
}
