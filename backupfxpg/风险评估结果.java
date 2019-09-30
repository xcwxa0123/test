package smart.modules.客户.在线风险评估;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;

import smart.lib.comm.CefCommon;
import smart.lib.comm.CommResult;
import smart.lib.constants.SmartConstants;
import smart.lib.dic.Dictionary;
import smart.lib.module.ModuleCefCalcTrade;
import smart.lib.template.FreeMarkerUtil;
import smart.lib.template.Model;
import cn.com.agree.ab.trade.cef.In;
import cn.com.agree.ab.trade.cef.ModuleRequest;
import cn.com.agree.ab.trade.cef.ModuleResponse;
import cn.com.agree.ab.trade.cef.Out;
import cn.com.agree.ab.trade.core.scope.Scope;
import cn.com.agree.ab.trade.dataStruct.view.View;

import com.alibaba.fastjson.JSONObject;

public class 风险评估结果 extends ModuleCefCalcTrade {
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

	public String numTotal = "";

	@Override
	public View doRenderView(ModuleRequest request) throws Exception {
		Model model = new Model();
		model.put("info", "正在进行风险评级，请稍后......");
		return FreeMarkerUtil.getTemplate(this, "default.ftl", model);
	}
	
	@Override
	public void doCalcSubmit(ModuleRequest request, ModuleResponse response)
			throws Exception {
		String answer = request.getDataBasketString("问卷答案");

		try {
			JSONObject res = new JSONObject();
			res = JSONObject.parseObject(answer);
			List<String[]> answerList = answerRank(res);
			Map teller = getTellerInfo();
			String RECYLEDATE = (String) teller.get("G_SYSDATE");
			JSONObject map0351 = new JSONObject();
			map0351.put("ICUSTNO", custNo);// 证件类型
			map0351.put("ISTARTDATE", RECYLEDATE);// 证件号码
			map0351.put("F03511", answerList);//
			CommResult result = CefCommon.exchange(this, "0351", "O03510",
					map0351);
			if (result.isSuccess()) {
				response.putDataBasket("success", "true");
				Scope scope = this.getScope(Scope.dataBasket);
				String 前台流水号 = (String) scope.get(SmartConstants.SMART_LSH);
				response.putDataBasket("qtls", 前台流水号);
				JSONObject jsonObjec0351 = result.getObjDataSop("O03511");
				String flag = jsonObjec0351.getString("OFLAG");
				// 07、激进型，06、进取型，04、平衡型，02、稳健型，01、谨慎型
				String datas = "";
				String flagData = "";
				if ("01".equals(flag)) {
					flagData = "谨慎型";
					datas = "您属于可以承担低风险而作风谨慎类型的投资者。您适合投资于以保本为主的投资工具，但您因此会牺牲资本升值的机会。";
				} else if ("02".equals(flag)) {
					flagData = "稳健型";
					datas = "您属于可以承担低至中等风险类型的投资者。您适合投资于能够权衡保本而亦有若干升值能力的投资工具。";
				} else if ("04".equals(flag)) {
					flagData = "平衡型";
					datas = "您属于可以承担中等风险类型的投资者。您适合投资于能够为您提供温和升值能力，而投资价值有温和波动的投资工具。";
				} else if ("06".equals(flag)) {
					flagData = "进取型";
					datas = "您属于可以承担中等至高风险类型的投资者。您适合投资于能够为您提供升值能力，而投资价值有波动的投资工具。";
				} else if ("07".equals(flag)) {
					flagData = "激进型";
					datas = "您属于可以承受高风险类型的投资者。您适合投资于能够为您提供高升值能力而投资价值波动大的投资工具。最坏的情况下，您可能失去全部投资本金并需对您投资所导致的任何亏蚀承担责任。";
				}
				公共提示标志 = "成功";
				公共提示信息 = "您的风险评估结果为：" + flagData + "\n" + datas;
				return;
			} else {
				公共提示标志 = "提示";
				公共提示信息 = "理财风险评估信息新增失败：" + result.getErrorMsg();
				response.putDataBasket("success", "false");
				return;
			}

		} catch (Exception e) {
			公共提示标志 = "提示";
			公共提示信息 = "理财风险评估信息新增异常！" + e.getMessage();
			response.putDataBasket("success", "false");
			return;
		}

	}

	/**
	 * 原风险评估答案上送时为乱序 不按题号排序 对电子问卷获取的答案 进行排序，成功返回排序后 list ,排序出现异常则原样返回 list
	 * 因为后台已经优化按照 题号读取
	 * 
	 * @param answerList
	 * @return
	 */
	public List<String[]> answerRank(JSONObject res) {
		List<Entry<String, String>> list = new ArrayList(res.entrySet());
		Collections.sort(list, new Comparator<Entry<String, String>>() {

			@Override
			public int compare(Entry<String, String> o1,
					Entry<String, String> o2) {
				return o1.getKey().compareTo(o2.getKey());
			}
		});
		ListIterator<Entry<String, String>> iterator = list.listIterator();
		List<String[]> result = new ArrayList<String[]>();
		while (iterator.hasNext()) {
			Entry<String, String> e = iterator.next();
			String key = e.getKey().replace("topic", "");
			String value = e.getValue();
			result.add(new String[]{key,value});
		}
		return result;
	}

}
