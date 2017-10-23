package JwSpider;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
    static HttpClient client = HttpClients.createDefault();
    		//ʵ����httpclientHttpClients.custom().setDefaultCookieStore(cookieStore).build();
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
    
    /*
     * ��ȡ���˿γ̵ķ���
     */
    public void getScore(){
    	  parse(getScoreParm("35","2"));
       	  parse(getScoreParm("36","2"));
    	  parse(getScoreParm("36","1"));
            parse(getScoreParm("37","1"));
    }

    /*   year��Ҫ��ѯ����ݣ�����2017����37��2015����35
     *   term��Ҫ��ѯ��ѧ�ڣ�����Ϊ��1��2��3
	 *   ��ȡ������Ҫ�õ�post��������Ҫ�ύ������
     */
    public HttpResponse getScoreParm(String year,String term){
        try {	HttpPost post=new HttpPost("http://jw.qdu.edu.cn/academic/manager/score/studentOwnScore.do?groupId=&moduleId=2020");
		ArrayList<NameValuePair> postData = new ArrayList<NameValuePair>();
        postData.add(new BasicNameValuePair("year", year));
        postData.add(new BasicNameValuePair("term", term));
        postData.add(new BasicNameValuePair("para", "0"));  
        postData.add(new BasicNameValuePair("sortColumn", ""));
        postData.add(new BasicNameValuePair("Submit", "�ύ"));
			post.setEntity(new UrlEncodedFormEntity(postData));
				HttpResponse response=client.execute(post) ;
				return response;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
	
      	
    }
    
    public void parse(HttpResponse response){
 
 
		try {
		   	Document doc = Jsoup.parse(EntityUtils.toString(response.getEntity()));
			   Element element= doc.select("table.datalist").first();
		       Elements th= element.select("th");
		       Elements td= element.select("tr ");
		       for(int i=1;i<td.size();i++){
		     	Elements trd= td.get(i).select("td"); 
		     	for (int j =0;j<trd.size();j++) {
		 			System.out.println(th.get(j).text()+":"+trd.get(j).text());
		 		}
		     	}
		} catch (ParseException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
     
    }
    
    
    
    
    
    
    public static void main(String[] args){  
        Test test = new Test("���ѧ��", "�������");  
        try {
			test.login();
			test.getInfo();
			test.getScore();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
    }  



	
}
