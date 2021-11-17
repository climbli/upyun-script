package demo;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import pojo.Bucket;
import pojo.BucketConfig;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *获取客户账号下所有 CDN 服务回源配置 和应用场景
 * 其他字段可根据文档自定义,默认数量 100 个服务单位,超过需要手动输入翻页参数
 *又拍云API文档：https://api.upyun.com/doc#/api/guide/overview
 *
 */
public class bucketsConfigDemo2 {
    public  static final String TOKEN = "";//用户token
    public  static final String TXT_PATH =System.getProperty("user.dir")+"/outputFile/configMES/bucketsconfig.txt";//配置信息输出文件位置


    public static void main(String[] args) throws Exception {
        getBuckets();
    }

    /**
     * 获取CDN服务
     * @throws Exception
     */
    private  static  void  getBuckets() throws Exception {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url("http://api.upyun.com/buckets?business_type=file&type=ucdn&limit=400")
                .method("GET", null)
                .addHeader("Authorization", "Bearer "+TOKEN)
                .build();
        Response response = client.newCall(request).execute();
        String string = response.body().string();
        JSONObject jsonObject = JSONObject.parseObject(string);
        JSONArray buckets = jsonObject.getJSONArray("buckets");
        if (buckets!=null){
            List<Bucket> bucketList=new ArrayList<Bucket>();
            List<String> tagList = new ArrayList<String>();
            for (int i = 0; i < buckets.size(); i++) {
                Bucket bucket=new Bucket();
                List<String> bucketDomainList=new ArrayList<String>();
                String o = buckets.get(i).toString();
                JSONObject jsonObject1 = JSONObject.parseObject(o);
                String bucket_name = jsonObject1.getString("bucket_name");
                String tag = jsonObject1.getString("tag");
                if (tag== null){
                    bucket.setTag("默认");
                }else if (tag.equals("picture")){
                    bucket.setTag("网页图片");
                }else if(tag.equals("download")){
                    bucket.setTag("文件下载");
                }else if(tag.equals("vod")){
                    bucket.setTag("音视频点播");
                }else if(tag.equals("dynamic")){
                    bucket.setTag("动态内容");
                }else if(tag.equals("fullSite")){
                    bucket.setTag("全站加速");
                }else{
                    bucket.setTag("默认");
                }
                bucket.setBucketName(bucket_name);
                JSONArray domains = jsonObject1.getJSONArray("domains");//获取服务下的域名
                if (domains!=null){
                    for (int i1 = 0; i1 < domains.size(); i1++) {
                        String domain = domains.getJSONObject(i1).getString("domain");
                        bucketDomainList.add(domain);
                    }
                    bucket.setDomainList(bucketDomainList);
                }

                Bucket bucketConfig = getBucketConfig(bucket);
                bucketList.add(bucketConfig);
            }

            writeFile(bucketList);
        }else{
            System.out.println(string);
        }


    }

    /**
     * 获取单个服务的回源IP信息
     * @param
     * @return
     * @throws Exception
     */
    private  static Bucket getBucketConfig(Bucket bucket) throws Exception {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url("http://api.upyun.com/v2/buckets/cdn/source?bucket_name="+bucket.getBucketName()+"&isp_return=false")
                .method("GET", null)
                .addHeader("Authorization", "Bearer "+TOKEN)
                .build();
        Response response = client.newCall(request).execute();
        String message = response.body().string();
        JSONObject jsonObject = JSONObject.parseObject(message);
        List<BucketConfig> bucketConfigList=new ArrayList<BucketConfig>();
       JSONArray servers = jsonObject.getJSONObject("data").getJSONObject("cdn").getJSONArray("servers");
       if (servers!=null){

           for (int i = 0; i < servers.size(); i++) {
               BucketConfig bucketConfig=new BucketConfig();
               String host = servers.getJSONObject(i).getString("host");
               String port = servers.getJSONObject(i).getString("port");
               String weight = servers.getJSONObject(i).getString("weight");
               String max_fails = servers.getJSONObject(i).getString("max_fails");
               String fail_timeout = servers.getJSONObject(i).getString("fail_timeout");
               try {
                   String backup = servers.getJSONObject(i).getString("backup");
                   if (bucket==null||backup.equals("false")){
                       bucketConfig.setBackup("主线路");
                   }else if (backup!=null||backup.equals("ture")){
                       bucketConfig.setBackup("备用线路");
                   }
               } catch (NullPointerException e) {
                   bucketConfig.setBackup("主线路");
               }

               bucketConfig.setHost(host);
               bucketConfig.setPort(port);
               bucketConfig.setWeight(weight);
               bucketConfig.setMax_fails(max_fails);
               bucketConfig.setFail_timeout(fail_timeout);
               bucketConfigList.add(bucketConfig);
           }
       }
       bucket.setConfigList(bucketConfigList);

        return bucket;

    }

    /**
     * 写出数据
     *
     */
    private static void writeFile(List<Bucket> bucketList) throws Exception {
        File  bucketsconfig=new File(TXT_PATH);
        JSONArray json=new JSONArray();
        for (int i = 0; i < bucketList.size(); i++) {
            JSONObject jsonObject=new JSONObject();
            jsonObject.put("服务名",bucketList.get(i).getBucketName());
            jsonObject.put("应用类型",bucketList.get(i).getTag());
            jsonObject.put("域名",bucketList.get(i).getDomainList());
            jsonObject.put("配置信息",bucketList.get(i).getConfigList());
            json.add(jsonObject);
        }

        try {
            if (!bucketsconfig.exists()) {
                bucketsconfig.createNewFile();
            } else {
                bucketsconfig.delete();
                bucketsconfig.createNewFile();
            }
            FileWriter fileWritter = new FileWriter(bucketsconfig, false);
            BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
            bufferWritter.write(json.toString());
            bufferWritter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
