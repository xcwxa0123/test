<script>
	<#include "signature.js">
</script>
<div class="modal fade" id="myModal">
    <div class="modal-dialog">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal"><span class="sr-only">关闭</span></button>
            <h3 class="modal-title">如您已知悉上述内容，请用<span class="modal-title-type">楷体</span>抄录“<span class="modal-title-type">已阅读，遵守</span>”并签署<span class="modal-title-type">本人姓名</span>。</h3>
        </div>
        <div class="modal-body" style="background: rgba(255,255,255,1) url('/resource/img/米字格.png') repeat;">
			
        </div>
        <div class="modal-footer">
           <!-- <button type="button" id="clean" class="my-button-small">重写</button> -->
            <div class="tab-content">
				<div class="slideunlock-wrapper">
					<input type="hidden" value="" class="slideunlock-lockable" />
					<div class="slideunlock-slider branch ">
						<span class="slideunlock-label">&gt;&gt;</span>
						<span class="slideunlock-lable-tip"></span>
					</div>
			    </div>
	        </div>
		<button type="button" id="clean" class="my-button-small">重写</button>
        </div>
    </div>
</div>

<script>


//电子签名框坐标和大小。先初始化为空，再在点击submit按钮的时候赋值
let FCoordinate = {};

//点击提交时是传送的数据。先初始化为空，若点击时传送数据则赋值，否则为默认空
let signData = "";

let delsignature = function () {
    const srcPath = "signature";
    domain.require("fs").then(cs => {
        cs.delDirectory(srcPath).then(result => {
            console.log("删除signature文件夹下内容结果:" + result);
        });
    });
}


let domToCanvas = function (canvasArea) {
    console.log("进入domToCanvas");
    html2canvas(canvasArea[0]).then(canvas => {
        let baseImgSuccess = function (result) {
            //先去打开模态窗
            let options = {
                backdrop: false,
                keyboard: false,
                position: 'center'
            }
            console.log("打开模态窗");
            $('#myModal').modal(options);
        }

        let baseImgFail = function (result) {
            showIsUserConfirm("生成信用卡申请声明图片失败,是否重试", "domToCanvas生成问卷图片失败,原因是:" + result, domToCanvas);
        }

        let basePath = canvas.toDataURL('image/jpeg', '1.0');
        base64ToImg(basePath).then(baseImgSuccess, baseImgFail).catch(err => {
            console.log(err);
        });
    });
}


let base64ToImg = function (basePath) {
    const filePath = "signature\\answer.jpg";
    return domain.require("fs").then(cs => {
        cs.writePhoto(filePath, basePath).then(result => {
            return result;
        });
    });
}

//模态窗完全渲染完成

$('#myModal').on('shown.zui.modal', function () {
    console.log("进入zui的模态框渲染完毕方法");
    sign_open(FCoordinate);
});

let sign_open = function (FCoordinate) {
    console.log("进入签名窗口实例化方法，并查看是否有接受到坐标值");
    console.log(FCoordinate);
    console.log("wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww");
    let successFun = function (data) {
        console.log("打开电子签名结果:" + data);
        let obj = eval('(' + data + ')');
        if ("result" == obj.type && "true" == obj.result) { } else {
            console.log("打开电子签名失败:" + data);
            errorHandler("打开电子签名失败," + data, "打开电子签名失败");
        }
    }
    let failFun = function (err) {
        console.log("打开电子签名异常:" + err);
        errorHandler("打开电子签名异常," + err, "打开电子签名异常");
    }
    let transparent = FCoordinate.transparent || "0";
    let xSite = FCoordinate.xSite || "477";
    let ySite = FCoordinate.ySite || "435";
    let height = FCoordinate.height || "303";
    let width = FCoordinate.width || "570";
    let title = FCoordinate.title || "";
    let desc = FCoordinate.desc || "";
    console.log("-------------测试接收数据--------------"+transparent+","+xSite+","+ySite+","+height+","+width+","+title+","+desc+"-------------------------------------");
    sign.openWindow(transparent, xSite, ySite, height, width, title, desc).then(successFun, failFun);
}

$("#save").click(function () {
	console.log('button has been clicked');
    let successFun = function (data) {
        console.log("保存电子签名结果:" + data);
        let obj = eval('(' + data + ')');
        if ("result" == obj.type && "true" == obj.result) {
            $('#myModal').modal('hide');
            sign_close_forSubmit();
        } else {
            if ("error" == obj.type && "e1204" == obj.code) {
                showInfo_warning("请完成电子签名");
                return;
            } else {
                errorHandler("保存电子签名失败," + data, "保存电子签名失败");
            }
        }
    }
    
    let failFun = function (err) {
        console.log("保存电子签名异常:" + err);
        errorHandler("保存电子签名异常," + err);
    }
    sign.save("png").then(successFun, failFun);
})

//正常关闭并继续流程
let sign_close_forSubmit = function () {
    let successFun = function (data) {
        console.log("关闭电子签名结果:" + data);
        console.log("测试是否可以接受到signData字符串"+signData+"--------------------------------------");
        let obj = eval('(' + data + ')');
        if ("result" == obj.type && "true" == obj.result) {
            if(signData == "" || signData == null || signData == undefined){
        		domain.callMethodPromise('pageSubmit');	
        	}else {
        		domain.callMethodPromise('pageSubmit',signData);
        	}
        } else {
            console.log("关闭失败，重试1次," + data);
            sign_close_forErr();
        }
    }

    let failFun = function (err) {
        console.log("关闭电子签名异常:" + err);
        console.log("关闭失败，重试1次");
        sign_close_forErr();
    }

    sign.close().then(successFun, failFun);
}

//异常关闭，并重试
let sign_close_forErr = function () {
    let successFun = function (data) {
        console.log("关闭电子签名重试结果:" + data);
        console.log("测试是否可以接受到signData字符串"+signData+"--------------------------------------");
        let obj = eval('(' + data + ')');
        if ("result" == obj.type && "true" == obj.result) {
        	if(signData == "" || signData == null || signData == undefined){
        		domain.callMethodPromise('pageSubmit');	
        	}else {
        		domain.callMethodPromise('pageSubmit',signData);
        	}
        } else {
            console.log("关闭电子签名重试失败:," + data);
            errorHandler("关闭电子签名重试失败:," + data, "关闭电子签名失败");
        }
    }

    let failFun = function (err) {
        console.log("重试关闭电子签名异常:" + err);
        errorHandler("重试关闭电子签名异常:" + err);
    }
    sign.close().then(successFun, failFun);
}

$("#clean").click(function () {
	console.log('button has been clicked');
    let successFun = function (data) {
        console.log("清除签名结果:" + data);
        let obj = eval('(' + data + ')');
        if ("result" == obj.type && "true" == obj.result) { } else {
            console.log("关闭电子签名重试失败:," + data);
            errorHandler("清除签名失败:" + data, "清除签名失败");
        }
    }

    let failFun = function (err) {
        console.log("清除签名异常:" + err);
        errorHandler("清除签名异常:" + err);
    }
    sign.clean().then(successFun, failFun);
})

//info 展示给客户的信息  desc 日志记录的描述信息
let showIsUserConfirm = function (info, desc, retry) {
    //弹出提示窗
    domain.userConfirm("", info, "重试", "取消", true, retry, function () {
        recordLog(desc);
    });
}

//服务端记录信息
let recordLog = function (desc) {
    domain.callMethodPromise("recordLog", desc);
}

//服务端异常处理
let errorHandler = function (log, info) {
    if (arguments.length == 1) {
        domain.callMethodPromise("errorHandler", log, "");
    } else {
        domain.callMethodPromise("errorHandler", log, info);
    }

}

let showInfo = function (info, type) {
    new $.zui.Messager(info, {
        type: type
    }).show();
}

let showInfo_danger = function (info) {
    showInfo(info, 'danger');
}

let showInfo_warning = function (info) {
    showInfo(info, 'warning');
}

let configJSign = function (submitButton, canvasArea, frameCoordinate, teleSignData) {
    //设置组件  电子签名层信息，单独作为一个方法提出来
    console.log("进入configJSign,测试是否能接受到传递数据");
    console.log(frameCoordinate);
    FCoordinate = frameCoordinate;
    signData = teleSignData;
    var swiper = new Swiper('#swiper', {
        scrollbar: '.swiper-scrollbar',
        direction: 'vertical',
        slidesPerView: 'auto',
        mousewheelControl: true,
        freeMode: true,
        roundLengths: true,//防止文字模糊
    });
    sign = new Signature();
    delsignature();//调用signInit.js
    if(submitButton == ""){
    	domToCanvas(canvasArea);
	    sliderInit();
    }else{
    	submitButton.click(function () {
	        console.log("submit按钮点击触发");
	        //调用signInit.js中的画框方法
	        domToCanvas(canvasArea);
		    sliderInit(); 
    	});
    }
    
}

/*滑块*/
//unlock方法，解锁成功后执行
let unlock = function (type) {
    console.log("执行了unlock方法");
    let request = {};
    let unlock = {};
    request.device = "lock"
    request.unlock = unlock;
    unlock.type = type;
    let successFun = function (data) {
        console.log("保存电子签名结果:" + data);
        let obj = eval('(' + data + ')');
        if ("result" == obj.type && "true" == obj.result) {
            $('#myModal').modal('hide');
            sign_close_forSubmit();
        } else {
            if ("error" == obj.type && "e1204" == obj.code) {
                showInfo_warning("请完成电子签名");
                return;
            } else {
                errorHandler("保存电子签名失败," + data, "保存电子签名失败");
            }
        }
    }
    let failFun = function (err) {
        console.log("保存电子签名异常:" + err);
        errorHandler("保存电子签名异常," + err);
    }
    sign.save("png").then(successFun, failFun);

}
//sliderInit方法
let sliderInit = function () {
    let slider_Branch = new SliderUnlock(".slideunlock-slider.branch", {
        successLabelTip: "确认中",
        labelTip: "&gt;&nbsp;滑动确认",
        duration: 200   // 
    }, function () {
        unlock("branch");
        setTimeout(function () {
            slider_Branch.reset();
        }, 2000)
    }, function () {

    });
    //slider_Branch.style.disply = "flex";
    //slider_Branch.style.justify-content = "center";
    //slider_Branch.style.align-items = "center";
    slider_Branch.init();
};

</script>