package smart.modules.协议阅读.信用卡面签声明;

import com.alibaba.fastjson.JSONObject;

import smart.lib.comm.ECMUtil;
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


public class 上传面签声明及签名影像  extends SignUploadCefTrade {
	//-------------------默认参数-------------------------
	private final String srcfolder = "signature";
	private final String appcode = "CCMQ";
	private final String imageType = "CCMQ";
	private final String SUCCESS = "success";
	private final String upFileType = "ccmq";

	// 默认是使用js去合成图片，
	public static boolean isClientPluginMerge = false;

	public String customName = "";

	public String idCardNo = "";

	public String date = BaseUtil.getCurrentDate();

	public String time = BaseUtil.getCurrentTime();

	public String fileNumber = "";
	
	//------------------------------------------------
	
	
	@Override
	public boolean canDoSubmit(ModuleRequest request) throws Exception {
		// TODO 自动生成的方法存根
		return true;
	}

	@Override
	public void doInit() throws Exception {
		logger.info("交易init");
		if (isClientPluginMerge) {
			moduleSubmit(null);
		}
	}



	@Override
	public void doModuleSubmit(ModuleRequest request, ModuleResponse response)
			throws Exception {
		// TODO 自动生成的方法存根
		try {
			Scope scope = this.getScope(Scope.dataBasket);
			scope.put("response", response);
			boolean flag = false;
			if (isClientPluginMerge) {
				//先调用图像生成
				flag = imageCCMQ();
			} else {
				//upImg方法调用成功后，creatImg变量为true
				flag = request.getDataBasketBooleanValue("creatImg");
			}
			//根据图像生成方法的返回来决定是否继续上传
			if (flag) {
				uploadImgHandler();
			} else {
				showMaskError("失败信息", "处理影像失败", "结束", "quitUpImg");
				response.stay();
			}
		} catch (Exception e) {
			// TODO: handle exception
			logger.info("处理影像异常", e);
			showMaskError("失败信息", "处理影像异常", "结束", "quitUpImg");
			response.stay();
		
		}
	}


	@Override
	public View doRenderView(ModuleRequest request) throws Exception {
				isClientPluginMerge = isNewMerge();
				Model model = new Model();
				customName = request.getDataBasketString("customName");
				idCardNo = request.getDataBasketString("idCardNo");
				model.put("customName", customName);
				model.put("idCardNo", idCardNo);
				model.put("date", date);
				model.put("time", time);
				fileNumber = request.getDataBasketString("fileNumber");
				if (isClientPluginMerge) {
					return FreeMarkerUtil.getTemplate(this, "default.ftl", model);
				} else {
					logger.info("上传电子签名。。。。");
					return FreeMarkerUtil2.getTemplate(this, "upImg.ftl", model);
				}
		
	}
	/**
	 * 上传影像处理
	 * 提出
	 * @throws Exception
	 */
	public void uploadImgHandler() throws Exception {
		Scope scope = this.getScope(Scope.dataBasket);
		ModuleResponse response = (ModuleResponse) scope.get("response");
		try {
			final String docid = fileNumber + fileNumber;
			logger.info("影像索引存档号docid" + fileNumber);
			String result = delete(docid);
			if (null != result) {
				if (SUCCESS.equals(result)) {
					logger.info("原影像删除成功！");
					result = uploadImg(docid);
					if (null != result) {
						if (SUCCESS.equals(result)) {
							logger.info("面签声明及电子签名上传成功！");
							response.putDataBasket("ccmqDocId", docid);
							response.putDataBasket("signResult", "true");
							exit(0);
						} else {
							showMaskError("失败信息", "上传影像失败,原因为:" + result + "!",
									"重试", "uploadImgHandler", "结束", "quitUpImg");
							response.stay();
						}
					} else {
						showMaskError("失败信息", "上传影像失败", "重试",
								"uploadImgHandler", "结束", "quitUpImg");
						response.stay();
					}
				} else {
					showMaskError("失败信息", "影像处理失败,原因为:" + result + "!", "重试",
							"uploadImgHandler", "结束", "quitUpImg");
					response.stay();
				}
			} else {
				showMaskError("失败信息", "影像处理失败", "重试", "uploadImgHandler", "结束",
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
	@Override
	public void upImg() throws Exception {
		logger.info("开始上传面签声明签署函及签名影像");
		ModuleRequest request = new ModuleRequest();
		request.putDataBasket("creatImg", true);
		moduleSubmit(request);
	}
	
	@Override
	public void failHandler(String info) throws Exception {
		logger.info("生成图片失败:" + info);
		ModuleRequest request = new ModuleRequest();
		request.putDataBasket("creatImg", false);
		moduleSubmit(request);
	}
	
	@Override
	public void failHandler(String error, String info) throws Exception {
		logger.info(error + "生成图片失败:" + info);
		ModuleRequest request = new ModuleRequest();
		request.putDataBasket("creatImg", false);
		moduleSubmit(request);
	}
	
	protected boolean imageCCMQ() {
		JSONObject param = new JSONObject();
		param.put("method", "CCMQ");
		JSONObject data = new JSONObject();
		data.put("flag", true);
		data.put("image1", srcfolder + "/answer.jpg");
		data.put("image2", srcfolder + "/signature.png");
		data.put("merge", srcfolder + "/ccmq.jpg");
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
	
	protected boolean isNewMerge() {
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
	
	protected String delete(String docid) throws Exception {
		logger.info("删除面签声明及电子签名");
		String result = null;
		result = ECMUtil.deleteImage(this, docid, appcode, imageType);
		return result;
	}
	
	protected String uploadImg(String docid) throws Exception {
		logger.info("面签声明及电子签名上传开始");
		// 分行-->总行
		// final String brno = "1001";
		String result = null;
		// Map tellerInfo = this.getTellerInfo();
		// String brno = (String)tellerInfo.get("G_BUSIBRNO");
		result = ECMUtil.upImage(this, srcfolder, docid, appcode, imageType,
				upFileType);
		return result;
	}

}
