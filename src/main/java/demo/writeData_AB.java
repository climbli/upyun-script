package demo;

import bean.User;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 根据模板 账号服务名 填充 月95带宽和账单扣款信息
 * 新增用户直接在模板中添加列即可
 * 生成后的模板格式需自行更改
 */
public class writeData_AB {

    public  static final String Month = "2020-11"; //输入要查询的月份，如2021-04
    public  static final String EXCEL_PATh = System.getProperty("user.dir") + "/AB客户月份信息统计表模板.xlsx";//excel表位置
    public  static final String SAVE_PATh = System.getProperty("user.dir") + "/AB客户"+Month+"月份信息统计表.xlsx";//excel表位置




    public static void main(String[] args) throws Exception {
            WriteExcel();


    }

    private static List<User> getUserList() throws Exception {

        List<User> payIdList = getPayIdList();
        for (int i = 0; i < payIdList.size(); i++) {
            User user = payIdList.get(i);
            String payId = user.getPayId();
            String userName = user.getUserName();
            user.setBandWidth(getBandWidth(payId,userName));
        }
        return payIdList;

    }
    /**
     * 获取yue95峰值
     * https://koala.upyun.com/bill/bandwidths?account_name=cdn-admin&bill_id=20210401374114284039&bytes=bytes
     * @param payID
     * @return
     * @throws Exception
     */
    private static String getBandWidth(String payID,String username) throws Exception {
        String bandWidth = null;



        if (!payID.contains("日计费")){
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            Request request = new Request.Builder()
                    .url("https://koala.upyun.com/bill/bandwidths?account_name="+username+"&bill_id="+payID+"&bytes=bytes")
                    .method("GET", null)
                    .addHeader("x-token", "c66f3949-1226-4252-89e4-86515580d535")
                    .build();
            Response response = client.newCall(request).execute();
            String message = response.body().string();
            JSONObject jsonObject = JSONObject.parseObject(message);
            try {
                bandWidth = jsonObject.getString("MONTHLY_95");
                BigDecimal Mb=new BigDecimal(1000);
                BigDecimal bigbandwidth=new BigDecimal(bandWidth);
                BigDecimal divide = bigbandwidth.divide(Mb, 4, RoundingMode.HALF_UP);
                BigDecimal zreo=new BigDecimal(0.0000);
                int i = divide.compareTo(zreo);
                if (i==0){
                    bandWidth ="0";
                }else{
                    bandWidth= String.valueOf(divide);
                }
            } catch (NullPointerException e) {
                bandWidth = "0";
            }
        }else{
            bandWidth="非月95计费";
        }





        return bandWidth;

    }




    /**
     * 获取用户账单信息{userName='cdn-admin', payId='20201101460523086239', amount='25295.07', bandWidth='null'}
     *
     * @return
     * @throws Exception
     */
    private static List<User> getPayIdList() throws Exception {
        ArrayList<User> payIdList = new ArrayList<User>();
        List<String> usersList = ReadExcel();
        User user = null;
        String username = null;
        for (int i = 0; i < usersList.size(); i++) {
            username = usersList.get(i);

            /*//多个账号情况
            if (!username.contains("、")) {
                //单个账号
                user = getUser(username);
                payIdList.add(user);
            } else {
                String[] split = username.split("、");
                List<String> strings = Arrays.asList(split);
                for (int i1 = 0; i1 < strings.size(); i1++) {
                    user = getUser(strings.get(i1));
                    payIdList.add(user);
                }
            }*/
            user = getUser(username);
            payIdList.add(user);
        }
        return payIdList;
    }


    /**
     * 获取账单
     * @param username
     * @return
     * @throws Exception
     */
    private static User getUser(String username) throws Exception {
        User user = new User();
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        List<String> usrsList = ReadExcel();
        for (int i = 0; i < usrsList.size(); i++) {
            usrsList.get(i);

        }
        String url = getURL("https://koala.upyun.com/transaction_logs", Month, getLastTime(Month), username);
        String message = null;
        Request request = new Request.Builder()
                //.url("https://koala.upyun.com/transaction_logs?start_time=1609430400&end_time=1621439999&key=username&limit=10&value=meituan")
                .url(url)

                .method("GET", null)
                .addHeader("x-token", "c66f3949-1226-4252-89e4-86515580d535")
                .build();
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            message = response.body().string();
        }
        //获取账单接口信息
        String relation_id = null;
        String out_amount = null;
        JSONObject jsonObject = JSONObject.parseObject(message);
        //提取制定字段
        //JSONObject tran_logs = jsonObject.getJSONObject("tran_logs");
        JSONArray tran_logs = jsonObject.getJSONArray("tran_logs");
        for (int i = 0; i < tran_logs.size(); i++) {
            if (tran_logs.getJSONObject(i).getString("in_out_type").contains("MONTH_BILL")) {
                relation_id = tran_logs.getJSONObject(i).getString("relation_id");
                out_amount = tran_logs.getJSONObject(i).getString("out_amount");
            }else if(tran_logs.getJSONObject(i).getString("in_out_type").contains("DAY_BILL")){
                relation_id = "日计费账单";
                out_amount = "日计费账单";
            }


        }
        if (relation_id == null) {
            user.setPayId("未查到" + Month + "月账单");
        }else {
            user.setPayId(relation_id);
        }

        if (out_amount == null) {
            user.setAmount("0");
        }else{
            user.setAmount(out_amount);
        }


        user.setUserName(username);
        return user;
    }

    /**
     * 读取Excel用户账号数据
     *
     * @return
     * @throws Exception
     */

    private static List<String> ReadExcel() throws Exception {
        File xlsFile = new File(EXCEL_PATh);

        // 工作表
        Workbook workbook = WorkbookFactory.create(xlsFile);
        ArrayList<String> usesList = new ArrayList();
        // 表个数。
        //int numberOfSheets = workbook.getNumberOfSheets();
        //读取第一个表
        int i = 0;

        Sheet sheet = workbook.getSheetAt(i);

        // 行数。
        int rowNumbers = sheet.getLastRowNum() + 1;

        // Excel第一行。
        Row temp = sheet.getRow(0);
        if (temp == null) {
            System.out.println("第一行为空");
        }

        //列数
        int cells = temp.getPhysicalNumberOfCells();
        // 读数据。
        for (int row = 2; row < rowNumbers - 2; row++) {
            Row r = sheet.getRow(row);
               /* for (int col = 0; col < cells; col++) {
                    System.out.print(r.getCell(col).toString()+" ");
                }*/
            String username = r.getCell(1).toString();
            if (username == null || username == "" || username.equals("")) {
                continue;
            }
            usesList.add(username);

        }

        /*for (int i1 = 0; i1 < usesList.size(); i1++) {
            System.out.println(usesList.get(i1));
        }*/
        return usesList;
    }

    /**
     * 写入excel数据
     * @return
     * @throws Exception
     */
    private static void WriteExcel() throws Exception {
        FileInputStream file = new FileInputStream(new File(EXCEL_PATh));
        XSSFWorkbook workbook = new XSSFWorkbook(file);
        XSSFSheet sheetAt = workbook.getSheetAt(0);
        int lastRowNum = sheetAt.getLastRowNum();
        List<User> userList = getUserList();
        try {
            for (int i = 2; i < lastRowNum; i++) {
                XSSFRow row = sheetAt.getRow(i);
                XSSFCell userCell = row.getCell(1);
                userCell.setCellValue(userList.get(i-2).getUserName());
                XSSFCell BWcell = row.getCell(5);
                BWcell.setCellValue(userList.get(i-2).getBandWidth());
                XSSFCell payCell = row.getCell(6);
                payCell.setCellValue(userList.get(i-2).getAmount());
            }
        } catch (Exception e) {

        }finally {
            file.close();
            FileOutputStream fileOutputStream = new FileOutputStream(new File(SAVE_PATh));
            workbook.write(fileOutputStream);
            fileOutputStream.close();
        }



    }

    /**
     * 请求URL https://koala.upyun.com/transaction_logs?start_time=1609430400&end_time=1621439999&key=username&limit=10&value=meituan
     *
     * @param api
     * @param stime
     * @param etime
     * @param username
     * @return
     * @throws ParseException
     */
    private static String getURL(String api, String stime, String etime, String username) throws ParseException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(api);
        stringBuilder.append("?");
        stringBuilder.append("start_time=" + dateToStamp(stime));
        stringBuilder.append("&");
        stringBuilder.append("end_time=" + dateToStamp2(etime));
        stringBuilder.append("&");
        stringBuilder.append("key=username");
        stringBuilder.append("&");
        stringBuilder.append("limit=10");
        stringBuilder.append("&");
        stringBuilder.append("value=" + username);
        String s = stringBuilder.toString();
        return s;
    }

    /**
     * 时间戳转换
     *
     * @param s
     * @return
     * @throws ParseException
     */
    private static String dateToStamp(String s) throws ParseException {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");
        Date date = simpleDateFormat.parse(s);
        Calendar instance = Calendar.getInstance();
        instance.setTime(date);
        instance.add(Calendar.MONTH, 1);
        Date time = instance.getTime();
        long ts = time.getTime() / 1000;
        res = String.valueOf(ts);
        return res;
    }

    //时间戳转换
    private static String dateToStamp2(String s) throws ParseException {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = simpleDateFormat.parse(s);
        Calendar instance = Calendar.getInstance();
        instance.setTime(date);
        instance.add(Calendar.MONTH, 1);
        Date time = instance.getTime();
        long ts = time.getTime() / 1000;
        res = String.valueOf(ts);
        return res;
    }

    //获取月末
    private static String getLastTime(String time) throws ParseException {

        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM");

        Calendar ca = Calendar.getInstance();
        ca.setTime(format1.parse(time));
        //将小时至23
        ca.set(Calendar.HOUR_OF_DAY, 23);
        //设置为最后一天
        ca.set(Calendar.DAY_OF_MONTH, ca.getActualMaximum(Calendar.DAY_OF_MONTH));

        //将分钟至59
        ca.set(Calendar.MINUTE, 59);
        //将秒至59
        ca.set(Calendar.SECOND, 59);
        //将毫秒至999
        ca.set(Calendar.MILLISECOND, 999);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String last = format.format(ca.getTime());
        return last;
    }
}


