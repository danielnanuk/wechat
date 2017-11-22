package me.nielcho.wechat.request;

import lombok.Data;

import java.util.List;

@Data
public class StatReportRequest {
  private BaseRequest BaseRequest;
  private Integer Count = 1;
  private List<Info> List;

  @Data
  public static class Info {
    private Integer Type = 1;
    private String Text;
  }
}
