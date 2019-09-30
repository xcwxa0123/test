<script>
	<#include "signature.js">
    <#include "jquery.slideunlock.js">	
</script>
<style>
    <#include "slideunlock.css">
    <#include "signLayer.css">
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

</style>
<div class='ab-wrapper'>
	<div class="ab-content">

			<div style="margin-top: 1400px;" >
			<span class="title" style="font-size:25px" ><strong style="text-align:center">�й�������пͻ�Ͷ�ʷ��ճ������������ʾ�</strong></span>	
			<div class="ab-text-title" style="margin-top:20px;">
			<p>���е�һ�������κ����Ͷ�ʲ�Ʒǰ������д���ʾ�����ÿ������������������ʾ�ּ���˽�</p>
			<p>���ɳ��ܵķ��ճ̶ȡ��Լ�����Ͷ�ʾ��飬���Э����ѡ����ʵ���Ʋ�Ʒ����Դﵽ����Ͷ��Ŀ�ꡣ</p>
			<p>�����н�������������������Ӱ��������ճ������������Σ��ٴι�����Ʋ�ƷʱӦ������Ҫ����ҵ����</p>
			<p>�������з��ճ�������������</p>
			</div>
			</div>

		<div class='ab-body'>
		
		</div>
		<div class='ab-controller-bar'>
			<button type="button" class="my-button" id='subBtn'><span>ȷ��</span></button>
		</div>
	</div>

</div>

<#include "openLayer.ftl" >

<script>

	let map = {};
    //true�״� false���״�
    let firstFlag;

    let createAndMonitor = function (topicData,length,flag) {
    	console.log("flag:"+flag);
    	if("true" == flag){
            firstFlag = true;
        }else{
            firstFlag = false;
        }
        console.log(topicData);
        createTopicHandle(topicData);
        changeFun(length);
    }

    let createTopicHandle = function (topicData) {
        let topicArray = eval(topicData);
        console.log(topicArray);
        let body_div = $(".ab-body");
        for (let i = 0; i < topicArray.length; i++) {
            let topicGroup = topicArray[i];
            createTopicFrame(topicGroup,body_div);
        }
    }

    //�������� 0Ϊ��� 1Ϊ��Ŀ �����
    let createTopicFrame = function (array,body_div) {
    	console.log(array);
        let topicNum = array[0];
        let topic = array[1];
        let topic_answer_div = $("<div class='topic_answer_" + topicNum + "' data-num='topic" + topicNum + "'></div>");
        let topic_title_div = createTopicTitle(topic,topicNum);
        for (let i = 2; i < array.length; i++) {
            let topicInfo = array[i];
            let answerNum = i-1;
            let answer = createTopicAnswer(topicInfo, topicNum,answerNum);
            topic_answer_div.append(answer);
        }
        body_div.append(topic_title_div);
        body_div.append(topic_answer_div);
		return body_div;
    }

    let createTopicTitle = function(topic,topicNum){
        let topic_title_div = $("<div class='topic_title_ "+ topicNum + "'></div>");
        let topic_p = $("<p></p>");
        let topic_h = $("<h3 class='header-dividing'></h3>");
        topic_h.text(topic);
        topic_p.append(topic_h);
        topic_title_div.append(topic_p);
        return topic_title_div;
    }

    let createTopicAnswer = function (topicInfo, topicNum,answerNum) {
        let div = $("<div class='radio'></div>");
        let label = $("<label></label>");
        let input = $("<input class='radio_options' type='radio' name='radio_options_" + topicNum +"'>");
        let span = $("<span></span>");
        input.attr("value", answerNum);
        span.text(topicInfo);
        label.append(input);
        label.append(span);
        div.append(label);
        return div;
    }
    
    //���涼��Ϊ�˹��������
    
    //�ռ�����
    $(".ab-content").on("change", ".radio_options", function () {
        let result = $(this).val();
        console.log(result);
        let label = $(this).parent();
        let div_radio = $(label).parent();
        let key = $($(div_radio).parent()).attr("data-num");
        map[key] = result;
    })

    //У���Ƿ���ɴ���
    let check = function(obj,length){
        let arr = Object.keys(obj);
        let arrayLength = arr.length;
        if (arrayLength == length){
            return true;
        }
        return false;
    }

    //�ύ��ť�����ķ���
    let changeFun = function(length){
    	let canvasArea = $(".ab-body");
        $('#subBtn').click(function () {
            let result = check(map,length);
            //��pageSubmit��ֵ
            str = JSON.stringify(map);
            if (result){
                domain.userConfirm("","ȷ���������ʾ������޸ģ�ȷ���ύ��", "ȷ��", "ȡ��", true ,function(){
                    if(firstFlag){
                        //�״δ���
                        console.log("�״η�������");
                        lockRadio();
                        let frameCoordinate = {
							"transparent":"0",
							"xSite":"477",
							"ySite":"435",
							"height":"303",
							"width":"570",
							"title":"",
							"desc":"",
						};
		                configJSign("", canvasArea, frameCoordinate, str);//����ǩ�������������ͨ��submit�����������NULL
                    }else{
                        //���״δ���
                        console.log("���״η�������");
                        domain.callMethodPromise('pageSubmit',str);
                    }
                },function(){
                    return;
                });
            }else {
               	domain.pushType("", "�����������������", "info");
				return;
            }
        })    
    }

    //��������
    let lockRadio = function(){
        $("input.radio_options").attr("disabled","disabled");
    }

</script>