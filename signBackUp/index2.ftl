<script>
	<#include "signature.js">
</script>
<style>
	::-webkit-scrollbar{
	    display:none;
	}
	.msgBtn{
		    transform: translateY(-5px);
	}
	.msgContainer
	{
		margin-top:30px;
	}
	#submit{
		margin-top: 80px;
	}
	.ab-info.margin
	{
		font-size:28px;
	}
	
	.ceb .ab-wrapper .ab-content{
		text-align:center; 
		align-items:center;
	}
	.ab-body{
		text-align:left; 
		align-items:left;
	}	
	.ab-content .ab-text-title{
		text-align:left; 
		align-items:left;
	}	
	.my-button{
		outline: none;
		text-align: center;
		box-shadow: none;
		border: none;
		color: #ffffff;
		background: #AE41CF;
		width: 200px;
		height: 60px;
		line-height: 60px;
		border-radius: 4px;
		font-size: 16px;
		margin: 30px;
	}

    .my-button-small{
		outline: none;
		text-align: center;
		box-shadow: none;
		border: none;
		color: #ffffff;
		background: #AE41CF;
		width: 100px;
		height: 40px;
		border-radius: 4px;
		font-size: 16px;
		margin: 15px;
	}

    #modalWindow{
        width: 300px;
        height: 300px;
        background-color: rgba(255,255,255,0);
        opacity:0.5;
    }

    #myModal{
        background-color: rgba(255,255,255,0);
    }

    .modal-dialog{
        background-color: rgba(255,255,255,0.4);
    }
    .modal-header{
        border:0px;
        background-color: rgba(255,255,255,0.3);
    }
    .modal-footer{
        border:0px;
        background-color: rgba(255,255,255,0.3);
    }
    .btn.btn-primary{
        background-color:#6b1685
    }
</style>
<div class='ab-wrapper'>
	<div class='ab-content'>
		 <div class="swiper-container" id='swiper'>
	                <div class="swiper-wrapper">
	                        <div class="swiper-slide">
	                            <div class='ab-protocal'>
	                            	<div class='ab-protocal-content' id='ab-protocal-content'>
	                            		${protocalContent!""}
	                            	</div>
	                            	<div class='ab-protocal-control'>
	                            		<!-- <button class='ab-button' data-target='html' data-apply='reject'>放弃</button> -->
	                            		<button id='subBtn' class='ab-button' type='submit' data-time='${time!"-1"}'>${btnName!"继续"}</button>
	                            	</div>
	                            </div>
	                        </div>
	                </div>
	                <div class='swiper-scrollbar'></div>
	      </div>
	</div>

</div>

<div class="modal fade" id="myModal">
    <div class="modal-dialog">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal"><span class="sr-only">关闭</span></button>
            <h3 class="modal-title">请在以下框内用正楷签名</h3>
        </div>
        <div class="modal-body" style="height: 300px;">
			
        </div>
        <div class="modal-footer">
            <button type="button" id="clean" class="my-button-small">重写</button>
            <button type="button" id="save" class="my-button-small">确定</button>
        </div>
    </div>
</div>

<script>
    $(function(){
    	let submit= $('#subBtn');
		let name =submit.text();
		let time = submit.data('time');
		if(time>0)
		{
			time=time*1000;
			submit.attr('disabled',true);
			let cycle =setInterval(function(){
				//发送事件
				domain.emit('notIdle');
				time-=1000;
				if(time<=0)
				{
					clearInterval(cycle);
					submit.text(name);
					submit.removeAttr('disabled');
				}else
				{
					submit.text(name+'('+(time/1000)+')');
				}			
			},1000);
		}
		//设置组件
		var swiper = new Swiper('#swiper',{
			scrollbar: '.swiper-scrollbar',
			direction:'vertical',
			slidesPerView: 'auto',
			mousewheelControl:true,
			freeMode:true,
			roundLengths:true,//防止文字模糊
		});
        sign = new Signature();
        delStatement();
        
    })

    let delStatement = function(){
        const srcPath = "statement";
        domain.require("fs").then(cs =>{
            cs.delDirectory(srcPath).then(result =>{
                console.log("删除statement文件夹下内容结果:"+result);
            });
        });
    }



    //提交按钮触发的方法
    let changeFun = function(length){
        $("#subBtn").click(function () {
            let result = check(map,length);
            //给pageSubmit的值
            str = JSON.stringify(map);
            if (result){
                domain.userConfirm("","确定后评估问卷不能再修改，确定提交？", "确定", "取消", true ,function(){
                        lockRadio();
                        domToCanvas();
                  
                },function(){
                    return;
                });
            }
        })    
    }

   

    let domToCanvas = function(){
        html2canvas($(".ab-body")[0]).then(canvas=>{
            let baseImgSuccess = function(result){
                //先去打开模态窗
                let options = {
                    backdrop:false,
                    keyboard:false,
                    position:'center'
                }
                $('#myModal').modal(options);
            }

            let baseImgFail = function(result){
                showIsUserConfirm("生成信用卡申请声明图片失败,是否重试","domToCanvas生成问卷图片失败,原因是:"+result,domToCanvas);
            }

            let basePath = canvas.toDataURL('image/jpeg', '1.0');
            base64ToImg(basePath).then(baseImgSuccess,baseImgFail).catch(err =>{
            	console.log(err);
            });
        });
    }

    let base64ToImg = function(basePath){
        const filePath = "statement\\answer.jpg";
        return domain.require("fs").then(cs =>{
            cs.writePhoto(filePath,basePath).then(result =>{
                return result;
            });
        });
    }

    //以上都是生成图片

    //锁定界面
    let lockRadio = function(){
        $("input.radio_options").attr("disabled","disabled");
    }

    //模态窗完全渲染完成
	$('#myModal').on('shown.zui.modal',function(){
        sign_open();
    });

    let sign_open = function (){
        let successFun = function(data){
            console.log("打开电子签名结果:"+data);
			let obj = eval('('+data+')');
            if("result" == obj.type && "true" == obj.result){}else{
                console.log("打开电子签名失败:"+data);
                errorHandler("打开电子签名失败,"+data,"打开电子签名失败");
            }
		}
		
		let failFun = function(err){
			console.log("打开电子签名异常:"+err);
            errorHandler("打开电子签名异常,"+err,"打开电子签名异常");
		}

        let transparent = "30";
		let xSite = "468";
        let ySite = "430";
        let height = "300";
		let	width = "600";
		let title = "";
		let desc = "";
		sign.openWindow(transparent,xSite,ySite,height,width,title,desc).then(successFun,failFun);
    }

    $("#save").click(function(){
        let successFun = function(data){
            console.log("保存电子签名结果:"+data);
            let obj = eval('('+data+')');
            if("result" == obj.type && "true" == obj.result){
                $('#myModal').modal('hide');
                sign_close_forSubmit();
            }else{
                if("error"==obj.type && "e1204" == obj.code){
                    showInfo_warning("请完成电子签名");
                    return;
                }else {
                    errorHandler("保存电子签名失败,"+data,"保存电子签名失败");     
                }
            }
        }

        let failFun = function(err){
            console.log("保存电子签名异常:"+err);
            errorHandler("保存电子签名异常,"+err);
        }
        sign.save("png").then(successFun,failFun);
    })

    //正常关闭并继续流程
    let sign_close_forSubmit = function(){
        let successFun = function(data){
            console.log("关闭电子签名结果:"+data);
            let obj = eval('('+data+')');
            if("result" == obj.type && "true" == obj.result){
                domain.callMethodPromise('pageSubmit',str);
            }else{
                console.log("关闭失败，重试1次,"+data);
                sign_close_forErr();
            }
        }

        let failFun = function(err){
            console.log("关闭电子签名异常:"+err);
            console.log("关闭失败，重试1次");
            sign_close_forErr();
        }

        sign.close().then(successFun,failFun);
    }

    //异常关闭，并重试
    let sign_close_forErr = function(){
        let successFun = function(data){
            console.log("关闭电子签名重试结果:"+data);
            let obj = eval('('+data+')');
            if("result" == obj.type && "true" == obj.result){
                domain.callMethodPromise('pageSubmit',str);
            }else{
                console.log("关闭电子签名重试失败:,"+data);
                errorHandler("关闭电子签名重试失败:,"+data,"关闭电子签名失败");      
            }
        }

        let failFun = function(err){
             console.log("重试关闭电子签名异常:"+err);
             errorHandler("重试关闭电子签名异常:"+err);
        }
        sign.close().then(successFun,failFun);
    }

    $("#clean").click(function(){
        let successFun = function(data){
            console.log("清除签名结果:"+data);
            let obj = eval('('+data+')');
            if("result" == obj.type && "true" == obj.result){}else{
                console.log("关闭电子签名重试失败:,"+data);
                errorHandler("清除签名失败:"+data,"清除签名失败");
            }
        }

        let failFun = function(err){
            console.log("清除签名异常:"+err);
            errorHandler("清除签名异常:"+err);
        }
        sign.clean().then(successFun,failFun);
    })

    //info 展示给客户的信息  desc 日志记录的描述信息
    let showIsUserConfirm = function(info,desc,retry){
        //弹出提示窗
        domain.userConfirm("",info,"重试","取消",true,retry,function(){
            recordLog(desc);
        });
    }

    //服务端记录信息
    let recordLog = function(desc){
        domain.callMethodPromise("recordLog",desc);
    }

    //服务端异常处理 
    let errorHandler = function(log,info){
        if(arguments.length == 1){
            domain.callMethodPromise("errorHandler",log,"");
        }else{
            domain.callMethodPromise("errorHandler",log,info);
        }
        
    }

    let showInfo = function(info,type){
        new $.zui.Messager(info, {
            type:type
        }).show();
    }

    let showInfo_danger = function(info){
        showInfo(info,'danger');
    }

    let showInfo_warning = function(info){
        showInfo(info,'warning');
    }

</script>