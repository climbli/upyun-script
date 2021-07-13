package demo;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import pojo.Analysis;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 查询单个URL的一段时间的访问流量和次数
 * 又拍云日志分析文档https://api.upyun.com/doc#/api/operation/log/GET%20%2Fanalysis
 * 输出结果为URL:/在2021-07-01---2021-07-12时间的请求次数为43,流量总数为6048
 */

public class getLogAnalysisDemo {
    public  static final String TOKEN = "";//token
    public  static final String BUCKET_NAME = "";//服务名
    public  static final String DOMAIN = "";//域名 服务名最少选择一个
    public  static final String DATE = "2021-07-01";//开始日期 格式"2012-01-01"
    public  static final String END_DATE = "2021-07-12";//结束开始日期，单天可不用写"
    public static void main(String[] args) throws Exception {

        getALLData("/");//获取一段时间的流量总数，填写要查询的URL
    }

    /**
     * 根据URL查询一段时间的总的数据
     *url  要查询的url
     */
    private static void getALLData(String url) throws Exception {
        List<String> dates = getDates();
        List<BigDecimal> flowList=new ArrayList<BigDecimal>();
        List<Integer> reqsList=new ArrayList<Integer>();
        for (int i = 0; i < dates.size(); i++) {
            String o = dates.get(i);
            Analysis dateData = getDateData(url, o);
            if (dateData==null){
                    continue;
            }else{
                flowList.add(dateData.getFlow());
                reqsList.add(dateData.getReqs());
            }

        }
        //计算flow的值
        BigDecimal b=new BigDecimal(0);
        for (int i = 0; i < flowList.size(); i++) {
            if (flowList.get(i)==null){
                continue;
            }else{
                b=flowList.get(i).add(b);
            }

        }
        //计算请求数reqs的值
        Integer integer=0;
        for (int i = 0; i < reqsList.size(); i++) {
            if (reqsList.get(i)==null){
                continue;
            }else{
                integer=reqsList.get(i)+integer;
            }

        }
        System.out.println("URL:"+url+"在"+DATE+"---"+END_DATE+"时间的请求次数为"+integer+",流量总数为"+b);
    }
    /**
     * 获取单天的访问分析数据
     */
    private static String getgetLogAnalysis(String date) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        String url =addURL(date);
        Request request = new Request.Builder()
                .url(url)
                .method("GET", null)
                .addHeader("Authorization", "Bearer "+TOKEN)
                .build();
        Response response = client.newCall(request).execute();
        String string = response.body().string();
        return string;
    }

    /**
     * 根据URL查询一天内的总数据
     * url  需要查询的URL
     */
    private static Analysis getDateData(String url,String date) throws IOException {
        String requestdata = getgetLogAnalysis(date);
        JSONObject jsonObject = JSONObject.parseObject(requestdata);
        JSONArray data = jsonObject.getJSONArray("data");
         Analysis analysis=new Analysis();
         if (data!=null){
             for (int i = 0; i < data.size(); i++) {
                 String content = data.getJSONObject(i).getString("content");
                 if (content.length()!=0&&content.equals(url)){
                     analysis.setContent(content);
                     String flow = data.getJSONObject(i).getString("flow");
                     BigDecimal bigDecimal = new BigDecimal(flow);
                     analysis.setFlow(bigDecimal);
                     analysis.setReqs(Integer.parseInt(data.getJSONObject(i).getString("reqs")));
                 }else {
                     continue;
                 }
             }
         }
        return analysis;
    }


    /**
     * 拼接请求分析的的URL
     * @return
     * date 日期
     */
    private static  String addURL(String date){
        StringBuilder sb=new StringBuilder();
        sb.append("http://api.upyun.com/analysis?");
        if (BUCKET_NAME!=null||BUCKET_NAME.length()!=0){
            sb.append("bucket_name=");
            sb.append(BUCKET_NAME);
        }
        if (DOMAIN!=null||DOMAIN.length()!=0){
            sb.append("&domain=");
            sb.append(DOMAIN);
        }
        if(BUCKET_NAME.length()==0&&DOMAIN.length()==0){
            System.out.println("服务名和域名必须选一个");
        }
        sb.append("&date="+date+"&type=url&order_by=1");


        String s = sb.toString();


        return s;
    }

    /**
     * 根据开始结束时间获取每天的List
     * @return
     * @throws Exception
     */
    private static List getDates() throws Exception {
            List<String> als = new ArrayList();
        try {
            String startDate = DATE;
            String endDate = END_DATE;
            int year = 0, month = 0, day = 0, length = 0;
            int time = 1000 * 24 * 60 * 60;
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
            Date date1 = dateformat.parse(startDate);
            Date date2 = dateformat.parse(endDate);
            calendar.setTime(date1);
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DATE);
            length = (int) ((date2.getTime() - date1.getTime()) / time);
            for (int i = 0; i <= length; i++) {
                calendar.set(year, month, day + i);
                String str = dateformat.format(calendar.getTime());
                als.add(str);
            }
        } catch (ParseException e) {
            System.out.println("时间格式有误");
        }
        return als;


    }

}
