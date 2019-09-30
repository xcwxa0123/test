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
	                            		<!-- <button class='ab-button' data-target='html' data-apply='reject'>����</button> -->
	                            		<button id='subBtn' class='ab-button' type='submit' data-time='${time!"-1"}'>${btnName!"����"}</button>
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
            <button type="button" class="close" data-dismiss="modal"><span class="sr-only">�ر�</span></button>
            <h3 class="modal-title">�������¿���������ǩ��</h3>
        </div>
        <div class="modal-body" style="height: 300px;">
			
        </div>
        <div class="modal-footer">
            <button type="button" id="clean" class="my-button-small">��д</button>
            <button type="button" id="save" class="my-button-small">ȷ��</button>
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
				//�����¼�
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
		//�������
		var swiper = new Swiper('#swiper',{
			scrollbar: '.swiper-scrollbar',
			direction:'vertical',
			slidesPerView: 'auto',
			mousewheelControl:true,
			freeMode:true,
			roundLengths:true,//��ֹ����ģ��
		});
        sign = new Signature();
        delStatement();
        
    })

    let delStatement = function(){
        const srcPath = "statement";
        domain.require("fs").then(cs =>{
            cs.delDirectory(srcPath).then(result =>{
                console.log("ɾ��statement�ļ��������ݽ��:"+result);
            });
        });
    }



    //�ύ��ť�����ķ���
    let changeFun = function(length){
        $("#subBtn").click(function () {
            let result = check(map,length);
            //��pageSubmit��ֵ
            str = JSON.stringify(map);
            if (result){
                domain.userConfirm("","ȷ���������ʾ������޸ģ�ȷ���ύ��", "ȷ��", "ȡ��", true ,function(){
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
                //��ȥ��ģ̬��
                let options = {
                    backdrop:false,
                    keyboard:false,
                    position:'center'
                }
                $('#myModal').modal(options);
            }

            let baseImgFail = function(result){
                showIsUserConfirm("�������ÿ���������ͼƬʧ��,�Ƿ�����","domToCanvas�����ʾ�ͼƬʧ��,ԭ����:"+result,domToCanvas);
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

    //���϶�������ͼƬ

    //��������
    let lockRadio = function(){
        $("input.radio_options").attr("disabled","disabled");
    }

    //ģ̬����ȫ��Ⱦ���
	$('#myModal').on('shown.zui.modal',function(){
        sign_open();
    });

    let sign_open = function (){
        let successFun = function(data){
            console.log("�򿪵���ǩ�����:"+data);
			let obj = eval('('+data+')');
            if("result" == obj.type && "true" == obj.result){}else{
                console.log("�򿪵���ǩ��ʧ��:"+data);
                errorHandler("�򿪵���ǩ��ʧ��,"+data,"�򿪵���ǩ��ʧ��");
            }
		}
		
		let failFun = function(err){
			console.log("�򿪵���ǩ���쳣:"+err);
            errorHandler("�򿪵���ǩ���쳣,"+err,"�򿪵���ǩ���쳣");
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
            console.log("�������ǩ�����:"+data);
            let obj = eval('('+data+')');
            if("result" == obj.type && "true" == obj.result){
                $('#myModal').modal('hide');
                sign_close_forSubmit();
            }else{
                if("error"==obj.type && "e1204" == obj.code){
                    showInfo_warning("����ɵ���ǩ��");
                    return;
                }else {
                    errorHandler("�������ǩ��ʧ��,"+data,"�������ǩ��ʧ��");     
                }
            }
        }

        let failFun = function(err){
            console.log("�������ǩ���쳣:"+err);
            errorHandler("�������ǩ���쳣,"+err);
        }
        sign.save("png").then(successFun,failFun);
    })

    //�����رղ���������
    let sign_close_forSubmit = function(){
        let successFun = function(data){
            console.log("�رյ���ǩ�����:"+data);
            let obj = eval('('+data+')');
            if("result" == obj.type && "true" == obj.result){
                domain.callMethodPromise('pageSubmit',str);
            }else{
                console.log("�ر�ʧ�ܣ�����1��,"+data);
                sign_close_forErr();
            }
        }

        let failFun = function(err){
            console.log("�رյ���ǩ���쳣:"+err);
            console.log("�ر�ʧ�ܣ�����1��");
            sign_close_forErr();
        }

        sign.close().then(successFun,failFun);
    }

    //�쳣�رգ�������
    let sign_close_forErr = function(){
        let successFun = function(data){
            console.log("�رյ���ǩ�����Խ��:"+data);
            let obj = eval('('+data+')');
            if("result" == obj.type && "true" == obj.result){
                domain.callMethodPromise('pageSubmit',str);
            }else{
                console.log("�رյ���ǩ������ʧ��:,"+data);
                errorHandler("�رյ���ǩ������ʧ��:,"+data,"�رյ���ǩ��ʧ��");      
            }
        }

        let failFun = function(err){
             console.log("���Թرյ���ǩ���쳣:"+err);
             errorHandler("���Թرյ���ǩ���쳣:"+err);
        }
        sign.close().then(successFun,failFun);
    }

    $("#clean").click(function(){
        let successFun = function(data){
            console.log("���ǩ�����:"+data);
            let obj = eval('('+data+')');
            if("result" == obj.type && "true" == obj.result){}else{
                console.log("�رյ���ǩ������ʧ��:,"+data);
                errorHandler("���ǩ��ʧ��:"+data,"���ǩ��ʧ��");
            }
        }

        let failFun = function(err){
            console.log("���ǩ���쳣:"+err);
            errorHandler("���ǩ���쳣:"+err);
        }
        sign.clean().then(successFun,failFun);
    })

    //info չʾ���ͻ�����Ϣ  desc ��־��¼��������Ϣ
    let showIsUserConfirm = function(info,desc,retry){
        //������ʾ��
        domain.userConfirm("",info,"����","ȡ��",true,retry,function(){
            recordLog(desc);
        });
    }

    //����˼�¼��Ϣ
    let recordLog = function(desc){
        domain.callMethodPromise("recordLog",desc);
    }

    //������쳣���� 
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