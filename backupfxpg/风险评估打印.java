package smart.modules.�ͻ�.���߷�������;

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

public class ����������ӡ extends ModuleCefTrade {	
	
	@In(Dictionary.�ͻ�.���֤��)
	public String idCardNo;
	@In(Dictionary.�ͻ�.�ͻ�����)
	public String customName;
	
	@Out(Dictionary.�����ڵ����.������ʾ��־)
	public String ������ʾ��־;

	@Out(Dictionary.�����ڵ����.������ʾ��Ϣ)
	public String ������ʾ��Ϣ;
	@Override
	public boolean canDoSubmit(ModuleRequest request) throws Exception {
		return true;
	}
	
	
	@Override
	public View doRenderView(ModuleRequest request) throws Exception {
		Model model = new Model();
		model.put("info", "���ڴ�ӡ�У����Ե�......");
		return FreeMarkerUtil.getTemplate(this, "default.ftl", model);
	}


	@Override
	public void doModuleSubmit(ModuleRequest request, ModuleResponse response)
			throws Exception {
		String �ʾ�� = request.getDataBasketString("�ʾ��");
		String ���������ʾ� = request.getDataBasketString("���������ʾ�����");
	    if(�ʾ�� == null || "".equals(�ʾ��) || ���������ʾ� == null || "".equals(���������ʾ�) ){
	    	������ʾ��־ = "����";
			������ʾ��Ϣ =  "��ӡʧ�ܣ��ļ�����Ϊ�գ�";
			response.putDataBasket("��ӡ��ʶ", false);
			return ;
	    }
	    try {
	    	StringBuffer sb = new  StringBuffer();
			sb.append("\n").append("                               ")
			.append("�й�������пͻ�Ͷ���������������ʾ�").append("\n").append("\n").append("\n");
			
			String title="���е�һ�������κ����Ͷ�ʲ�Ʒǰ������д���ʾ�����ÿ���������������" +
					"���ʾ�ּ���˽����ɳ��ܵķ��ճ̶ȡ��Լ�����Ͷ�ʾ��飬���Э����ѡ����ʵ���Ʋ�Ʒ����Դﵽ����Ͷ��Ŀ�ꡣ" +
					"�����н�������������������Ӱ��������ճ������������Σ��ٴι�����Ʋ�ƷʱӦ������Ҫ����ҵ���ж������з��ճ�������������";
			strLength(sb,title,40);
			//�ʾ��
			Map<Integer,String> map=new HashMap<Integer,String>();
			JSONObject res= new JSONObject();
			res=JSONObject.parseObject(�ʾ��);
			for(Map.Entry<String, Object> entry:res.entrySet()){
				int a= Integer.parseInt(entry.getKey().replace("topic", ""));
				String b= entry.getValue().toString();
				String letter = getLetter(Integer.parseInt(b));
				map.put(a, "     ��ѡ���ѡ����:"+letter);
			}

			//�ʾ���Ŀ �� �ʾ�ѡ��
		   JSONArray array = JSONArray.parseArray(���������ʾ�);
		   for(int i=0;i<array.size();i++){
			  JSONArray arr= array.getJSONArray(i);
			    for(int j=1;j<arr.size();j++){
			    	String n=arr.getString(j);
			    	int len = 40;
			    		if(j==1){
			    			if(n.length()>len){
					    		strLength(sb,n,len);
					    	}else{
			    			//����
			    			 sb.append("     ").append(n).append("\n").append("\n");
					    	}
			    			//��
				    		sb.append(map.get(i+1).toString()).append("\n").append("\n");
				    	}else{
				    		String lett = getLetter(j-1); 
				    		if(n.length()>len){
				    			n = lett+":"+n;
					    		strLength(sb,n,len);
					    	}else{
					    		//ѡ��
					           sb.append("     ").append(lett).append(":").append(n).append("\n").append("\n");
					    	}
				    		
				    	}
			    }
		   }
			
		    PrintConfig config = new PrintConfig();
			config.setColSpacing(13);
			config.setRowSpacing(30);
			config.setPaperLength(86);//�ﵽ������ʱ��ҳ
			config.setDeviceType(config.VALUE_�����ӡ��);
			config.setOrientation(1);//2���Ŵ�ӡ 1���Ŵ�ӡ
			String printStr = "";
			printStr=sb.toString();
			
			String[] s= printStr.split("\n\n");
	        StringBuffer sb1=new StringBuffer();
	        String data = this.getTellerInfo().get("G_SYSDATE").toString();
	        for(int i=0;i<s.length;i++){
	        	if((i+1)%40==0){
	        		sb1.append("                                                  �ͻ�����:").append(customName)
	 			   .append("        �ͻ�ǩ��:").append("\n").append("\n");
	        		sb1.append("                                                  ֤������:").append(idCardNo).append("\n").append("\n");
	        		sb1.append("                                                  ��������:").append(data)
	        		 .append("       ��ƾ���ǩ��:").append("\n").append("\n").append("\n").append("\n");
	        	}
	        	sb1.append(s[i]).append("\n").append("\n");
	        }
			
			if(sb1.length()%40!=0){
				sb1.append("                                                  �ͻ�����:").append(customName)
				   .append("        �ͻ�ǩ��:").append("\n").append("\n");
	    		sb1.append("                                                  ֤������:").append(idCardNo).append("\n").append("\n");
	    		sb1.append("                                                  ��������:").append(data)
	    		.append("       ��ƾ���ǩ��:").append("\n").append("\n");
			}
			pub_StmPrtObject.print(this, "��ӡֽ", sb1.toString(), null , config, null,null);
		} catch (Exception e) {
			������ʾ��־ = "����";
			������ʾ��Ϣ =  "�ļ���ӡʧ�ܣ�";
			response.putDataBasket("��ӡ��ʶ", false);
		}
		
		
	}

	@Override
	public void doInit() throws Exception {
		moduleSubmit(null);
	}
	
	//����
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
