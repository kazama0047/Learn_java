<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";
%>
<html>
<head>
    <base href="<%=basePath%>"/>
    <meta charset="UTF-8">

    <link href="jquery/bootstrap_3.3.0/css/bootstrap.min.css" type="text/css" rel="stylesheet"/>
    <link href="jquery/bootstrap-datetimepicker-master/css/bootstrap-datetimepicker.min.css" type="text/css" rel="stylesheet"/>
    <link href="jquery/bs_pagination/jquery.bs_pagination.min.css" type="text/css" rel="stylesheet"/>

    <script type="text/javascript" src="jquery/jquery-1.11.1-min.js"></script>
    <script type="text/javascript" src="jquery/bootstrap_3.3.0/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/js/bootstrap-datetimepicker.js"></script>
    <script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/locale/bootstrap-datetimepicker.zh-CN.js"></script>
    <script type="text/javascript" src="jquery/bs_pagination/en.js"></script>
    <script type="text/javascript" src="jquery/bs_pagination/jquery.bs_pagination.min.js"></script>

    <script type="text/javascript">

        $(function () {
            // 创建
            $("#addBtn").click(function () {
                $.ajax({
                    url: "workbench/activity/getUserList.do",
                    dataType: "json",
                    type: "get",
                    async: false,
                    success: function (data) {
                        var html = "<option></option>";
                        $.each(data, function (i, n) {
                            html += "<option value='" + n.id + "'>" + n.name + "</option>";
                        })
                        $("#create-owner").html(html);
                    }
                })
                // 由于我的电脑反应慢,浏览器在获取ajax响应生成列表前,已经执行到获取id的代码
                // 导致有id值,但是没有列表项,最终不能显示 --> 解决:ajax异步 改为 同步
                var id = "${user.id}";
                // 下拉列表默认值为当前用户
                $("#create-owner").val(id);
                // 触发 模态窗口显示
                $("#createActivityModal").modal("show");
            })
            // 时间控件
            $(".time").datetimepicker({
                minView: "month",
                language: 'zh-CN',
                format: 'yyyy-mm-dd',
                autoclose: true,
                todayBtn: true,
                pickerPosition: "bottom-left"
            });
            // 创建市场活动表单 提交
            $("#saveBen").click(function () {
                $.ajax({
                    url: "workbench/activity/save.do",
                    data: {
                        "owner": $.trim($("#create-owner").val()),
                        "name": $.trim($("#create-name").val()),
                        "startDate": $.trim($("#create-startDate").val()),
                        "endDate": $.trim($("#create-endDate").val()),
                        "cost": $.trim($("#create-cost").val()),
                        "description": $.trim($("#create-description").val()),
                    },
                    dataType: "json",
                    type: "post",
                    success: function (data) {
                        // 返回添加 成功/失败 {success:true/false}
                        if (data.success) {
                            // 刷新市场活动信息列表
                            // pageList(1, 2);
                            // 跳转至展示列表第一页
                            pageList(1, $("#activityPage").bs_pagination('getOption', 'rowsPerPage'));
                            // 清空模态窗口中的数据
                            $("#activityAddForm")[0].reset();
                            // 关闭模态窗口a
                            $("#createActivityModal").modal("hide");
                        } else {
                            alert(data.msg);
                        }
                    }
                })
            })
            // 搜索按钮
            $("#searchBtn").click(function () {
                // 点击 '搜索' 将表单信息保存至隐藏域
                $("#hidden-name").val($.trim($("#search-name").val()));
                $("#hidden-owner").val($.trim($("#search-owner").val()));
                $("#hidden-startDate").val($.trim($("#search-startDate").val()));
                $("#hidden-endDate").val($.trim($("#search-endDate").val()));
                pageList(1, 2);
            })
            pageList(1, 2);
            // 全选/反选
            $("#qx").click(function () {
                $("input[name=xz]").prop("checked", this.checked);
            })
            $("#activityBody").on("click", $("input[name=xz]"), function () {
                $("#qx").prop("checked", $("input[name=xz]").length == $("input[name=xz]:checked").length);
            })
            // '删除'
            $("#deleteBtn").click(function () {
                // 获取被点击的 标签jq对象
                var $xz = $("input[name=xz]:checked");
                // 没有点击,则弹出提醒
                if ($xz.length == 0) {
                    alert("请选择需要删除的记录");
                } else {
                    // 优化体验,删除前弹出 确认提示框
                    if (confirm("确定删除选中的记录吗?")) {
                        var param = "";
                        // 遍历jq对象
                        for (var i = 0; i < $xz.length; i++) {
                            // 使用下标,获取dom对象,转换为jq对象,获取 value上保存的 活动id值
                            param += "id=" + $($xz[i]).val();
                            // 数据拼接
                            if (i < $xz.length - 1) {
                                param += "&";
                            }
                        }
                        $.ajax({
                            url: "workbench/activity/delete.do",
                            data: param,
                            type: "post",
                            dataType: "json",
                            success: function (data) {
                                // 删除成功,则刷新 展示列表
                                if (data.success) {
                                    pageList(1, $("#activityPage").bs_pagination('getOption', 'rowsPerPage'));
                                } else {
                                    alert("删除失败");
                                }
                            }
                        })
                    }
                }
            })
            // '修改'
            $("#editBtn").click(function () {
                var $xz = $("input[name=xz]:checked");
                if ($xz.length == 0) {
                    alert("请选择需要修改的记录");
                } else if ($xz.length > 1) {
                    alert("只能选择一条记录");
                } else {
                    var id = $xz.val();
                    $.ajax({
                        url: "workbench/activity/getUserListAndActivity.do",
                        data: {"id": id},
                        dataType: "json",
                        type: "post",
                        async: false,
                        success: function (data) {
                            // 需求:{用户列表uList,单条市场活动对象activity}
                            var html = "<option></option>";
                            $.each(data.uList, function (i, n) {
                                html += "<option value='" + n.id + "'>" + n.name + "</option>";
                            })
                            $("#edit-owner").html(html);

                            // 将被修改的活动的id,保存到 隐藏表单中
                            $("#edit-id").val(data.a.id);
                            $("#edit-name").val(data.a.name);
                            $("#edit-owner").val(data.a.owner);
                            $("#edit-startDate").val(data.a.startDate);
                            $("#edit-endDate").val(data.a.endDate);
                            $("#edit-cost").val(data.a.cost);
                            $("#edit-description").val(data.a.description);
                        }
                    })
                    $("#editActivityModal").modal("show");
                }
            })
            // 修改 提交
            $("#updateBtn").click(function () {
                // 复制 创建活动的提交按钮
                $.ajax({
                    url: "workbench/activity/update.do",
                    data: {
                        "id": $.trim($("#edit-id").val()),
                        "owner": $.trim($("#edit-owner").val()),
                        "name": $.trim($("#edit-name").val()),
                        "startDate": $.trim($("#edit-startDate").val()),
                        "endDate": $.trim($("#edit-endDate").val()),
                        "cost": $.trim($("#edit-cost").val()),
                        "description": $.trim($("#edit-description").val()),
                    },
                    dataType: "json",
                    type: "post",
                    success: function (data) {
                        // 返回添加 成功/失败 {success:true/false}
                        if (data.success) {
                            // 刷新市场活动信息列表
                            // pageList(1, 2);
                            pageList($("#activityPage").bs_pagination('getOption', 'currentPage'),
                                $("#activityPage").bs_pagination('getOption', 'rowsPerPage'));
                            // 无需清空模态窗口中的数据,每次修改模态窗口打开,都会被重新赋值
                            // 关闭模态窗口a
                            $("#editActivityModal").modal("hide");
                        } else {
                            alert(data.msg);
                        }
                    }
                })
            })
        });

        // pageNo:页码 pageSize:每页记录数
        function pageList(pageNo, pageSize) {
            // 避免 使用全选删除后,刷新页面,全选还是点击状态
            $("#qx").prop("checked", false);
            $("#search-name").val($.trim($("#hidden-name").val()));
            $("#search-owner").val($.trim($("#hidden-owner").val()));
            $("#search-startDate").val($.trim($("#hidden-startDate").val()));
            $("#search-endDate").val($.trim($("#hidden-endDate").val()));
            $.ajax({
                url: "workbench/activity/pageList.do",
                data: {
                    "pageNo": pageNo,
                    "pageSize": pageSize,
                    "name": $.trim($("#search-name").val()),
                    "owner": $.trim($("#search-owner").val()),
                    "startDate": $.trim($("#search-startDate").val()),
                    "endDate": $.trim($("#search-endDate").val())
                },
                dataType: "json",
                type: "get",
                success: function (data) {
                    var html = "";
                    $.each(data.dataList, function (i, n) {
                        html += '<tr class="active">';
                        html += '<td><input type="checkbox" name="xz" value="' + n.id + '"/></td>';
                        html += '<td><a style="text-decoration: none; cursor: pointer;" onclick="window.location.href=\'workbench/activity/detail.do?id=' + n.id + '\';">';
                        html += n.name + '</a></td>';
                        html += '<td>' + n.owner + '</td>';
                        html += '<td>' + n.startDate + '</td>';
                        html += '<td>' + n.endDate + '</td>';
                        html += '</tr>';
                    })
                    $("#activityBody").html(html);
                    var totalPages = data.total % pageSize == 0 ? data.total / pageSize : parseInt(data.total / pageSize) + 1;
                    $("#activityPage").bs_pagination({
                        currentPage: pageNo, // 页码
                        rowsPerPage: pageSize, // 每页显示的记录条数
                        maxRowsPerPage: 20, // 每页最多显示的记录条数
                        totalPages: totalPages, // 总页数
                        totalRows: data.total, // 总记录条数

                        visiblePageLinks: 3, // 显示几个卡片

                        showGoToPage: true,
                        showRowsPerPage: true,
                        showRowsInfo: true,
                        showRowsDefaultInfo: true,

                        onChangePage: function (event, data) {
                            pageList(data.currentPage, data.rowsPerPage);
                        }
                    });
                }
            })
        }

    </script>
</head>
<body>
    <input type="hidden" id="hidden-name"/>
    <input type="hidden" id="hidden-owner"/>
    <input type="hidden" id="hidden-startDate"/>
    <input type="hidden" id="hidden-endDate"/>
    <!-- 创建市场活动的模态窗口 -->
    <div class="modal fade" id="createActivityModal" role="dialog">
        <div class="modal-dialog" role="document" style="width: 85%;">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal">
                        <span aria-hidden="true">×</span>
                    </button>
                    <h4 class="modal-title" id="myModalLabel1">创建市场活动</h4>
                </div>
                <div class="modal-body">

                    <form id="activityAddForm" class="form-horizontal" role="form">

                        <div class="form-group">
                            <label for="create-marketActivityOwner" class="col-sm-2 control-label">所有者<span
                                    style="font-size: 15px; color: red;">*</span></label>
                            <div class="col-sm-10" style="width: 300px;">
                                <select class="form-control" id="create-owner">

                                </select>
                            </div>
                            <label for="create-marketActivityName" class="col-sm-2 control-label">名称<span
                                    style="font-size: 15px; color: red;">*</span></label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="create-name">
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="create-startTime" class="col-sm-2 control-label">开始日期</label>
                            <div class="col-sm-10" style="width: 300px;">
                                <!--添加 time类-->
                                <input type="text" class="form-control time" id="create-startDate">
                            </div>
                            <label for="create-endTime" class="col-sm-2 control-label">结束日期</label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control time" id="create-endDate">
                            </div>
                        </div>
                        <div class="form-group">

                            <label for="create-cost" class="col-sm-2 control-label">成本</label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="create-cost">
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="create-describe" class="col-sm-2 control-label">描述</label>
                            <div class="col-sm-10" style="width: 81%;">
                                <textarea class="form-control" rows="3" id="create-description"></textarea>
                            </div>
                        </div>

                    </form>

                </div>
                <div class="modal-footer">
                    <!--data-dismiss:关闭模态窗口-->
                    <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                    <button type="button" class="btn btn-primary" id="saveBen">保存</button>
                </div>
            </div>
        </div>
    </div>

    <!-- 修改市场活动的模态窗口 -->
    <div class="modal fade" id="editActivityModal" role="dialog">
        <div class="modal-dialog" role="document" style="width: 85%;">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal">
                        <span aria-hidden="true">×</span>
                    </button>
                    <h4 class="modal-title" id="myModalLabel2">修改市场活动</h4>
                </div>
                <div class="modal-body">

                    <form class="form-horizontal" role="form">
                        <%--                        保存 点击修改时,被修改的活动id--%>
                        <input type="hidden" id="edit-id"/>

                        <div class="form-group">
                            <label for="edit-marketActivityOwner" class="col-sm-2 control-label">所有者<span
                                    style="font-size: 15px; color: red;">*</span></label>
                            <div class="col-sm-10" style="width: 300px;">
                                <select class="form-control" id="edit-owner">

                                </select>
                            </div>
                            <label for="edit-marketActivityName" class="col-sm-2 control-label">名称<span
                                    style="font-size: 15px; color: red;">*</span></label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="edit-name">
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="edit-startTime" class="col-sm-2 control-label">开始日期</label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control time" id="edit-startDate">
                            </div>
                            <label for="edit-endTime" class="col-sm-2 control-label">结束日期</label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control time" id="edit-endDate">
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="edit-cost" class="col-sm-2 control-label">成本</label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="edit-cost">
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="edit-describe" class="col-sm-2 control-label">描述</label>
                            <div class="col-sm-10" style="width: 81%;">
                                <textarea class="form-control" rows="3" id="edit-description"></textarea>
                            </div>
                        </div>

                    </form>

                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                    <button type="button" class="btn btn-primary" id="updateBtn">更新</button>
                </div>
            </div>
        </div>
    </div>


    <div>
        <div style="position: relative; left: 10px; top: -10px;">
            <div class="page-header">
                <h3>市场活动列表</h3>
            </div>
        </div>
    </div>
    <div style="position: relative; top: -20px; left: 0px; width: 100%; height: 100%;">
        <div style="width: 100%; position: absolute;top: 5px; left: 10px;">

            <div class="btn-toolbar" role="toolbar" style="height: 80px;">
                <form class="form-inline" role="form" style="position: relative;top: 8%; left: 5px;">

                    <div class="form-group">
                        <div class="input-group">
                            <div class="input-group-addon">名称</div>
                            <input class="form-control" type="text" id="search-name">
                        </div>
                    </div>

                    <div class="form-group">
                        <div class="input-group">
                            <div class="input-group-addon">所有者</div>
                            <input class="form-control" type="text" id="search-owner">
                        </div>
                    </div>


                    <div class="form-group">
                        <div class="input-group">
                            <div class="input-group-addon">开始日期</div>
                            <input class="form-control" type="text" id="search-startDate"/>
                        </div>
                    </div>
                    <div class="form-group">
                        <div class="input-group">
                            <div class="input-group-addon">结束日期</div>
                            <input class="form-control" type="text" id="search-endDate">
                        </div>
                    </div>

                    <button type="button" id="searchBtn" class="btn btn-default">查询</button>

                </form>
            </div>
            <div class="btn-toolbar" role="toolbar"
                 style="background-color: #F7F7F7; height: 50px; position: relative;top: 5px;">
                <div class="btn-group" style="position: relative; top: 18%;">
                    <!--
                        data-toggle:该按钮触发模态窗口
                        data-target:通过id指定目标模态窗口
                    -->
                    <button type="button" class="btn btn-primary" id="addBtn"><span
                            class="glyphicon glyphicon-plus"></span> 创建
                    </button>
                    <button type="button" class="btn btn-default" id="editBtn">
                        <span class="glyphicon glyphicon-pencil"></span> 修改
                    </button>
                    <button type="button" class="btn btn-danger" id="deleteBtn"><span
                            class="glyphicon glyphicon-minus"></span> 删除
                    </button>
                </div>

            </div>
            <div style="position: relative;top: 10px;">
                <table class="table table-hover">
                    <thead>
                    <tr style="color: #B3B3B3;">
                        <td><input id="qx" type="checkbox"/></td>
                        <td>名称</td>
                        <td>所有者</td>
                        <td>开始日期</td>
                        <td>结束日期</td>
                    </tr>
                    </thead>
                    <tbody id="activityBody">
                    <%--                    <tr class="active">--%>
                    <%--                        <td><input type="checkbox"/></td>--%>
                    <%--                        <td><a style="text-decoration: none; cursor: pointer;"--%>
                    <%--                               onclick="window.location.href='workbench/activity/detail.jsp';">发传单</a></td>--%>
                    <%--                        <td>zhangsan</td>--%>
                    <%--                        <td>2020-10-10</td>--%>
                    <%--                        <td>2020-10-20</td>--%>
                    <%--                    </tr>--%>
                    <%--                    <tr class="active">--%>
                    <%--                        <td><input type="checkbox"/></td>--%>
                    <%--                        <td><a style="text-decoration: none; cursor: pointer;"--%>
                    <%--                               onclick="window.location.href='detail.jsp';">发传单</a></td>--%>
                    <%--                        <td>zhangsan</td>--%>
                    <%--                        <td>2020-10-10</td>--%>
                    <%--                        <td>2020-10-20</td>--%>
                    <%--                    </tr>--%>
                    </tbody>
                </table>
            </div>

            <div style="height: 50px; position: relative;top: 30px;">
                <div id="activityPage">

                </div>
            </div>

        </div>

    </div>
</body>
</html>