package smart.modules.客户.在线风险评估;

import smart.lib.dic.Dictionary;
import smart.lib.module.CefTrade;
import smart.lib.module.ModuleCefCalcTrade;
import smart.lib.template.FreeMarkerUtil;
import smart.lib.template.Model;
import smart.lib.tools.licai.UploadLCPGInfo;
import cn.com.agree.ab.trade.cef.In;
import cn.com.agree.ab.trade.cef.ModuleRequest;
import cn.com.agree.ab.trade.cef.ModuleResponse;
import cn.com.agree.ab.trade.dataStruct.view.View;

public class 上传签名绑定信息 extends ModuleCefCalcTrade {
	private final String SMART_TRADEQTLS = "qtls";
	private final String ECIF_RISKLEVEL = "ecif风险等级";
	private final String LCPGDOCID = "lcpgDocId";
	private final String UPLCPGDOCID = "uplcpgDocId";

	@In(Dictionary.客户.身份证号)
	public String cardNum;

	@In(Dictionary.客户.客户号)
	public String customNum;

	@In(Dictionary.客户.客户姓名)
	public String customName;

	@Override
	public void doCalcSubmit(ModuleRequest request, ModuleResponse response)
			throws Exception {
		// TODO 自动生成的方法存根
		String ecif_RiskLevel = request.getDataBasketString(ECIF_RISKLEVEL);
		if (null == ecif_RiskLevel || "".equals(ecif_RiskLevel)) {
			logger.info("ecif无等级,需上传客户签名信息");
			String docId = request.getDataBasketString(LCPGDOCID);
			String updocId = request.getDataBasketString(UPLCPGDOCID);
			if ((null == docId || "".equals(docId))  && (null == updocId || "".equals(updocId))) {
				logger.info("lcpg的docId不存在,不上传相关绑定信息!");
			}else {
				logger.info("lcpg的docId存在,上传相关绑定信息!docId:"+docId);
				String fserialNo = request.getDataBasketString(SMART_TRADEQTLS);
				try {
					if(updocId != null && !"".equals(updocId)){
						pushInfo("不带签名",true);
						uploadLCPGInfo(this, updocId, fserialNo);
					}else{
						pushInfo("带签名");
						uploadLCPGInfo(this, docId, fserialNo);
					}
				} catch (Exception e) {
					// TODO: handle exception
					logger.info("登记影像信息异常:", e);
				}
			}
			
		} else {
			logger.info("ecif有等级,无需上传客户签名信息");
		}
	}

	private boolean uploadLCPGInfo(CefTrade trade, String docId,
			String fserialNo) {
		final String cardType = "1";
		boolean uploadFlag = UploadLCPGInfo.uploadInfoHandler(trade, docId,
				cardNum, fserialNo, customNum, customName, cardType);
		return uploadFlag;
	}

	@Override
	public boolean canDoSubmit(ModuleRequest request) throws Exception {
		// TODO 自动生成的方法存根
		return true;
	}

	@Override
	public View doRenderView(ModuleRequest request) throws Exception {
		// TODO 自动生成的方法存根
		Model model = new Model();
		return FreeMarkerUtil.getTemplate(this, "default.ftl", model);
	}
}
