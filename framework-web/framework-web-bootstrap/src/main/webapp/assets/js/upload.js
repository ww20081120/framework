(function ($) {
    // 当domReady的时候开始初始化
    $(function () {
        var $wrap = $('#uploader'),
            $queue = $('<ul class="filelist"></ul>').appendTo($wrap.find('.queueList')),           // 图片容器
            $statusBar = $wrap.find('.statusBar'),          // 状态栏，包括进度和控制按钮
            $info = $statusBar.find('.info'),               // 文件总体选择信息。
            $upload = $wrap.find('.uploadBtn'),             // 上传按钮
            $placeHolder = $wrap.find('.placeholder'),      // 没选择文件之前的内容。
            $progress = $statusBar.find('.progress').hide(),
            fileCount = 0,           // 添加的文件数量
            fileSize = 0,            // 添加的文件总大小
            ratio = window.devicePixelRatio || 1,     // 优化retina, 在retina下这个值是2
            thumbnailWidth = 110 * ratio,             // 缩略图大小
            thumbnailHeight = 110 * ratio,
            state = 'pedding',                 // 可能有pedding, ready, uploading, confirm, done.
            percentages = {},  // 所有文件的进度信息，key为file id
            isSupportBase64 = (function () {   // 判断浏览器是否支持图片的base64
                var data = new Image();
                var support = true;
                data.onload = data.onerror = function () {
                    if (this.width != 1 || this.height != 1) {
                        support = false;
                    }
                }
                data.src = "data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///ywAAAAAAQABAAACAUwAOw==";
                return support;
            })(),
            supportTransition = (function () {
                var s = document.createElement('p').style,
                    r = 'transition' in s ||
                        'WebkitTransition' in s ||
                        'MozTransition' in s ||
                        'msTransition' in s ||
                        'OTransition' in s;
                s = null;
                return r;
            })(),
            uploader = WebUploader.create({
                pick: {
                    id: '#filePicker',
                    label: '点击选择文件'
                },
                dnd: '#dndArea',
                paste: '#uploader',
                swf: BASEPATH + 'assets/lib/webuploader-0.1.5/Uploader.swf',
                chunked: false,
                chunkSize: 512 * 1024,
                server: BASEPATH + 'resource/upload',
                // runtimeOrder: 'flash',
                accept: {
                    title: 'function',
                    extensions: 'xls,xlsx',
                    mimeTypes: 'application/msexcel'
                },

                // 禁掉全局的拖拽功能。这样不会出现图片拖进页面的时候，把图片打开。
                disableGlobalDnd: true,
                fileNumLimit: 1,
                fileSizeLimit: 200 * 1024 * 1024,    // 200 M
                fileSingleSizeLimit: 50 * 1024 * 1024    // 50 M
            });
        var fn = {
            addFile: function (file) {   // 当有文件添加进来时执行，负责view的创建
                var $li = $('<li id="' + file.id + '">' +
                        '<p class="title">' + file.name + '</p>' +
                        '<p class="imgWrap"></p>' +
                        '<p class="progress"><span></span></p>' +
                        '</li>'),

                    $btns = $('<div class="file-panel">' +
                            //'<span class="rotateRight">向右旋转</span>' +
                            //'<span class="rotateLeft">向左旋转</span>' +
                        '<span class="cancel">删除</span>' +
                        '</div>'
                    ).appendTo($li),
                    $prgress = $li.find('p.progress span'),
                    $wrap = $li.find('p.imgWrap'),
                    $info = $('<p class="error"></p>'),

                    showError = function (code) {
                        switch (code) {
                            case 'exceed_size':
                                text = '文件大小超出';
                                break;

                            case 'interrupt':
                                text = '上传暂停';
                                break;

                            default:
                                text = '上传失败，请重试';
                                break;
                        }

                        $info.text(text).appendTo($li);
                    };

                if (file.getStatus() === 'invalid') {
                    showError(file.statusText);
                } else {
                    // @todo lazyload
                    $wrap.text('预览中');
                    uploader.makeThumb(file, function (error, src) {
                        var img;
                        if (error) {
                            $wrap.text('不能预览');
                            return;
                        }
                        if (isSupportBase64) {
                            img = $('<img src="' + src + '">');
                            $wrap.empty().append(img);
                        } else {
                            $.ajax('../../server/preview.php', {
                                method: 'POST',
                                data: src,
                                dataType: 'json'
                            }).done(function (response) {
                                if (response.result) {
                                    img = $('<img src="' + response.result + '">');
                                    $wrap.empty().append(img);
                                } else {
                                    $wrap.text("预览出错");
                                }
                            });
                        }
                    }, thumbnailWidth, thumbnailHeight);

                    percentages[file.id] = [file.size, 0];
                    file.rotation = 0;
                }
                file.on('statuschange', function (cur, prev) {
                    if (prev === 'progress') {
                        $prgress.hide().width(0);
                    } else if (prev === 'queued') {
                        $li.off('mouseenter mouseleave');
                        $btns.remove();
                    }

                    // 成功
                    if (cur === 'error' || cur === 'invalid') {
                        console.log(file.statusText);
                        showError(file.statusText);
                        percentages[file.id][1] = 1;
                    } else if (cur === 'interrupt') {
                        showError('interrupt');
                    } else if (cur === 'queued') {
                        percentages[file.id][1] = 0;
                    } else if (cur === 'progress') {
                        $info.remove();
                        $prgress.css('display', 'block');
                    } else if (cur === 'complete') {
                        $li.append('<span class="success"></span>');
                    }

                    $li.removeClass('state-' + prev).addClass('state-' + cur);
                });

                $li.on('mouseenter', function () {
                    $btns.stop().animate({height: 30});
                }).on('mouseleave', function () {
                    $btns.stop().animate({height: 0});
                }).appendTo($queue);

                $btns.on('click', 'span', function () {
                    var index = $(this).index(),
                        deg;
                    switch (index) {
                        case 0:
                            uploader.removeFile(file);
                            return;
                        case 1:
                            file.rotation += 90;
                            break;
                        case 2:
                            file.rotation -= 90;
                            break;
                    }
                    if (supportTransition) {
                        deg = 'rotate(' + file.rotation + 'deg)';
                        $wrap.css({
                            '-webkit-transform': deg,
                            '-mos-transform': deg,
                            '-o-transform': deg,
                            'transform': deg
                        });
                    } else {
                        $wrap.css('filter', 'progid:DXImageTransform.Microsoft.BasicImage(rotation=' + (~~((file.rotation / 90) % 4 + 4) % 4) + ')');
                    }
                });
            },
            // 负责view的销毁
            removeFile: function (file) {
                var $li = $('#' + file.id);
                delete percentages[file.id];
                this.updateTotalProgress();
                $li.off().find('.file-panel').off().end().remove();
            },
            updateTotalProgress: function () {
                var loaded = 0,
                    total = 0,
                    spans = $progress.children(),
                    percent;

                $.each(percentages, function (k, v) {
                    total += v[0];
                    loaded += v[0] * v[1];
                });
                percent = total ? loaded / total : 0;
                spans.eq(0).text(Math.round(percent * 100) + '%');
                spans.eq(1).css('width', Math.round(percent * 100) + '%');
                this.updateStatus();
            },
            updateStatus: function () {
                var text = '', stats;
                if (state === 'ready') {
                    text = '选中' + fileCount + '个文件，共' + WebUploader.formatSize(fileSize) + '。';
                } else if (state === 'confirm') {
                    stats = uploader.getStats();
                    if (stats.uploadFailNum) {
                        text = '已成功上传' + stats.successNum + '个文件，' + stats.uploadFailNum + '个文件上传失败，<a class="retry" href="#">重新上传</a>失败文件或<a class="ignore" href="#">忽略</a>'
                    }
                } else {
                    stats = uploader.getStats();
                    text = '共' + fileCount + '个文件（' + WebUploader.formatSize(fileSize) + '），已上传' + stats.successNum + '个文件';
                    if (stats.uploadFailNum) {
                        text += '，失败' + stats.uploadFailNum + '个文件';
                    }
                }
                $info.html(text);
            },
            setState: function (val) {
                var file, stats;
                if (val === state) {
                    return;
                }
                $upload.removeClass('state-' + state);
                $upload.addClass('state-' + val);
                state = val;
                switch (state) {
                    case 'pedding':
                        $placeHolder.removeClass('element-invisible');
                        $queue.hide();
                        $statusBar.addClass('element-invisible');
                        uploader.refresh();
                        break;
                    case 'ready':
                        $placeHolder.addClass('element-invisible');
                        $('#filePicker2').removeClass('element-invisible');
                        $queue.show();
                        $statusBar.removeClass('element-invisible');
                        uploader.refresh();
                        break;
                    case 'uploading':
                        $('#filePicker2').addClass('element-invisible');
                        $progress.show();
                        $upload.text('暂停上传');
                        break;
                    case 'paused':
                        $progress.show();
                        $upload.text('继续上传');
                        break;
                    case 'confirm':
                        $progress.hide();
                        $('#filePicker2').removeClass('element-invisible');
                        $upload.text('开始上传');
                        stats = uploader.getStats();
                        if (stats.successNum && !stats.uploadFailNum) {
                            this.setState('finish');
                            return;
                        }
                        break;
                    case 'finish':
                        stats = uploader.getStats();
                        if (stats.successNum) {
                            console.log('上传成功');
                        } else {
                            // 没有成功的图片，重设
                            state = 'done';
                            location.reload();
                        }
                        break;
                }
                this.updateStatus();
            },
            hidden: function (name, value) {
                return $('<input/>').attr('type', 'hidden').attr('name', name).val(value);
            },
            callback: function (param) {
                var form = $('form');
                form.append(fn.hidden("mediaId", param.mediaId)).append(fn.hidden("mediaName", param.mediaName));
                form.submit();
            }
        };

        // 添加“添加文件”的按钮，
        //uploader.addButton({
        //    id: '#filePicker2',
        //    label: '继续添加'
        //});

        // 拖拽时不接受 js, txt 文件。
        uploader.on('dndAccept', function (items) {
            var denied = false,
                len = items.length,
                i = 0,
                unAllowed = 'text/plain;application/javascript ';  // 修改js类型

            for (; i < len; i++) {
                // 如果在列表里面
                if (~unAllowed.indexOf(items[i].type)) {
                    denied = true;
                    break;
                }
            }
            return !denied;
        }).on('ready', function () {
            window.uploader = uploader;

        }).on('fileQueued', function (file) {
            fileCount++;
            fileSize += file.size;
            if (fileCount === 1) {
                $placeHolder.addClass('element-invisible');
                $statusBar.show();
            }
            fn.addFile(file);
            fn.setState('ready');
            fn.updateTotalProgress();
        }).on('fileDequeued', function (file) {
            fileCount--;
            fileSize -= file.size;

            if (!fileCount) {
                fn.setState('pedding');
            }

            fn.removeFile(file);
            fn.updateTotalProgress();
        }).on('all', function (type) {

            switch (type) {
                case 'uploadFinished':
                    fn.setState('confirm');
                    break;
                case 'startUpload':
                    fn.setState('uploading');
                    break;
                case 'stopUpload':
                    fn.setState('paused');
                    break;
            }
        }).on('uploadProgress', function (file, percentage) {
            var $li = $('#' + file.id),
                $percent = $li.find('.progress span');

            $percent.css('width', percentage * 100 + '%');
            percentages[file.id][1] = percentage;
            fn.updateTotalProgress();
        }).on('uploadSuccess', function (file, response) {
            var res;
            if (response && (res = response.medias)) {
                if (res.length) {
                    fn.callback(res[0]);
                }
            }
        }).on('error', function (code) {
            if ('Q_EXCEED_SIZE_LIMIT' == code) {
                alert("只能选择一个文件！");
            }
            else {
                alert('Error: ' + code);
            }
        });


        $info.on('click', '.retry', function () {
            uploader.retry();
        }).on('click', '.ignore', function () {
            console.log('todo');
        });

        $upload.on('click', function () {
            if ($(this).hasClass('disabled')) {
                return false;
            }

            if (state === 'ready') {
                uploader.upload();
            } else if (state === 'paused') {
                uploader.upload();
            } else if (state === 'uploading') {
                uploader.stop();
            }
        }).addClass('state-' + state);

        fn.updateTotalProgress();
    });

})(jQuery);