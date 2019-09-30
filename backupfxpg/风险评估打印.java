package smart.modules.客户.在线风险评估;

import java.util.HashMap;
import java.util.Map;

import smart.lib.axpe.PrintConfig;
import smart.lib.axpe.pub_StmPrtObject;
import smart.lib.dic.Dictionary;
import smart.lib.module.ModuleCefTrade;
import smart.lib.template.FreeMarkerUtil;
import smart.lib.template.Model;
import cn.com.agree.ab.trade.cef.In;
import cn.com.agree.ab.trade.cef.ModuleRequest;
import cn.com.agree.ab.trade.cef.ModuleResponse;
import cn.com.agree.ab.trade.cef.Out;
import cn.com.agree.ab.trade.dataStruct.view.View;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class 风险评估打印 extends ModuleCefTrade {	
	
	@In(Dictionary.客户.身份证号)
	public String idCardNo;
	@In(Dictionary.客户.客户姓名)
	public String customName;
	
	@Out(Dictionary.公共节点变量.公共提示标志)
	public String 公共提示标志;

	@Out(Dictionary.公共节点变量.公共提示信息)
	public String 公共提示信息;
	@Override
	public boolean canDoSubmit(ModuleRequest request) throws Exception {
		return true;
	}
	
	
	@Override
	public View doRenderView(ModuleRequest request) throws Exception {
		Model model = new Model();
		model.put("info", "正在打印中，请稍等......");
		return FreeMarkerUtil.getTemplate(this, "default.ftl", model);
	}


	@Override
	public void doModuleSubmit(ModuleRequest request, ModuleResponse response)
			throws Exception {
		String 问卷答案 = request.getDataBasketString("问卷答案");
		String 风险评估问卷 = request.getDataBasketString("风险评估问卷内容");
	    if(问卷答案 == null || "".equals(问卷答案) || 风险评估问卷 == null || "".equals(风险评估问卷) ){
	    	公共提示标志 = "错误";
			公共提示信息 =  "打印失败，文件内容为空！";
			response.putDataBasket("打印标识", false);
			return ;
	    }
	    try {
	    	StringBuffer sb = new  StringBuffer();
			sb.append("\n").append("                               ")
			.append("中国光大银行客户投资能力风险评估问卷").append("\n").append("\n").append("\n");
			
			String title="向本行第一次申请任何理财投资产品前，请填写本问卷，并于每年进行重新评估。" +
					"本问卷旨在了解您可承受的风险程度、以及您的投资经验，借此协助您选择合适的理财产品类别，以达到您的投资目标。" +
					"本银行建议您，当您发生可能影响自身风险承受能力的情形，再次购买理财产品时应当主动要求商业银行对您进行风险承受能力评估。";
			strLength(sb,title,40);
			//问卷答案
			Map<Integer,String> map=new HashMap<Integer,String>();
			JSONObject res= new JSONObject();
			res=JSONObject.parseObject(问卷答案);
			for(Map.Entry<String, Object> entry:res.entrySet()){
				int a= Integer.parseInt(entry.getKey().replace("topic", ""));
				String b= entry.getValue().toString();
				String letter = getLetter(Integer.parseInt(b));
				map.put(a, "     您选择的选项是:"+letter);
			}

			//问卷题目 和 问卷选项
		   JSONArray array = JSONArray.parseArray(风险评估问卷);
		   for(int i=0;i<array.size();i++){
			  JSONArray arr= array.getJSONArray(i);
			    for(int j=1;j<arr.size();j++){
			    	String n=arr.getString(j);
			    	int len = 40;
			    		if(j==1){
			    			if(n.length()>len){
					    		strLength(sb,n,len);
					    	}else{
			    			//问题
			    			 sb.append("     ").append(n).append("\n").append("\n");
					    	}
			    			//答案
				    		sb.append(map.get(i+1).toString()).append("\n").append("\n");
				    	}else{
				    		String lett = getLetter(j-1); 
				    		if(n.length()>len){
				    			n = lett+":"+n;
					    		strLength(sb,n,len);
					    	}else{
					    		//选项
					           sb.append("     ").append(lett).append(":").append(n).append("\n").append("\n");
					    	}
				    		
				    	}
			    }
		   }
			
		    PrintConfig config = new PrintConfig();
			config.setColSpacing(13);
			config.setRowSpacing(30);
			config.setPaperLength(86);//达到多少行时换页
			config.setDeviceType(config.VALUE_激光打印机);
			config.setOrientation(1);//2横着打印 1竖着打印
			String printStr = "";
			printStr=sb.toString();
			
			String[] s= printStr.split("\n\n");
	        StringBuffer sb1=new StringBuffer();
	        String data = this.getTellerInfo().get("G_SYSDATE").toString();
	        for(int i=0;i<s.length;i++){
	        	if((i+1)%40==0){
	        		sb1.append("                                                  客户姓名:").append(customName)
	 			   .append("        客户签名:").append("\n").append("\n");
	        		sb1.append("                                                  证件号码:").append(idCardNo).append("\n").append("\n");
	        		sb1.append("                                                  交易日期:").append(data)
	        		 .append("       理财经理签字:").append("\n").append("\n").append("\n").append("\n");
	        	}
	        	sb1.append(s[i]).append("\n").append("\n");
	        }
			
			if(sb1.length()%40!=0){
				sb1.append("                                                  客户姓名:").append(customName)
				   .append("        客户签名:").append("\n").append("\n");
	    		sb1.append("                                                  证件号码:").append(idCardNo).append("\n").append("\n");
	    		sb1.append("                                                  交易日期:").append(data)
	    		.append("       理财经理签字:").append("\n").append("\n");
			}
			pub_StmPrtObject.print(this, "打印纸", sb1.toString(), null , config, null,null);
		} catch (Exception e) {
			公共提示标志 = "错误";
			公共提示信息 =  "文件打印失败！";
			response.putDataBasket("打印标识", false);
		}
		
		
	}

	@Override
	public void doInit() throws Exception {
		moduleSubmit(null);
	}
	
	//换行
	public  void strLength(StringBuffer sb,String str,int len){
		int num=str.length();
		int num1 = num%len;
		int num2= num-num1;
		System.out.println(str.length());
		for(int i=0;i<num2;i++){
			sb.append("     ").append(str.substring(i, i+len)).append("\n").append("\n");
			i=i+(len-1);
		}
		if(num1!=0){
			sb.append("     ").append(str.substring(num2, num)).append("\n").append("\n");
		}
	}
	public String getLetter(int num){
		String[] str=new String[]{"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
		String letter=str[num-1];
		return letter;
	}

}
