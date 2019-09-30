package smart.modules.协议阅读.信用卡面签声明;

import java.io.File;
import java.net.URL;

import com.alibaba.fastjson.JSONObject;

import smart.lib.device.DeviceUtil;
import smart.lib.dic.Dictionary;
import smart.lib.module.SignSubmitCefTrade;
import smart.lib.template.FreeMarkerUtil2;
import smart.lib.template.Model;
import cn.com.agree.ab.trade.cef.In;
import cn.com.agree.ab.trade.cef.ModuleRequest;
import cn.com.agree.ab.trade.cef.ModuleResponse;
import cn.com.agree.ab.trade.cef.Out;
import cn.com.agree.ab.trade.dataStruct.view.View;

public class 面签声明  extends SignSubmitCefTrade {
	public static final String 协议名称 = "protocal";
	public static final String 阅读时间 = "time";
	public static final String 按钮名称 = "btnName";
	public static final String PDF = "pdf";
	
	@In(Dictionary.客户.客户姓名)
	public String customName;

	@In(Dictionary.客户.身份证号)
	public String idCardNo;
	
	@Out(Dictionary.公共节点变量.公共提示标志)
	public String 公共提示标志;

	@Out(Dictionary.公共节点变量.公共提示信息)
	public String 公共提示信息;	
	
	@Out(Dictionary.COBPS.业务影像索引)
	public String CCMQID;
	
	public String failResult = "";
	
	public boolean signFlag = true;
	
	@Override
	public boolean canDoSubmit(ModuleRequest request) throws Exception {
		return true;
	}

	@Override
	public void doInit() throws Exception {
		
	}

	@Override
	public void doModuleSubmit(ModuleRequest request, ModuleResponse response)
			throws Exception {
		// 同意协议,继续走,什么都不用做
		try {
			if (signFlag) {	
				String fileNumber =  request.getDataBasketString("存档号");			
				logger.info("fileNumber"+fileNumber);
				JSONObject customInfo = new JSONObject();
				customInfo.put("customName", customName);
				customInfo.put("idCardNo", idCardNo);
				customInfo.put("fileNumber", fileNumber);
				JSONObject upImgResult = syncOpenFrame(上传面签声明及签名影像.class.getName(), customInfo);
				String signResult = upImgResult.getString("signResult");
				String docId = upImgResult.getString("ccmqDocId");
				response.putDataBasket("signResult", signResult);
				if (null != signResult && "true".equals(signResult)) {
					response.putDataBasket("ccmqDocId",docId);
					CCMQID = docId;
				}else {
					response.putDataBasket("ccmqDocId","");
					公共提示标志 = "错误";
					公共提示信息 = "上传影像失败,请联系大堂经理!";
				}
			}else{
				response.putDataBasket("signResult", "false");
				公共提示标志 = "错误";
				公共提示信息 = failResult+",请联系大堂经理!";
			}	
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

	@Override
	public View doRenderView(ModuleRequest request) throws Exception {
		String ftlName = "viewer.html";	
//			String protocal = request.getDataBasketString(协议名称);
			String btnName = request.getDataBasketString(按钮名称);
			String time = request.getDataBasketString(阅读时间);
			Model model = new Model();
			model.put("time", time);
			model.put("btnName", btnName);
			String pdf="/resource/pdf/电子申请声明及签署与授权书2.pdf";
			if (pdf != null) {
				// 先判断本地www下有没有这个文件
				URL url2 = this.getClass().getClassLoader().getResource(".");
				String pdfReally=pdf.substring(pdf.lastIndexOf("/")+1);
				logger.info("截取文件名称"+pdfReally);
					if (url2 != null) {
						File f = new File(url2.getFile());
						File gts = f.getParentFile();
						File target = new File(gts, "resources/www/pdf/" + pdfReally);
						logger.info("打开文件"+pdfReally+"  "+target.getPath());	
					}
				model.put(PDF, pdf);
			}
			return FreeMarkerUtil2.getTemplate(this, ftlName, model);
	}


	public void pageSubmit() throws Exception {
		moduleSubmit(null);
	}

	@Override
	public void pageSubmit(String str) throws Exception {
		// TODO 自动生成的方法存根
		
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
	public void errorHandler(String log, String info) throws Exception {
		if (null == info || "".equals(info)) {
			failResult = log;
		} else {
			failResult = info;
		}
		//失败处理 不走影像
		signFlag = false;
		logger.info("js操作失败:" + log);
		moduleSubmit(null);
	}
	
	// 重要的日志记一下
	public void recordLog(String info) {
		logger.info(info);
	}
	
}
