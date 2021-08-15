package top.yang;

import java.io.IOException;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.seimicrawler.xpath.JXDocument;
import org.seimicrawler.xpath.JXNode;
import top.yang.net.HttpManager;
import top.yang.net.response.IResponseHandler;
import top.yang.net.utils.HttpClientUtils;

public class Main {

  public static void main(String[] args) {
    OkHttpClient okHttpClient = HttpClientUtils.getOkHttpClient();
    String domain = "http://26th.cc";
    String cookie = "WMwh_2132_lastvisit=1628406538; WMwh_2132_saltkey=SYg2J1Mg; WMwh_2132_smile=1D1; WMwh_2132_home_diymode=1; WMwh_2132_st_t=0%7C1628420668%7Cafeaf66d45f6891ec1c8b472813007a2; WMwh_2132_forum_lastvisit=D_69_1628410218D_47_1628410318D_181_1628420668; WMwh_2132_secqaa=655579.e16581a942b5f45289; WMwh_2132_st_p=0%7C1628420855%7Cd50885444296803de163d9edb7073e24; WMwh_2132_viewid=tid_2351735; WMwh_2132_lastact=1628421054%09home.php%09misc; WMwh_2132_sendmail=1";
    HttpManager.httpFactory().get(okHttpClient).url(domain + "/forum.php?mod=forumdisplay&fid=181&typeid=664&typeid=664&filter=typeid&page=74")
        .addHeader("Cookie",
            cookie)
        .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4585.0 Safari/537.36 Edg/94.0.972.2")
        .enqueue(new IResponseHandler() {
          @Override
          public void onSuccess(Response response) {
            ResponseBody body = response.body();
            try {
              String string = body.string();
              Document document = Jsoup.parse(string);
              Element bodyTag = document.body();
              Element elementById = bodyTag.getElementById("threadlisttableid");
              assert elementById != null;
              Elements elements = elementById.getElementsByTag("a");
              for (Element element : elements) {
                String href = element.attr("href");
                String url = domain + "/" + href;
                HttpManager.httpFactory().get(okHttpClient).url(url).enqueue(new IResponseHandler() {
                  @Override
                  public void onSuccess(Response response) {
                    ResponseBody responseBody = response.body();
                    try {
                      String s = responseBody.string();
                      Document document1 = Jsoup.parse(s);
                      Element htmlBody = document1.body();
                      Elements elements1 = htmlBody.getElementsByClass("attnm");
                      for (Element element1 : elements1) {
                        Elements byTag = element1.getElementsByTag("a");
                        for (Element tag : byTag) {
                          String tagUrl = tag.attr("href");
                          String url1 = domain + "/" + tagUrl;
                          HttpManager.httpFactory().get(okHttpClient).url(url1)
                              .addHeader("Cookie",
                                  cookie)
                              .addHeader("User-Agent",
                                  "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4585.0 Safari/537.36 Edg/94.0.972.2")
                              .enqueue(new IResponseHandler() {
                                @Override
                                public void onSuccess(Response response) {
                                  ResponseBody responseBody1 = response.body();
                                  try {
                                    String string1 = responseBody1.string();
                                    JXDocument jxDocument = JXDocument.create(string1);
                                    JXNode jxNode = jxDocument.selNOne("/html/body/div[7]/div/div[2]/div[2]/a");
                                    String asString = jxNode.asString();
                                    System.out.println(asString);
                                  } catch (IOException e) {
                                    e.printStackTrace();
                                  }
                                }

                                @Override
                                public void onFailure(Call call, String e) {

                                }

                                @Override
                                public void onProgress(long currentBytes, long totalBytes) {

                                }
                              });
                        }
                      }
                    } catch (IOException e) {
                      e.printStackTrace();
                    }
                  }

                  @Override
                  public void onFailure(Call call, String e) {

                  }

                  @Override
                  public void onProgress(long currentBytes, long totalBytes) {

                  }
                });
              }
            } catch (IOException e) {
              e.printStackTrace();
            }

          }

          @Override
          public void onFailure(Call call, String e) {

          }

          @Override
          public void onProgress(long currentBytes, long totalBytes) {

          }
        });
    HttpManager.httpFactory().cancelAll(okHttpClient);
  }
}
