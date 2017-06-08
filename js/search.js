var delay = (function(){
    var timer = 0;
    return function(callback, ms){
        clearTimeout (timer);
        timer = setTimeout(callback, ms);
    };
})();

function getDomainFromURL(url) {
    var domain;
    if (url.indexOf("://") > -1) {
        domain = url.split('/')[2];
    }
    else {
        domain = url.split('/')[0];
    }
    domain = domain.split(':')[0];

    return domain;
}

$.fn.submitButton = function(status) {
    var $btn = $(this).find('.btn-with-loader');
    if ($btn.length) {
        if (status == 'enabled') {
            $btn.removeClass('loading');
            $btn.attr('disabled', false);
        } else if (status == 'disabled') {
            $btn.addClass('loading');
            $btn.attr('disabled', 'disabled');
        }
    }
};

var isSearching = false,
    isLoaded = false,
    baseUrl,
    textUnavailable = 'We\'re sorry, this service is down, please try again later',
    optimizeIsLocked,
    optimizeUserIP,
    optimizeDomain,
    template = false;

function getSearchTemplate() {
    if (template) {
        return template;
    } else {
        var source   = $("#search-item").html();
        template = Handlebars.compile(source);
        return template;
    }
}

function updateResults() {

    if (isSearching) {
        return;
    }
    var $form = $('#header-search-form'),
        $input = $form.find('input[type=text]'),
        query = $input.val(),
        $searchResults = $('.search-results'),
        $loader = $('.navbar-search .loader');

    if ((query == $input.attr('data-query') && isLoaded) || query.length < 2 ){
        return;
    }

    isSearching = true;
    isLoaded = true;

    $.ajax({
        url: baseUrl + 'search/results.json?query=' + query,
        type: 'post',
        dataType: 'json',
        beforeSend: function() {
            $searchResults.addClass('loading');
            $loader.show();
        }
    })
    .done( function(data, textStatus, jqXHR) {
        var template = getSearchTemplate();
        $searchResults.html(template(data));
        $("html, body").animate({ scrollTop: 0 }, "fast");
        if (history.pushState) {
            history.pushState({ query: query }, "Search results", baseUrl + 'results?query=' + query);
        }
    })
    .fail( function() {
        $searchResults.html($("#search-error").html());
    })
    .always( function() {
        $searchResults.removeClass('loading');
        $loader.hide();
        $input.attr('data-query', query);
        isSearching = false;
    });
}

$(document).ready(function() {

    baseUrl = $("meta[name='url']").attr('content');

    $('body')
        .tooltip({
            selector: '[rel=tooltip]'
        })
        .on('submit', '.search-index form', function(event) {
            var query = $(this).find('.search-query input').val();
            if (query.length == 0) {
                event.preventDefault();
            }
        })
        .on('keyup', 'input[type="text"]', function(event) {
            if (event.keyCode == 13 && Modernizr.touch) {
                $(this).blur();
            }
        })
        .on('beforeSubmit', 'form', function(event) {
            $(this).submitButton('disabled');
        })
        .on('afterValidate', '.form-with-captcha', function(event, messages) {
            var $form = $(this);
            $.each(messages, function(id, message) {
                if ($('#' + id).hasClass('captcha-input')) {
                    $form.find('.captcha-image').yiiCaptcha('rearrange');
                }
            });
        })
        .on('mouseup', '.form-with-captcha .captcha-image', function() {
            $('.captcha-input').val('');
        })
        .on('submit', '#share-form', function() {
            var $form = $(this),
                $parent = $('.site-share'),
                $message = $parent.find('.share-status');
            $.ajax({
                url: baseUrl + 'share',
                data: $(this).serialize(),
                type: 'post',
                success: function(data) {
                    if (data.status == 'ok') {
                        $message.addClass('hidden');
                        $form.find('.share-status').addClass('hidden');
                        $form.find('.form-domain').val('');
                        $form.find('.captcha-input').val('');
                        $form.yiiActiveForm('resetForm');
                        $form.find('.captcha-image').yiiCaptcha('refresh');
                        $parent.find('.share-form').addClass('hidden');
                        $parent.find('.share-done').removeClass('hidden');
                    } else {
                        $message.removeClass('hidden').find('p').html(data.message);
                        $form.find('.share-status').removeClass('hidden').find('p').html(data.message);
                        $parent.find('.crawler-text').addClass('hidden');
                        $form.find('.captcha-image').yiiCaptcha('rearrange');
                    }
                    $form.submitButton('enabled');
                },
                error: function() {
                    $message.removeClass('hidden').find('p').html(textUnavailable);
                    $form.submitButton('enabled');
                }
            });
            return false;
        })
        .on('click', '.share-done .another', function(event) {
            event.preventDefault();
            $('.share-done').addClass('hidden');
            $('.share-form').removeClass('hidden');
        })
        .on('submit', '#crawler-form', function() {
            var $form = $(this),
                $parent = $('.site-crawler'),
                $message = $parent.find('.submit-status');
            $.ajax({
                url: baseUrl + 'crawler',
                data: $(this).serialize(),
                type: 'post',
                success: function(data) {
                    if (data.status == 'ok') {
                        var $text = $parent.find('.crawler-text');
                        $form.find('.submit-status').addClass('hidden');
                        $text.removeClass('hidden').html(data.text);
                        $("html, body").animate({ scrollTop: $text.offset().top }, 'slow');
                        $form.find('.form-domain').val('');
                        $form.find('.captcha-input').val('');
                        $form.yiiActiveForm('resetForm');
                        $form.find('.captcha-image').yiiCaptcha('refresh');
                    } else {
                        $form.find('.submit-status').removeClass('hidden').find('p').html(data.message);
                        $parent.find('.crawler-text').addClass('hidden');
                        $form.find('.captcha-image').yiiCaptcha('rearrange');
                    }
                    $form.submitButton('enabled');
                },
                error: function() {
                    $message.removeClass('hidden').find('p').html(textUnavailable);
                    $form.submitButton('enabled');
                }
            });
            return false;
        })
        .on('submit', '#optimize-domain-form', function(event) {
            var $form = $(this),
                $parent = $('.site-content'),
                $message = $parent.find('.optimize-status');
            $.ajax({
                url: baseUrl + 'optimize',
                data: $form.serialize(),
                type: 'post',
                success: function(data) {
                    if (data.status == 'ok') {
                        $parent.find('.domain-form').addClass('hidden');
                        $parent.find('.optimize-form').removeClass('hidden');
                        $parent.find('.optimize-form .domain-label').html(data.domain);
                        $parent.find('.optimize-form .optimize-hash').val(data.hash);
                        $parent.find('.field-optimizeform-links').addClass('hidden');
                        $message.addClass('hidden');
                        $form.find('.captcha-input').val('');
                        $form.find('.captcha-image').yiiCaptcha('refresh');
                        $form.yiiActiveForm('resetForm');
                        optimizeIsLocked = data.isLocked;
                        optimizeUserIP = data.userIP;
                        optimizeDomain = data.domain;
                        if (data.isLocked) {
                            $('.jumbotron .locked-status').removeClass('hidden').html('Locked');
                        } else {
                            $('.jumbotron .locked-status').addClass('hidden').html('');
                        }
                        $parent.find('.optimize-form .field-optimizeform-description')
                            .removeClass('hidden')
                            .find('textarea')
                            .val(data.description ? data.description : '');
                        $parent.find('.optimize-form .field-optimizeform-companyname')
                            .removeClass('hidden')
                            .find('input')
                            .val(data.companyName ? data.companyName : '');
                    } else {
                        $message.removeClass('hidden').find('p').html(data.message);
                        $form.find('.captcha-image').yiiCaptcha('rearrange');
                        if (data.status == 'validation') {
                            $form.yiiActiveForm('validate');
                        }
                    }
                    $form.submitButton('enabled');
                },
                error: function() {
                    $message.removeClass('hidden').find('p').html(textUnavailable);
                    $form.submitButton('enabled');
                }
            });

            return false;
        })
        .on('submit', '#optimize-data-form', function(event) {
            var $form = $(this),
                $parent = $('.site-content'),
                $message = $parent.find('.optimize-status'),
                mode = $form.find('.optimize-mode').val();

            if (!$form.find('.optimize-description').val() &&
                !$form.find('.optimize-webpage').val() &&
                !$form.find('.optimize-keywords').val()) {
                $form.submitButton('enabled');
                return false;
            }
            $.ajax({
                url: baseUrl + 'optimize',
                data: $form.serialize(),
                type: 'post',
                success: function(data) {
                    if (data.status == 'ok') {
                        if (mode == 0) {
                            $parent.find('.optimize-form').addClass('hidden');
                            $parent.find('.optimize-done').removeClass('hidden');
                        } else {
                            $form.find('.optimize-webpage').val('');
                            $form.find('.optimize-keywords').val('');
                            $form.find('.optimize-links').html(data.links);
                            $form.find('.field-optimizeform-links').removeClass('hidden');
                            $('.tags-input').tagsinput('removeAll');
                        }
                        $message.addClass('hidden');
                    } else {
                        $message.removeClass('hidden').find('p').html(data.message);
                        if (data.status == 'validation') {
                            $form.yiiActiveForm('validate');
                        }
                    }
                    $form.submitButton('enabled');
                    $("html, body").animate({ scrollTop: $message.offset().top - 60}, 'slow');
                },
                error: function() {
                    $message.removeClass('hidden').find('p').html(textUnavailable);
                    $form.submitButton('enabled');
                }
            });

            return false;
        })
        .on('click', '.optimize-done .another', function(event) {
            event.preventDefault();
            $('.optimize-done').addClass('hidden');
            $('.optimize-form').addClass('hidden');
            $('.domain-form').removeClass('hidden');
            $('.domain-form .captcha-image').yiiCaptcha('refresh');
            $('#optimizeform-keywords, .add-webpage .bootstrap-tagsinput > input').attr('placeholder', '400 chars max');
            $('body h1').html('Search Engine Optimization');
        })
        .on('click', '.optimize-done .addwebpage', function(event) {
            event.preventDefault();
            var $info = $('.company-info'),
                $mode = $('.optimize-form .optimize-mode');
            $('.optimize-done').addClass('hidden');
            $('.optimize-form').removeClass('hidden');
            $('.optimize-form .company-info').addClass('hidden');
            $('.optimize-form .add-webpage').removeClass('hidden');
            $('.domain-form').addClass('hidden');
            $info.addClass('hidden');
            $mode.val(1);
            $('.optimize-toggle-forms').html('Change Description')
            $('#optimizeform-keywords, .add-webpage .bootstrap-tagsinput > input').attr('placeholder', '400 chars max');
            $('body h1').html('Search Engine Optimization');
        })
        .on('click', '.site-forms .btn-lock', function(event) {
            $('.optimize-form').addClass('hidden');
            var $form = $('.lock-form');
            $form.removeClass('hidden');
            $form.find('.domain-label').html(optimizeDomain);
            $form.find('.lock-ip').html(optimizeUserIP);
            $form.find('.lock-domain').val(optimizeDomain);
            $form.find('.captcha-image').yiiCaptcha('refresh');
            $form.find('.lock-status').html(optimizeIsLocked ? 'Currently locked' : 'Not locked');
            $('body h1').html('Lock your domain so only you can Optimize');
        })
        .on('click', '.site-forms .lock-form .btn-cancel', function() {
            var $mode = $('.optimize-form .optimize-mode');
            $('.lock-form').addClass('hidden');
            $('.optimize-form').removeClass('hidden');
            $('.add-webpage').addClass('hidden');
            $('.company-info').removeClass('hidden');
            $mode.val(0);
            $('.optimize-toggle-forms').html('Add Pages')
            $('body h1').html('Search Engine Optimization');
        })
        .on('submit', '#lock-form', function() {
            var $form = $(this),
                $parent = $('.site-content'),
                $message = $parent.find('.optimize-status');
            $.ajax({
                url: baseUrl + 'lock',
                data: $form.serialize(),
                type: 'post',
                success: function(data) {
                    if (data.status == 'ok') {
                        $form.find('.lock-status').html(data.message);
                        $message.addClass('hidden');
                        $form.find('.captcha-input').val('');
                        $form.find('.captcha-image').yiiCaptcha('refresh');
                        $form.yiiActiveForm('resetForm');
                        $('.jumbotron .locked-status').html('Locked').removeClass('hidden');
                        $('.lock-form').addClass('hidden');
                        $('.optimize-form').removeClass('hidden');
                        $('body h1').html('Optimize a Business');
                        optimizeIsLocked = true;
                    } else {
                        $message.removeClass('hidden').find('p').html($('<div/>').text(data.message).html());
                        $form.find('.captcha-image').yiiCaptcha('rearrange');
                        if (data.status == 'validation') {
                            $form.yiiActiveForm('validate');
                        }
                    }
                    $form.submitButton('enabled');
                },
                error: function() {
                    $message.removeClass('hidden').find('p').html(textUnavailable);
                    $form.submitButton('enabled');
                }
            });

            return false;
        })
        .on('submit', '#boost-domain-form', function() {
            var $form = $(this),
                $parent = $('.site-content'),
                $message = $parent.find('.boost-status');
            $.ajax({
                url: baseUrl + 'boost',
                data: $form.serialize(),
                type: 'post',
                success: function(data) {
                    if (data.status == 'ok') {
                        $parent.find('.domain-form').addClass('hidden');
                        $parent.find('.boost-form').removeClass('hidden');
                        $parent.find('.boost-form .domain-label').html(data.domain);
                        $parent.find('.boost-form .boost-hash').val(data.hash);
                        $parent.find('.boost-form .boost-months-remaining').html(data.boostBalance);
                        if (data.isLocked) {
                            $('.jumbotron .locked-status').removeClass('hidden').html('Locked');
                        } else {
                            $('.jumbotron .locked-status').addClass('hidden').html('');
                        }
                        $message.addClass('hidden');
                        $form.find('.captcha-input').val('');
                        $form.find('.captcha-image').yiiCaptcha('refresh');
                        $form.yiiActiveForm('resetForm');
                    } else {
                        $message.removeClass('hidden').find('p').html(data.message);
                        $form.find('.captcha-image').yiiCaptcha('rearrange');
                        if (data.status == 'validation') {
                            $form.yiiActiveForm('validate');
                        }
                    }
                    $form.submitButton('enabled');
                },
                error: function() {
                    $message.removeClass('hidden').find('p').html(textUnavailable);
                    $form.submitButton('enabled');
                }
            });

            return false;
        })
        .on('click', '.boost-cancel', function(event) {
            event.preventDefault();
            $('.boost-form').addClass('hidden');
            $('.domain-form').removeClass('hidden');
        });

    $('.tags-input').tagsinput();

    $('.optimize-toggle-forms').click(function(event) {
        event.preventDefault();
        var $info = $('.company-info'),
            $addWebpage = $('.add-webpage'),
            $btn = $(this),
            $mode = $btn.closest('form').find('.optimize-mode');
        if ($info.hasClass('hidden')) {
            $info.removeClass('hidden');
            $addWebpage.addClass('hidden');
            $btn.html('Add Pages');
            $mode.val(0);
        } else {
            $addWebpage.removeClass('hidden');
            $info.addClass('hidden');
            $btn.html('Change Description');
            $mode.val(1);
        }
    });

    $(window).bind('popstate', function(event) {
        var $searchResults = $('.search-results'),
            state = event.originalEvent.state;
        if ($searchResults.length) {
            $('.navbar-search input[type="text"]').val(state.query);
            updateResults();
        }
    });

    $('#header-search-form').submit(function(event) {
        event.preventDefault();
        updateResults();
    });

    $('.site-boosted-domains .month-picker').on('change', function(event) {
        var costPerMonth = $(this).attr('data-cost-month'),
            costPerYear = $(this).attr('data-cost-year'),
            val = $(this).val(),
            total = val == 12 ? costPerYear * 1.0 : val * costPerMonth;
        $('.total-amount').html('$' + total.toFixed(2));
    });

    $('.keywords-mode input').on('change', function() {
        if ($(this).val() == 0) {
            $('#optimizeform-keywords, .add-webpage .bootstrap-tagsinput > input').attr('placeholder', '400 chars max');
        } else {
            $('#optimizeform-keywords, .add-webpage .bootstrap-tagsinput > input').attr('placeholder', '2000 chars max');
        }
    });

});

Handlebars.registerHelper("inc", function(value, options) {
    return parseInt(value, 10) + 1;
});
Handlebars.registerHelper("equal", function(a, b, options) {
    return a == b;
});