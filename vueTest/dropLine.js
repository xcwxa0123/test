
      !function () {
        //获取dom对象对应属性名property的值，若没有该property则返回传入的t
        function getSetProperty(obj, property, defaultValue) {
  
          return obj.getAttribute(property) || defaultValue
  
        }
        //根据标签的名字tagName返回该DOM对象
        function getElementsByTagName(tagName) {
  
          return document.getElementsByTagName(tagName)
  
        }
  
        // 返回一个带有
        /*
          * l script对象长度
          * z zIndex值
          * o 透明度值
          * c color值
          * n count值
        */
        // 的对象
        function getScriptObj() {
  
          var scriptArr = getElementsByTagName("script"), scriptLength = scriptArr.length, scriptObj = scriptArr[scriptLength - 1];
  
          return {
  
            l: scriptLength, z: getSetProperty(scriptObj, "zIndex", 9999999), o: getSetProperty(scriptObj, "opacity", .8), c: getSetProperty(scriptObj, "color", "0,0,0"), n: getSetProperty(scriptObj, "count", 99)
  
          }
  
        }
        //初始化a和c为创建的canvas的长宽
        function o() {
  
          a = m.width = window.innerWidth || document.documentElement.clientWidth || document.body.clientWidth,
  
            c = m.height = window.innerHeight || document.documentElement.clientHeight || document.body.clientHeight
  
        }
  
        function i() {
  
          r.clearRect(0, 0, a, c);
  
          var n, e, t, o, m, l;
          //遍历s数组中的每一个元素，其中i代表当前元素，x代表当前元素下标
          //s数组为一组含有随机数x，随机数y，随机x速度xa，随机y速度ya和max的数组
          s.forEach(function (i, x) {
  
            for (i.x += i.xa, i.y += i.ya, i.xa *= i.x > a || i.x < 0 ? -1 : 1, i.ya *= i.y > c || i.y < 0 ? -1 : 1, r.fillRect(i.x - .5, i.y - .5, 1, 1), e = x + 1; e < u.length; e++)n = u[e],
              //i.x为当前元素的x坐标，n.x为下一元素的x坐标，则o为当前元素和下一元素的x坐标差，m为y坐标差
              null !== n.x && null !== n.y && (o = i.x - n.x, m = i.y - n.y,
                //l为坐标差的平方和
                l = o * o + m * m, 
                
                l < n.max && (n === y && l >= n.max / 2 && (i.x -= .03 * o, i.y -= .03 * m),
  
                  t = (n.max - l) / n.max, r.beginPath(), r.lineWidth = t / 2, r.strokeStyle = "rgba(" + d.c + "," + (t + .2) + ")", r.moveTo(i.x, i.y), r.lineTo(n.x, n.y), r.stroke()))
  
          }),
  
            x(i)
  
        }
        //创建a,c,u,m四个变量，其中初始化m为画布
        var a, c, u, m = document.createElement("canvas"),
          //d 本界面动画参数：scrpit对象长度 count color 透明度 zindex
          //l = c_n1
          //r = 用canvas m 创建画笔r
          d = getScriptObj(), l = "c_n" + d.l, r = m.getContext("2d"),
          //指定x为动画绘制函数
          x = window.requestAnimationFrame || window.webkitRequestAnimationFrame || window.mozRequestAnimationFrame || window.oRequestAnimationFrame || window.msRequestAnimationFrame ||
  
            function (n) {
  
              window.setTimeout(n, 1e3 / 45)
  
            },
          //指定w为随机数，
          w = Math.random, 
          y = { x: null, y: null, max: 6e4 }; 
          //m.id = c_n1
          m.id = l, 
          m.style.cssText = "position:fixed;top:0;left:0;z-index:" + d.z + ";opacity:" + d.o, 
          //动画画布画在body下
          getElementsByTagName("html")[0].appendChild(m), 
          //a为m.width,c为m.height
          o(), 
          window.οnresize = o,
  
            window.onmousemove = function (n) {
  
              n = n || window.event, y.x = n.clientX, y.y = n.clientY
  
            },
  
            window.onmouseout = function () {
  
              y.x = null, y.y = null
  
            };
  
        for (var s = [], f = 0; d.n > f; f++) {
  
          var h = w() * a, g = w() * c, v = 2 * w() - 1, p = 2 * w() - 1; s.push({ x: h, y: g, xa: v, ya: p, max: 6e3 })
  
        }
  
        u = s.concat([y]),
        console.log([y]);
          setTimeout(function () { i() }, 100)
  
      }();
      