package JwSpider;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
public class Test {
//    RequestConfig config= RequestConfig.custom().setRedirectsEnabled(false).build();//不允许重定向
    private String accout;
    private String password;
    static CookieStore cookieStore = new BasicCookieStore();
    static HttpClient client = HttpClients.custom().setDefaultCookieStore(cookieStore).build();//实例化httpclient
    HttpResponse response = null;
    String rawHtml;
    public Test(String accout, String password) {
        super();
        this.accout = accout;
        this.password = password;
    }

    public void  login()  {
    	/*
    	 * 图片验证码的产生后有一个随机参数，就是类似与时间戳的东西（青大用的随机数）产生的，
    	 * 这里为什么要先访问下教务处的登录界面呢，就是因为你先访问了教务处登录界面之后，你在访问图片验证码的地址之后，验证码不会在改变
    	 *
    	 */
    	String result = null;
        HttpGet getLoginPage = new HttpGet("http://jw.qdu.edu.cn/academic/common/security/login.jsp?login_error=1");//教务处登陆页面get
        
        try {
        	String code;
            client.execute(getLoginPage);
            //获取验证码
            getVerifyingCode(client);
            System.out.println("请输入验证码：");
            Scanner in = new Scanner(System.in);
            code = in.nextLine();
           in.close();
            //设定post参数，和上图中的内容一致
            ArrayList<NameValuePair> postData = new ArrayList<NameValuePair>();
            postData.add(new BasicNameValuePair("groupId", null));//这里就是埋的hidden
            postData.add(new BasicNameValuePair("j_username", accout));//学号
            postData.add(new BasicNameValuePair("j_password", password));//密码
            postData.add(new BasicNameValuePair("j_captcha", code));//验证码
            HttpPost post = new HttpPost("http://jw.qdu.edu.cn/academic/j_acegi_security_check");//构建post对象
            post.setEntity(new UrlEncodedFormEntity(postData));
            response = client.execute(post);
        } catch (ClientProtocolException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
    
    void getVerifyingCode(HttpClient client) {//这里使用的是把验证码下载下来的方式，分来想用自动识别的，但是自动识别的正确率太低了
        HttpGet getVerifyCode = new HttpGet("http://jw.qdu.edu.cn/academic/getCaptcha.do");//验证码get
        FileOutputStream fileOutputStream = null;
        HttpResponse response;
        try {
            response = client.execute(getVerifyCode);//获取验证码
            fileOutputStream = new FileOutputStream(new File("d:/verifyCode.jpeg"));
            response.getEntity().writeTo(fileOutputStream);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public void getInfo() throws ClientProtocolException, IOException{//使用jsoup对教务处进行解析
    	HttpResponse response=client.execute(new HttpGet("http://jw.qdu.edu.cn/academic/showPersonalInfo.do")) ;
    	Document doc=Jsoup.parse(EntityUtils.toString(response.getEntity()).replace("&nbsp", ""));
   doc.setBaseUri("http://jw.qdu.edu.cn/");    
    	Elements elements=doc.select("table.form tr");
    	for (Element element : elements) {
    		Elements tit=	element.select("th");
    		Elements info=	element.select("td");
    		for(int i=0;i<tit.size();i++){
    		System.out.println(tit.get(i).text()+":"+info.get(i).text());	
    		}
		}
    }
    public static void main(String[] args){  
        Test test = new Test("你的学号", "你的密码");  
        try {
			test.login();
			test.getInfo();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
    }  



	
}
