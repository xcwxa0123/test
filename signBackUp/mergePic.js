function mergePic() {

}

/**
 * 
 * 获取图片信息内容
 * @param {图片路径} img 
 * @param {宽度} width 
 * @param {高度} height
 * 返回一个对象 含有图片base64,naturalWidth,naturalHeight 
 */
mergePic.prototype.getImgInfo = function (img, width, height) {
    function getBase64Image(img, width, height) {
        let canvas = $("<canvas></canvas>")[0];
        canvas.width = width ? width : img.width;
        canvas.height = height ? height : img.height;
        let ctx = canvas.getContext("2d");
        ctx.drawImage(img, 0, 0, canvas.width, canvas.height);
        let dataURL = canvas.toDataURL();
        let result = {};
        result.baseCode = dataURL;
        result.naturalWidth = img.naturalWidth;
        result.naturalHeight = img.naturalHeight;
        return result;
    }
    let image = new Image();
    image.src = img;
    return new Promise((resolve, reject) => {
        if (img) {
            image.onload = function () {
                try {
                    resolve(getBase64Image(image, width, height));
                } catch (err) {
                    reject(err);
                }
            }
            image.onerror = function () {
                reject("The image [" + img + "] could not be loaded");
            }
        } else {
            throw new Error("The image [" + img + "] could not be loaded");
        }
    });
}

/**
 * 合并图片
 * @param {图片1baseCode} baseImg1 
 * @param {图片2baseCode} baseImg2 
 * 返回一个Promise
 */

mergePic.prototype.merge = function (baseImg1, baseImg2, param1, param2, canvas_width, canvas_height) {
    let image1 = new Image();
    image1.src = baseImg1;
    let image2 = new Image();
    image2.src = baseImg2;
    let toBase64 = function (image1, image2, param1, param2, canvas_width, canvas_height) {
        let canvas = $("<canvas></canvas>")[0];
        let img1_height = param1.height ? param1.height : image1.height;
        let img1_width = param1.width ? param1.width : image1.width;
        let img2_height = param2.height ? param2.height : image2.height;
        let img2_width = param2.width ? param2.width : image2.width;
        canvas.width = canvas_width ? canvas_width : ((img1_width > img2_width) ? img1_width : img2_width);
        canvas.height = canvas_height ? canvas_height : (img1_height + img2_height);
        ctx = canvas.getContext('2d');
        ctx.fillStyle = "#ffffff";
        ctx.fillRect(0, 0, canvas.width, canvas.height);
        let img1_x = param1.x ? param1.x : 0;
        let img1_y = param1.y ? param1.y : 0;
        let img2_x = param2.x ? param2.x : 0;
        let img2_y = param2.y ? param2.y : img1_height;
        console.log("img1_x:" + img1_x + "\r\nimg1_y:" + img1_y + "\r\nimg1_width:" + img1_width + "\r\nimg1_height:" + img1_height);
        console.log("img2_x:" + img2_x + "\r\nimg2_y:" + img2_y + "\r\nimg2_width:" + img2_width + "\r\nimg2_height:" + img2_height);
        console.log("canvas.width:" + canvas.width + "\r\ncanvas.height:" + canvas.height);
        ctx.drawImage(image1, img1_x, img1_y, img1_width, img1_height);
        ctx.drawImage(image2, img2_x, img2_y, img2_width, img2_height);
        return canvas.toDataURL();
    }
    return new Promise((resolve, reject) => {
        if (baseImg1 && baseImg2) {
            let promiseAll = [];
            let picArray = new Array();
            picArray.push(image1);
            picArray.push(image2);
            for (let i = 0; i < picArray.length; i++) {
                promiseAll[i] = new Promise((resolve, reject) => {
                    picArray[i].onload = function () {
                        resolve(picArray[i]);
                    }
                    picArray[i].onerror = function () {
                        reject("The image [" + picArray[i].src + "] could not be loaded");
                    }
                })
            }
            Promise.all(promiseAll).then((picArray) => {
                try {
                    resolve(toBase64(picArray[0], picArray[1], param1, param2, canvas_width, canvas_height));
                } catch (err) {
                    reject(err);
                }
            }).catch((err) => {
                reject(err);
            });
        } else {
            reject("The image could not be loaded");
        }
    })

}

/**
 * 
 * @param {添加文字的图片baseCode} baseImg 
 * @param {包含参数对象的数组} param 
 */
mergePic.prototype.additional = function (baseImg, param) {
    let image = new Image();
    image.src = baseImg;
    let add = function (img, param) {
        let canvas = $("<canvas></canvas>")[0];
        canvas.width = img.width;
        canvas.height = img.height;
        ctx = canvas.getContext('2d');
        ctx.drawImage(img, 0, 0);
        paramHandler(ctx, param);
        return canvas.toDataURL("image/jpeg");
    }
    let paramHandler = function (ctx, param) {
        if (undefined == param || null == param || param.length <= 0) {
            throw new Error("The param Array is wrong");
        } else {
            for (let i = 0; i < param.length; i++) {
                let p = param[i];
                ctx.font = p.font;
                console.log(" content:" + p.content + ",x:" + p.x + ",y:" + p.y)
                ctx.fillText(p.content, p.x, p.y);
            }
        }
    }
    return new Promise((resolve, reject) => {
        if (baseImg) {
            image.onload = function () {
                try {
                    resolve(add(image, param));
                }catch(err){
                    reject(err);
                }
            }
            image.onerror = function () {
                reject("The image could not be loaded");
            }
        } else {
            reject("The image could not be loaded");
        }
    });

}
