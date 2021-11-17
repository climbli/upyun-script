package pojo;

import java.util.List;

public class Bucket {
    private String bucketName;
    private List<String> domainList;//域名
    private List<BucketConfig> configList;//回源配置
    private String tag; //应用类型

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public List<String> getDomainList() {
        return domainList;
    }

    public void setDomainList(List<String> domainList) {
        this.domainList = domainList;
    }

    public List<BucketConfig> getConfigList() {
        return configList;
    }

    public void setConfigList(List<BucketConfig> configList) {
        this.configList = configList;
    }

    @Override
    public String toString() {
        return "Bucket{" +
                "bucketName='" + bucketName + '\'' +
                ", domainList=" + domainList +
                ", configList=" + configList +
                ", tag='" + tag + '\'' +
                '}';
    }
}
