<%@ page import="com.naoki.crm.settings.domain.DicValue" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.naoki.crm.workbench.domain.Tran" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";
    // 获取 阶段 中的所有dicvalue对象
    List<DicValue> dvList = (List<DicValue>) application.getAttribute("stageList");
    // 获取 可能性值 和 阶段的映射关系
    Map<String, String> pMap = (Map<String, String>) application.getAttribute("pMap");
    // 正常阶段 与 丢失阶段的分界点下标
    int point = 0;
    for (int i = 0; i < dvList.size(); i++) {
        DicValue dv = dvList.get(i);
        String stage = dv.getValue();
        String possibility = pMap.get(stage);
        if("0".equals(possibility)){
            point=i;
            break;
        }
    }
%>
<html>
<head>
    <base href="<%=basePath%>"/>
    <meta charset="UTF-8">

    <link href="jquery/bootstrap_3.3.0/css/bootstrap.min.css" type="text/css" rel="stylesheet"/>

    <style type="text/css">
        .mystage {
            font-size: 20px;
            vertical-align: middle;
            cursor: pointer;
        }

        .closingDate {
            font-size: 15px;
            cursor: pointer;
            vertical-align: middle;
        }
    </style>

    <script type="text/javascript" src="jquery/jquery-1.11.1-min.js"></script>
    <script type="text/javascript" src="jquery/bootstrap_3.3.0/js/bootstrap.min.js"></script>

    <script type="text/javascript">

        //默认情况下取消和保存按钮是隐藏的
        var cancelAndSaveBtnDefault = true;

        $(function () {
            $("#remark").focus(function () {
                if (cancelAndSaveBtnDefault) {
                    //设置remarkDiv的高度为130px
                    $("#remarkDiv").css("height", "130px");
                    //显示
                    $("#cancelAndSaveBtn").show("2000");
                    cancelAndSaveBtnDefault = false;
                }
            });

            $("#cancelBtn").click(function () {
                //显示
                $("#cancelAndSaveBtn").hide();
                //设置remarkDiv的高度为130px
                $("#remarkDiv").css("height", "90px");
                cancelAndSaveBtnDefault = true;
            });

            $(".remarkDiv").mouseover(function () {
                $(this).children("div").children("div").show();
            });

            $(".remarkDiv").mouseout(function () {
                $(this).children("div").children("div").hide();
            });

            $(".myHref").mouseover(function () {
                $(this).children("span").css("color", "red");
            });

            $(".myHref").mouseout(function () {
                $(this).children("span").css("color", "#E6E6E6");
            });


            //阶段提示框
            $(".mystage").popover({
                trigger: 'manual',
                placement: 'bottom',
                html: 'true',
                animation: false
            }).on("mouseenter", function () {
                var _this = this;
                $(this).popover("show");
                $(this).siblings(".popover").on("mouseleave", function () {
                    $(_this).popover('hide');
                });
            }).on("mouseleave", function () {
                var _this = this;
                setTimeout(function () {
                    if (!$(".popover:hover").length) {
                        $(_this).popover("hide")
                    }
                }, 100);
            });
            showHistoryList();
        });

        //显示历史列表
        function showHistoryList() {
            $.ajax({
                url: "workbench/transaction/getHistoryByTranId.do",
                data: {tranId: "${t.id}"},
                dataType: "json",
                type: "get",
                success: function (data) {
                    let html = "";
                    $.each(data, function (i, n) {
                        html += '<tr>';
                        html += '<td>' + n.stage + '</td>';
                        html += '<td>' + n.money + '</td>';
                        html += '<td>' + n.possibility + '</td>';
                        html += '<td>' + n.expectedDate + '</td>';
                        html += '<td>' + n.creatTime + '</td>';
                        html += '<td>' + n.createBy + '</td>';
                        html += '</tr>';
                    })
                    $("#tranHistoryBody").html(html);
                }
            })
        }

        // 改变交易阶段
        // stage : 遍历阶段赋予的 stage的value属性值
        // i : 当前阶段下标
        function changeStage(stage, i) {
            $.ajax({
                url: "workbench/transaction/changeStage.do",
                data: {
                    id: "${t.id}",//被修改的交易id
                    stage: stage, //变更后的阶段
                    money: "${t.money}", //写入交易历史中的money
                    expectedDate: "${t.expectedDate}"//写入交易历史中的expectedDate
                },
                dataType: "json",
                type: "post",
                success: function (data) {
                    // {success:true/false,t:{交易对象}}
                    if (data.success) {
                        // 更新数据 修改人 修改时间 阶段 可能性
                        $("#stage").html(data.t.stage);
                        $("#possibility").html(data.t.possibility);
                        $("#editBy").html(data.t.editBy);
                        $("#editTime").html(data.t.editTime);
                        // 刷新图标
                        changeIcon(stage,i);
                        // 刷新历史列表
                        showHistoryList();
                    } else {
                        alert("改变阶段失败");
                    }
                }
            })
        }
        function changeIcon(stage,x){
            let currentStage=stage;
            // 获取当前阶段可能性  注意点,不能从request域中获取,如果修改过stage,页面显示的是覆盖后的值,request域中的值还是未修改时的
            let currentPossibility=$("#possibility").html();
            // 当前阶段下标
            let index=x;
            // 分界点下标  java数据转换为js数据,只能是基本类型,必须写在双引号内
            let point="<%=point%>";
            //如果当前阶段的可能性为0
            if(currentPossibility=="0"){
                //遍历前7个
                for(let i=0;i<point;i++){
                    // 黑圈
                    // 移除原有样式
                    $("#"+i).removeClass();
                    // 添加样式
                    $("#"+i).addClass("glyphicon glyphicon-record mystage");
                    // css样式颜色属性
                    $("#"+i).css("color","#000000");
                }
                //遍历后2个
                for(let i=point;i<<%=dvList.size()%>;i++){
                    // 是当前阶段
                    if(index==i){
                        //红叉
                        $("#"+i).removeClass();
                        $("#"+i).addClass("glyphicon glyphicon-remove mystage");
                        $("#"+i).css("color","#FF0000");
                    }else{
                        //黑叉
                        $("#"+i).removeClass();
                        $("#"+i).addClass("glyphicon glyphicon-remove mystage");
                        $("#"+i).css("color","#000000");
                    }
                }
            }else{//如果当前阶段的可能性不为0
                for(let i=0;i<point;i++){
                    if(index==i){
                        //绿色标记
                        $("#"+i).removeClass();
                        $("#"+i).addClass("glyphicon glyphicon-map-marker mystage");
                        $("#"+i).css("color","#90F790");
                    }else if(index>i){
                        //绿圈
                        $("#"+i).removeClass();
                        $("#"+i).addClass("glyphicon glyphicon-record mystage");
                        $("#"+i).css("color","#90F790");
                    }else{
                        //黑圈
                        $("#"+i).removeClass();
                        $("#"+i).addClass("glyphicon glyphicon-record mystage");
                        $("#"+i).css("color","#000000");
                    }
                }
                for(let i=point;i<<%=dvList.size()%>;i++){
                    //黑叉
                    $("#"+i).removeClass();
                    $("#"+i).addClass("glyphicon glyphicon-remove mystage");
                    $("#"+i).css("color","#000000");
                }
            }
        }
    </script>

</head>
<body>

    <!-- 返回按钮 -->
    <div style="position: relative; top: 35px; left: 10px;">
        <a href="javascript:void(0);" onclick="window.history.back();"><span class="glyphicon glyphicon-arrow-left"
                                                                             style="font-size: 20px; color: #DDDDDD"></span></a>
    </div>

    <!-- 大标题 -->
    <div style="position: relative; left: 40px; top: -30px;">
        <div class="page-header">
            <h3>${t.customerId}-${t.name} <small>￥${t.money}</small></h3>
        </div>
        <div style="position: relative; height: 50px; width: 250px;  top: -72px; left: 700px;">
            <button type="button" class="btn btn-default" onclick="window.location.href='edit.html';"><span
                    class="glyphicon glyphicon-edit"></span> 编辑
            </button>
            <button type="button" class="btn btn-danger"><span class="glyphicon glyphicon-minus"></span> 删除</button>
        </div>
    </div>

    <!-- 阶段状态 -->
    <div style="position: relative; left: 40px; top: -50px;">
        阶段&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
        <%
            //当前交易对象
            Tran t = (Tran) request.getAttribute("t");
            //当前交易的阶段value
            String currentStage = t.getStage();
            //获取 当前阶段可能性值
            String currentPossibility = pMap.get(currentStage);
            //如果当前阶段可能性为 0,说明当前阶段 是末尾两个阶段之一,另7个阶段图标确定,均是黑圈
            if ("0".equals(currentPossibility)) {
                // 遍历 数据字典_阶段
                for (int i = 0; i < dvList.size(); i++) {
                    DicValue dv = dvList.get(i);
                    String listStage = dv.getValue();
                    String listPossibility = pMap.get(listStage);
                    // 如果遍历的阶段为0,则判断当前阶段显示图标,及另一个图标
                    if ("0".equals(listPossibility)) {
                        //当前阶段,红叉
                        if (listStage.equals(currentStage)) {
                            // 红叉
        %>
        <span id="<%=i%>" onclick="changeStage('<%=listStage%>','<%=i%>')" class="glyphicon glyphicon-remove mystage"
              data-toggle="popover" data-placement="bottom"
              data-content="<%=dv.getText()%>" style="color: #FF0000;"></span>
        -----------
        <%
        } else { //非当前阶段,黑叉
            //黑叉
        %>
        <span id="<%=i%>" onclick="changeStage('<%=listStage%>','<%=i%>')" class="glyphicon glyphicon-remove mystage"
              data-toggle="popover" data-placement="bottom"
              data-content="<%=dv.getText()%>" style="color: #000000;"></span>
        -----------
        <%

            }
        } else { //遍历的阶段非0,并不是当前阶段,均为黑圈
            //黑圈
        %>
        <span id="<%=i%>" onclick="changeStage('<%=listStage%>','<%=i%>')" class="glyphicon glyphicon-record mystage"
              data-toggle="popover" data-placement="bottom"
              data-content="<%=dv.getText()%>" style="color: #000000;"></span>
        -----------
        <%
                }
            }
        } else { //当前阶段可能性不为0,说明是 前7个阶段之一
            int index = 0;
            for (int i = 0; i < dvList.size(); i++) {
                DicValue dv = dvList.get(i);
                String listStage = dv.getValue();
                // 如果遍历到 当前阶段
                if (listStage.equals(currentStage)) {
                    // 保存 当前阶段 数组下标
                    index = i;
                    break;
                }
            }
            for (int i = 0; i < dvList.size(); i++) {
                DicValue dv = dvList.get(i);
                String listStage = dv.getValue();
                String listPossibility = pMap.get(listStage);
                // 如果 遍历到可能性为0的阶段,当前阶段不在其中,图标确定为 黑叉
                if ("0".equals(listPossibility)) {
                    // 黑叉
        %>
        <span id="<%=i%>" onclick="changeStage('<%=listStage%>','<%=i%>')" class="glyphicon glyphicon-remove mystage"
              data-toggle="popover" data-placement="bottom"
              data-content="<%=dv.getText()%>" style="color: #000000;"></span>
        -----------
        <%
        } else {//如果 遍历到可能性非0的7个阶段,当前阶段在其中,需要继续判断
            if (i == index) { // 如果遍历到的阶段 是 当前阶段
                // 绿色标记
        %>
        <span id="<%=i%>" onclick="changeStage('<%=listStage%>','<%=i%>')"
              class="glyphicon glyphicon-map-marker mystage" data-toggle="popover" data-placement="bottom"
              data-content="<%=dv.getText()%>" style="color: #90F790;"></span>
        -----------
        <%
        } else if (i < index) {// 如果遍历到的阶段 小于 当前阶段
            // 绿圈
        %>
        <span id="<%=i%>" onclick="changeStage('<%=listStage%>','<%=i%>')" class="glyphicon glyphicon-ok-circle mystage"
              data-toggle="popover" data-placement="bottom"
              data-content="<%=dv.getText()%>" style="color: #90F790;"></span>
        -----------
        <%
        } else {
            // 黑圈
        %>
        <span id="<%=i%>" onclick="changeStage('<%=listStage%>','<%=i%>')" class="glyphicon glyphicon-record mystage"
              data-toggle="popover" data-placement="bottom"
              data-content="<%=dv.getText()%>" style="color: #000000;"></span>
        -----------
        <%
                        }
                    }
                }
            }
        %>
        <!--<span class="glyphicon glyphicon-ok-circle mystage" data-toggle="popover" data-placement="bottom"
              data-content="资质审查" style="color: #90F790;"></span>
        -----------
        <span class="glyphicon glyphicon-ok-circle mystage" data-toggle="popover" data-placement="bottom"
              data-content="需求分析" style="color: #90F790;"></span>
        -----------
        <span class="glyphicon glyphicon-ok-circle mystage" data-toggle="popover" data-placement="bottom"
              data-content="价值建议" style="color: #90F790;"></span>
        -----------
        <span class="glyphicon glyphicon-ok-circle mystage" data-toggle="popover" data-placement="bottom"
              data-content="确定决策者" style="color: #90F790;"></span>
        -----------
        <span class="glyphicon glyphicon-map-marker mystage" data-toggle="popover" data-placement="bottom"
              data-content="提案/报价" style="color: #90F790;"></span>
        -----------
        <span class="glyphicon glyphicon-record mystage" data-toggle="popover" data-placement="bottom"
              data-content="谈判/复审"></span>
        -----------
        <span class="glyphicon glyphicon-record mystage" data-toggle="popover" data-placement="bottom"
              data-content="成交"></span>
        -----------
        <span class="glyphicon glyphicon-record mystage" data-toggle="popover" data-placement="bottom"
              data-content="丢失的线索"></span>
        -----------
        <span class="glyphicon glyphicon-record mystage" data-toggle="popover" data-placement="bottom"
              data-content="因竞争丢失关闭"></span>
        -----------  -->
        <span class="closingDate">${t.expectedDate}</span>
    </div>

    <!-- 详细信息 -->
    <div style="position: relative; top: 0px;">
        <div style="position: relative; left: 40px; height: 30px;">
            <div style="width: 300px; color: gray;">所有者</div>
            <div style="width: 300px;position: relative; left: 200px; top: -20px;"><b>${t.owner}</b></div>
            <div style="width: 300px;position: relative; left: 450px; top: -40px; color: gray;">金额</div>
            <div style="width: 300px;position: relative; left: 650px; top: -60px;"><b>${t.money}</b></div>
            <div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px;"></div>
            <div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px; left: 450px;"></div>
        </div>
        <div style="position: relative; left: 40px; height: 30px; top: 10px;">
            <div style="width: 300px; color: gray;">名称</div>
            <div style="width: 300px;position: relative; left: 200px; top: -20px;"><b>${t.customerId}-${t.name}</b>
            </div>
            <div style="width: 300px;position: relative; left: 450px; top: -40px; color: gray;">预计成交日期</div>
            <div style="width: 300px;position: relative; left: 650px; top: -60px;"><b>${t.expectedDate}</b></div>
            <div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px;"></div>
            <div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px; left: 450px;"></div>
        </div>
        <div style="position: relative; left: 40px; height: 30px; top: 20px;">
            <div style="width: 300px; color: gray;">客户名称</div>
            <div style="width: 300px;position: relative; left: 200px; top: -20px;"><b>${t.customerId}</b></div>
            <div style="width: 300px;position: relative; left: 450px; top: -40px; color: gray;">阶段</div>
            <div style="width: 300px;position: relative; left: 650px; top: -60px;"><b id="stage">${t.stage}</b></div>
            <div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px;"></div>
            <div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px; left: 450px;"></div>
        </div>
        <div style="position: relative; left: 40px; height: 30px; top: 30px;">
            <div style="width: 300px; color: gray;">类型</div>
            <div style="width: 300px;position: relative; left: 200px; top: -20px;"><b>${t.type}</b></div>
            <div style="width: 300px;position: relative; left: 450px; top: -40px; color: gray;">可能性</div>
            <div style="width: 300px;position: relative; left: 650px; top: -60px;"><b
                    id="possibility">${t.possibility}</b></div>
            <div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px;"></div>
            <div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px; left: 450px;"></div>
        </div>
        <div style="position: relative; left: 40px; height: 30px; top: 40px;">
            <div style="width: 300px; color: gray;">来源</div>
            <div style="width: 300px;position: relative; left: 200px; top: -20px;"><b>${t.source}</b></div>
            <div style="width: 300px;position: relative; left: 450px; top: -40px; color: gray;">市场活动源</div>
            <div style="width: 300px;position: relative; left: 650px; top: -60px;"><b>${t.activityId}</b></div>
            <div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px;"></div>
            <div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px; left: 450px;"></div>
        </div>
        <div style="position: relative; left: 40px; height: 30px; top: 50px;">
            <div style="width: 300px; color: gray;">联系人名称</div>
            <div style="width: 500px;position: relative; left: 200px; top: -20px;"><b>${t.contactsId}</b></div>
            <div style="height: 1px; width: 550px; background: #D5D5D5; position: relative; top: -20px;"></div>
        </div>
        <div style="position: relative; left: 40px; height: 30px; top: 60px;">
            <div style="width: 300px; color: gray;">创建者</div>
            <div style="width: 500px;position: relative; left: 200px; top: -20px;">
                <b>${t.createBy}&nbsp;&nbsp;</b><small style="font-size: 10px; color: gray;">${t.createTime}</small>
            </div>
            <div style="height: 1px; width: 550px; background: #D5D5D5; position: relative; top: -20px;"></div>
        </div>
        <div style="position: relative; left: 40px; height: 30px; top: 70px;">
            <div style="width: 300px; color: gray;">修改者</div>
            <div style="width: 500px;position: relative; left: 200px; top: -20px;"><b id="editBy">${t.editBy}&nbsp;&nbsp;</b><small
                    style="font-size: 10px; color: gray;" id="editTime">${t.editTime}</small></div>
            <div style="height: 1px; width: 550px; background: #D5D5D5; position: relative; top: -20px;"></div>
        </div>
        <div style="position: relative; left: 40px; height: 30px; top: 80px;">
            <div style="width: 300px; color: gray;">描述</div>
            <div style="width: 630px;position: relative; left: 200px; top: -20px;">
                <b>
                    ${t.description}
                </b>
            </div>
            <div style="height: 1px; width: 850px; background: #D5D5D5; position: relative; top: -20px;"></div>
        </div>
        <div style="position: relative; left: 40px; height: 30px; top: 90px;">
            <div style="width: 300px; color: gray;">联系纪要</div>
            <div style="width: 630px;position: relative; left: 200px; top: -20px;">
                <b>
                    &nbsp;${t.contactSummary}
                </b>
            </div>
            <div style="height: 1px; width: 850px; background: #D5D5D5; position: relative; top: -20px;"></div>
        </div>
        <div style="position: relative; left: 40px; height: 30px; top: 100px;">
            <div style="width: 300px; color: gray;">下次联系时间</div>
            <div style="width: 500px;position: relative; left: 200px; top: -20px;"><b>${t.nextContactTime}&nbsp;</b>
            </div>
            <div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -20px;"></div>
        </div>
    </div>

    <!-- 备注 -->
    <div style="position: relative; top: 100px; left: 40px;">
        <div class="page-header">
            <h4>备注</h4>
        </div>

        <!-- 备注1 -->
        <div class="remarkDiv" style="height: 60px;">
            <img title="zhangsan" src="image/user-thumbnail.png" style="width: 30px; height:30px;">
            <div style="position: relative; top: -40px; left: 40px;">
                <h5>哎呦！</h5>
                <font color="gray">交易</font> <font color="gray">-</font> <b>动力节点-交易01</b> <small style="color: gray;">
                2017-01-22 10:10:10 由zhangsan</small>
                <div style="position: relative; left: 500px; top: -30px; height: 30px; width: 100px; display: none;">
                    <a class="myHref" href="javascript:void(0);"><span class="glyphicon glyphicon-edit"
                                                                       style="font-size: 20px; color: #E6E6E6;"></span></a>
                    &nbsp;&nbsp;&nbsp;&nbsp;
                    <a class="myHref" href="javascript:void(0);"><span class="glyphicon glyphicon-remove"
                                                                       style="font-size: 20px; color: #E6E6E6;"></span></a>
                </div>
            </div>
        </div>

        <!-- 备注2 -->
        <div class="remarkDiv" style="height: 60px;">
            <img title="zhangsan" src="image/user-thumbnail.png" style="width: 30px; height:30px;">
            <div style="position: relative; top: -40px; left: 40px;">
                <h5>呵呵！</h5>
                <font color="gray">交易</font> <font color="gray">-</font> <b>动力节点-交易01</b> <small style="color: gray;">
                2017-01-22 10:20:10 由zhangsan</small>
                <div style="position: relative; left: 500px; top: -30px; height: 30px; width: 100px; display: none;">
                    <a class="myHref" href="javascript:void(0);"><span class="glyphicon glyphicon-edit"
                                                                       style="font-size: 20px; color: #E6E6E6;"></span></a>
                    &nbsp;&nbsp;&nbsp;&nbsp;
                    <a class="myHref" href="javascript:void(0);"><span class="glyphicon glyphicon-remove"
                                                                       style="font-size: 20px; color: #E6E6E6;"></span></a>
                </div>
            </div>
        </div>

        <div id="remarkDiv" style="background-color: #E6E6E6; width: 870px; height: 90px;">
            <form role="form" style="position: relative;top: 10px; left: 10px;">
                <textarea id="remark" class="form-control" style="width: 850px; resize : none;" rows="2"
                          placeholder="添加备注..."></textarea>
                <p id="cancelAndSaveBtn" style="position: relative;left: 737px; top: 10px; display: none;">
                    <button id="cancelBtn" type="button" class="btn btn-default">取消</button>
                    <button type="button" class="btn btn-primary">保存</button>
                </p>
            </form>
        </div>
    </div>

    <!-- 阶段历史 -->
    <div>
        <div style="position: relative; top: 100px; left: 40px;">
            <div class="page-header">
                <h4>阶段历史</h4>
            </div>
            <div style="position: relative;top: 0px;">
                <table id="activityTable" class="table table-hover" style="width: 900px;">
                    <thead>
                    <tr style="color: #B3B3B3;">
                        <td>阶段</td>
                        <td>金额</td>
                        <td>可能性</td>
                        <td>预计成交日期</td>
                        <td>创建时间</td>
                        <td>创建人</td>
                    </tr>
                    </thead>
                    <tbody id="tranHistoryBody">
                    <%--                    <tr>--%>
                    <%--                        <td>资质审查</td>--%>
                    <%--                        <td>5,000</td>--%>
                    <%--                        <td>10</td>--%>
                    <%--                        <td>2017-02-07</td>--%>
                    <%--                        <td>2016-10-10 10:10:10</td>--%>
                    <%--                        <td>zhangsan</td>--%>
                    <%--                    </tr>--%>
                    <%--						<tr>--%>
                    <%--							<td>需求分析</td>--%>
                    <%--							<td>5,000</td>--%>
                    <%--							<td>20</td>--%>
                    <%--							<td>2017-02-07</td>--%>
                    <%--							<td>2016-10-20 10:10:10</td>--%>
                    <%--							<td>zhangsan</td>--%>
                    <%--						</tr>--%>
                    <%--						<tr>--%>
                    <%--							<td>谈判/复审</td>--%>
                    <%--							<td>5,000</td>--%>
                    <%--							<td>90</td>--%>
                    <%--							<td>2017-02-07</td>--%>
                    <%--							<td>2017-02-09 10:10:10</td>--%>
                    <%--							<td>zhangsan</td>--%>
                    <%--						</tr>--%>
                    </tbody>
                </table>
            </div>

        </div>
    </div>

    <div style="height: 200px;"></div>

</body>
</html>