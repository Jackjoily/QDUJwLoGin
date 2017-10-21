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
//    RequestConfig config= RequestConfig.custom().setRedirectsEnabled(false).build();//�������ض���
    private String accout;
    private String password;
    static CookieStore cookieStore = new BasicCookieStore();
    static HttpClient client = HttpClients.custom().setDefaultCookieStore(cookieStore).build();//ʵ����httpclient
    HttpResponse response = null;
    String rawHtml;
    public Test(String accout, String password) {
        super();
        this.accout = accout;
        this.password = password;
    }

    public void  login()  {
    	/*
    	 * ͼƬ��֤��Ĳ�������һ���������������������ʱ����Ķ���������õ�������������ģ�
    	 * ����ΪʲôҪ�ȷ����½��񴦵ĵ�¼�����أ�������Ϊ���ȷ����˽��񴦵�¼����֮�����ڷ���ͼƬ��֤��ĵ�ַ֮����֤�벻���ڸı�
    	 *
    	 */
    	String result = null;
        HttpGet getLoginPage = new HttpGet("http://jw.qdu.edu.cn/academic/common/security/login.jsp?login_error=1");//���񴦵�½ҳ��get
        
        try {
        	String code;
            client.execute(getLoginPage);
            //��ȡ��֤��
            getVerifyingCode(client);
            System.out.println("��������֤�룺");
            Scanner in = new Scanner(System.in);
            code = in.nextLine();
           in.close();
            //�趨post����������ͼ�е�����һ��
            ArrayList<NameValuePair> postData = new ArrayList<NameValuePair>();
            postData.add(new BasicNameValuePair("groupId", null));//����������hidden
            postData.add(new BasicNameValuePair("j_username", accout));//ѧ��
            postData.add(new BasicNameValuePair("j_password", password));//����
            postData.add(new BasicNameValuePair("j_captcha", code));//��֤��
            HttpPost post = new HttpPost("http://jw.qdu.edu.cn/academic/j_acegi_security_check");//����post����
            post.setEntity(new UrlEncodedFormEntity(postData));
            response = client.execute(post);
        } catch (ClientProtocolException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
    
    void getVerifyingCode(HttpClient client) {//����ʹ�õ��ǰ���֤�����������ķ�ʽ�����������Զ�ʶ��ģ������Զ�ʶ�����ȷ��̫����
        HttpGet getVerifyCode = new HttpGet("http://jw.qdu.edu.cn/academic/getCaptcha.do");//��֤��get
        FileOutputStream fileOutputStream = null;
        HttpResponse response;
        try {
            response = client.execute(getVerifyCode);//��ȡ��֤��
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
    
    public void getInfo() throws ClientProtocolException, IOException{//ʹ��jsoup�Խ��񴦽��н���
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
        Test test = new Test("���ѧ��", "�������");  
        try {
			test.login();
			test.getInfo();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
    }  



	
}
