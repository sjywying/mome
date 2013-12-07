/**
 * 应用名称：mome-core
 * 文件名：aaa.java
 * 
 * 版本信息： 
 * 日期：2013-9-13
 * 
 */
package com.mome.core;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import sun.security.provider.MD5;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * <p>
 * TODO(用一句话描述该文件做什么)
 * </p>
 *
 * @author Crazy/Y </br>
 * @Email sjywying105@gmail.com
 * @date 2013-9-13 下午2:44:18
 * @version V1.0
 */
public class aaa {

	/**
	 * <p>
	 * TODO(这里用一句话描述这个方法的作用)
	 * </p>
	 * @param args
	 * 
	 * @author : Crazy/sjy
	 * @date : 2013-9-13 下午2:44:18
	 */
	public static void main(String[] args) {
		System.out.println("张三aaa".matches("[a-zA-Z0-9]*"));
		
		System.out.println(StringEscapeUtils.escapeHtml4("<scr<script>ipt>alert('test')</script>"));
		
//		System.out.println(StringEscapeUtils.escapeHtml3("<!-- --><a></a><script>alert('xxxx');</script>"));
//		System.out.println(StringEscapeUtils.escapeHtml4("<!-- --><a></a><script>alert('xxxx');</script>"));
//		System.out.println(StringEscapeUtils.escapeHtml3("' \" < > \\ & #"));
//		System.out.println(StringEscapeUtils.escapeHtml4("' \" < > \\ & #"));
//		try {
//			System.out.println(new String("<script>alert('xxxx');</script>".getBytes("GBK"),"utf-8"));
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		System.out.println(URLEncoder.encode("<script>alert('xxxx');</script>"));
//		System.out.println(StringEscapeUtils.unescapeHtml4("&lt;script&gt;alert('xxxx');&lt;/script&gt;"));
//		remove();
		
	}
	
	public void base64() {
		BASE64Decoder bd = new BASE64Decoder();
		BASE64Encoder be = new BASE64Encoder();
		try {
			String data = "http://localhost:8080/s/xss/non-persisent?name=<script>eval(\"alert(document.cookie)\")</script>";
			System.out.println(data);
			System.out.println("BASE64密文" + be.encode(data.getBytes()));
			System.out.println("BASE64明文" + new String(bd.decodeBuffer(be.encode(data.getBytes()))));
			
			byte[] resultData = bd.decodeBuffer("aHR0cDovL3VybDcubWUvVGlOcQ==");
			System.out.println(new String(resultData));
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void test() {
		long startTime = System.currentTimeMillis();
		for (int i = 0; i < 10000; i++) {
			ObjectMapper om = new ObjectMapper();
			try {
				Thread.currentThread().sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
		System.out.println("xxxxxxxxxxx");
	}
	
	public static void remove() {
		List<Long> ids = new ArrayList<Long>();
//		ids.add(1L);
//		ids.add(2L);
		Long currentId = 1L;
        for (Iterator iterator = ids.iterator(); iterator.hasNext();) {
            Long id = (Long) iterator.next();
            if(id == currentId) {
                iterator.remove();
                break;
            }
        }
        
        System.out.println(ids);
	}

}
