<html>

<head>
    <meta charset="UTF-8">
    <title></title>
    <script type="text/javascript" src="js/jquery-1.11.0.js"></script>
    <style>
        * {
            margin: 0;
        }
        table {
            width: 800px;
            margin: 0 auto;
            border: 1px solid #ddd;
            border-collapse: collapse;
            text-align: center;
            margin-top: 50px;
        }
        td {
            height: 40px;
            border: 1px solid #ddd;
        }
        input {
            width: 100px;
            border: 1px solid #ddd;
            outline: none;
            padding: 6px;
            border-radius: 4px;
            display: none;
        }
        .btn {
            border: none;
            outline: none;
            cursor: pointer;
            padding: 6px 8px;
            color: #fff;
            border-radius: 4px;
        }
        .btn-red {
            background: darkred;
        }
        .que_change {
            display: none;
            background: #9e9e9e;
        }
        .del_change {
            background: #FF6428;
        }
        .btn-add {
            background: green;
            width: 200px;
        }
    </style>
</head>

<body>

<table>
    <thead>
    <tr>
        <td>姓名</td>
        <td>年龄</td>
        <td>成绩1</td>
        <td>操作</td>
    </tr>
    </thead>

    <tbody>

    </tbody>
    <tfoot>
    <tr class="btn">
        <td colspan="4">
            <button class="btn btn-add">增加</button>
        </td>
    </tr>
    </tfoot>

</table>

<script>
    $.ajax({
        type: "get",
        url: "data/data.json",
        success: function(res) {
            var str = ""
            for(var i = 0; i < res.length; i++) {
                str += '<tr><td><span>' + res[i].name + '</span><input type="text" /></td><td><span>' + res[i].age + '</span><input type="text"  /></td><td><span>' + res[i].score + '</span><input type="text" /></td><td><button class="btn btn-red change">修改</button> <button class="btn que_change">确定</button> <button class="btn del_change">删除</button></td></tr>'
            }
            $("tbody").append(str)
        }
    })
    //			修改
    var arrInfo = []
    $(document).on("click", ".change", function() {
        arrInfo = []
        $(this).hide()
        $(this).siblings(".que_change").show()

        $(this).parent().siblings().find("span").each(function() {
            arrInfo.push($(this).text())
        })

        $(this).parent().siblings().find("input").each(function(i) {
            $(this).val(arrInfo[i])
        })

        $(this).parent().siblings().find("span").hide()
        $(this).parent().siblings().find("input").show()

    })
    //              确定
    var arrList = []

    $(document).on("click", ".que_change", function() {
        arrList = []
        $(this).hide()
        $(this).siblings(".change").show()
        $(this).parent().siblings().find("input").each(function() {
            arrList.push($(this).val())
        })

        $(this).parent().siblings().find("span").each(function(i) {
            $(this).text(arrList[i])
        })
        $(this).parent().siblings().find("span").show()
        $(this).parent().siblings().find("input").hide()
    })
    //				增加
    $(".btn-add").click(function() {
        var str = '<tr><td><span style="display: none;"></span><input type="text" class="re_name" style="display: block;"/></td><td><span style="display: none";></span><input type="text" class="re_age" style="display:block"/></td><td><span style="display:none"></span><input type="text" class="re_score" style="display:block"/></td><td><button class="btn btn-red change" style="display:none">修改</button> <button class="btn que_change" style="display:inline-block">确定1</button> <button class="btn del_change">删除</button></td></tr>'
        $("tbody").append(str)
        $(this).parents("tr").prev().find(".que_change").text("确定")
        $(this).parents("tr").prev().find(".re_name").show().siblings().hide()
        $(this).parents("tr").prev().find(".re_age").show().siblings().hide()
        $(this).parents("tr").prev().find(".re_score").show().siblings().hide()
    })
    //删除
    $(function() {
        $(document).on("click", ".del_change", function() {
            $(this).parents("tr").remove()
        })
    })
</script>

</body>

</html>