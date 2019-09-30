package smart.modules.客户.在线风险评估;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import smart.lib.comm.CefCommon;
import smart.lib.comm.CommResult;
import smart.lib.dic.Dictionary;
import smart.lib.module.ModuleCefTrade;
import smart.lib.module.SignSubmitCefTrade;
import smart.lib.template.FreeMarkerUtil;
import smart.lib.template.Model;
import ceb00.trx.lib.tools.BaseUtil;
import cn.com.agree.ab.trade.cef.In;
import cn.com.agree.ab.trade.cef.ModuleRequest;
import cn.com.agree.ab.trade.cef.ModuleResponse;
import cn.com.agree.ab.trade.cef.Out;
import cn.com.agree.ab.trade.dataStruct.view.View;
import cn.com.agree.commons.csv.CsvUtil;


public class 在线风险评估  extends SignSubmitCefTrade{
	@In(Dictionary.客户.客户号)
	public String custNo;
	
	@Out(Dictionary.公共节点变量.公共提示标志)
	public String 公共提示标志;
	  
	@Out(Dictionary.公共节点变量.公共提示信息)
	public String 公共提示信息;

	@Override
	public boolean canDoSubmit(ModuleRequest request) throws Exception {
		// TODO 自动生成的方法存根
		return true;
	}

	@Override
	public void doInit() throws Exception {
		// TODO 自动生成的方法存根
		try{
			JSONArray array = new JSONArray();
			JSONArray 联动查询0353=联动查询0353(this);
			if( 联动查询0353 == null || 联动查询0353.size()==0){
				pushError("获取风险评估问卷失败！");
				return;
			}
			for(int i=0;i<联动查询0353.size();i++){
				JSONArray resultArray = 联动查询0353.getJSONArray(i);
				
				String anwserTotal=resultArray.getString(13);//答案总数
				String quesID=resultArray.getString(0);//问题编号
				String quesType=resultArray.getString(1);//问题类型都为0 
				String quesDesc=resultArray.getString(2);//问题描述
				JSONArray topic = new JSONArray();
				topic.add(quesID);
				topic.add(quesID+":"+quesDesc);	
				for(int j=0;j<Integer.parseInt(anwserTotal);j++){
					//4  0 1 2 3
					String anwser=resultArray.getString(j+3);
					topic.add(anwser);
				}
				array.add(topic);
			}
			String script = "createAndMonitor("+array.toString()+","+numTotal+")";
			logger.info("aaaaaaaa:"+array.toJSONString());
			eval(script);
		}catch(Exception e){
			pushError("获取风险评估问卷异常！"+e.getMessage());
			return;
		}
	

		
		//eval(script);
	}

	@Override
	public void doModuleSubmit(ModuleRequest request, ModuleResponse response)
			throws Exception {
		// TODO 自动生成的方法存根

		try {
			List<String> list=Arrays.asList(answer.split(","));
			List<String[]> datalist=new ArrayList<String[]>();
			for(int i=0;i<list.size();i++){
				String[] split = list.get(i).split(":",-1);
				datalist.add(new String[]{split[0].replace("topic", ""),split[1]});
			}
			Map teller = getTellerInfo();
			String RECYLEDATE=(String) teller.get("G_SYSDATE");
			JSONObject map0351 = new JSONObject();
			map0351.put("ICUSTNO", custNo);//证件类型
			map0351.put("ISTARTDATE",RECYLEDATE);//证件号码
			map0351.put("F03511", datalist);//
			CommResult result = CefCommon.exchange(this, "0351",
					"O03510", map0351);
			JSONObject res= new JSONObject();
			res=JSONObject.parseObject(answer);
			for(Map.Entry<String, Object> entry:res.entrySet()){
				String a= entry.getKey();
				String b= entry.getValue().toString();
				logger.info("1111111111"+a+b);
			}
			if (result.isSuccess()) {
				response.putDataBasket("success", "true");
				return;
			}else{
				公共提示标志="提示";
				公共提示信息="理财风险评估信息新增失败："+result.getErrorMsg();
				response.putDataBasket("success", "false");
				return;
			}
			
			
		} catch (Exception e) {
			公共提示标志="提示";
			公共提示信息="理财风险评估信息新增异常！"+e.getMessage();
			response.putDataBasket("success", "false");
			return;
		}
	
//		if (result.isSuccess()) {
//			response.putDataBasket("success", "true");
//			return;
//			}else{
//				公共提示标志="提示";
//				公共提示信息="个人税收居民信息新增失败！"+result.getErrorMsg();
//				response.putDataBasket("success", "false");
//				return;
//			}
//		} catch (Exception e) {
//			公共提示标志="提示";
//			公共提示信息="个人税收居民信息新增异常！"+e.getMessage();
//			response.putDataBasket("success", "false");
//			return;
//		}
//		   ArrayList<String[]> list = new ArrayList<String[]>(Arrays.asList(mapString));

		
	}
	public  String numTotal="";
	@Override
	public View doRenderView(ModuleRequest request) throws Exception {
		// TODO 自动生成的方法存根
		Model model = new Model();
		return FreeMarkerUtil.getTemplate(this,"index.ftl", model);
	}
	private  JSONArray 联动查询0353(ModuleCefTrade trade)
	throws Exception {
		String num = "";
		String beginNum = "1";
		String selectNum = "20";
		JSONArray jsonArray0353 = new JSONArray();
		while (true) {
			try {		
				if ("".equals(num)) {
					JSONObject map0353 = new JSONObject();
					map0353.put("IQSHBS", beginNum);
					map0353.put("ICHXBS", selectNum);
					CommResult result = CefCommon.exchange(trade, "0353",
							"O03530", map0353);
					if (!result.isSuccess()) {
						break;
					}
					JSONObject jsonObjec0353 = result.getObjDataSop("O03531");
					String number = jsonObjec0353.getString("OBISHU");
					numTotal=number;
					trade.logger.info("获取后台数据总笔数" + number);
					jsonArray0353.addAll(jsonObjec0353.getJSONArray("F03531"));
					num = number;
				} else {
		
					beginNum = BaseUtil.addBigDecimal(selectNum, beginNum);
					trade.logger.info("本次查询开始笔数" + beginNum);
					if (BaseUtil.compare(beginNum, num) > 0) {
						trade.logger.info("本次查询开始笔数大于总笔数");
						break;
					} else {
						JSONObject map0353 = new JSONObject();
						map0353.put("IQSHBS", beginNum);
						map0353.put("ICHXB", selectNum);
						CommResult result = CefCommon.exchange(trade, "0353",
								"O03530", map0353);
						if (!result.isSuccess()) {
							break;
						}
						JSONObject jsonObjec0353 = result
								.getObjDataSop("O03531");
						jsonArray0353.addAll(jsonObjec0353
								.getJSONArray("F03531"));
					}
				}
			} catch (Exception e) {
				break;
			}
		}
		if (jsonArray0353 != null && jsonArray0353.size() > 0)
			return jsonArray0353;
		else
			return null;
}
	
	public String answer="";
	public void pageSubmit(String mapString) throws Exception{
		if(mapString==null || "".equals(mapString)){
			pushError("提交异常，请退出重试！");
			return;
		}
		answer =mapString;
		
		moduleSubmit(null);
//		this.pushError("获取风险评估问卷异常！"+);
//		logger.info(map.get("0001")+"::"+map.get("0002")+"::"+map.get("0003")+"::"+map.get("0004")+"::"+map.get("0005")+"::"+map.get("0006"));
	}

	@Override
	public void pageSubmit() throws Exception {
		// TODO 自动生成的方法存根
		
	}

	@Override
	public void recordLog(String info) {
		// TODO 自动生成的方法存根
		
	}

	@Override
	public void errorHandler(String log, String info) throws Exception {
		// TODO 自动生成的方法存根
		
	}
}
