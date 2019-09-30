package smart.modules.协议阅读.信用卡面签声明;

import smart.lib.comm.ECMUtil;
import smart.lib.dic.Dictionary;
import smart.lib.face.FaceFileUtil;
import smart.lib.module.ModuleCefTrade;
import smart.lib.template.FreeMarkerUtil;
import smart.lib.template.Model;
import cn.com.agree.ab.trade.cef.In;
import cn.com.agree.ab.trade.cef.ModuleRequest;
import cn.com.agree.ab.trade.cef.ModuleResponse;
import cn.com.agree.ab.trade.dataStruct.view.View;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class 追加面签影像 extends ModuleCefTrade {
	
	@In(Dictionary.客户.现场照人脸信息)
	public String 现场照人脸信息;
	
	@In(Dictionary.客户.人脸识别现场照索引)
	public String ImglocalId;
	
	/**
	 * 人脸历史影像索引---现场照
	 */
	public String docidRlls = "";
	/**
	 * 人脸识别分数
	 */
	public String score = "";
	
	private final String SUCCESS = "success";
	
	@Override
	public boolean canDoSubmit(ModuleRequest request) throws Exception {
		// TODO 自动生成的方法存根
		return true;
	}

	@Override
	public void doInit() throws Exception {
		// TODO 自动生成的方法存根
		moduleSubmit(null);
	}
	
	public void submitAndcloseMaskPage() throws Exception{
		closeMaskPage();
		submit();
	}
	@Override
	public void doModuleSubmit(ModuleRequest request, ModuleResponse response)
			throws Exception {
		// TODO 自动生成的方法存根
		try {
			 String docid = request.getDataBasketString("ccmqdocid");
				String result =appendUpImg(docid); 
				if (null != result) {
					if (SUCCESS.equals(result)) {
						logger.info("追加客户证件影像成功！");
						response.putDataBasket("追加影像结果", "true");
					} else {
						showMaskError("失败信息", "影像处理失败,原因为:" + result + "!", "重试",
								"submitAndcloseMaskPage","结束","moduleDone");
						response.stay();
						return;
					}
				}else{
					showMaskError("失败信息", "影像处理失败", "重试", "submitAndcloseMaskPage","结束","moduleDone");
					response.stay();
					return;
				}
			
		}  catch (Exception e) {
			// TODO: handle exception
			logger.info("追加客户影像异常", e);
			showMaskError("失败信息", "追加影像异常", "重试", "submitAndcloseMaskPage","结束","moduleDone");
			response.stay();
			return;
		}	
	}

	@Override
	public View doRenderView(ModuleRequest request) throws Exception {
		// TODO 自动生成的方法存根
		Model model = new Model();
		model.put("info", "正在进行影像上传，请稍后......");
		return FreeMarkerUtil.getTemplate(this, "default.ftl", model);
	}

	
	/**
	 * 上传影像  CCMQ
	 * 
	 * @return
	 */
	private String appendUpImg(String docid) throws Exception {
		logger.info("追加影像信息");
		String result = null;
		JSONArray fileName = new JSONArray();
		fileName.add("IDback");
		fileName.add("IDfront");
		fileName.add("IDhead");
		fileName.add("finger");
		result = ECMUtil.appendUpImage(this, "idcard", docid, "CCMQ", "CCMQ", fileName);
		if (null != result && SUCCESS.equals(result)) {
			JSONArray fileNameface = new JSONArray();
				JSONObject faceInfo = null;
				if (现场照人脸信息 != null && !"".equals(现场照人脸信息)) {
					faceInfo = JSONObject.parseObject(现场照人脸信息);
					docidRlls = faceInfo.getString("DOCID");
					score = faceInfo.getString("LOCALESCORE");
				}
				/**
				 * 2、进行客户拍照操作的情况：此情况存在takephoto
				 * 2.1、当前人脸摄像头不正常
				 * 2.2、现场照影像是不存在
				 * 2.3、人脸摆拍照未保存（人体检活失败），分数值为-200。
				 */
				boolean status = checkCameraStatus();
				if (!status || docidRlls == null || "".equals(docidRlls)
						|| "-200".equals(score)) {
					fileNameface.add("takePhoto");
				}else{				
					//自动拍照的照片名称
					String localId= faceInfo.getString("LOCALEID");
					if(localId == null || localId.length() == 0){
						localId = ImglocalId;
					}
					fileNameface.add(localId);
				}
				result = ECMUtil.appendUpImage(this, "faceImg", docid, "CCMQ", "CCMQ", fileNameface);

		}else{
			return result;
		}
		return result;
	}
		/**
		 * 检测人脸摄像头状态是否正常
		 */
		public boolean checkCameraStatus() {
			String cameraStatus = "-15";
			try {
				cameraStatus = issueCall("faceCamera",
						FaceFileUtil.issueParameter("status"));
			} catch (Exception e) {
				logger.error("获取摄像头状态异常");
			}
			if ("1".equals(cameraStatus)) {
				return true;
			}
			return false;
		}

}
