package com.zcf.framework.common.yunos;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;

public class AliyunAPI {

		/**
	     * 获取6位随机验证码
	     * @return
	     */
	    /*public static String getValidationCode(){
	        return String.valueOf((new Random().nextInt(899999) + 100000));
	    }*/
	
	    // 产品名称:云通信短信API产品,开发者无需替换
	    private static final String product = "Dysmsapi";
	    // 产品域名,开发者无需替换
	    private static final String domain = "dysmsapi.aliyuncs.com";

	    // 此处需要替换成开发者自己的AK(在阿里云访问控制台寻找)
	    private static String accessKeyId = "LTAIXo4mLjP2N0FX";
	    private static String accessKeySecret = "fHBrfqws345niv6rt6KksICZexwWEQ";
	    private static String signName = "共享架子鼓";
	    private static String identifyingTempleteCode = "SMS_146804062";

	    public static void init(String accessKeyId, String accessKeySecret, String signName, String identifyingTempleteCode) {
	    	AliyunAPI.accessKeyId = accessKeyId;
	    	AliyunAPI.accessKeySecret = accessKeySecret;
	    	AliyunAPI.signName = signName;
	    	AliyunAPI.identifyingTempleteCode = identifyingTempleteCode;
	    }

	    public static void main(String[] args) throws Exception {
	    	//AliyunAPI.init(accessKeyId, accessKeySecret, signName, identifyingTempleteCode);
	        // 发短信

	       // SendSmsResponse response = AliyunAPI.sendIdentifyingCode("17721751993", getValidationCode());


	        
	       /* System.out.println("短信接口返回的数据----------------");
	        System.out.println("Code=" + response.getCode());
	        System.out.println("Message=" + response.getMessage());
	        System.out.println("RequestId=" + response.getRequestId());
	        System.out.println("BizId=" + response.getBizId());*/
	       
	    }

	    public static SendSmsResponse sendSms(String mobile, String templateParam, String templateCode)
	            throws ClientException {

	        // 可自助调整超时时间
	        System.setProperty("sun.net.client.defaultConnectTimeout", "60000");
	        System.setProperty("sun.net.client.defaultReadTimeout", "60000");

	        // 初始化acsClient,暂不支持region化
	        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
	        DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
	        IAcsClient acsClient = new DefaultAcsClient(profile);

	        // 组装请求对象-具体描述见控制台-文档部分内容
	        SendSmsRequest request = new SendSmsRequest();

	        // 必填:待发送手机号
	        request.setPhoneNumbers(mobile);
	        // 必填:短信签名-可在短信控制台中找到
	        request.setSignName(signName);
	        // 必填:短信模板-可在短信控制台中找到
	        request.setTemplateCode(templateCode);

	        // 可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
	        request.setTemplateParam(templateParam);

	        // 选填-上行短信扩展码(无特殊需求用户请忽略此字段)
	        // request.setSmsUpExtendCode("90997");

	        // 可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
	        request.setOutId("yourOutId");

	        // hint 此处可能会抛出异常，注意catch
	        SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);

	        return sendSmsResponse;
	    }

	    public static SendSmsResponse sendIdentifyingCode(String mobile, String code) throws Exception {
	        try {
	            return sendSms(mobile, "{\"code\":\"" + code + "\"}", identifyingTempleteCode);
	        } catch (ClientException e) {
	            throw new Exception(e.getMessage());
	        }
	    }
	
}
