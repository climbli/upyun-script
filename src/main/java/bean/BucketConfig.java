package bean;

public class BucketConfig {

    private  String host;  //回源IP
    private  String port;  //端口
    private  String weight; //权重
    private  String max_fails;  //最大失败次数
    private  String fail_timeout; //静默时间
    private  String backup; //是否备用默认空

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getMax_fails() {
        return max_fails;
    }

    public void setMax_fails(String max_fails) {
        this.max_fails = max_fails;
    }

    public String getFail_timeout() {
        return fail_timeout;
    }

    public void setFail_timeout(String fail_timeout) {
        this.fail_timeout = fail_timeout;
    }

    public String getBackup() {
        return backup;
    }

    public void setBackup(String backup) {
        this.backup = backup;
    }

    @Override
    public String toString() {
        return "BucketConfig{" +
                "host='" + host + '\'' +
                ", port='" + port + '\'' +
                ", weight='" + weight + '\'' +
                ", max_fails='" + max_fails + '\'' +
                ", fail_timeout='" + fail_timeout + '\'' +
                ", backup='" + backup + '\'' +
                '}';
    }
}
