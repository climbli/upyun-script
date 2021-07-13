package pojo;

import java.math.BigDecimal;

public class Analysis {

    private String reqs;
    private String content;
    private BigDecimal flow;

    public String getReqs() {
        return reqs;
    }

    public void setReqs(String reqs) {
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
