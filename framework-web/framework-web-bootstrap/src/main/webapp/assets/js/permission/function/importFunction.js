(function ($) {
    var uploader = WebUploader.create({
        // swf文件路径
        swf: BASEPATH + 'assets/lib/webuploader-0.1.5/Uploader.swf',

        // 文件接收服务端。
        server: BASEPATH + 'resource/upload',
        //formData: $("#importForm").serialize(),

        //指定Drag And Drop拖拽的容器，如果不指定，则不启动
        //dnd: '#uploader .queueList',

        // 选择文件的按钮。可选。
        // 内部根据当前运行是创建，可能是input元素，也可能是flash.
        pick: '#filePicker',

        // 不压缩image, 默认如果是jpeg，文件上传前会压缩一把再上传！
        //resize: false,
        accept: {
            title: 'function',
            extensions: 'xls,xlsx',
            mimeTypes: 'application/msexcel'
        }

    });
    // 当有文件被添加进队列的时候
    uploader.on('fileQueued', function (file) {
        var $list = $("#thelist");
        $list.append('<div id="' + file.id + '" class="item">' +
            '<h4 class="info">' + file.name + '</h4>' +
            '<p class="state">等待上传...</p>' +
            '</div>');
    });
    // 文件上传过程中创建进度条实时显示。
    uploader.on('uploadProgress', function (file, percentage) {
        var $li = $('#' + file.id),
            $percent = $li.find('.progress .progress-bar');

        // 避免重复创建
        if (!$percent.length) {
            $percent = $('<div class="progress progress-striped active">' +
                '<div class="progress-bar" role="progressbar" style="width: 0%">' +
                '</div>' +
                '</div>').appendTo($li).find('.progress-bar');
        }

        $li.find('p.state').text('上传中');

        $percent.css('width', percentage * 100 + '%');
    });
    uploader.on('uploadSuccess', function (file) {
        $('#' + file.id).find('p.state').text('已上传');
    });

    uploader.on('uploadError', function (file) {
        $('#' + file.id).find('p.state').text('上传出错');
    });

    uploader.on('uploadComplete', function (file) {
        $('#' + file.id).find('.progress').fadeOut();
    });

    $("#ctlBtn").click(function () {
        uploader.upload();
    });
})(jQuery);