package smart.modules.客户.在线风险评估;

import smart.lib.comm.CefCommon;
import smart.lib.comm.CommResult;
import smart.lib.device.DeviceUtil;
import smart.lib.dic.Dictionary;
import smart.lib.module.ModuleCefTrade;
import smart.lib.module.SignSubmitCefTrade;
import smart.lib.template.FreeMarkerUtil2;
import smart.lib.template.Model;
import ceb00.trx.lib.tools.BaseUtil;
import cn.com.agree.ab.trade.cef.In;
import cn.com.agree.ab.trade.cef.ModuleRequest;
import cn.com.agree.ab.trade.cef.ModuleResponse;
import cn.com.agree.ab.trade.cef.Out;
import cn.com.agree.ab.trade.dataStruct.view.View;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class 风险评估问卷 extends SignSubmitCefTrade {
	@In(Dictionary.客户.客户号)
	public String custNo;

	@Out(Dictionary.公共节点变量.公共提示标志)
	public String 公共提示标志;

	@Out(Dictionary.公共节点变量.公共提示信息)
	public String 公共提示信息;
	
	@In(Dictionary.客户.客户姓名)
	public String customName;

	@In(Dictionary.客户.身份证号)
	public String idCardNo;

	public boolean signFlag = true;
	
	public String failResult = "";
	
	public String ecif_RiskLevel;

	@Override
	public boolean canDoSubmit(ModuleRequest request) throws Exception {
		// TODO 自动生成的方法存根
		return true;
	}
	public String 风险评估问卷内容 = "";
	@Override
	public void doInit() throws Exception {
		// TODO 自动生成的方法存根
		try {
			JSONArray array = new JSONArray();
			JSONArray 联动查询0353 = 联动查询0353(this);
			if (联动查询0353 == null || 联动查询0353.size() == 0) {
				pushError("获取风险评估问卷失败！");
				return;
			}
			for (int i = 0; i < 联动查询0353.size(); i++) {
				JSONArray resultArray = 联动查询0353.getJSONArray(i);
				String anwserTotal = resultArray.getString(13);// 答案总数
				String quesID = resultArray.getString(0);// 问题编号
				String quesType = resultArray.getString(1);// 问题类型都为0
				String quesDesc = resultArray.getString(2);// 问题描述
				JSONArray topic = new JSONArray();
				topic.add(quesID);
				topic.add(quesID + ":" + quesDesc);
				for (int j = 0; j < Integer.parseInt(anwserTotal); j++) {
					// 4 0 1 2 3
					String anwser = resultArray.getString(j + 3);
					topic.add(anwser);
				}
				array.add(topic);
			}
			风险评估问卷内容 = array.toString();
			ecif_RiskLevel = getRequest().getDataBasketString("ecif风险等级");
			String script = "";
			if (null == ecif_RiskLevel || "".equals(ecif_RiskLevel)) {
				script = "createAndMonitor(" + array.toString() + ","
						+ numTotal +",'true'"+")";
			}else {
				script = "createAndMonitor(" + array.toString() + ","
						+ numTotal +",'false'"+")";
			}
			logger.info("风险评估问卷的script:"+script);
			eval(script);
		} catch (Exception e) {
			pushError("获取风险评估问卷异常！" + e.getMessage());
			return;
		}

		// eval(script);
	}

	@Override
	public void doModuleSubmit(ModuleRequest request, ModuleResponse response)
			throws Exception {
		// TODO 自动生成的方法存根
		try {
			response.putDataBasket("风险评估问卷内容", 风险评估问卷内容);
			response.putDataBasket("signFlag",signFlag);
			response.putDataBasket("问卷答案", answer);
			if (signFlag) {
				// TODO 自动生成的方法存根
				if(null == ecif_RiskLevel || "".equals(ecif_RiskLevel)){
					JSONObject customInfo = new JSONObject();
					customInfo.put("customName", customName);
					customInfo.put("idCardNo", idCardNo);
					JSONObject upImgResult = syncOpenFrame(上传问卷及签名影像.class.getName(), customInfo);
					String signResult = upImgResult.getString("signResult");
					String docId = upImgResult.getString("lcpgDocId");
					response.putDataBasket("signResult", signResult);
					if (null != signResult && "true".equals(signResult)) {
						response.putDataBasket("lcpgDocId",docId);
					}else {
						response.putDataBasket("lcpgDocId","");
						failResult = "上传影像失败";
					}
				}else {
					response.putDataBasket("signResult", "true");
				}
			} else {
				response.putDataBasket("signResult", "true");
			}
			公共提示标志 = "错误";
			公共提示信息 = failResult + "，请联系理财经理，填写《中国光大银行客户投资风险能力评估问卷》后继续操作，谢谢！";
		} finally {
			for (int i = 0; i < 3; i++) {
				logger.info("第" + (i + 1) + "次关闭电子签名窗口");
				try {
					String result = DeviceUtil.interrupt(this,
							DeviceUtil.SIGNATURE);
					if (null != result && "true".equals(result)) {
						logger.info("第" + (i + 1) + "次关闭电子签名窗口成功");
						break;
					} else {
						logger.info("第" + (i + 1) + "次关闭电子签名窗口失败");
						continue;
					}
				} catch (Exception e) {
					// TODO: handle exception
					logger.info("第" + (i + 1) + "次关闭电子签名窗口失败", e);
					continue;
				}
			}
		}

	}

	public String numTotal = "";

	@Override
	public View doRenderView(ModuleRequest request) throws Exception {
		// TODO 自动生成的方法存根
		Model model = new Model();
		return FreeMarkerUtil2.getTemplate(this, "index.ftl", model);
	}

	private JSONArray 联动查询0353(ModuleCefTrade trade) throws Exception {
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
					numTotal = number;
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

	public String answer = "";

	public void pageSubmit(String mapString) throws Exception {
		if (mapString == null || "".equals(mapString)) {
			pushError("提交异常，请退出重试！");
			return;
		}
		answer = mapString;

		moduleSubmit(null);
		// this.pushError("获取风险评估问卷异常！"+);
		// logger.info(map.get("0001")+"::"+map.get("0002")+"::"+map.get("0003")+"::"+map.get("0004")+"::"+map.get("0005")+"::"+map.get("0006"));
	}

	// 重要的日志记一下
	public void recordLog(String info) {
		logger.info(info);
	}

	/**
	 * JavaScript操作异常处理
	 * 
	 * @param log
	 *            记录日志信息
	 * @param info
	 *            展示信息
	 * @throws Exception
	 */
	public void errorHandler(String log, String info,String mapString) throws Exception {
		if (null == info || "".equals(info)) {
			failResult = log;
		} else {
			failResult = info;
		}
		//失败处理 不走影像
		signFlag = false;
		answer = mapString;
		logger.info("js操作失败:" + log);
		moduleSubmit(null);
	}

	@Override
	public void pageSubmit() throws Exception {
		// TODO 自动生成的方法存根
		
	}

	@Override
	public void errorHandler(String log, String info) throws Exception {
		// TODO 自动生成的方法存根
		
	}
}
