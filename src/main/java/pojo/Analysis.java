package pojo;

import java.math.BigDecimal;

public class Analysis {

    private Integer reqs;
    private String content;
    private BigDecimal flow;

    public Integer getReqs() {
        return reqs;
    }

    public void setReqs(Integer reqs) {
        this.reqs = reqs;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public BigDecimal getFlow() {
        return flow;
    }

    public void setFlow(BigDecimal flow) {
        this.flow = flow;
    }

    @Override
    public String toString() {
        return "Analysis{" +
                "reqs='" + reqs + '\'' +
                ", content='" + content + '\'' +
                ", flow=" + flow +
                '}';
    }
}
