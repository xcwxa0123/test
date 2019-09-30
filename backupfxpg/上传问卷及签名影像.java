package smart.modules.客户.在线风险评估;

import smart.lib.comm.ECMUtil;
import smart.lib.comm.SmartBaseUtil;
import smart.lib.module.ModuleCefTrade;
import smart.lib.module.SignUploadCefTrade;
import smart.lib.service.OpenService;
import smart.lib.template.FreeMarkerUtil;
import smart.lib.template.FreeMarkerUtil2;
import smart.lib.template.Model;
import ceb00.trx.lib.tools.BaseUtil;
import cn.com.agree.ab.trade.cef.ModuleRequest;
import cn.com.agree.ab.trade.cef.ModuleResponse;
import cn.com.agree.ab.trade.core.scope.Scope;
import cn.com.agree.ab.trade.dataStruct.view.View;

import com.alibaba.fastjson.JSONObject;

public class 上传问卷及签名影像 extends SignUploadCefTrade {
	private final String srcfolder = "signature";
	private final String appcode = "LCPG";
	private final String imageType = "LCPG";
	private final String SUCCESS = "success";
	private final String upFileType = "lcpg";

	// 默认是使用js去合成图片，
	public boolean isClientPluginMerge = false;

	public String customName = "";

	public String idCardNo = "";

	public String date = BaseUtil.getCurrentDate();

	public String time = BaseUtil.getCurrentTime();
	
	public String isUpImg = "";

	@Override
	public boolean canDoSubmit(ModuleRequest request) throws Exception {
		// TODO 自动生成的方法存根
		return true;
	}

	@Override
	public void doInit() throws Exception {
		// TODO 自动生成的方法存根
		logger.info("交易init");
		if (isClientPluginMerge) {
			moduleSubmit(null);
		}
	}

	public void upImg() throws Exception {
		logger.info("开始上传问卷及签名影像");
		ModuleRequest request = new ModuleRequest();
		request.putDataBasket("creatImg", true);
		moduleSubmit(request);
	}

	public void failHandler(String info) throws Exception {
		logger.info("生成图片失败:" + info);
		ModuleRequest request = new ModuleRequest();
		request.putDataBasket("creatImg", false);
		moduleSubmit(request);
	}

	@Override
	public void doModuleSubmit(ModuleRequest request, ModuleResponse response)
			throws Exception {
		// TODO 自动生成的方法存根
		Scope scope = this.getScope(Scope.dataBasket);
		scope.put("response", response);
		boolean flag = false;
		if (isClientPluginMerge) {
			if (isUpImg != null && !"".equals(isUpImg) && "上传问卷及影像".equals(isUpImg)) {
				flag = imageAddContent();
			}else {				
				flag = imageMerge();
			}
		} else {
			flag = request.getDataBasketBooleanValue("creatImg");
		}
		if (flag) {
			uploadImgHandler();
		} else {
			showMaskError("失败信息", "处理影像失败", "结束", "quitUpImg");
			response.stay();
		}
	}

	private boolean imageMerge() {
		JSONObject param = new JSONObject();
		param.put("method", "LCPG");
		JSONObject data = new JSONObject();
		data.put("flag", true);
		data.put("image1", srcfolder + "/answer.jpg");
		data.put("image2", srcfolder + "/signature.png");
		data.put("merge", srcfolder + "/lcpg.jpg");
		data.put("customName", customName);
		data.put("idCardNo", idCardNo);
		data.put("tradeDate", date);
		data.put("tradeTime", time);
		param.put("data", data);
		try {
			String filePath = issueCall("imageMerge", param.toString());
			if (null != filePath && filePath.length() > 0) {
				return true;
			}
		} catch (Exception e) {
			// TODO 自动生成的 catch 块
			logger.info("图片合并发生异常:", e);
		}
		return false;
	}
	
	private boolean imageAddContent() {
		JSONObject param = new JSONObject();
		param.put("method", "LCPG");
		JSONObject data = new JSONObject();
		data.put("flag", false);
		data.put("image", srcfolder + "/answer.jpg");
		data.put("merge", srcfolder + "/lcpg.jpg");
		data.put("customName", customName);
		data.put("idCardNo", idCardNo);
		data.put("tradeDate", date);
		data.put("tradeTime", time);
		param.put("data", data);
		try {
			String filePath = issueCall("imageMerge", param.toString());
			if (null != filePath && filePath.length() > 0) {
				return true;
			}
		} catch (Exception e) {
			// TODO 自动生成的 catch 块
			logger.info("图片合并发生异常:", e);
		}
		return false;
	}

	@Override
	public View doRenderView(ModuleRequest request) throws Exception {
		// TODO 自动生成的方法存根
		isClientPluginMerge = isNewMerge();
		Model model = new Model();
		customName = request.getDataBasketString("customName");
		idCardNo = request.getDataBasketString("idCardNo");
		model.put("customName", customName);
		model.put("idCardNo", idCardNo);
		model.put("date", date);
		model.put("time", time);
		isUpImg = request.getDataBasketString("上传影像标志");
		if (isClientPluginMerge) {
			return FreeMarkerUtil.getTemplate(this, "default.ftl", model);
		} else {
			if (isUpImg != null && !"".equals(isUpImg) && "上传问卷及影像".equals(isUpImg)) {
				logger.info("不上传电子签名。。。。");
				return FreeMarkerUtil2.getTemplate(this, "fupImg.ftl", model);
			}else {
				logger.info("上传电子签名。。。。");
				return FreeMarkerUtil2.getTemplate(this, "upImg.ftl", model);
			}
		}
	}

	/**
	 * 上传影像 评估问卷 LCPG
	 * 
	 * @return
	 */
	private String uploadImg(String docid) throws Exception {
		logger.info("问卷影像及电子签名上传开始");
		// 分行-->总行
		final String brno = "1001";
		String result = null;
		result = ECMUtil.upImage(this, srcfolder, docid, appcode, imageType,
				upFileType, brno);
		return result;
	}

	/**
	 * 上传影像处理
	 * 
	 * @throws Exception
	 */
	public void uploadImgHandler() throws Exception {
		Scope scope = this.getScope(Scope.dataBasket);
		ModuleResponse response = (ModuleResponse) scope.get("response");
		final String docid = SmartBaseUtil.createDocIndex(this);
		try {
			String result = uploadImg(docid);
			if (null != result) {
				if (SUCCESS.equals(result)) {
					logger.info("问卷影像及电子签名上传成功！");
					response.putDataBasket("lcpgDocId", docid);
					response.putDataBasket("signResult", "true");
					exit(0);
				} else {
					showMaskError("失败信息", "上传影像失败,原因为:" + result + "!", "重试",
							"uploadImgHandler", "结束", "quitUpImg");
					response.stay();
				}
			} else {
				showMaskError("失败信息", "上传影像失败", "重试", "" + "", "结束",
						"quitUpImg");
				response.stay();
			}
		} catch (Exception e) {
			// TODO: handle exception
			logger.info("上传影像异常", e);
			showMaskError("失败信息", "上传影像异常", "重试", "uploadImgHandler", "结束",
					"quitUpImg");
			response.stay();
		}
	}

	/**
	 * 退出子交易
	 * 
	 */
	public void quitUpImg() {
		Scope scope = this.getScope(Scope.dataBasket);
		ModuleResponse response = (ModuleResponse) scope.get("response");
		response.putDataBasket("signResult", "false");
		exit(0);
	}

	private boolean isNewMerge() {
		final String qualifier = "智柜图片";
		final String key = "imageMerge";
		final String status = "0";
		try {
			String value = OpenService.db.selectOtherParamInfo(qualifier, key,
					status);
			if (null != value && "1".equals(value)) {
				return true;
			}
		} catch (Exception e) {
			// TODO: handle exception
			logger.info("查询合并模式发生异常:", e);
		}
		return false;
	}

	@Override
	public void failHandler(String error, String info) throws Exception {
		// TODO 自动生成的方法存根
		
	}
}
